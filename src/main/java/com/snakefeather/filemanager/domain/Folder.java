package com.snakefeather.filemanager.domain;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Folder {
    // 当前文件夹的路径
    private String folderStr;
    private Path folderPath;
    // 当前文件夹下的 子文件夹
    private List<Folder> childFolder = new LinkedList<>();
    // 当前文件夹下的 子文件
    private List<FileTextList> childFile = new LinkedList<>();
    //  读取到的所有文件
    private static Map<Integer, FileTextList> fileMap = new HashMap();

    public Folder(Path path) {
        folderPath = path;
        folderStr = path.toString();
        loader(path);
    }

    public Folder(File file) {
        this(file.getAbsolutePath());
    }

    public Folder(String path) {
        folderPath = Paths.get(path);
        folderStr = path;
        loader(this.folderPath);
    }

    /**
     * 加载文件路径
     *
     * @param path 路径
     */
    public void loader(Path path) {
        for (File file : path.toFile().listFiles()) {
            if (file.isDirectory()) {
                childFolder.add(new Folder(file));
            } else {
                FileTextList fileTextList = new FileTextList(file);
                childFile.add(fileTextList);
                fileMap.put(fileTextList.hashCode(), fileTextList);
            }
        }
    }

    public String getFolderStr() {
        return folderStr;
    }

    public void setFolderStr(String folderStr) {
        this.folderStr = folderStr;
    }

    public Path getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(Path folderPath) {
        this.folderPath = folderPath;
    }

    public List<Folder> getChildFolder() {
        return childFolder;
    }

    public void setChildFolder(List<Folder> childFolder) {
        this.childFolder = childFolder;
    }

    public List<FileTextList> getChildFile() {
        return childFile;
    }

    public void setChildFile(List<FileTextList> childFile) {
        this.childFile = childFile;
    }
}
