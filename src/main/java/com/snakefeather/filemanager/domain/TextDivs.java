package com.snakefeather.filemanager.domain;

import com.snakefeather.filemanager.domain.md.PhotoMsg;
import com.snakefeather.filemanager.domain.md.PhotoMsgs;
import com.snakefeather.filemanager.regex.RegexStore;

import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * 工具类
 */
public class TextDivs {

    // 图片URL正则   ![]()
//    private static Pattern photoURLPattern = Pattern.compile(RegexStore.PHOTO_HTML);
    // 图片IMG正则      ![]()
    private static Pattern photoImgPattern = Pattern.compile(RegexStore.EASY_PHOTO_IMG);

    //  工厂方法   根据文本，进行格式匹配，分析文本类型。实例化该类，并返回根类型。
    public static TextDiv getTextDivByStr(Path path, long lineCount, String str) {
        //  尝试 匹配图片正则
        PhotoMsg photoMsg = PhotoMsgs.tryGetPhotoMsg(str);
        if (photoMsg != null){
            photoMsg.decorate(new TextDiv(path,lineCount,str));
            return (TextDiv)photoMsg;
        }

        return new TextDiv(path,lineCount,str,TextDiv.MsgTypeEnum.ORDINARY);
    }
}
