package com.snakefeather.filemanager;

import com.snakefeather.filemanager.file.FileOperation;
import com.snakefeather.filemanager.text.MarkdownOperation;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class snakefeather {
//    public static void main(String[] args) throws Exception {
//        FileManager fileManager = new FileManager();
//
//        String pathStr = "D:\\testFolder\\041java.md";
////        String pathStr = "D:\\装机存储\\object\\QQBot";
//
//        ArrayList<String> strArrayList = fileManager.getAllFolderPath(pathStr);
//        for (String s : strArrayList) {
//            System.out.println("" + s);
//            File file = new File(s);
//            File[] listFiles = file.listFiles();
//            for (File f : listFiles) {
//                if (!f.isDirectory()) {
//                    String x = String.join("", Collections.nCopies(s.length(), "-"));
//                    System.out.println(x + f.getName());
//                }
//            }
//        }
//
////        ArrayList<String> stringArrayList = FileManager.getAllFilePath("C:\\ProgramData\\Autodesk\\ADPSDK\\INSTALLER\\AdpSDK.config");
//
//    }
//
//    @Test
//    public void getFileBySuffix() throws Exception {
//        FileManager fileManager = new FileManager();
//        String pathStr = "D:\\装机存储\\object\\QQBot";
//        List<String> strArrayList = fileManager.getFileBySuffix(pathStr, "class");
//        for (String s : strArrayList) {
//            System.out.println(s);
//        }
//
//    }
//
//    @Test
//    public void getLingText() throws Exception {
//        FileManager fileManager = new FileManager();
//        String pathStr = "E:\\test\\test\\041java.md";
////        String pathStr = "E:\\test\\test\\个人总计.md";
////        String pathStr = "E:\\笔记\\";
////        String pathStr = "E:\\笔记\\buy.md";
//        List<String> strArrayList = fileManager.readLine(pathStr);
//        MdRegex mdRegex = new MdRegex();
//        for (String s : strArrayList) {
//            if (mdRegex.containPhotoUrl(s)) {
//                System.out.println(s);
//            }
//        }
//    }


    @Test
    public void getAllFolder() throws IOException {
//        File file = new File("D://MobileEmnMaster");
//        Properties p = new Properties();
//        p.load(new FileInputStream("src//mysql.properties"));
//        String url = p.getProperty("url");
//        System.out.println(url);
//        if (file.exists()) {
//            System.out.println("MobileEmnMaster文件存在");
//        } else {
//            System.out.println(file.getAbsolutePath() + "不存在");
//        }
//        FileOperation fileO = new FileOperation();
////        String s = fileO.getAbsoluteFile("D:\\testFolder\\");
//        List<String> folderList = fileO.getAllFolder("D:\\");
//        System.out.println("开始输出：");
//        for (String folder : folderList) {
//            System.out.println(folder);
//        }
//        Set<String> fileList = fileO.getAllFile("D:\\").keySet();
//        System.out.println("开始输出：");
//        for (String f : fileList) {
//            System.out.println(f);
//        }
//
////        List<String> photoUrl = fileO.getAllPhotoUrl("D:\\testFolder\\041java.md");
//        System.out.println("开始输出图片链接：");
//        for (String f : photoUrl) {
//            System.out.println(f);
//        }
//        fileO.makeLog("D:\\testFolder\\test.txt", photoUrl);

    }

    @Test
    public void photoUrl() throws IOException {
        File f = new File("D:\\testFolder\\test.txt");
        List<String> stringList = new LinkedList<>();
        String s = "";
        try (FileReader fw = new FileReader(f);
             BufferedReader bufw = new BufferedReader(fw)) {
            while ((s = bufw.readLine()) != null) {
                stringList.add(s);
            }
        }
        Map<String,String> stringMap = new HashMap<>();
        String REGEX_PHOTOURL = ".*\\!\\[(?<remark>.*)\\]\\((?<filePath>.*[\\/]{1,2}(?<fileName>[^\\/.]+[.](png)|(jpg)))\\)";
//        String REGEX_PHOTOURL = ".*\\!\\[(?<remark>.*)\\]\\((?<filePath>.*[\\/]{1,2}(?<fileName>[^\\/.]+[.][(png)(jpg)]))\\)";
        for (String str : stringList) {
            Matcher matcher = Pattern.compile(REGEX_PHOTOURL).matcher(str);
            if (matcher.matches()) {
                stringMap.put(matcher.group("fileName"),str);
            }
        }
        FileOperation fileO = new FileOperation();
        Map<String,String> map = fileO.getAllFile("D:\\");
        Set<String> fileList = map.keySet();
        Set<String> fileSet = stringMap.keySet();
        fileList.stream().forEach(key -> System.out.println(key +"\t:\t" + map.get(key)));
//        fileSet.stream().forEach(key -> System.out.println(key +"\t:\t" + stringMap.get(key)));
//        for (String fileName : fileSet){
//            if (!fileList.contains(fileName)){
//                System.out.println("缺少：" + fileName);
//            }else {
//                System.out.println("含有：" + fileName);
//            }
//        }

    }

    @Test
    public void testa() throws IOException {
        FileOperation fileO = new FileOperation();
        Map<String,String> map = fileO.getAllFile("D:\\");
        map.keySet().stream().forEach(key -> System.out.println(key + "\t:\t" + map.get(key)));


        File f = new File("D:\\testFolder\\test.txt");
        List<String> stringList = new LinkedList<>();
        String s = "";
        try (FileReader fw = new FileReader(f);
             BufferedReader bufw = new BufferedReader(fw)) {
            while ((s = bufw.readLine()) != null) {
                stringList.add(s);
            }
        }

        Set<String> txtByFile = map.keySet();
        for (String str: stringList){
            if (txtByFile.contains(s)){
                System.out.println("含有：" + str);
            }else{
                System.out.println("缺少：" + str);
            }
        }
    }


    /**
     *  随机访问文件测试
     * @throws IOException
     */
    @Test
    public void testb() throws IOException {
        File file = new File("D:\\testFolder\\test.txt");
        int lineNum = -1;
        try (FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
        ){
            String s = "";
            while ((s = bufferedReader.readLine()) != null){
                lineNum++;
                if (s.equals("debug"))break;
            }
        }
        try (FileWriter writer = new FileWriter(file);
             BufferedWriter bufferedWriter = new BufferedWriter(writer);
        ){
//            while (bufferedWriter.)
        }



    }


    @Test
    public  void main2()
    {
        String srcPathStr = "D:\\testFolder\\041java.md"; //源文件地址
        String desPathStr = "D:\\Program Files (x86)"; //目标文件地址


    }


    @Test
    public void testcd() {
        List<String> stringList = new ArrayList<>();
        stringList.add("111");
        stringList.add("222");
        stringList.add("333");
        stringList.add("444");
        stringList.add("555");
        stringList.add("666");
        for (int i = 0; i < stringList.size(); i++) {
//            System.out.println("列表的大小：" + stringList.size() + "列表元素：" + stringList.get(i));
            if ("444".equals(stringList.get(i))) {
                stringList.remove(i);
//                System.out.println("移除成功，列表的大小：" + stringList.size());
            }
            if ("555".equals(stringList.get(i))) {
                stringList.remove(i);
//                System.out.println("移除成功，列表的大小：" + stringList.size());
            }
        }
        System.out.println(stringList);
    }


    @Test
    public void testcc() {
        List<String> stringList = new ArrayList<>();
        stringList.add("111");
        stringList.add("222");
        stringList.add("333");
        stringList.add("444");
        stringList.add("555");
        stringList.add("666");
        for (int i = 0; i < stringList.size(); i++) {
            if ("444".equals(stringList.get(i))) {
                stringList.remove(stringList.size());
                System.out.println("移除成功，列表的大小：" + stringList.size());
            }
        }
    }
}
