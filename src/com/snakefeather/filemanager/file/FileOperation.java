package com.snakefeather.filemanager.file;

import com.snakefeather.filemanager.function.LineTextFind;
import com.snakefeather.filemanager.function.LineTextHandle;
import com.snakefeather.filemanager.regex.FileRegex;
import org.junit.Test;
import sun.misc.CharacterEncoder;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.util.*;
import java.util.regex.Pattern;

public final class FileOperation implements FileRegex {

    // 文件路径匹配
    private static Pattern pattern = Pattern.compile(FileRegex.REGEX_FILEPATH);

    // 搜索到的所有 子文件夹 List<文件夹绝对路径>
    private static List<String> folderAllList = new LinkedList<>();
    // 搜索到的所有 子文件  Map<文件名,文件绝对路径>
    private static Map<String, String> fileAllMap = new HashMap<>();

    // 直接内存的空间大小
    private static final int SPACE1MB = 1024 * 1024;


    //#region 文件搜索

    /**
     * 传入文件路径，获取文件夹的绝对路径
     * 如果是文件，就获取它所在的文件夹的绝对路径。
     * 如果是文件夹，直接获取它的绝对路径。
     *
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static String getAbsoluteFile(String path) throws FileNotFoundException {
        if (!pattern.matcher(path).matches())
            throw new FileNotFoundException("路径无效");
        File file = new File(path);
        if (!file.exists())
            throw new FileNotFoundException("找不到该文件");
        if (file.isDirectory()) {
            //  是文件夹  //   返回此文件夹的绝对地址
            return file.getAbsolutePath();
        } else {
            //  是文件  // 返回所在文件夹
            return file.getParent();
        }
    }

    /**
     * 传入一个文件夹路径  遍历，递归获取所有的子文件夹  List<子路径>
     *
     * @param folderPath
     * @return
     * @throws FileNotFoundException
     */
    public static List<String> getAllFolder(String folderPath) throws FileNotFoundException {
        File file = new File(folderPath);
        String[] fileList = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String folderPath = dir.getAbsolutePath() + File.separator + name;
                if (!Pattern.compile(FileRegex.REGEX_SYSTEMFILE).matcher(name).matches()) {
                    //  排除系统文件
                    File folder = new File(folderPath);
                    if (folder.exists() && folder.isDirectory() && folder.canRead() && folder.canExecute()) {
                        //  文件存在，是文件夹，可读
                        return true;
                    }
                }
                return false;
            }
        });
        if (null != fileList) {
            if (fileList.length > 0) {
                for (String folder : fileList) {
                    //  添加子文件夹      //  如果使用字符串拼接，容易出现双斜杠的路径。在匹配时要考虑双斜杠和单斜杆路径，有点麻烦。
                    folderAllList.add(new File(file.getAbsolutePath() + File.separator + folder).getAbsolutePath());
                    //  递归子文件夹
                    getAllFolder(file.getAbsolutePath() + File.separator + folder);
                }
            }
        } else {
            System.out.println("NullPointException:" + file.getAbsolutePath() + "文件异常。");
            //  移除这个文件夹
            folderAllList.remove(file.getAbsolutePath());
        }
        return folderAllList;
    }

    /**
     * 传入文件夹路径，获取它下面的所有文件以及子文件。 Map<文件名，文件路径>
     *
     * @param path
     * @return 文件名 ，文件路径
     * @throws FileNotFoundException
     */
    public static Map<String, String> getAllFile(String path) throws FileNotFoundException {
        getAllFolder(path);
        if (null != folderAllList && folderAllList.size() > 0) {
            for (String folderPath : folderAllList) {
                //  遍历文件夹列表
                File folder = new File(folderPath);
                String[] fileList = folder.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        File f = new File(dir.getAbsoluteFile() + File.separator + name);
                        if (f.isFile()) {
                            return true;
                        }
                        return false;
                    }
                });
                if (null != fileList) {
                    if (fileList.length > 0) {
                        //  遍历文件夹中的文件
                        for (String file : fileList) {
                            File f = new File(folder + File.separator + file);
                            fileAllMap.put(f.getName(), f.getAbsolutePath());
                        }
                    }
                } else {
                    System.out.println("NullPointException:" + folder.getAbsolutePath() + "异常");
                }
            }
        }
        return fileAllMap;
    }
    //#endregion

    //#region 行文本处理

    /**
     * 传入文件路径，遍历它下面的每一行。根据lambda表达式。并根据语句，进行相应的处理。只读取，不修改语句。（安全）
     *
     * @param filePath     具体文件的路径
     * @param findPhotoUrl 函数式接口
     * @return 获取到的文本
     * @throws IOException
     */
    public static boolean lineTextFindOperation(String filePath, LineTextFind findPhotoUrl, LineTextHandle lineTextHandle) throws IOException {
        File file = new File(filePath);
        if (!(file.exists() && file.isFile() && file.canRead() && file.canExecute()))
            return false;
        try (FileReader fr = new FileReader(file);
             BufferedReader bufr = new BufferedReader(fr);) {
            String s = null;
            while ((s = bufr.readLine()) != null) {
                if (findPhotoUrl.getLineTest(s)) {    // 筛选语句
                    lineTextHandle.updateLineTest(s);    // 进行操作
                }
            }
        }
        return true;
    }

    /**
     * 传入文件路径，遍历它下面的每一行。根据lambda表达式。并根据语句，进行相应的处理。可能会修改语句。（不安全）
     *
     * @param filePath     具体文件的路径
     * @param findPhotoUrl 函数式接口
     * @return 获取到的文本
     * @throws IOException
     */
    public static boolean lineTextUpdateOperation(String filePath, LineTextFind findPhotoUrl, LineTextHandle lineTextHandle) throws IOException {
        File file = new File(filePath);
        if (!(file.exists() && file.isFile() && file.canRead() && file.canExecute()))
            return false;
        File tempFile = makeFile(file.getAbsolutePath());
        try (BufferedReader bufr = new BufferedReader(new FileReader(file));
             BufferedWriter bufw = new BufferedWriter(new FileWriter(tempFile));) {
            String s = null;
            while ((s = bufr.readLine()) != null) {
                if (findPhotoUrl.getLineTest(s)) {    // 筛选语句
                    s = lineTextHandle.updateLineTest(s);
                }
                bufw.write(s + "\n");    // 修改行文本
            }
        }
        // 替换文件
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
    public static boolean lineTextAddOperation(String filePath, List<String> stringList) {
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
    public static boolean copyFile(String filePath, String folderPath) {
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
    public static boolean copyFile(String filePath, String folderPath, String fileName) {
        File file = new File(filePath);
        //  确定文件有效性
        if (!file.exists()) {
            System.out.println("\n路径无效，该文件不存在。\n路径:" + file.getAbsolutePath());
        } else if (!file.isFile()) {
            System.out.println("\n目标对象不是文件。\n路径:" + file.getAbsolutePath());
        }
        File folder = new File(folderPath);
        //  确定文件夹有效性
        if (!folder.exists()) {
            //  win10中，文件夹可以是“1.txt"这种新式，有点麻烦。
            if (!folder.isDirectory()) {
                System.out.println("\n目标路径是文件，不是文件夹。\n路径:" + folder.getAbsolutePath());
                return false;
            }
            if (!folder.mkdirs()) {
                //  没有目标文件夹，尝试进行创建，创建失败。
                System.out.println("\n目标路径不存在，尝试创建时失败。\n路径:" + folder.getAbsolutePath());
                return false;
            }
        }
        // 如果文件名为空  就使用原文件的文件名  //  所指定的文件名
        if (null == fileName) fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
        else {
            // 获取指定文件的后缀    //  隐患  // 文件名中可以含有'.' // File.getName()会在File对象是文件夹时，返回文件夹路径
            String suffix;
            if (file.getName().indexOf('.') >= 0) {
                suffix = file.getName().substring(file.getName().indexOf('.'));
                fileName += suffix;
            }
        }
        //  确定指定文件名有效性
        if (!Pattern.compile(REGEX_FOLDERPATH).matcher(fileName).matches()) {
            System.out.println("指定的文件名无效。");
            return false;
        }

        //  创建指定文件。
        File newFile = new File(folder.getAbsolutePath() + File.separator + fileName);
        if (newFile.exists()) {
            System.out.println("\n目标位置存在指定文件。\n路径:" + newFile.getAbsolutePath());
            return false;
        } else {
            try {
                if (!newFile.createNewFile()) {
                    System.out.println("\n创建指定文件失败。\n路径:" + newFile.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();        //  后期优化为日志文件
                System.out.println("\n创建指定文件失败。\n路径:" + newFile.getAbsolutePath());
            }
        }
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
        return true;
    }
    //#endregion

    /**
     * 获取文件编码  // 待优化，暂时写死
     *
     * @param file 已确认的存在的文件
     * @return 编码
     */
    private static String getFileEncoding(File file) {
        return "UTF-8";
    }

}
