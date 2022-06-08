package com.snakefeather.filemanager.service.impl;

import com.snakefeather.filemanager.file.FileOperation;
import com.snakefeather.filemanager.file.FolderOperation;
import com.snakefeather.filemanager.regex.RegexStore;
import com.snakefeather.filemanager.service.MarkdownService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 封存Markdown操作的工具类
 */
public class MarkdownServiceImpl implements MarkdownService {


    //  变量   代码块flog值   getPhotoUrlMap()中lambda表达式使用，用于跳过代码块
    private boolean isCodeChunk = false;

    /**
     * 获取到指定文件夹下所有的MD文件
     *
     * @param folderPath
     * @return
     */
    private Map<String, String> getAllMd(String folderPath) {
        Map<String, String> fileMap = null;                 //  存储所有的文件 Map<文件名，绝对地址>
        try {
            //  获取到所有的md文件
            fileMap = FolderOperation.getAllFileBySuffix(folderPath, ".*\\.md");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileMap;
    }

    //#region   获取图片链接
    @Override
    public Map<String, String> getPhotoUrlMap(String filePath) throws IOException {
        List<String> urlList = new ArrayList<>();

        FileOperation.lineTextFindOperation(filePath,
                textLine -> {
                    if (textLine.matches("`{3,3}.*")) {
                        isCodeChunk = !isCodeChunk;
                    }
                    if (isCodeChunk) return false;  // 最原始的方法，跳过代码块。
                    return Pattern.compile(".*" + RegexStore.PHOTO_URL + ".*").matcher(textLine).matches();
                }, lineText -> {
                    urlList.add(lineText);
                    return "";
                }
        ); //  获取具体文件的所有图片链接

        Map<String, String> urlMap = new HashMap<>();
        for (String s : urlList) {
            int start = 0;
            Matcher matcher = Pattern.compile(RegexStore.PHOTO_URL).matcher(s);
            while (matcher.find(start)) {
                urlMap.put(matcher.group("fileName"), s);   // Map<图片名，URL原文>
                //  处理一行内多个符合条件的情况。
                start = matcher.end();
            }
        }
        isCodeChunk = false;
        return urlMap;
    }

    @Override
    public Map<String, String> getAllPhotoUrlMap(String folderPath) throws IOException {
        Map<String, String> fileMap = getAllMd(folderPath);  //  所有的文件 Map<文件名，绝对地址>
        Map<String, String> urlMap = new HashMap<>();       // 所有URL链接 Map<图片名，URL原文>
        //  遍历所有md文件，获取到所有的PhotoUrl链接。
        for (String fileName : fileMap.keySet()) {
            Map<String, String> photoUrlMap = this.getPhotoUrlMap(fileMap.get(fileName));
            urlMap.putAll(photoUrlMap);
        }
        return urlMap;
    }

    public Map<String,String> getPhotoHtmlMap(String filePath) throws IOException {
        List<String> urlList = new ArrayList<>();

        FileOperation.lineTextFindOperation(filePath,
                textLine -> {
                    if (textLine.matches("`{3,3}.*")) {
                        isCodeChunk = !isCodeChunk;
                    }
                    if (isCodeChunk) return false;  // 最原始的方法，跳过代码块。
                    return Pattern.compile(".*" + RegexStore.PHOTO_HTML + ".*").matcher(textLine).matches();
                }, lineText -> {
                    urlList.add(lineText);
                    return "";
                }
        ); //  获取具体文件的所有图片链接

        Map<String, String> urlMap = new HashMap<>();
        for (String s : urlList) {
            int start = 0;
            Matcher matcher = Pattern.compile(RegexStore.PHOTO_URL).matcher(s);
            while (matcher.find(start)) {
                urlMap.put(matcher.group("fileName"), s);   // Map<图片名，URL原文>
                //  处理一行内多个符合条件的情况。
                start = matcher.end();
            }
        }
        isCodeChunk = false;
        return urlMap;
    }
    //#endregion

    //#region  找出 无效的图片   无效的链接
    @Override
    public Map<String, String> surplusFileByUrl(String folderPath, String photoFolderPath) throws IOException {
        //   surplus  ： 冗余

        Map<String, String> surplusFileMap = new HashMap<>();
        // 冗余的图片隐射  Map<图片名，绝对路径>
        Set<String> photoSet = getAllPhotoUrlMap(folderPath).keySet();
        // 所有Md文件中扫出来的，需要的图片。
        Map<String, String> photoMap = FolderOperation.getAllFileBySuffix(photoFolderPath, RegexStore.PHOTO);
        //  实际存在的图片 Map<图片名，绝对地址>

        for (String photoName : photoMap.keySet()) {
            if (!photoSet.contains(photoName)) {
                // 不存在的图片  //  记录
                surplusFileMap.put(photoName, photoMap.get(photoName));
            }
        }
        return surplusFileMap;
    }

    @Override
    public Map<String, String> lackUrlByFile(String filePath, String photoFolderPath) throws IOException {
        // lack  缺乏
        Map<String, String> urlMap = getPhotoUrlMap(filePath);  // md文件中获取到的图片链接
        Set<String> fileSet = FolderOperation.getAllFileBySuffix(photoFolderPath, RegexStore.PHOTO).keySet();

        Map<String,String> lackUrlMap = new HashMap<>();
        for (String photoName : urlMap.keySet()) {
            if (!fileSet.contains(photoName)) {
                lackUrlMap.put(photoName,urlMap.get(photoName));
            }
        }
        return lackUrlMap;
    }

    @Override
    public Map<String, String> lackAllUrlByFolder(String folderPath, String photoFolderPath) throws IOException {
        // lack  缺乏
        Map<String, String> urlMap = getAllPhotoUrlMap(folderPath);  // md文件中获取到的图片链接
        Set<String> fileSet = FolderOperation.getAllFileBySuffix(photoFolderPath, RegexStore.PHOTO).keySet();

        Map<String,String> lackUrlMap = new HashMap<>();
        for (String photoName : urlMap.keySet()) {
            if (!fileSet.contains(photoName)) {
                lackUrlMap.put(photoName,urlMap.get(photoName));
            }
        }
        return lackUrlMap;
    }

    //#endregion


    @Override
    public boolean removeInvalidImages(String targetPath, String mirrorPath) throws IOException {
        Map<String,String> surplusPhotoMap = surplusFileByUrl(targetPath,targetPath);
        for (String photoPath : surplusPhotoMap.values()){
            FileOperation.copyFile(photoPath,mirrorPath);
            System.out.println("移除文件："+ photoPath);
            File file =new File(photoPath);
            file.delete();
        }
        return true;
    }


}
