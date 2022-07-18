package com.snakefeather.filemanager.service;

import com.snakefeather.filemanager.domain.FileTextList;
import com.snakefeather.filemanager.domain.TextDiv;
import com.snakefeather.filemanager.domain.md.PhotoMsg;
import com.snakefeather.filemanager.domain.md.PhotoMsgs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface MarkdownService {


    /**
     * 文件里的图片链接
     * 找出MD文件中所有的图片名
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public Map<String, PhotoMsg> getPhotoMsgMap(String filePath) throws IOException;

    /**
     * 文件夹下的 所有文件 里的 图片链接
     * 传入一个文件夹，扫描所有MD文件，获取所有的URL地址。
     *
     * @param folderPath
     * @return
     * @throws IOException
     */
    public Map<String, PhotoMsg> getAllPhotoMsgMap(String folderPath) throws IOException;


    /**
     * 返回实际多余的图片
     * 比对图片链接：传入MD文件读取到的图片链接和实际拥有的图片，返回实际多余的图片。（排除无效图片）
     * 文件夹中的多个文件使用。
     *
     * @param folderPath      md文件的路径
     * @param photoFolderPath 图片的路径
     * @return 多余的图片的Map  Map<图片名，绝对路径>
     * @throws IOException
     */
    public Map<String, Path> surplusPhotos(String folderPath, String photoFolderPath) throws IOException;

    /**
     * 返回多余的图片链接   反过来就是  返回缺少的图片
     * 找出指定文件夹下所有md文件中，匹配无效的图片链接。 即，有链接，缺少图片。
     *
     * @param folderPath      md文件的文件夹
     * @param photoFolderPath 图片的文件夹
     * @return 多余的图片链接
     * @throws IOException
     */
    public Map<String, PhotoMsg> surplusPhotoMsg(String folderPath, String photoFolderPath) throws IOException;


    /**
     * 移出多余的图片
     * 去指定的文件，扫描MD文件与实际图片的匹配关系。
     * 筛选出无效的图片，并保留映射文件，将无效图片复制到指定目录。
     *
     * @param targetPath 目标路径
     * @param mirrorPath 镜像路径，会将无效图片与映射文件放在这个目录
     * @return
     */
    public boolean removeInvalidImages(String targetPath, String mirrorPath) throws IOException;

    /**
     *   修改 图片文件夹
     *   修改指定md文件中，所有图片链接
     * @param fileName
     * @param photoPath
     */
    public void updatePhotoPathByFile(String fileName, String photoPath);
}
