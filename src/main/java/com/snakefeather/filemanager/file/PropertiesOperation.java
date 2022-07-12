package com.snakefeather.filemanager.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *   Properties  继承于Hashtable  应该带有存储功能，暂且直接作为字典来使用。
 *
 */
public class PropertiesOperation {




    /**
     * 从项目资源路径中获取配置文件
     *
     * @param propertiesPath
     * @return
     */
    public Properties getPropertiesByRes(String propertiesPath) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            // 从项目资源路径中  获得配置文件流
            inputStream = this.getClass().getClassLoader().getResourceAsStream(propertiesPath);
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("配置文件路径无效");
            e.printStackTrace();
        }
        return properties;
    }

    /**
     *   从相对（相对于项目）或绝对路径中获取配置文件
     * @param propertiesPath
     * @return
     */
    public Properties getPropertiesByAbso(String propertiesPath) {
        Properties properties = new Properties();
        try {
            // 从指定路径获取配置文件
            properties.load(new FileInputStream(propertiesPath));
        } catch (IOException e) {
            System.out.println("配置文件路径无效"+propertiesPath);
            e.printStackTrace();
        }
        return properties;
    }


    public Properties newPropertiesByRes(String propertiesPath){
        return null;
    }
}
