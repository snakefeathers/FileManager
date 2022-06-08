package com.snakefeather.filemanager.domain;

import com.snakefeather.filemanager.regex.RegexStore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  计划存储Markdown文件读出的信息，之后写入。
 */
public class MdUrlMsg {

    //  信息所属文件
    private String fileName = "";
    //  信息ID
    private String textId = "";
    //  原始信息
    private String originalText = "";
    //  关键信息数组
    private String[] keyMsg;
    //  优化后供使用的图片绝对地址
    private String absolutePath = "";
    //  地址是否是绝对地址
    private boolean isAbsolute;
    //  文件中的指针
    private long targetPointer;
    //  相关信息
    private Object correlation;

    //    String REGEX_PHOTOURL = ".*\\!\\[(?<remark>.*)\\]\\((?<filePath>.*[\\/]{1,2}(?<fileName>[^\\/.]+[.](png)|(jpg)))\\)";
//    private static final Pattern urlPattern = Pattern.compile(REGEX_PHOTOURL);
    private static final Pattern urlPattern = Pattern.compile("");
    private static final Pattern symbolPattern = Pattern.compile(RegexStore.SYMBOL);

    public MdUrlMsg() {
    }

    public MdUrlMsg(String fileName, String originalText,long pointer) {
        this.fileName = fileName;
        this.originalText = originalText;
        Matcher matcher = urlPattern.matcher(originalText);
        if (matcher.matches()) {
            this.textId = matcher.group("fileName");
            this.keyMsg = new String[]{matcher.group("remark"), matcher.group("filePath"),};
            Matcher symbolMatcher = symbolPattern.matcher(originalText);
            if (this.isAbsolute = symbolMatcher.matches()){
                //  该地址是绝对地址
                absolutePath = keyMsg[1];
            }else {
                //  该地址是相对地址
                //  拼凑图片绝对地址
                absolutePath = fileName.substring(0,fileName.lastIndexOf("\\")) + keyMsg[1];
            }
        }
        this.targetPointer = pointer;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTextId() {
        return textId;
    }

    public void setTextId(String textId) {
        this.textId = textId;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String[] getKeyMsg() {
        return keyMsg;
    }

    public void setKeyMsg(String[] keyMsg) {
        this.keyMsg = keyMsg;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public boolean isAbsolute() {
        return isAbsolute;
    }

    public void setAbsolute(boolean absolute) {
        isAbsolute = absolute;
    }

    public Object getCorrelation() {
        return correlation;
    }

    public void setCorrelation(Object correlation) {
        this.correlation = correlation;
    }

    public static Pattern getUrlPattern() {
        return urlPattern;
    }

    public static Pattern getSymbolPattern() {
        return symbolPattern;
    }

    public long isTargetPointer() {
        return targetPointer;
    }

    public void setTargetPointer(long targetPointer) {
        this.targetPointer = targetPointer;
    }
}
