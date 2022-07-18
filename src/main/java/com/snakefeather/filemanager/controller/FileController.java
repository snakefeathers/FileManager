package com.snakefeather.filemanager.controller;


import com.snakefeather.filemanager.service.FileService;
import com.snakefeather.filemanager.service.impl.FileServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/file")
public class FileController {

    private FileService fileService = new FileServiceImpl();

    private Logger logger = Logger.getLogger(this.getClass().getName());
//    private static Logger logger = Logger.getLogger(FileController.class);

    @PostMapping("/photo/save")
    @ResponseBody
    public Map imageUpload(HttpServletRequest request, @RequestParam(value = "editormd-image-file", required = false) MultipartFile file) {
        logger.debug("|FileController：上传图片。");
        String path = request.getSession().getServletContext().getRealPath("/photos");
        String photoName = fileService.save(file);

        //            success : 0 | 1,           // 0 表示上传失败，1 表示上传成功
        //            message : "提示的信息，上传成功或上传失败及错误信息等。",
        //            url     : "图片地址"        // 上传成功时才返回
        Map<String, Object> json = new HashMap<>();
        if (null != photoName) {
            json.put("success", 1);
            json.put("message", "上传成功");
//            json.put("url", "http://localhost:8080/FileManager/photos/2635c469b6e0e5f3b2abfc2b62e76a8b.jpg");
            json.put("url", "http://localhost:8080/FileManager/file/photos/" + photoName);
            logger.debug("|FileController：上传成功。" + "\t|图片名：" + photoName);
        } else {
            json.put("sucess", 0);
            json.put("message", "上传失败");
            logger.debug("|FileController：上传失败。");
        }
        return json;
    }

}
