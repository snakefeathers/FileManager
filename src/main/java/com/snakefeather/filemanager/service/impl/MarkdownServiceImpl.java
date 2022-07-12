package com.snakefeather.filemanager.service.impl;

import com.snakefeather.filemanager.domain.TextDiv;
import com.snakefeather.filemanager.domain.md.MdTextImgUrl;
import com.snakefeather.filemanager.file.FileOperation;
import com.snakefeather.filemanager.file.FolderOperation;
import com.snakefeather.filemanager.regex.RegexStore;
import com.snakefeather.filemanager.service.MarkdownService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private Map<String, Path> getAllMd(String folderPath) {
        Map<String, Path> fileMap = null;                 //  存储所有的文件 Map<文件名，绝对地址>
        try {
            //  获取到所有的md文件
            fileMap = FolderOperation.getAllFileBySuffix(folderPath, ".*\\.md");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileMap;
    }

    //#region   对目的md文件进行预处理，方便操作。    //  预处理只是简单的处理、

    /**
     * 对指定文件下的 图片链接进行处理
     *
     * @param filePath 指定md文件
     * @throws IOException
     */
    public void disposePhotoUrl(String filePath) throws IOException {
        FileOperation.lineTextUpdateOperation(filePath, textLine -> {
                    return Pattern.compile(".*" + RegexStore.PHOTO_URL_EASY + ".*").matcher(textLine).matches();
                },
                textLine ->
                {
                    //  对一行中有多个图片链接的，进行分割处理。
                    Matcher matcher = Pattern.compile(".*" + RegexStore.PHOTO_URL_MORE + ".*").matcher(textLine);
                    if (matcher.matches()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        String[] strs = textLine.split("\\!\\[");
                        for (String str : strs) {
                            if (str.length() > 0) {
                                stringBuilder.append("![");
                                stringBuilder.append(str + "\n");
                            }
                        }
                        return stringBuilder.toString();
                    } else {
                        return textLine;
                    }
                });
    }

    /**
     * 对指定文件夹下的所有文件的  图片链接进行处理
     *
     * @param folderPath 文件夹
     * @throws IOException
     */
    public void disposeAllPhotoUrl(String folderPath) throws IOException {
        Map<String, Path> fileMap = getAllMd(folderPath);
        for (Path filePath : fileMap.values()) {
            disposePhotoUrl(filePath.toString());
        }
    }

    //#endregion

    //#region   获取图片链接
    @Override
    public Map<String, TextDiv> getPhotoUrlMap(String filePath) throws IOException {
        disposePhotoUrl(filePath);
        List<TextDiv> urlList = new ArrayList<>();
        final LineNumber number = new LineNumber();
        number.lineNumber = 0;

        FileOperation.lineTextFindOperation(filePath,
                textLine -> {
                    ++number.lineNumber;
                    if (textLine.matches("`{3,3}.*")) {
                        isCodeChunk = !isCodeChunk;
                    }
                    if (isCodeChunk) return false;  // 最原始的方法，跳过代码块。
                    //   先用最简单的，筛选快一点。      快筛。
                    return Pattern.compile(".*" + RegexStore.PHOTO_URL_EASY + ".*").matcher(textLine).matches();
                }, lineText -> {
                    if (lineText.matches(".*" + RegexStore.PHOTO_URL + ".*")){     //  二次筛选
                        urlList.add(new MdTextImgUrl(Paths.get(filePath), number.lineNumber, lineText));
                    }
                    return "";
                }
        ); //  获取具体文件的所有图片链接

        Map<String, TextDiv> urlMap = new HashMap<>();
        for (TextDiv msg : urlList) {
            urlMap.put(msg.getPrimaryText(), msg);
        }
        isCodeChunk = false;
        return urlMap;
    }

    @Override
    public Map<String, TextDiv> getAllPhotoUrlMap(String folderPath) throws IOException {
        disposeAllPhotoUrl(folderPath);
        Map<String, Path> fileMap = getAllMd(folderPath);  //  所有的文件 Map<文件名，绝对地址>
        Map<String, TextDiv> urlMap = new HashMap<>();       // 所有URL链接 Map<图片名，URL信息>
        //  遍历所有md文件，获取到所有的PhotoUrl链接。
        for (String fileName : fileMap.keySet()) {
            System.out.println("文件" + fileName);
            Map<String, TextDiv> photoUrlMap = this.getPhotoUrlMap(fileMap.get(fileName).toString());
            urlMap.putAll(photoUrlMap);
        }
        return urlMap;
    }

    public Map<String, MdTextImgUrl> getPhotoHtmlMap(String filePath) throws IOException {

        disposePhotoUrl(filePath);
        List<MdTextImgUrl> urlList = new ArrayList<>();
        final LineNumber number = new LineNumber();
        number.lineNumber = 0;

        FileOperation.lineTextFindOperation(filePath,
                textLine -> {
                    ++number.lineNumber;
                    if (textLine.matches("`{3,3}.*")) {
                        isCodeChunk = !isCodeChunk;
                    }
                    if (isCodeChunk) return false;  // 最原始的方法，跳过代码块。
                    //   先用最简单的，筛选快一点。      快筛。
                    return Pattern.compile(".*" + RegexStore.PHOTO_HTML + ".*").matcher(textLine).matches();
                }, lineText -> {
                    urlList.add(new MdTextImgUrl(Paths.get(filePath), number.lineNumber,  lineText));
                    return "";
                }
        ); //  获取具体文件的所有图片链接

        Map<String, MdTextImgUrl> urlMap = new HashMap<>();
        for (MdTextImgUrl msg : urlList) {
            urlMap.put(msg.getPrimaryText(), msg);
        }
        isCodeChunk = false;
        return urlMap;
    }
    //#endregion

    //#region  找出 无效的图片   无效的链接
    @Override
    public Map<String, Path> surplusFileByUrl(String folderPath, String photoFolderPath) throws IOException {
        //   surplus  ： 冗余

        // 冗余的图片隐射  Map<图片名，绝对路径>
        Map<String, Path> surplusFileMap = new HashMap<>();
        // 所有Md文件中扫出来的，需要的图片。
        Set<String> photoSet = getAllPhotoUrlMap(folderPath).keySet();
        //  实际存在的图片 Map<图片名，绝对地址>
        Map<String, Path> photoMap = FolderOperation.getAllFileBySuffix(photoFolderPath, RegexStore.PHOTO);

        for (String photoName : photoMap.keySet()) {
            if (!photoSet.contains(photoName)) {
                // 不存在的图片  //  记录
                surplusFileMap.put(photoName, photoMap.get(photoName));
            }
        }
        return surplusFileMap;
    }

    @Override
    public Map<String, TextDiv> lackUrlByFile(String filePath, String photoFolderPath) throws IOException {
        // lack  缺乏
        Map<String, TextDiv> urlMap = getPhotoUrlMap(filePath);  // md文件中获取到的图片链接
        Set<String> fileSet = FolderOperation.getAllFileBySuffix(photoFolderPath, RegexStore.PHOTO).keySet();

        Map<String, TextDiv> lackUrlMap = new HashMap<>();
        for (String photoName : urlMap.keySet()) {
            if (!fileSet.contains(photoName)) {
                lackUrlMap.put(photoName, urlMap.get(photoName));
            }
        }
        return lackUrlMap;
    }

    @Override
    public Map<String, TextDiv> lackAllUrlByFolder(String folderPath, String photoFolderPath) throws IOException {
        // lack  缺乏
        Map<String, TextDiv> urlMap = getAllPhotoUrlMap(folderPath);  // md文件中获取到的图片链接
        Set<String> fileSet = FolderOperation.getAllFileBySuffix(photoFolderPath, RegexStore.PHOTO).keySet();

        Map<String, TextDiv> lackUrlMap = new HashMap<>();
        for (String photoName : urlMap.keySet()) {
            if (!fileSet.contains(photoName)) {
                lackUrlMap.put(photoName, urlMap.get(photoName));
            }
        }
        return lackUrlMap;
    }

    //#endregion


    @Override
    public boolean removeInvalidImages(String targetPath, String mirrorPath) throws IOException {
        Map<String, Path> surplusPhotoMap = surplusFileByUrl(targetPath, targetPath);
        for (Path photoPath : surplusPhotoMap.values()) {
            FileOperation.copyFile(photoPath.toString(), mirrorPath);
            System.out.println("移除文件：" + photoPath);
            File file = photoPath.toFile();
            file.delete();
        }
        return true;
    }


    private class LineNumber {
        long lineNumber;
    }

}
