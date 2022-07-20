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
     *  读取文件夹 获取到所有的 md文件的映射
     * @param folderPath
     * @return
     */
    Map<String, Path> getAllMd(String folderPath);

    /**
     * 获取文件里的图片链接 （只读操作）
     * 找出MD文件中所有的图片名 （只适合读，不适合修改）
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    Map<String, PhotoMsg> getPhotoMsgMap(String filePath) throws IOException;

    /**
     * 获取文件夹下的 所有文件 里的 图片链接 （只读操作）
     * 传入一个文件夹，扫描所有MD文件，获取所有的URL地址。
     *
     * @param folderPath
     * @return
     * @throws IOException
     */
    Map<String, PhotoMsg> getAllPhotoMsgMap(String folderPath) throws IOException;

    /**
     *   获取文件里的图片链接  （读写操作）
     *   根据自定义的File类型，获取其中所有的 PhotoMsg对象
     * @param fileLists
     * @return
     */
    Map<String,PhotoMsg> getAllPhotoMsgMspRW(List<FileTextList> fileLists);


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
    Map<String, Path> surplusPhotos(String folderPath, String photoFolderPath) throws IOException;

    /**
     * 返回多余的图片链接   反过来就是  返回缺少的图片
     * 找出指定文件夹下所有md文件中，匹配无效的图片链接。 即，有链接，缺少图片。
     *
     * @param folderPath      md文件的文件夹
     * @param photoFolderPath 图片的文件夹
     * @return 多余的图片链接
     * @throws IOException
     */
    Map<String, PhotoMsg> surplusPhotoMsg(String folderPath, String photoFolderPath) throws IOException;


    /**
     * 移出多余的图片
     * 去指定的文件，扫描MD文件与实际图片的匹配关系。
     * 筛选出无效的图片，并保留映射文件，将无效图片复制到指定目录。
     *
     * @param targetPath 目标路径
     * @param mirrorPath 镜像路径，会将无效图片与映射文件放在这个目录
     * @return
     */
    boolean removeInvalidImages(String targetPath, String mirrorPath) throws IOException;

    /**
     * 修改 所有图片链接的 图片路径 （批量操作）
     * 修改指定md文件中  所有图片链接中的 图片路径
     *
     * @param fileName
     * @param photoPath
     */
    void updatePhotoPathByFile(String fileName, String photoPath);

    /**
     * 补充图片
     * @param folder        指定md文件目录
     * @param targetPath    图片目标路径
     * @param photoPaths    图片来源路径
     * @return
     */
    void replenishPhoto(String folder,String targetPath,String... photoPaths);
}
