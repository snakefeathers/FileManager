package com.snakefeather.filemanager.text;

import com.snakefeather.filemanager.file.FileOperation;
import com.snakefeather.filemanager.function.LineTextFind;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownOperation {

    /**
     * Markdown 图片链接正则
     */
    private String REGEX_PHOTOURL = ".*\\!\\[(?<remark>.*)\\]\\((?<filePath>.*[\\/]{1,2}(?<fileName>[^\\/.]+[.](png)|(jpg)))\\)";


    //#region  图片链接处理
    private static boolean isCodeChunk = true;
    //  图片链接筛选器  变量  一个函数
    private LineTextFind FUNCTION_PHOTOFIND = textLine -> {
        if (textLine.matches(".{3,3}.*")) {
            isCodeChunk = !isCodeChunk;
        }
        if (isCodeChunk) return false;  // 最原始的方法，跳过代码块。
        return Pattern.compile(REGEX_PHOTOURL).matcher(textLine).matches();
    };


    /**
     * 传入具体文件的路径，筛选出文件的图片链接，之后转为"文件名+原文本"的map。
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public Map<String, String> getPhotoUrlMap(String filePath) throws IOException {
        List<String> urlList = new ArrayList<>();
        // 筛选行语句  // 添加到list中。
        FileOperation.lineTextFindOperation(filePath, FUNCTION_PHOTOFIND, lineText -> {
            urlList.add(lineText);
            return "";
        }); //  获取具体文件的所有图片链接
        Map<String, String> urlMap = new HashMap<>();
        for (String s : urlList) {
            Matcher matcher = Pattern.compile(REGEX_PHOTOURL).matcher(s);
            if (matcher.matches()) {
                urlMap.put(matcher.group("fileName"), s);
            } else {
                throw new IOException("MD文件图片链接匹配失败。"); // 基本不会发生，毕竟取的时候就是正则取出来的。
            }
        }
        return urlMap;
    }

    /**
     * 传入一个文件夹，扫描所有MD文件，获取所有的URL地址。
     *
     * @param folder
     * @return
     * @throws IOException
     */
    public Map<String, String> getAllPhotoUrlMap(String folder) throws IOException {
        Map<String, String> urlMap = new HashMap<>();       // 存储所有URL链接
        Map<String, String> fileMap = FileOperation.getAllFile(folder);  // 获取所有.md文件
        Set<String> fileNameSet = fileMap.keySet(); // 所有文件文件名

        // 筛选.md文件 // 依次获取.md文件下的所有URL链接    // 合并URL集合
        fileNameSet.stream().filter(fileName -> fileName.matches("^[^ .].*(.md)$")).forEach(fileName -> {
            try {
                Map<String, String> photoUrlMap = getAllPhotoUrlMap(fileMap.get(fileName));  // 合并URL集合 // 输入.md文件的地址
                for (Map.Entry<String, String> entry : photoUrlMap.entrySet()) {
                    urlMap.put(entry.getKey(), fileMap.get(fileName) + " <SN> " + entry.getValue());
                    // <SN> 分隔符  前后有空格。文件结尾不可能为空格，文件夹命名不能有尖括号。
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return urlMap;
    }


    /**
     * 比对图片链接：传入MD文件读取到的图片链接和实际拥有的图片，返回实际缺少的图片。（寻找空缺图片的链接）
     * 单文件使用
     *
     * @return
     */
    public Map<String, String> lackFileByUrl(String filePath, String photoFolderPath) {
        Map<String, String> urlMap = null;  // md文件中获取到的图片链接
        Map<String, String> fileMap = null;
        try {
            urlMap = getPhotoUrlMap(filePath);
            fileMap = FileOperation.getAllFile(photoFolderPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, String> lackPhotoMap = new HashMap<>();
        for (String url : urlMap.keySet()) {
            if (!fileMap.keySet().contains(url)) {
                // 链接指定的图片不存在，无效图片
                lackPhotoMap.put(url, urlMap.get(url));
            }
        }
        return lackPhotoMap;
    }

    /**
     * 比对图片链接：传入MD文件读取到的图片链接和实际拥有的图片，返回实际多余的图片。（排除无效图片）
     * 文件夹中的多个文件使用。
     *
     * @return
     */
    public Map<String, String> surplusFileByUrl(String folderPath, String photoFolderPath) {
        Map<String, String> surplusFileMap = new HashMap<>();
        return surplusFileMap;
    }

    //#endregion
}
