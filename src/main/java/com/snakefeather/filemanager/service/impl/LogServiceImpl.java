package com.snakefeather.filemanager.service.impl;

import com.snakefeather.filemanager.file.FileOperation;

import java.io.File;

/**
 *  建立日志文件的工具类
 */
public class LogServiceImpl {

    private final String PREFIX = "snakeFeather|";

    /**
     * 创建日志文件
     *
     * @param folderPath
     * @param fileName
     * @return
     */
    public File makeLog(String folderPath, String fileName) {
        File file = FileOperation.makeFile(folderPath, PREFIX + fileName);
        return file;
    }

    public File addLog(String folderPath, String fileName, Exception exception) {
        File logFile = makeLog(folderPath, fileName);
//        exception.printStackTrace();

        return logFile;
    }


}
