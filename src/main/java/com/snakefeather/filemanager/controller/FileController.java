package com.snakefeather.filemanager.controller;


import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/file")
public class FileController {

    @PostMapping("/photo")
    @ResponseBody
    public Map<String,String> imageUpload(HttpServletRequest request, @RequestParam(value = "editormd-image-file", required = false) MultipartFile file) {

//        JSONPObject
        Map<String,String> json = new HashMap<>();
        try {
            file.transferTo(new File(""));
            //返回Editor回调json格式：{success:1|0,message:"成功|失败",url:"url"}
//            return new FileUpload(1, "上传成功", uploadPath+newFileName);
            json.put("success","0");
            json.put("message","testSuc");
            json.put("url","http://localhost:8080/FileManager/photos/80440620_p0.jpg");
        } catch (IOException e) {
            e.printStackTrace();
            json.put("sucess","0");
            json.put("message","testErr");
        }
        return json;
    }
}
