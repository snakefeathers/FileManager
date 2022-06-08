package com.snakefeather.filemanager.regex;

public class RegexStore {

    /**
     * 匹配盘符
     */
    public static final String REGEX_SYMBOL = ".*(?<symbol>[a-zA-z]):[\\/].*";

    /**
     * 文件夹 正则
     */
    public static final String REGEX_FOLDERPATH = "[^<>/\\\\|:*? \\\\]{1}([^<>/\\\\|:*?]*[^ \\\\]*[^<>/\\\\|:*? \\\\]{1})?";
    //    private static final String REGEX_FOLDERPATH = "[^ \\\\]?[^<>/\\\\|:*?]*[^ \\\\]?";
    //    private static final String REGEX_FOLDERPATH = "[^ \\\\][^<>/\\\\|:*?]+[^ \\\\]"; //  无形中限制了长度大于等于3

    /**
     * 文件路径正则
     * 例如：传入：
     * E:\     C:\
     * E:\笔记
     * C:\Program Files (x86)\Adobe\Adobe Creative Cloud Experience\js\node_modules\@ccx\node-imslib\src
     * C:\Program Files (x86)\Adobe\Adobe Creative Cloud Experience\js\node_modules\@ccx\node-imslib\src\
     * C:\Program Files (x86)\Adobe\Adobe Creative Cloud Experience\js\node_modules\@ccx\node-imslib\src\file.txt
     * "E:\笔记\buy.md"
     * 命名规则：
     * 固定开头：大写或小写字母开头，加冒号，下划线。
     * 子路径：文件夹名不能以空格开头或结尾。文件夹名中不包含<>/\|:*?字符。
     * 末尾：可能是文件夹名结尾，或者删除文件名之后的斜杆,或者文件名
     */
    public static final String REGEX_FILEPATH = "^[C-Zc-z]:[\\/|\\\\]((" + REGEX_FOLDERPATH + "){1}(\\\\" + REGEX_FOLDERPATH + ")*\\\\?)?";
//    String REGEX_FILEPATH = "^[C-Zc-z]:\\\\((" + REGEX_FOLDERPATH + "){1}(\\\\" + REGEX_FOLDERPATH + ")*\\\\?)?";

    /**
     * 系统文件和配置文件 正则
     */
    public static final String REGEX_SYSTEMFILE = "^[$.].*";

    // 图片匹配正则
    public static final String PHOTO;

    // Markdown 图片链接正则
    public static final String PHOTOURL;

    // Markdown 图片 HTML标签正则
    public static final String PHOTOHTML;

    static {
        /*
            实例：
                wps545.jpg
                image-20220226194846347.png
         */
//      "[^\\/.]+\\.(jpg|png|jpeg)"
        StringBuilder photo = new StringBuilder();
//        文件名不能包含以下任何字符：\/:*?"<>|
        photo.append("[^\\\\/:*?\"\\<\\>\\|]+");     // 文件名不带指定字符。
        photo.append("\\.");
        photo.append("(");
        photo.append("jpg").append("|png").append("|jpeg");
        photo.append(")");

        PHOTO = photo.toString();
        System.out.println("photo" + PHOTO);
    }

    static {
        /*
        实例：
            ![img](photos/wps545.jpg)
            ![image-20220226194846347](photos\image-20220226194846347.png)
            ![img](https://lab.huaweicloud.com/img/tc/sp-im/1518/1584001714-step-0.png)
            ![image-20220319181759862](E:\notes\main\notes\photos\image-20220319181759862.png)
            ![img](photos/wps535.jpg)![img](photos/wps536.png)
            <img src="photos/依赖倒转原则.png" style="zoom:80%;" />
         */
//   "\\!\\[(?<remark>((\\\\])|[^\\]])*)\\]\\((?<filePath>[^\\]\\(\\!]*[\\/|\\\\](?<fileName>" + PHOTO + "))\\)";
        StringBuilder photoUrl = new StringBuilder();
        photoUrl.append("\\!"); // 感叹号开头

        photoUrl.append("\\["); // 中括号
        photoUrl.append("(?<remark>").append("((\\\\])|[^\\]])*").append(")");    // 备注
        photoUrl.append("\\]"); // 中括号

        photoUrl.append("\\("); // 圆括号
        photoUrl.append("(?<filePath>")     // 路径
                .append("[^\\]\\(\\!]*")
                .append("[\\/\\|\\\\]{1,2}")     //  路径分隔符          //  也有可能直接在同一个目录中。那样的话，就不会有路径分隔符。
                .append("(?<fileName>").append(PHOTO).append(")")       // 文件名
                .append(")");
        photoUrl.append("\\)"); // 圆括号

        PHOTOURL = photoUrl.toString();
        System.out.println("photoUrl" + PHOTOURL);
    }

    static {
        /*
         实例：
            <img src="photos/依赖倒转原则.png" style="zoom:80%;" />
            <img src="photos\依赖倒转原则.png" style="zoom:80%;" />
            <img src="https://lab.huaweicloud.com/img/tc/sp-im/1518/1584001714-step-0.png" style="zoom:80%;" />
            <img src="E:\notes\main\notes\photos\image-20220319181759862.png" style="zoom:80%;" />
         */

        StringBuilder photoHtml = new StringBuilder();
        photoHtml.append("\\<");    // 尖括号

        photoHtml.append("\\/\\>");     // `/>

        PHOTOHTML = photoHtml.toString();
        System.out.println("photo" + PHOTOHTML);
    }

}
