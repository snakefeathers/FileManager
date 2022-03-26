package com.snakefeather.filemanager.text;

import com.snakefeather.filemanager.file.FileOperation;
import com.snakefeather.filemanager.function.LineTextFind;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownOperation {

    /**
     * Markdown 图片链接正则
     */
    private String REGEX_PHOTOURL = ".*\\!\\[(?<remark>.*)\\]\\((?<filePath>.*[\\/]{1,2}(?<fileName>[^\\/.]+[.](png)|(jpg)))\\)";

    private static boolean isCodeChunk = true;
    // 筛选出图片链接
    private LineTextFind PHOTOFIND = textLine -> {
        if (textLine.matches(".{3,3}.*")){
            isCodeChunk = !isCodeChunk;
        }
        if (isCodeChunk)return false;
        return Pattern.compile(REGEX_PHOTOURL).matcher(textLine).matches();
    };


    /**
     * 传入具体文件的路径，根据正则表达式，筛选出图片链接。
     *
     * @param filePath 文件正则
     * @return 符合条件的文本列表
     * @throws IOException
     */
    public List<String> getPhotoUrlList(String filePath) throws IOException {
        List<String> list = new ArrayList<>();
        // 筛选行语句  // 添加到list中。
        FileOperation.lineTextFindOperation(filePath, PHOTOFIND, lineText -> {
            list.add(lineText);
            return "";
        });
        return list;
    }

    /**
     * 传入具体文件的路径，筛选出文件的图片链接，之后转为"文件名+原文本"的map。
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public Map<String, String> getPhotoUrlMap(String filePath) throws IOException {
        List<String> urlList = getPhotoUrlList(filePath);
        Map<String, String> urlMap = new HashMap<>();
        for (String s : urlList) {
            Matcher matcher = Pattern.compile(REGEX_PHOTOURL).matcher(s);
            if (matcher.matches()){
                urlMap.put(matcher.group("fileName"), s);
            }
        }
        return urlMap;
    }

    /**
     * 传入MD文件读取到的图片链接和实际拥有的图片，返回实际缺少的图片。（寻找空缺图片的链接）
     *
     * @param urlSet  md文件中获取到的图片链接
     * @param fileSet 实际图片的链接
     * @return
     */
    public Set<String> lackFileByUrl(Set<String> urlSet, Set<String> fileSet) {
        Set<String> needFileSet = new HashSet<>();
        for (String url : urlSet) {
            if (!fileSet.contains(url)) {
                needFileSet.add(url);
//                System.out.println("缺少：" + url);
            } else {
//                System.out.println("含有：" + url);
            }
        }
        return needFileSet;
    }

    /**
     * 传入MD文件读取到的图片链接和实际拥有的图片，返回实际多余的图片。（排除无效图片）
     *
     * @param urlSet  md文件中获取到的图片链接
     * @param fileSet 实际图片的链接
     * @return
     */
    public Set<String> surplusFileByUrl(Set<String> urlSet, Set<String> fileSet) {
        Set<String> surplusFileSet = new HashSet<>();
        for (String url : fileSet) {
            if (!urlSet.contains(url)) {
                surplusFileSet.add(url);
//                System.out.println("多余：" + url);
            } else {
//                System.out.println("拥有：" + url);
            }
        }
        return surplusFileSet;
    }

    @Test
    public void testc() throws IOException {
        String folderPath = "D:\\test\\noteTest";
        String filePath = "D:\\test\\noteTest\\041java.md";
        MarkdownOperation mdOpera = new MarkdownOperation();
        Map<String, String> urlMap = mdOpera.getPhotoUrlMap(filePath);
        Map<String, String> fileMap = FileOperation.getAllFile(folderPath);
        Set<String> lackSet = mdOpera.lackFileByUrl(urlMap.keySet(), fileMap.keySet());     // 缺少图片的图片链接    // 无效的链接
        Set<String> surplus = mdOpera.surplusFileByUrl(urlMap.keySet(), fileMap.keySet());  // 没有图片链接的图片 // 多余的图片
        System.out.println("总共链接：" + urlMap.keySet().size());
        System.out.println("总共图片：" + fileMap.keySet().size());
        System.out.println("缺少的图片：" + lackSet.size());
        System.out.println("多余的图片：" + surplus.size());
        System.out.println("缺少的图片：");
        lackSet.stream().forEach(System.out::println);
        System.out.println("多余的图片：");
        surplus.stream().forEach(System.out::println);
    }


}
