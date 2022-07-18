package com.snakefeather.filemanager.service.impl;

import com.snakefeather.filemanager.file.FileOperation;
import com.snakefeather.filemanager.service.FileService;
import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;


public class FileServiceImpl implements FileService {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    //    private static Logger logger = Logger.getLogger(FileServiceImpl.class);



    // 设置文件存储路径
    File folderPath = new File("D:\\test\\fileTest");
    // 重名的图片 因为是采用MD5计算的值作为文件名，可以一定程度去重。 不过还是有可能重复的，作为特殊数据，个人收藏一下。
    File folderPath_01 = new File("D:\\test\\notes");


    @Override
    public String save(MultipartFile file) {
        logger.debug("|FileServiceImpl：开始上传图片");
        //获取文件名
        String fileName = file.getOriginalFilename();
        //文件类型后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // SF:重命名文件
        String newFileName = null;
        // SF: 在指定目录下建立一个空文件
        File dest = null;
        try {
            // SF: MD5计算出唯一ID
            newFileName = FileOperation.md5HashCode32(file.getInputStream()) + suffix;
            dest = new File(folderPath, newFileName);
            if (dest.exists()) {
                dest = new File(folderPath_01, newFileName);
            }
            // SF: 创建该文件   将图片写入
            dest.createNewFile();
            file.transferTo(dest);
        } catch (IOException e) {
            logger.warn("|图片上传错误。" + dest.getAbsolutePath());
            e.printStackTrace();
            return null;
        }
        logger.debug("|FileServiceImpl：上传图片成功。\t" + "|图片上传路径：" + dest.getAbsolutePath());
        return newFileName;
    }
}
