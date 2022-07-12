package com.snakefeather.filemanager.controller;

import com.snakefeather.filemanager.domain.Folder;
import com.snakefeather.filemanager.file.PropertiesOperation;
import javafx.application.Application;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Properties;


//@Controller 是Spring框架提供的注解。
@Controller
@RequestMapping("/note")
public class NoteController {

    @RequestMapping("/test")
    public ModelAndView index(ModelAndView model) {
        model.setViewName("note");
        model.addObject("str", "笔记，测试");
//        model.addObject("str", new MdTextURL(Paths.get("ww"),1l,"![img](photos/535.jpg)"));
        return model;
    }

    @RequestMapping("/show")
    public ModelAndView noteIndex(ModelAndView modelAndView, HttpServletRequest request) {
        // 获取数据
        PropertiesOperation pro = new PropertiesOperation();
        Properties properties = pro.getPropertiesByAbso("E:\\0z_SnakeFeatherObject\\FileManager\\config\\defult.properties");
        Folder folder = new Folder(properties.getProperty("folderPath"));
        modelAndView.addObject("folder", folder.getChildFolder());
        modelAndView.addObject("files", folder.getChildFile());
        modelAndView.setViewName("note");
        return modelAndView;
    }

    @RequestMapping("/details/{mdId}/show")
    public ModelAndView noteDetails(ModelAndView modelAndView, @PathVariable Integer mdId) {
        System.out.println("mdId:" + mdId);
        // 获取数据
        PropertiesOperation pro = new PropertiesOperation();
        Properties properties = pro.getPropertiesByAbso("E:\\0z_SnakeFeatherObject\\FileManager\\config\\defult.properties");
        Folder folder = new Folder(properties.getProperty("folderPath"));
        modelAndView.addObject("folder", folder.getChildFolder());
        modelAndView.addObject("files", folder.getChildFile());
        StringBuffer sb = new StringBuffer();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\test\\notes\\013Spring.md"), "UTF-8"))) {
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
                sb.append("\n");
                System.out.println(s);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        modelAndView.addObject("mdText", sb.toString());
        modelAndView.addObject("mdId", 233);
        modelAndView.setViewName("noteDetails");
        return modelAndView;
    }

    /**
     * 修改md文件
     *
     * @param modelAndView
     * @param mdId         目的md文件的hash值
     * @param mdText       md文件的所有内容（源码）
     * @return 是否成功
     */
    @RequestMapping("/details/{mdId}/updateAll")
    @ResponseBody
    public boolean noteDetailsUpdate(ModelAndView modelAndView, @PathVariable Integer mdId, @RequestParam(value = "mdText", required = false) String mdText) {
        System.out.println("mdId:" + mdId);
        System.out.println(mdText);
        modelAndView.addObject("is", true);
        modelAndView.setViewName("noteDetails");
//        List<Boolean> list = new ArrayList<>();
//        list.add(true);
//        return list;
        return true;
    }


}
