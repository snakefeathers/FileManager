package com.snakefeather.filemanager.domain;

import com.snakefeather.filemanager.regex.RegexStore;

import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * 工具类
 */
public class TextDivs {

    // 图片URL正则   ![]()
    private static Pattern photoURLPattern = Pattern.compile(RegexStore.PHOTO_HTML);
    // 图片IMG正则      ![]()
    private static Pattern photoImgPattern = Pattern.compile(RegexStore.PHOTO_URL_EASY);

    //  工厂方法   根据文本，进行格式匹配，分析文本类型。实例化该类，并返回根类型。
    public static TextDiv getTextDivByStr(Path path, long lineCount, String str) {
//        if (photoURLPattern.matcher(str).matches() ){
//            return  new MdTextURL(path,lineCount,str);
//        }
        return new TextDiv(path,lineCount,str,TextDiv.MsgTypeEnum.ORDINARY);
    }
}
