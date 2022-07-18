package com.snakefeather.filemanager.service.impl;

import com.snakefeather.filemanager.domain.FileTextList;
import com.snakefeather.filemanager.domain.TextDiv;
import com.snakefeather.filemanager.domain.md.MdTextImg;
import com.snakefeather.filemanager.domain.md.PhotoMsg;
import com.snakefeather.filemanager.domain.md.PhotoMsgs;
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
import java.util.stream.Collectors;

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

    //#region   文件预处理
    //      对目的md文件进行预处理，方便操作。    //  预处理只是简单的处理、

    /**
     * 对指定文件下的 图片链接进行处理
     *
     * @param filePath 指定md文件
     * @throws IOException
     */
    public void disposePhotoUrl(String filePath) throws IOException {
        FileOperation.lineTextUpdateOperation(filePath, textLine -> {
                    return Pattern.compile(".*" + RegexStore.EASY_PHOTO_IMG + ".*").matcher(textLine).matches();
                },
                textLine ->
                {
                    //  对一行中有多个图片链接的，进行分割处理。
//                    Matcher matcher = Pattern.compile(".*" + RegexStore.PHOTO_URL_MORE + ".*").matcher(textLine);
                    Matcher matcher = Pattern.compile(".*" + RegexStore.MORE_PHOTO_IMG + ".*").matcher(textLine);
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
    public Map<String, PhotoMsg> getPhotoMsgMap(String filePath) throws IOException {
        disposePhotoUrl(filePath);
        List<PhotoMsg> photoList = new ArrayList<>();
        Path path = Paths.get(filePath);
        final LineNumber number = new LineNumber();
        number.lineNumber = 0;

        FileOperation.lineTextFindOperation(filePath,
                textLine -> {
                    ++number.lineNumber;
                    if (textLine.matches("`{3,3}.*")) {
                        isCodeChunk = !isCodeChunk;
                        return false;   // 最原始的方法，跳过代码块。
                    }
                    // 粗筛
                    return PhotoMsgs.isPhotoMsg(textLine);
                }, lineText -> {
                    TextDiv textDiv = new TextDiv(path, number.lineNumber, lineText);
                    PhotoMsg photoMsg = PhotoMsgs.tryGetPhotoMsg(lineText);
                    if (photoMsg != null){
                        photoMsg.decorate(textDiv);
                        photoList.add(photoMsg);
                    }
                    return "";
                }
        ); //  获取具体文件的所有图片链接

        Map<String, PhotoMsg> urlMap = new HashMap<>();
        for (PhotoMsg msg : photoList) {
            urlMap.put(msg.getPhotoName(), msg);
        }
        isCodeChunk = false;
        return urlMap;
    }

    @Override
    public Map<String, PhotoMsg> getAllPhotoMsgMap(String folderPath) throws IOException {
        disposeAllPhotoUrl(folderPath);
        Map<String, Path> fileMap = getAllMd(folderPath);  //  所有的文件 Map<文件名，绝对地址>
        Map<String, PhotoMsg> urlMap = new HashMap<>();       // 所有URL链接 Map<图片名，URL信息>
        //  遍历所有md文件，获取到所有的PhotoUrl链接。
        for (String fileName : fileMap.keySet()) {
//            System.out.println("getAllPhotoMsgMap：从文件中获取图片链接。文件：" + fileName);
            Map<String, PhotoMsg> photoUrlMap = this.getPhotoMsgMap(fileMap.get(fileName).toString());
            urlMap.putAll(photoUrlMap);
        }
        return urlMap;
    }

    //#endregion

    //#region  找出 多余的图片  多余的链接（缺少的图片）
    @Override
    public Map<String, Path> surplusPhotos(String folderPath, String photoFolderPath) throws IOException {
        //   surplus  ： 冗余

        // 冗余的图片隐射  Map<图片名，绝对路径>
        Map<String, Path> surplusFileMap = new HashMap<>();
        // 所有Md文件中扫出来的，需要的图片。
        Set<String> photoSet = getAllPhotoMsgMap(folderPath).keySet();
        //  实际存在的图片 Map<图片名，绝对地址>
        Map<String, Path> photoMap = FolderOperation.getAllFileBySuffix(photoFolderPath, RegexStore.PHOTO);

        for (String photoName : photoMap.keySet()) {
            if (!photoSet.contains(photoName)) {
                // 多余的图片  //  记录
                surplusFileMap.put(photoName, photoMap.get(photoName));
            }
        }
        return surplusFileMap;
    }

    @Override
    public Map<String, PhotoMsg> surplusPhotoMsg(String folderPath, String photoFolderPath) throws IOException {
        //   与找出多余的图片类似   surplusPhotos()   都是找到 图片链接Map， 实际图片Map。  两个对比，看看差那个。
        Map<String, PhotoMsg> urlMap = getAllPhotoMsgMap(folderPath);  // md文件中获取到的图片链接
        Set<String> fileSet = FolderOperation.getAllFileBySuffix(photoFolderPath, RegexStore.PHOTO).keySet();

        Map<String, PhotoMsg> lackUrlMap = new HashMap<>();
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
        Map<String, Path> surplusPhotoMap = surplusPhotos(targetPath, targetPath);
        for (Path photoPath : surplusPhotoMap.values()) {
            FileOperation.copyFile(photoPath.toString(), mirrorPath);
            System.out.println("移除文件：" + photoPath);
            File file = photoPath.toFile();
            file.delete();
        }
        return true;
    }

    @Override
    public void updatePhotoPathByFile(String fileName, String photoPath) {
        FileTextList fileTextList = new FileTextList(fileName);
        fileTextList.read();
        List<TextDiv> textDivs = fileTextList.stream().filter(textDiv -> {
            return textDiv.getTextType() == TextDiv.MsgTypeEnum.IMG || textDiv.getTextType() == TextDiv.MsgTypeEnum.LABEL_PHOTO;
        }).collect(Collectors.toList());
        textDivs.sort(new Comparator<TextDiv>() {
            @Override
            public int compare(TextDiv o1, TextDiv o2) {
                long l = o1.getLineCount() - o2.getLineCount();
                return l == 0 ? 0 : l > 0 ? 1 : -1;
            }
        });
        for (TextDiv textDiv : textDivs) {
            int ind = fileTextList.indexOf(textDiv);
            PhotoMsg photoMsg = (PhotoMsg) textDiv;
            PhotoMsgs.updateFolderPath(photoMsg, photoPath);
            fileTextList.set(ind,textDiv);
        }
        fileTextList.write();
    }



    //  行号 lambda表达式不能传递变量。   使用常量对象，传递一下。
    private class LineNumber {
        long lineNumber;
    }

}
