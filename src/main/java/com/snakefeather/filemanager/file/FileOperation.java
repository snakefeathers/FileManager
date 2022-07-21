package com.snakefeather.filemanager.file;

import com.snakefeather.filemanager.regex.RegexStore;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystemException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;


/**
 * 基础的文件操作进行封装，文件的工具类
 */
public final class FileOperation {


    // 直接内存的空间大小  // 暂且写死 // 后期优化为动态改变
    private static final int SPACE1MB = 1024 * 1024;


    //#region 行文本处理

    /**
     * 传入文件路径，遍历它下面的每一行。根据lambda表达式。并根据语句，进行相应的处理。只读取，不修改语句。（安全）
     *
     * @param filePath       具体文件的路径
     * @param findPhotoUrl   筛选函数
     * @param lineTextHandle 处理函数
     * @return 获取到的文本
     * @throws IOException
     */
    public static boolean lineTextFindOperation(String filePath,
                                                Predicate<String> findPhotoUrl,
                                                Function<String, String> lineTextHandle) throws IOException {
        File file = new File(filePath);
        if (!(file.exists() && file.isFile() && file.canRead() && file.canExecute()))
            return false;
        try (FileReader fr = new FileReader(file);
             BufferedReader bufr = new BufferedReader(fr);) {
            String s = null;
            while ((s = bufr.readLine()) != null) {
                if (findPhotoUrl.test(s)) {    // 筛选语句
                    lineTextHandle.apply(s);    // 进行操作
                }
            }
        }
        return true;
    }

    /**
     * 传入文件路径，遍历它下面的每一行。根据lambda表达式。并根据语句，进行相应的处理。可能会修改语句。（不安全）
     *
     * @param filePath       具体文件的路径
     * @param findPhotoUrl   筛选函数
     * @param lineTextHandle 处理函数
     * @return 获取到的文本
     * @throws IOException
     */
    public static boolean lineTextUpdateOperation(String filePath, Predicate<String> findPhotoUrl, Function<String, String> lineTextHandle) throws IOException {
        File file = new File(filePath);
        if (!(file.exists() && file.isFile() && file.canRead() && file.canExecute()))
            return false;
        File tempFile = makeFile(file.getAbsolutePath());
        try (BufferedReader bufr = new BufferedReader(new FileReader(file));
             BufferedWriter bufw = new BufferedWriter(new FileWriter(tempFile));) {
            String s = null;
            while ((s = bufr.readLine()) != null) {
                if (findPhotoUrl.test(s)) {    // 筛选语句
                    s = lineTextHandle.apply(s);
                }
                bufw.write(s + "\n");    // 修改行文本   //收到输入可能会忽视换行，直接手动加一个。
            }
        }
        // 替换文件 // 删除旧文件，新文件重命名为原文件，以此替代原文件。
        String fileName = file.getAbsolutePath();
        file.delete();
        tempFile.renameTo(new File(fileName));
        return true;
    }
    //#endregion

    /**
     * 将指定数据插入指定文件的结尾
     *
     * @param filePath
     * @param stringList
     * @return
     */
    public static boolean textAddOperation(String filePath, List<String> stringList) {
        File file = new File(filePath);
        if (!(file.exists() && file.isFile() && file.canRead() && file.canExecute()))
            return false;
        String encoding = getFileEncoding(file);
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");) {
            // 移动指针到末尾
            raf.seek(raf.length());
            for (String s : stringList) {
                raf.write((s + "\n").getBytes(encoding));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 创建指定文件  一定会创建成功。 只是在遇到重名文件，会自动生成特制文件名，
     *
     * @param folderPath 文件夹路径
     * @param fileName   指定文件名
     * @return 创建的文件
     */
    public static File makeFile(String folderPath, String fileName) {
        File file = makeFile(folderPath);
        File f = new File(fileName);
        if (!f.exists()) {
            file.renameTo(f);
        } else {
            System.out.println("目标文件已存在，自动生成合成文件名。");
            // 自动生成为随机命名文件
            file.renameTo(new File("snakeFeather|" + new Date().hashCode() + fileName));
        }
        return file;
    }

    /**
     * 在指定目录创建临时文件 文件名待定
     *
     * @param filePath 文件夹目录
     * @return
     */
    private static File makeFile(String filePath) {
        File tempFile = new File(filePath + "tempFile&" + new Date().hashCode());// 避免重名 // 临时文件  // 后期优化抽取出去。
        int i = 0;
        try {
            while (tempFile.exists()) {
                i++;
                if (i > 100) throw new FileSystemException("临时文件创建失败。");
                Thread.currentThread().wait(5000);          //当前线程等待一段时间 // new Date()的哈希值会变化
                tempFile = new File(filePath + "tempFile&" + new Date().hashCode()); // 重新赋值
            }
            tempFile.createNewFile();
        } catch (InterruptedException | FileSystemException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("临时文件创建异常");
            e.printStackTrace();
        }
        return tempFile;
    }

    //#region  文件复制

    /**
     * 将指定文件复制到指定路径中。——默认文件名
     * 目标文件未指定文件名。
     *
     * @param filePath   指定文件
     * @param folderPath 目标路径
     * @return 操作是否成功
     */
    public static String copyFile(String filePath, String folderPath) {
        return copyFile(filePath, folderPath, null);
    }

    /**
     * 将指定文件复制到指定路径中。——指定文件名(无须后缀)
     * 指定文件名。
     * 后期优化：文件名后缀获取漏洞  、 文件读取和写入抽取（重复代码）
     *
     * @param filePath   指定文件
     * @param folderPath 目标路径
     * @param fileName   目标文件名
     * @return 操作是否成功
     */
    public static String copyFile(String filePath, String folderPath, String fileName) {
        File file = new File(filePath);
        File folder = new File(folderPath);

        //  确定文件有效性
        if (!file.exists() || !file.isFile()) {
            //SF: 做一次基础检查。   具体检查靠上层。
            throw new IllegalArgumentException("路径无效，该文件不存在。");
        }
        //  确定文件夹有效性
        if (!folder.exists() || !folder.isDirectory()) {
            //  win10中，文件夹可以是“1.txt"这种新式，有点麻烦。
            throw new IllegalArgumentException("目标路径无效。");
        }

        //SF: 调用方法.  如果目标位置已有同名文件，就会生成随机的文件。  没有的话，就使用原有的文件名。
        if (fileName == null) {
            fileName = makeFileName(folder, file.getName());
        } else {
            fileName = makeFileName(folder, fileName);
        }
        //  创建指定文件。
        File newFile = new File(folder.getAbsolutePath() + File.separator + fileName);
        if (newFile.exists()) {
            throw new IllegalArgumentException("创建文件失败，可能是并发创建文件导致重名");
        }

        //SF:   SnakeFeather 2022——05——18 改于此处。
        //SF:  记录：   以上验证代码抽象处理。   此方法方法尽可能精简。
        //  开始移动  // 低效  // 优化  使用直接内存
        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(newFile);
        ) {
            byte[] datas = new byte[1024 * 8];  //  中间数组。
            int len = 0;    //  存储长度
            while ((len = fis.read(datas)) != -1) {
                fos.write(datas, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //
        try (FileChannel from = new FileInputStream(file).getChannel();
             FileChannel to = new FileOutputStream(newFile).getChannel()) {
            ByteBuffer bb = ByteBuffer.allocateDirect(SPACE1MB);
            // 使用读写缓冲区
            while (true) {
                int len = from.read(bb);
                if (len == -1) {
                    break;
                }
                bb.flip();
                to.write(bb);
                bb.clear();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFile.getAbsolutePath();
    }
    //#endregion


    /**
     * 在指定路径中，起个文件名。重名就加序号。
     *
     * @param folder
     * @param fileName
     * @return
     */
    private static String makeFileName(File folder, String fileName) {
        File file = new File(folder.getAbsolutePath() + File.separator + fileName);
        for (int i = 1; file.exists(); i++) {
            if (i > 1000) {
                //SF:  为了安全，加一条吧。
                throw new IllegalArgumentException("重名文件太多。");
            }
            StringBuffer newFileName = new StringBuffer(folder.getAbsolutePath());
            newFileName.append(File.separator);
            //SF:  存在重名文件，加序号。
            if (fileName.indexOf('.') >= 0) {
                //SF: 加文件名
                newFileName.append(fileName.substring(0, fileName.indexOf('.')));
                //SF: 加序号
                newFileName.append("_").append(i);
                //SF: 加后缀
                newFileName.append(fileName.substring(fileName.indexOf('.')));
            } else {
                //SF: 加文件名
                newFileName.append(fileName);
                //SF: 加序号
                newFileName.append("_").append(i);
            }
            file = new File(newFileName.toString());
        }
        return file.getName();
    }

    /**
     * 获取文件编码  // 待优化，暂时写死
     *
     * @param file 已确认的存在的文件
     * @return 编码
     */
    private static String getFileEncoding(File file) {
        return "UTF-8";
    }

    /**
     * java计算文件32位的md5值
     *   结果必定是32位长度
     *
     * @param in 输入流
     * @return
     */
    public static String md5HashCode32(InputStream in) {
        try {
            //  初始化一个MD5转换器。   //  可以传入参数"SHA-1"或"SHA-256"
            MessageDigest md = MessageDigest.getInstance("MD5");

            //  分片计算
            byte[] buffer = new byte[4096];
            //  判断长度，以此判定结尾。
            int length = -1;
            while ((length = in.read(buffer, 0, 4096)) != -1) {
                md.update(buffer, 0, length);
            }
            in.close();
            //  执行最后的操作（如：填充），完成计算。
            //  转换并返回包含16个字节的数组，字节元素数值范围为-128~127
            byte[] md5Bytes = md.digest();
            StringBuffer hash = new StringBuffer();
            for (byte md5Byte : md5Bytes) {
                //  SF: 将byte值转为int类型  无符号
                //  1. byte 值为-128~127   16位 符号位一位，数据位15位
                //     & 0xff    16*16 = 256  占16位。
                //  2. (byte & 0xff) 即，将符号位视为数据位，将byte（1+15数据位）转为16数据位
                int val = ((int) md5Byte) & 0xff;
                if (val < 16) {
                    //  0~ 15
                    //  填充，保证一个byte得到两位16进制
                    hash.append("0");
                }
                //  16~255 转换为16进制，必定是两位十六进制
                hash.append(Integer.toHexString(val));
            }
            //  返回16进制的字符串
            return hash.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


}
