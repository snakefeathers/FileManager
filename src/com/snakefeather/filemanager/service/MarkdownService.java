package com.snakefeather.filemanager.service;

import java.io.IOException;
import java.util.Map;

public interface MarkdownService {


    /**
     * 找出MD文件中所有的图片名
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public Map<String, String> getPhotoUrlMap(String filePath) throws IOException;


    /**
     * 传入一个文件夹，扫描所有MD文件，获取所有的URL地址。
     *
     * @param folderPath
     * @return
     * @throws IOException
     */
    public Map<String, String> getAllPhotoUrlMap(String folderPath) throws IOException;

    /**
     * 比对图片链接：传入MD文件读取到的图片链接和实际拥有的图片，返回实际多余的图片。（排除无效图片）
     * 文件夹中的多个文件使用。
     *
     * @param folderPath      md文件的路径
     * @param photoFolderPath 图片的路径
     * @return 多余的图片的Map  Map<图片名，绝对路径>
     * @throws IOException
     */
    public Map<String, String> surplusFileByUrl(String folderPath, String photoFolderPath) throws IOException;


    /**
     * 比对图片链接：传入MD文件读取到的图片链接和实际拥有的图片，返回实际缺少的图片。（寻找空缺图片的链接）
     * 单文件使用
     *
     * @return
     */
    public Map<String, String> lackUrlByFile(String filePath, String photoFolderPath) throws IOException;

    /**
     *      找出指定文件夹下所有md文件中，匹配无效的图片链接。即，有链接，缺少图片。
     * @param folderPath    知道的文件夹
     * @param photoFolderPath   图片的文件夹
     * @return  多余的图片链接
     * @throws IOException
     */
    public Map<String, String> lackAllUrlByFolder(String folderPath, String photoFolderPath) throws IOException;


    /**
     * 去指定的文件，扫描MD文件与实际图片的匹配关系。
     * 筛选出无效的图片，并保留映射文件，将无效图片复制到指定目录。
     *
     * @param targetPath 目标路径
     * @param mirrorPath 镜像路径，会将无效图片与映射文件放在这个目录
     * @return
     */
    public boolean removeInvalidImages(String targetPath, String mirrorPath) throws IOException;
}
