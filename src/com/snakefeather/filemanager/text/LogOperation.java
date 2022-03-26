package com.snakefeather.filemanager.text;

import com.snakefeather.filemanager.file.FileOperation;

import javax.xml.crypto.Data;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Date;
import java.util.List;

public class LogOperation {

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
