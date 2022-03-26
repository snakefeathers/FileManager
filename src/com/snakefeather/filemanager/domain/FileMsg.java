package com.snakefeather.filemanager.domain;

public class FileMsg {

    //  文件名
    private String fileName;
    //  绝对地址
    private String absolutePath;
    //  相对地址
    private String relativePath;

    public FileMsg() {
    }

    public FileMsg(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }
}
