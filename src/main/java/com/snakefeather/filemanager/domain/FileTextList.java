package com.snakefeather.filemanager.domain;

import com.snakefeather.filemanager.domain.md.MdTextCode;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.LinkedList;


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

    private int fileId;


    private static final DecimalFormat df = new DecimalFormat("#0.00");


    public FileTextList(String fileName) {
        this(new File(fileName));
    }

    public FileTextList(File file) {
        this.path = Paths.get(file.getAbsolutePath());
        fileName = file.getName();
        capacity = new Float(df.format(file.length() / 1024.0));
        read(file.getAbsolutePath());
        fileId = hashCode();
    }

    public void read(String fileName) {
        // 用于处理代码块的临时变量
        MdTextCode codePiece = null;
        // 标志处于代码块中
        boolean isCode = false;
        //  行号
        long lineNumber = 1;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String str = new String();
            while ((str = reader.readLine()) != null) {
                // 代码块 单独拎出来处理
//                if (str.matches("```.*")) {
//                    isCode = !isCode;
//                    if (isCode) {
//                        //  代码块
//                        if (codePiece == null) {
//                            //  代码块开头   初始化代码块对象
//                            codePiece = new MdTextCode(path, lineCount++, str, TextDiv.MsgTypeEnum.CODE);
//                        } else {
//                            // 代码块中间    直接加文本就行
//                            lineCount++;
//                            codePiece.add(str);
//                        }
//                    } else {
//                        //  代码块 结尾
//                        // 将代码块对象添加到列表中。
//                        lineCount++;
//                        add(codePiece);
//                        codePiece = null;
//                    }
//                } else {
                add(TextDivs.getTextDivByStr(path, lineNumber++, str));
//                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("加载文件异常" + fileName);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("加载文件异常" + fileName);
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

    // 文本分类
    private void addText(String str) {

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

    public int getFileId() {
        return fileId;
    }
}
