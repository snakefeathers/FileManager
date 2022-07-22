package com.snakefeather.filemanager.domain;

import com.snakefeather.filemanager.domain.md.MdTextCode;
import com.snakefeather.filemanager.domain.md.PhotoMsg;
import com.snakefeather.filemanager.file.FileOperation;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * 初始化时指定文件名。
 * 后期可以通过read()方法修改
 */

public class FileTextList extends LinkedList<TextDiv> {

    //  文件路径
    private Path path;
    //  文件名
    private String fileName;
    //  文件大小
    private float capacity;
    //  通过md加密计算出来的文件id
    private String fileId;

    //  用于格式化 文件大小 单位Kb
    private static final DecimalFormat df = new DecimalFormat("#0.00");


    public FileTextList(String fileName) {
        this(new File(fileName));
    }

    public FileTextList(File file) {
        this.path = Paths.get(file.getAbsolutePath());
        fileName = file.getName();
        capacity = new Float(df.format(file.length() / 1024.0));
        setFileId();        //  计算文件ID
    }

    public void read() {
        read(path.toString());
    }

    public void read(String fileName) {
        // 用于处理代码块的临时变量
        MdTextCode codePiece = null;
        // 标志处于代码块中
        boolean isCode = false;
        //  行号
        long lineCount = 1;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String str = new String();
            while ((str = reader.readLine()) != null) {
                //   代码块 单独拎出来处理
                if (str.matches("```.*")) {
                    //  代码块开头   初始化代码块对象
                    codePiece = new MdTextCode(path, lineCount++, str);
                    while ((str = reader.readLine()) != null) {
                        codePiece.add(str);
                        lineCount++;
                        if (str.matches("```.*")) {
                            break;
                        }
                    }
                    //   不管是 break  还是(str == null) 都要将代码块添加。
                    add(codePiece);
                } else {
                    add(TextDivs.getTextDivByStr(path, lineCount++, str));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("找不到该文件：" + fileName);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("加载文件异常：" + fileName);
            e.printStackTrace();
        }
    }

    public boolean write() {
        return write(path);
    }

    public boolean write(Path path) {
        try (PrintWriter writer = new PrintWriter(path.toFile())) {
            while (size() > 0) {
                writer.println(remove(0));
            }
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }

    public Map<String, PhotoMsg> getAllPhotoMsp() {
        Map<String, PhotoMsg> msgMap = new HashMap<>();
        for (TextDiv textDiv : this) {
            if (textDiv.getTextType() == TextDiv.MsgTypeEnum.IMG
                    || textDiv.getTextType() == TextDiv.MsgTypeEnum.LABEL_PHOTO) {
                PhotoMsg photoMsg = (PhotoMsg) textDiv;
                msgMap.put(photoMsg.getPhotoName(), photoMsg);
            }
        }
        return msgMap;
    }

    // 文本分类
    private void addText(String str) {

    }

    //  设置文件Id  //  修改文件时有可能修改。  //  虽然文件几乎是整个替换（删除，添加），没有修改的说法。还是加一个意思一下。
    public void setFileId() {
        File file = new File(path.toString());
        try {
            this.fileId = FileOperation.md5HashCode32(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public float getCapacity() {
        return capacity;
    }

    public void setCapacity(float capacity) {
        this.capacity = capacity;
    }

    public String getFileId() {
        return fileId;
    }
}
