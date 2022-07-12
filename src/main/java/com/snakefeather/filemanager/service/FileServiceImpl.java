package com.snakefeather.filemanager.service;

import com.snakefeather.filemanager.file.FileOperation;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FileServiceImpl implements FileService {


    // 设置文件存储路径
    File folderPath = new File("D:\\test\\notes");
    // 重名的图片 因为是采用MD5计算的值作为文件名，可以一定程度去重。 不过还是有可能重复的，作为特殊数据，个人收藏一下。
    File folderPath_01 = new File("D:\\test\\notes");


    @Override
    public String save(MultipartFile file) {
        //获取文件名
        String fileName = file.getOriginalFilename();
        //文件类型后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // SF: 在指定目录下建立一个空文件
        File dest = null;
        try {
            //重命名文件
            String newFileName = null;
            newFileName = FileOperation.md5HashCode32(file.getInputStream());
            dest = File.createTempFile(newFileName, suffix, folderPath);
        } catch (IOException e) {
            System.out.println("文件异常");
            return null;
        }

        //            success : 0 | 1,           // 0 表示上传失败，1 表示上传成功
        //            message : "提示的信息，上传成功或上传失败及错误信息等。",
        //            url     : "图片地址"        // 上传成功时才返回
        return null;
    }
}
