package com.snakefeather.filemanager.regex;

import java.util.function.Supplier;

public class RegexStore {

    //#region  文件正则      ----------------------------------

    //  系统文件和配置文件 正则
    public static final String SYSTEMFILE = "^[$.].*";
    //  盘符 正则
    public static final String SYMBOL = "[A-Z]\\:[\\\\/]{1,2}";


    // 文件正则
    public static final String FILE = new Supplier<String>() {
        @Override
        public String get() {
            //        文件名不能包含以下任何字符：\/:*?"<>|
            /*
                    实例：
                        photo
                        图片
                        下载
                        document
                        222.png
                        222.jpg
             */
            StringBuilder file = new StringBuilder();
//            file.append("[^\\\\/:\\*\\?\"<>|]+");     // 文件名不带指定字符。
            file.append("[^\\\\/:\\*\\?\"<>|\\(\\!]+");     // 为了区分正则表达式
            return file.toString();
        }
    }.get();

    // 文件夹 web路径正则
    public static final String WEB_FOLDER_PATH = new Supplier<String>() {

        @Override
        public String get() {
        /*
            实例：
         */
            StringBuilder folderPath = new StringBuilder();
            folderPath.append("(http)|(https)").append("\\:");     //      协议
            folderPath.append("([^.]*\\.)*").append("[^.]*");       //      域名
            folderPath.append("(\\:\\d{1,5})?");       //      端口      // 可能有，也可能没有
            folderPath.append("/");
            folderPath.append("(").append(FILE).append("/").append(")*");

            return folderPath.toString();
        /*
            历史版本：
         */
        }
    }.get();
    // 文件夹 绝对路径正则
    public static final String ABSOLUTE_FOLDER_PATH = new Supplier<String>() {

        @Override
        public String get() {
        /*
            实例：

         */
            StringBuilder folderPath = new StringBuilder();
            folderPath.append(SYMBOL);         // 盘符
            folderPath.append("(").append(FILE).append("[\\\\/]{1,2}").append(")*");     //  文件夹目录     // 可能为空

            return folderPath.toString();
        /*
            历史版本：

         */
        }
    }.get();
    // 文件夹 相对路径正则
    public static final String RELATIVE_FOLDER_PATH = new Supplier<String>() {

        @Override
        public String get() {
        /*
            实例：

         */
            StringBuilder folderPath = new StringBuilder();
            folderPath.append("[\\\\/]{0,2}");       // 开头可能有斜杠或反斜杠，也可能没有
            folderPath.append("(").append(FILE).append("[\\\\/]{1,2}").append(")*");

            return folderPath.toString();
        /*
            历史版本：

         */
        }
    }.get();

    //  文件夹  路径正则   整合了 web路径、绝对路径、相对路径
    public static final String FOLDER_PATH = new Supplier<String>() {

        @Override
        public String get() {
        /*
            实例：

         */
            StringBuilder folderPath = new StringBuilder();
            folderPath.append("(");
            folderPath.append("(" + WEB_FOLDER_PATH + ")|");
            folderPath.append("(" + ABSOLUTE_FOLDER_PATH + ")|");
            folderPath.append("(" + RELATIVE_FOLDER_PATH + ")");
            folderPath.append(")");

            return folderPath.toString();
        /*
            历史版本：

         */
        }
    }.get();

    //#endregion      ----------------------------------


    //#region  Markdown 正则      ----------------------------------

    // 图片名 正则
    public static final String PHOTO = new Supplier<String>() {

        @Override
        public String get() {
        /*
            实例：
                wps545.jpg
                image-20220226194846347.png
         */
//      "[^\\/.]+\\.(jpg|png|jpeg)"
            StringBuilder photo = new StringBuilder();
            photo.append(FILE);
            photo.append("\\.");
            photo.append("(");
            photo.append("jpg").append("|png").append("|jpeg");
            photo.append(")");

            return photo.toString();
        }
    }.get();

    // 图片路径 正则
    public static final String PHOTO_PATH = new Supplier<String>() {

        @Override
        public String get() {
        /*
            实例：

         */
            StringBuilder folderPath = new StringBuilder();
            folderPath.append("(?<folderPath>");
            folderPath.append(FOLDER_PATH);
            folderPath.append("(?<fileName>").append(PHOTO).append(")");
            folderPath.append(")");

            return folderPath.toString();
        }
    }.get();

    // Markdown 图片链接正则
    public static final String PHOTO_URL = new Supplier<String>() {

        @Override
        public String get() {
        /*
        实例：
            ![img](photos/wps545.jpg)
            ![image-20220226194846347](photos\image-20220226194846347.png)
            ![img](https://lab.huaweicloud.com/img/tc/sp-im/1518/1584001714-step-0.png)
            ![image-20220319181759862](E:\notes\main\notes\photos\image-20220319181759862.png)
            ![img](photos/wps535.jpg)![img](photos/wps536.png)
            <img src="photos/依赖倒转原则.png" style="zoom:80%;" />
         */
            /*   历史版本：
            StringBuilder photoUrl = new StringBuilder();
            photoUrl.append("\\!"); // 感叹号开头

            photoUrl.append("\\["); // 中括号
            photoUrl.append("(?<remark>").append("((\\\\])|[^\\]])*").append(")");    // 备注
            photoUrl.append("\\]"); // 中括号

            photoUrl.append("\\("); // 圆括号
            photoUrl.append("(?<filePath>")     // 路径
                    .append(SYMBOL).append(FOLDERPATH)
                    .append("(?<fileName>").append(PHOTO).append(")")       // 文件名
                    .append(")");
            photoUrl.append("\\)"); // 圆括号

            return photoUrl.toString();
             */
            StringBuilder photoUrl = new StringBuilder();
            photoUrl.append("\\!"); // 感叹号开头

            photoUrl.append("\\["); // 中括号
//            photoUrl.append("(?<remark>").append("((\\\\\\])|(^\\]))*").append(")");    // 备注         //  预期匹配 `\]`
            photoUrl.append("(?<remark>").append("([^\\]])*").append(")");    // 备注
            photoUrl.append("\\]"); // 中括号

            photoUrl.append("\\("); // 圆括号
            photoUrl.append(PHOTO_PATH);
            photoUrl.append("\\)"); // 圆括号

            return photoUrl.toString();
            /*
                历史版本：
                        "\\!\\[(?<remark>((\\\\])|[^\\]])*)\\]\\((?<filePath>[^\\]\\(\\!]*[\\/|\\\\](?<fileName>" + PHOTO + "))\\)";
             */
        }
    }.get();

    // Markdown 图片 HTML标签正则
    public static final String PHOTO_HTML = new Supplier<String>() {

        @Override
        public String get() {
        /*
         实例：
            <img src="photos/依赖倒转原则.png" style="zoom:80%;" />
            <img src="photos\依赖倒转原则.png" style="zoom:80%;" />
            <img src="https://lab.huaweicloud.com/img/tc/sp-im/1518/1584001714-step-0.png" style="zoom:80%;" />
            <img src="E:\notes\main\notes\photos\image-20220319181759862.png" style="zoom:80%;" />
         */

            StringBuilder photoHtml = new StringBuilder();
            photoHtml.append("\\<img");    // 尖括号
            photoHtml.append("[^src]*").append("src=\"");
            photoHtml.append(PHOTO_PATH).append("\"");

            photoHtml.append(".*");

            photoHtml.append("\\/\\>");     // `/>

            return photoHtml.toString();
        }
    }.get();

    //#endregion      ----------------------------------


}
