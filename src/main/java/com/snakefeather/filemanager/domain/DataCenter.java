package com.snakefeather.filemanager.domain;

import com.snakefeather.filemanager.file.PropertiesOperation;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 唯一的类。
 * 用于分配，管理数据。
 */
public class DataCenter {

    //  文件字典   //  主要指文本文件
    public static Map<String, FileTextList> fileMap = null;
    //   配置文件   // 包含一些 常修改调整的常量
    public static Properties properties = null;


    /**
     * 获取文件字典
     *
     * @return
     */
    public static Map<String, FileTextList> getFileMap() {
        if (null == fileMap) {
            fileMap = new HashMap<>();
            String[] folders = getProperty("folderPath").split(";");
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


}
