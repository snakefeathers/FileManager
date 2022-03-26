package com.snakefeather.filemanager.regex;

public interface FileRegex {

    /**
     *  匹配盘符
     */
    String REGEX_SYMBOL = ".*(?<symbol>[a-zA-z]):[\\/].*";

    /**
     * 文件夹 正则
     */
    String REGEX_FOLDERPATH = "[^<>/\\\\|:*? \\\\]{1}([^<>/\\\\|:*?]*[^ \\\\]*[^<>/\\\\|:*? \\\\]{1})?";
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
    String REGEX_FILEPATH = "^[C-Zc-z]:[\\/]((" + REGEX_FOLDERPATH + "){1}(\\\\" + REGEX_FOLDERPATH + ")*\\\\?)?";
//    String REGEX_FILEPATH = "^[C-Zc-z]:\\\\((" + REGEX_FOLDERPATH + "){1}(\\\\" + REGEX_FOLDERPATH + ")*\\\\?)?";

    /**
     * 系统文件和配置文件 正则
     */
    String REGEX_SYSTEMFILE = "^[$.].*";


}
