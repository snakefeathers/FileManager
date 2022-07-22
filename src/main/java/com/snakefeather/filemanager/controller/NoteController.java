package com.snakefeather.filemanager.controller;

import com.snakefeather.filemanager.domain.DataCenter;
import com.snakefeather.filemanager.domain.FileTextList;
import com.snakefeather.filemanager.domain.Folder;
import com.snakefeather.filemanager.file.PropertiesOperation;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Map;
import java.util.Properties;


//@Controller 是Spring框架提供的注解。
@Controller
@RequestMapping("/note")
public class NoteController {


    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * 显示笔记列表
     *
     * @param modelAndView
     * @param request
     * @return
     */
    @RequestMapping("/list")
    public ModelAndView noteIndex(ModelAndView modelAndView, HttpServletRequest request) {
        // 获取数据
        Map<String, FileTextList> fileMap = DataCenter.getFileMap();
//        modelAndView.addObject("folder", folder.getChildFolder());
//        modelAndView.addObject("files", folder.getChildFile());
        modelAndView.setViewName("note");
        return modelAndView;
    }

    /**
     * 笔记展示
     *
     * @param modelAndView
     * @param mdId         必要的。   指定文件ID
     * @return
     */
    @RequestMapping("/details/{mdId}/show")
    public ModelAndView noteDetails(ModelAndView modelAndView, @PathVariable Integer mdId) {
        logger.debug("|NoteController：获取笔记内容。" + "\t|笔记ID：" + mdId);
        // 从配置文件中获取到目标路径
        PropertiesOperation pro = new PropertiesOperation();
        Properties properties = pro.getPropertiesByAbso("E:\\0z_SnakeFeatherObject\\FileManager\\config\\defult.properties");
        //  此处文件初始化时会扫描路径。   //  后期可优化为单例模式。 不过，在间隔一段时间或一定访问量后，会刷新一下。
        Folder folder = new Folder(properties.getProperty("notesPath"));

        modelAndView.addObject("folder", folder.getChildFolder());
        modelAndView.addObject("files", folder.getChildFile());
        StringBuffer sb = new StringBuffer();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\test\\notes\\013Spring.md"), "UTF-8"))) {
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
                sb.append("\n");
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
     * @param mdId         目的md文件的id
     * @param mdText       md文件的所有内容（源码）
     * @return 是否成功
     */
    @RequestMapping("/details/{mdId}/updateAll")
    @ResponseBody
    public boolean noteDetailsUpdate(ModelAndView modelAndView, @PathVariable Integer mdId, @RequestParam(value = "mdText", required = false) String mdText) {
        logger.debug("|NoteController：修改笔记内容。" + "\t|笔记ID：" + mdId);
        logger.debug("|笔记内容：" + mdText);

        modelAndView.addObject("is", true);
        modelAndView.setViewName("noteDetails");
        return true;
    }


}
