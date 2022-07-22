package com.snakefeather.filemanager.domain;

import com.snakefeather.filemanager.domain.md.PhotoMsg;
import com.snakefeather.filemanager.file.PropertiesOperation;
import com.snakefeather.filemanager.service.impl.MarkdownServiceImpl;
import com.sun.javafx.binding.StringFormatter;
import org.apache.log4j.Logger;

import java.beans.XMLEncoder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 唯一的类。
 * 用于分配，管理数据。
 */
public class DataCenter {


    private Logger logger = Logger.getLogger(this.getClass().getName());

    //  文件字典   //  主要指文本文件
    public static Map<String, FileTextList> fileMap = null;
    //   配置文件   // 包含一些 常修改调整的常量
    public static Properties properties = null;
    //   修改记录  日志文件夹的路径
    private static String updateLogFolder = getProperty("LogDirectory") + File.separator + "mosheyu";

    /**
     * 获取文件字典
     *
     * @return
     */
    public static Map<String, FileTextList> getFileMap() {
        if (null == fileMap) {
            fileMap = new HashMap<>();
            String[] folders = getProperty("notesPath").split(";");
            for (String folderPath : folders) {
                Folder folder = new Folder(folderPath);
                //  合并两个映射   //  遍历新读取的文件映射，将其合并到现有的总映射中。  相同可以时，以新值为准。
                (folder.getFileMap()).forEach((key, value) -> fileMap.merge(key, value, (v1, v2) -> v2));
            }

        }
        return fileMap;
    }

    /**
     * 获取配置文件中的一些属性值
     *
     * @param key 属性的key
     * @return
     */
    public static String getProperty(String key) {
        if (null == properties) {
            PropertiesOperation pro = new PropertiesOperation();
            //  配置文件
            properties = pro.getPropertiesByAbso("E:\\0z_SnakeFeatherObject\\FileManager\\config\\defult.properties");
        }
        //   有可能为null
        return properties.getProperty(key);
    }


    //#region  获取、复制日志文件  （没有的话，会创建）

    /**
     * 获取日志文件
     *
     * @return
     */
    private static String getLogPath(String folderPath, String fileName) {
        String log = folderPath + File.separator + fileName;

        File file = new File(log);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("DataCenter | getLogPath：创建文件异常");

        }
        return file.getAbsolutePath();
    }

    /**
     * 获取 xml文件
     * 方便操作
     *
     * @return
     */
    private static String getXMLPath(String folderPath, String fileName) {
        String xml = folderPath + File.separator + fileName;
        File file = new File(xml);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            if (!file.isHidden()) {
//                    隐藏文件
//                    String sets = "attrib +H \"" + file.getAbsolutePath() + "\"";
//                    Runtime.getRuntime().exec(sets);
                Files.setAttribute(file.toPath(), "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("DataCenter | getXMLPath：创建文件、隐藏文件异常");
        }
        return file.getAbsolutePath();
    }

    /**
     * 保存 一份复制品
     *
     * @param title  标题
     * @param logXml 日志源文件
     */
    public static void saveXMLPath(String title, String logXml) {
        File folder = new File(updateLogFolder);
        if (!folder.exists()) folder.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd hh-mm-ss");
        File file = new File(updateLogFolder + File.separator + title + dateFormat.format(new Date()) + ".xml");
        try {
            System.out.println("生成日志文件：" + file.getAbsolutePath());
            Files.copy(Paths.get(logXml), file.toPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("DataCenter : savePhotoMsgXMLPath: 找不到文件。文件：" + logXml);
            return;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("DataCenter : savePhotoMsgXMLPath: 异常");
            return;
        }
    }

    //#endregion

    //#region  图片链接的日志

    /**
     * 获取 图片链接的日志文件
     *
     * @return
     */
    public static String getPhotoMsgLogPath() {
        return getLogPath(getProperty("LogDirectory"), getProperty("PhotoMsgLog"));
    }

    /**
     * 获取 图片链接的Xml文件
     *
     * @return
     */
    public static String getPhotoMsgXMLPath() {
        return getLogPath(getProperty("LogDirectory"), getProperty("PhotoMsgXml"));
    }

    /**
     * 将图片链接信息写入到日志文件中
     *
     * @param photoMsgs
     */
    public static void writePhotoMsg(Collection<PhotoMsg> photoMsgs) {
        // 默认日志文件
        writePhotoMsgToFile(photoMsgs, getPhotoMsgLogPath());
    }

    public static void writePhotoMsgToFile(Collection<PhotoMsg> photoMsgs, String targetFile) {
        String xmlPath = getPhotoMsgXMLPath();
        try (XMLEncoder xmlEncoder = new XMLEncoder(new BufferedOutputStream(
                new FileOutputStream(xmlPath)));
             BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile))) {
            for (PhotoMsg photoMsg : photoMsgs) {
                TextDiv textDiv = (TextDiv) photoMsg;
                writer.write(String.format("%s\t\t%s\t\t%d\t\t%s",
                        photoMsg.getPhotoName(),    // 图片名
                        textDiv.getFilePath().toString(),   // 所属文件
                        textDiv.getLineNumber(),    // 行号
                        textDiv.getOriginalText()   // 原文
                ));
                writer.newLine();
                xmlEncoder.writeObject(textDiv);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("DataCenter | writePhotoMsgToFile : 找不到文件。目标文件：" + targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("DataCenter | writePhotoMsgToFile : IO异常。目标文件：" + targetFile);
        }
        saveXMLPath("PhotoMsg", xmlPath);
    }

    /**
     * 读取日志文件
     *
     * @return
     */
    public static List<String> readPhotoMsgByFile() {
        // 默认日志文件
        return readPhotoMsgByFile(getPhotoMsgLogPath());
    }

    public static List<String> readPhotoMsgByFile(String targetFile) {
        List<String> list = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(targetFile))) {
            String s = null;
            while ((s = reader.readLine()) != null) {
                //  不能是空数据
                if (s != "" && !s.matches("\\s")) {
                    list.add(s.split("\t\t")[0]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("DataCenter | readPhotoMsgByFile : 找不到目标文件：" + targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("DataCenter | readPhotoMsgByFile : IO异常。目标文件：" + targetFile);
        }
        return list;
    }

    //#endregion


    //#region  图片的日志


    /**
     * 获取 图片路径的日志文件
     *
     * @return
     */
    public static String getPhotoPathLogPath() {
        return getLogPath(getProperty("LogDirectory"), getProperty("PhotoPathLog"));
    }

    /**
     * 获取 图片路径的Xml文件
     *
     * @return
     */
    public static String getPhotoPathXMLPath() {
        return getLogPath(getProperty("LogDirectory"), getProperty("PhotoPathXml"));
    }


    public static void writePhotoPath(Map<String, Path> photosPath) {
        writePhotoPathToFile(photosPath, getPhotoPathLogPath());
    }

    public static void writePhotoPathToFile(Map<String, Path> photosPath, String targetFile) {
        String xmlPath = getPhotoPathXMLPath();
        try (XMLEncoder xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(xmlPath)));
             BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile));
        ) {
            for (String photoName : photosPath.keySet()) {
                Path path = photosPath.get(photoName);
                writer.write(String.format("%s\t\t%s", photoName, path.toString()));
                writer.newLine();
                xmlEncoder.writeObject(path.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("DataCenter | writePhotoPathToFile : 找不到文件。目标文件：" + targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("DataCenter | writePhotoPathToFile : IO异常。目标文件：" + targetFile);
        }
        saveXMLPath("PhotoPath", xmlPath);
    }

    public static Map<String, Path> readPhotoPathByFile() {
        // 默认日志文件
        return readPhotoPathByFile(getPhotoPathLogPath());
    }

    public static Map<String, Path> readPhotoPathByFile(String targetFile) {
        Map<String, Path> pathMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(targetFile));) {
            String s = null;
            while ((s = reader.readLine()) != null) {
                //  不能是空数据
                if (s != "" && !s.matches("\\s")) {
                    String[] strings = s.split("\t\t");
                    pathMap.put(strings[0], Paths.get(strings[1]));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("DataCenter | readPhotoMsgByFile : 找不到目标文件：" + targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("DataCenter | readPhotoMsgByFile : IO异常。目标文件：" + targetFile);
        }
        return pathMap;
    }

    //#endregion
}
