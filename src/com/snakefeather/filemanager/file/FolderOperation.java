package com.snakefeather.filemanager.file;

import com.snakefeather.filemanager.regex.FileRegex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FolderOperation {


    // 搜索到的所有 子文件夹 List<文件夹绝对路径>
    private static List<String> folderAllList = new LinkedList<>();

    // 搜索到的所有 子文件  Map<文件名,文件绝对路径>
    private static Map<String, String> fileAllMap = new HashMap<>();


    //#region 文件搜索


    /**
     * 传入一个文件夹路径  遍历，递归获取所有的子文件夹  List<子路径>
     *
     * @param folderPath
     * @return
     * @throws FileNotFoundException
     */
    public static List<String> getAllFolder(String folderPath) throws FileNotFoundException {
        File file = new File(folderPath);
        if (file.isFile()) return new LinkedList<>();        // 是文件，直接返回
        String[] fileList = file.list(new FilenameFilter() {
            // 筛选出符合条件的文件夹
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
                    //  递归子文件夹  // 注：List是static类型
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
        // 添加这个文件夹本身
        folderAllList.add(path);
        if (null != folderAllList && folderAllList.size() > 0) {
            for (String folderPath : folderAllList) {
                //  遍历文件夹列表
                File folder = new File(folderPath);
                String[] fileList = folder.list(new FilenameFilter() {
                    // 筛选出符合条件的文件
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
                            File f = new File(folder.getAbsolutePath() + File.separator + file);
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

}
