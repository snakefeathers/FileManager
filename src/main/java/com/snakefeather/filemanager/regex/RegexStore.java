package com.snakefeather.filemanager.regex;

import java.util.function.Supplier;

public class RegexStore {


    //  系统文件和配置文件 正则  // 以 "."或 "$"开头
    public static final String SYSTEMFILE = "^[$.].*";
    //  盘符 正则   //  大写或小写字母开头，加冒号，斜杠或反斜杠
    public static final String SYMBOL = "[a-zA-Z]\\:[\\\\/]{1,2}";


    //**文件夹正则    // "[^\\\\/:\\*\\?\"<>|]+"
    public static final String FOLDER = new Supplier<String>() {
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
            file.append("[^\\\\/:\\*\\?\"<>|]+");     // 文件名不带指定字符。
//            file.append("[^\\\\/:\\*\\?\"<>|\\(\\!]+");  // 多了"!("   // 为了区分正则表达式
            return file.toString();
        }
    }.get();

    // 图片名 正则   // FOLDER + "\\.(jpg|png|jpeg)"
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
            photo.append(FOLDER);
            photo.append("\\.");
            photo.append("(");
            photo.append("jpg").append("|png").append("|jpeg");
            photo.append(")");

            return photo.toString();
        }
    }.get();

    //#region  三种路径      ----------------------------------

    //**三种路径的正则 —— web、绝对、相对
    //  web路径正则 前提：结尾以斜杠或反斜杠结尾         （后面跟有文件的话可以保证有斜杠或反斜杠）
    private static final String WEB_FOLDER_PATH = new Supplier<String>() {

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
            folderPath.append("(").append(FOLDER).append("/").append(")*");

            return folderPath.toString();
        /*
            历史版本：
         */
        }
    }.get();
    //  绝对路径正则 前提：结尾以斜杠或反斜杠结尾   （后面跟有文件的话可以保证有斜杠或反斜杠）
    private static final String ABSOLUTE_FOLDER_PATH = new Supplier<String>() {

        @Override
        public String get() {
        /*
            实例：

         */
            StringBuilder folderPath = new StringBuilder();
            folderPath.append(SYMBOL);         // 盘符
            folderPath.append("(").append(FOLDER).append("[\\\\/]{1,2}").append(")*");     //  文件夹目录     // 可能为空

            return folderPath.toString();
        /*
            历史版本：

         */
        }
    }.get();
    //  相对路径正则 前提：结尾以斜杠或反斜杠结尾   （后面跟有文件的话可以保证有斜杠或反斜杠）
    private static final String RELATIVE_FOLDER_PATH = new Supplier<String>() {

        @Override
        public String get() {
        /*
            实例：

         */
            StringBuilder folderPath = new StringBuilder();
            folderPath.append("[\\\\/]{0,2}");       // 开头可能有斜杠或反斜杠，也可能没有
            folderPath.append("(").append(FOLDER).append("[\\\\/]{1,2}").append(")*");

            return folderPath.toString();
        /*
            历史版本：

         */
        }
    }.get();

    //**三合一
    //  文件夹  路径正则   整合了 web路径、绝对路径、相对路径      前提：结尾以斜杠或反斜杠结尾   （后面跟有文件的话可以保证有斜杠或反斜杠）
    private static final String FOLDER_PATH_ALL = new Supplier<String>() {

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


    //#region img语句  匹配  ![]()    ----------------------------------
    // 示例：
    //    ![img](photos/535.jpg)
    //    ![img](/photos/535.jpg)           //  尽管带有斜杠，两者在md笔记中效果 相同，但是在java代码中使用时需要注意
    //    ![image-20220313173812070](C:\Users\Administrator\AppData\Roaming\image-20220313173812070.png)
    //    ![img](https://lab.huaweicloud.com/img/tc/sp-im/1519/1567681665-step-1.png)
    //    ![img](photos/535.jpg)
    //    ![image-20220313173812070](C:\Users\Administrator\AppData\Roaming\image-20220313173812070.png)
    //    ![img](https://lab.huaweicloud.com/img/tc/sp-im/1519/1567681665-step-1.png)
    //    ![运行结果](/photos/image-20211221213605502.png)

    //    复杂
    //    ![img](photos/53](22)22.jpg)
    //    ![img](photos/535.jpg)![img](photos/536.png)
    //    ![img](photos/53![5](22)22.jpg)
    //    ![img](photos/53![5](22)22.jpg)![img](photos/536.png)               // 有RAID0、RAID1、RAID10那味儿了。
    //    ![im\](g](photos/53![5](22)22.jpg)![img](photos/536.png)
    //  构思：
    //      1. 粗匹配，筛选出可能含有图片链接的语句。      （虽然是粗匹配，但是在日常使用情况下，能保证必定匹配一条及以上的图片链接。）
    //      2. 多个图片链接匹配。（处理一行内多条语句的情况）
    //      3. 三种地址情况下的图片语句，  任一匹配。提取需要的信息。   （有不匹配的可能性。但是很低了。）

    //  IMG         图片链接粗匹配      匹配  ![]()
    public static final String EASY_PHOTO_IMG = new Supplier<String>() {
        @Override
        public String get() {
            StringBuilder photoUrl = new StringBuilder();
            photoUrl.append("\\!"); // 感叹号开头

            photoUrl.append("\\["); // 中括号
            photoUrl.append("(.*)?");
            photoUrl.append("\\]"); // 中括号

            photoUrl.append("\\("); // 圆括号
            photoUrl.append("(.*)+");
            photoUrl.append("\\)"); // 圆括号
            return photoUrl.toString();
        }
    }.get();

    //  IMG     多个图片链接匹配     //  避免将一行中多条链接视为一条链接使用。 //  作用：分隔出每条IMG语句。没有捕获数组。
    public static final String MORE_PHOTO_IMG = new Supplier<String>() {
        @Override
        public String get() {
            StringBuilder photoUrl = new StringBuilder();

            photoUrl.append("\\!\\[");
            photoUrl.append("[^\\]]*").append("\\]\\(").append("[^\\)]*");
            photoUrl.append("\\)").append("[^(\\!\\[)]*");
            photoUrl.append("(?<ind>").append("\\!\\[)").append("[^\\)]*\\)");       // 匹配  ]  ![组合。  其中可能有填充字符。

            return photoUrl.toString();
        }
    }.get();

    //  IMG     Web地址   含捕获数组 remark  folderPath fileName
    public static final String WEB_PHOTO_IMG = new Supplier<String>() {
        @Override
        public String get() {
            StringBuilder photoUrl = new StringBuilder();
            photoUrl.append("\\!"); // 感叹号开头

            photoUrl.append("\\["); // 中括号
            photoUrl.append("(?<remark>" + "([^\\]])*" + ")");    // 备注
            photoUrl.append("\\]"); // 中括号

            photoUrl.append("\\("); // 圆括号
            photoUrl.append("(?<folderPath>" + WEB_FOLDER_PATH + ")").append("(?<fileName>" + PHOTO + ")");
//            photoUrl.append("\\s*\".*\"\\s*"); // 可选标题
            photoUrl.append("\\)"); // 圆括号

            return photoUrl.toString();
        }
    }.get();
    //  IMG     绝对地址   含捕获数组 remark  folderPath fileName
    public static final String ABSOLUTE_PHOTO_IMG = new Supplier<String>() {

        @Override
        public String get() {
            StringBuilder photoUrl = new StringBuilder();
            photoUrl.append("\\!"); // 感叹号开头

            photoUrl.append("\\["); // 中括号
            photoUrl.append("(?<remark>" + "([^\\]])*" + ")");    // 备注
            photoUrl.append("\\]"); // 中括号

            photoUrl.append("\\("); // 圆括号
            photoUrl.append("(?<folderPath>" + ABSOLUTE_FOLDER_PATH + ")").append("(?<fileName>" + PHOTO + ")");
            photoUrl.append("\\)"); // 圆括号

            return photoUrl.toString();
        }
    }.get();
    //  IMG     相对地址   含捕获数组 remark  folderPath fileName
    public static final String RELATIVE_PHOTO_IMG = new Supplier<String>() {

        @Override
        public String get() {
            StringBuilder photoUrl = new StringBuilder();
            photoUrl.append("\\!"); // 感叹号开头

            photoUrl.append("\\["); // 中括号
            photoUrl.append("(?<remark>" + "([^\\]])*" + ")");    // 备注
            photoUrl.append("\\]"); // 中括号

            photoUrl.append("\\("); // 圆括号
            photoUrl.append("(?<folderPath>" + RELATIVE_FOLDER_PATH + ")").append("(?<fileName>" + PHOTO + ")");
            photoUrl.append("\\)"); // 圆括号

            return photoUrl.toString();
        }
    }.get();


    //#endregion      ----------------------------------


    //#region  imgLabe语句  匹配 <img src="" />
    //    <img src="E:\notes\main\notes\photos\image-20220319181759862.png" style="zoom:80%;" />
    //    <img src="photos/依赖倒转原则.png" style="zoom:80%;" />
    //    <img src="photos\依赖倒转原则.png" style="zoom:80%;" />
    //    <img src="https://lab.huaweicloud.com/img/tc/sp-im/1518/1584001714-step-0.png" style="zoom:80%;" />
    //    <img src = "https://lab.huaweicloud.com/img/tc/sp-im/1518/1584001714-step-0.png" style="zoom:80%;" />
    //    <img src="E:\notes\main\notes\photos\image-20220319181759862.png" style="zoom:80%;" />
    //    <img src = "photos/wps331.jpg" alt="img" style="zoom:33%;" >
    //    <IMG src="photos/wps331.jpg" alt="img" style="zoom:33%;" />    //  img可以大写  src不可以
    //    <img src="2635'c46'9b6e0e5f3b2abfc2b62e76a8b.jpg" alt="img" style="zoom:33%;" />  // 难处理的双引号嵌套

    //  ImgLabel    图片链接粗匹配      匹配  <img src="" />
    public static final String EAYS_PHOTO_LABEL = new Supplier<String>() {

        @Override
        public String get() {

            StringBuilder photoHtml = new StringBuilder();
            photoHtml.append("\\<[img|IMG]");    // <img <IMG
            photoHtml.append(".*");
            photoHtml.append("src\\s*=\\s*").append("(?<quotes>\"|')").append(".*").append("\\k<quotes>"); // 匹配 src = " "
            photoHtml.append(".*");
            photoHtml.append("\\/?\\>");     // /> >

            return photoHtml.toString();
        }
    }.get();

    //  ImgLabel     Web地址   含捕获数组   folderPath fileName
    public static final String WEB_PHOTO_LABEL = new Supplier<String>() {

        @Override
        public String get() {
            StringBuilder photoHtml = new StringBuilder();
            photoHtml.append("\\<[img|IMG]");
            photoHtml.append(".*");
            photoHtml.append("src").append("\\s*=\\s*");
            photoHtml.append("(?<quotes00>\"|')")
                    .append("(?<folderPath>" + WEB_FOLDER_PATH + ")")
                    .append("(?<fileName>" + PHOTO + ")")
                    .append("\\k<quotes00>");     // 反向引用

//            photoHtml.append("(?<flag>.*?)??");
//
//            photoHtml.append("(").append("alt").append("\\s*=\\s*");
//            photoHtml.append("(?<quotes01>\"|')")
//                    .append("(?<alt>.*?)")
//                    .append("\\k<quotes01>")
//                    .append(")*");
//
//            photoHtml.append("(.*?)?");
            photoHtml.append(".*");

            photoHtml.append("\\/?\\>");     // /> >

            return photoHtml.toString();
        }
    }.get();
    //  ImgLabel    绝对地址   含捕获数组   folderPath fileName
    public static final String ABSOLUTE_PHOTO_LABEL = new Supplier<String>() {

        @Override
        public String get() {
            StringBuilder photoHtml = new StringBuilder();
            photoHtml.append("\\<[img|IMG]");
            photoHtml.append(".*");
            photoHtml.append("src").append("\\s*=\\s*");

            photoHtml.append("(?<quotes>\"|')")
                    .append("(?<folderPath>" + ABSOLUTE_FOLDER_PATH + ")")
                    .append("(?<fileName>" + PHOTO + ")")
                    .append("\\k<quotes>");     // 反向引用
            photoHtml.append(".*");

            photoHtml.append("\\/?\\>");     // /> >

            return photoHtml.toString();
        }
    }.get();
    //  ImgLabel    相对地址   含捕获数组   folderPath fileName
    public static final String RELATIVE_PHOTO_LABEL = new Supplier<String>() {

        @Override
        public String get() {
            StringBuilder photoHtml = new StringBuilder();
            photoHtml.append("\\<[img|IMG]");
            photoHtml.append(".*");
            photoHtml.append("src").append("\\s*=\\s*");

            photoHtml.append("(?<quotes>\"|')")
                    .append("(?<folderPath>" + RELATIVE_FOLDER_PATH + ")")
                    .append("(?<fileName>" + PHOTO + ")")
                    .append("\\k<quotes>");     // 反向引用
            photoHtml.append(".*");

            photoHtml.append("\\/?\\>");     // /> >

            return photoHtml.toString();
        }
    }.get();
    //  ImgLabel    alt 信息   含捕获数组   alt
    //      (挖坑)暂且添加一个额外的正则来提取alt信息。
    //      * alt 对于src的相对位置不固定。  alt可能在src的前面或后面，暂且无法处理。目前以我的水平无法放在同一正则表达式中。
    //      * 理想中，可以使用勉强型，？来匹配，但是这样的话，会有两个捕获分组，捕获分组不能重名，哪怕是if、else（|）这种机制中。
    //      * 为了方便开发，暂且使用两个正则表达式来解决。
    public static final String ALT_PHOTO_LABEL = new Supplier<String>() {

        @Override
        public String get() {
            StringBuilder photoHtml = new StringBuilder();
            photoHtml.append("\\<[img|IMG]");
            photoHtml.append(".*");
            photoHtml.append("alt").append("\\s*=\\s*");

            photoHtml.append("(?<quotes>\"|')")
                    .append("(?<alt>.*?)")
                    .append("\\k<quotes>");     // 反向引用
            photoHtml.append(".*");

            photoHtml.append("\\/?\\>");     // /> >

            return photoHtml.toString();
        }
    }.get();
    //#endregion

}
