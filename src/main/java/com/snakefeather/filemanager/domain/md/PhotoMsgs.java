package com.snakefeather.filemanager.domain.md;

import com.snakefeather.filemanager.domain.TextDiv;
import com.snakefeather.filemanager.regex.RegexStore;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhotoMsgs {

    // 枚举数组    三种类型地址
    static PhotoMsg.PathType[] pathTypeArr = {PhotoMsg.PathType.WEB, PhotoMsg.PathType.ABSOLUTE, PhotoMsg.PathType.RELATIVE};

    //#region  img  正则
    static Pattern pImgTry = Pattern.compile(".*" + RegexStore.EASY_PHOTO_IMG + ".*");
    static Pattern pImgMore = Pattern.compile(".*" + RegexStore.MORE_PHOTO_IMG + ".*");
    //  前后可能含有内容   或者同样的图片链接   //  只处理其中在意的地方，其他地方忽略。
    static Pattern pImgWeb = Pattern.compile(".*" + RegexStore.WEB_PHOTO_IMG + ".*");
    static Pattern pImgAbs = Pattern.compile(".*" + RegexStore.ABSOLUTE_PHOTO_IMG + ".*");
    static Pattern pImgRel = Pattern.compile(".*" + RegexStore.RELATIVE_PHOTO_IMG + ".*");

    static Pattern[] pImgArr = {pImgWeb, pImgAbs, pImgRel};
    //#endregion

    //#region  label  正则
    static Pattern pLabelTry = Pattern.compile(".*" + RegexStore.EAYS_PHOTO_LABEL + ".*");
//    static Pattern pLabelMore = Pattern.compile(".*" + RegexStore.MORE_PHOTO_IMG + ".*");

    static Pattern pLabelWeb = Pattern.compile(".*" + RegexStore.WEB_PHOTO_LABEL + ".*");
    static Pattern pLabelAbs = Pattern.compile(".*" + RegexStore.ABSOLUTE_PHOTO_LABEL + ".*");
    static Pattern pLabelRel = Pattern.compile(".*" + RegexStore.RELATIVE_PHOTO_LABEL + ".*");

    static Pattern pLabelAlt = Pattern.compile(".*" + RegexStore.ALT_PHOTO_LABEL + ".*");

    static Pattern[] pLabelArr = {pLabelWeb, pLabelAbs, pLabelRel};
    //#endregion


    //#region  img  方法

    /**
     * 尝试获取Img链接，  是否获取成功可以通过属性MsgTypeEnum 来判断
     * 注：  没有处理一行中含有多个图片链接的情况   这种情况应该在预处理过程中处理。而不是留在这里。
     *
     * @param textDiv
     * @return
     */
    public static TextDiv tryGetImg(TextDiv textDiv) {
        String originalText = textDiv.getOriginalText();
        MdTextImg imgDiv = null;      //  临时变量
        //   粗筛
        if (pImgTry.matcher(originalText).matches()) {
            imgDiv = getImgAll(originalText);
            if (imgDiv != null) {
                imgDiv.decorate(textDiv);
                return imgDiv;
            }
        }
        return textDiv;
    }

    /**
     * 正则匹配内容。   提取需要的信息并存入对象中，返回。
     *
     * @param originalText 文本原文
     * @return MdTextImg
     */
    public static MdTextImg getImgAll(String originalText) {
        MdTextImg textDiv = null;
        //  三种 类型都尝试一次。  尝试成功就返回创建的对象，尝试失败就返回null。  // 相对路径排在了最后面，因为它的通用性较强。
        for (int i = 0; i < 3; i++) {
            //   matcher  在并发中需要结合  reset() 方法进行优化。
            textDiv = getImg(pImgArr[i].matcher(originalText), pathTypeArr[i]);
            if (textDiv != null) {
                return textDiv;
            }
        }
        return null;
    }

    /**
     * 提取有效信息，并初始化MdTextImg对象。
     * 提取的信息： remark  folderPath fileName
     *
     * @param matcher
     * @return
     */
    public static MdTextImg getImg(Matcher matcher, PhotoMsg.PathType pathType) {
        if (matcher.matches()) {
            String remark = matcher.group("remark") != null ? matcher.group("remark") : "";
            // 初始化对象
            MdTextImg mdTextImg = new MdTextImg(pathType, matcher.group("folderPath"), matcher.group("fileName"), remark);
            return mdTextImg;
        } else {
            return null;
        }
    }

    //#endregion

    //#region   labelPhoto  方法

    /**
     * 尝试获取labelPhoto链接，  是否获取成功可以通过属性MsgTypeEnum 来判断
     * 注：  没有处理一行中含有多个图片链接的情况   这种情况应该在预处理过程中处理。而不是留在这里。
     *
     * @param textDiv
     * @return
     */
    public static TextDiv tryGetLabel(TextDiv textDiv) {
        String originalText = textDiv.getOriginalText();
        MdTextLabelPhoto labelPhoto = null;
        //   粗筛
        if (pImgTry.matcher(originalText).matches()) {
            //  三种路径都匹配一遍。 匹配到的话，必定会返回一个非空对象。
            labelPhoto = getLabelAll(originalText);
            //  如果匹配成功，装饰基类
            if (labelPhoto != null) {
                labelPhoto.decorate(textDiv);
                return labelPhoto;
            }
        }
        // 没有修改，返回原textDiv 对象
        return textDiv;
    }

    /**
     * 正则匹配内容。   提取需要的信息并存入对象中，返回。
     *
     * @param originalText 文本原文
     * @return MdTextLabelPhoto
     */
    public static MdTextLabelPhoto getLabelAll(String originalText) {
        MdTextLabelPhoto textDiv = null;
        //  三种 类型都尝试一次。  尝试成功就返回创建的对象，尝试失败就返回null。  // 相对路径排在了最后面，因为它的通用性较强。
        for (int i = 0; i < 3; i++) {
            //   matcher  在并发中需要结合  reset() 方法进行优化。
            textDiv = getLabel(pLabelArr[i].matcher(originalText), pathTypeArr[i]);
            if (textDiv != null) {
                return textDiv;
            }
        }
        return null;
    }

    /**
     * 提取有效信息，并初始化 MdTextLabelPhoto 对象
     * 提取的信息： folderPath fileName alt
     *
     * @param matcher
     * @return
     */
    public static MdTextLabelPhoto getLabel(Matcher matcher, PhotoMsg.PathType pathType) {
        //   调用alt 的正则表达式提取有效信息  参数是matcher匹配到的内容。
        if (matcher.matches()) {
            String altStr = null;
            Matcher matcherAlt = pLabelAlt.matcher(matcher.group());
            if (matcherAlt.matches()) {
                altStr = matcherAlt.group("alt");
            } else {
                altStr = "";
            }
            // 初始化对象
            MdTextLabelPhoto mdLabelPhoto =
                    new MdTextLabelPhoto(
                            pathType,
                            matcher.group("folderPath"),
                            matcher.group("fileName"),
                            altStr);
            return mdLabelPhoto;
        } else return null;
    }

    //#endregion


    //#region   视二为一   使用PhotoMsg 接口  （挖坑）感觉其实可以抽象出来。 后期再改吧。
    //    忽略  MdTextImg  MdTextLabelPhoto 细节

    /**
     * 用于粗筛   判断是 图片类型的语句
     *
     * @param originalText
     * @return
     */
    public static boolean isPhotoMsg(String originalText) {
        return pImgTry.matcher(originalText).matches()
                || pLabelTry.matcher(originalText).matches();
    }

    /**
     * 尝试 初始化PhotoMsg对象
     *
     * @param originalText
     * @return
     */
    public static PhotoMsg tryGetPhotoMsg(String originalText) {
        if (pImgTry.matcher(originalText).matches()) {
            return PhotoMsgs.getImgAll(originalText);
        } else {
            return PhotoMsgs.getLabelAll(originalText);
        }
    }


    /**
     * 获取 对应类型的正则  Pattern对象
     *
     * @param photoMsg
     * @return
     */
    public static Pattern getPhotoMsgPattern(PhotoMsg photoMsg) {
        TextDiv textDiv = (TextDiv) photoMsg;
        PhotoMsg.PathType pathType = null;
        Matcher matcher = null;
        if (textDiv.getTextType() == TextDiv.MsgTypeEnum.IMG) {
            if (pathType == PhotoMsg.PathType.WEB) {
                return pImgWeb;
            } else if (pathType == PhotoMsg.PathType.ABSOLUTE) {
                return pImgAbs;
            } else {
                return pImgRel;
            }
        } else {
//            TextDiv.MsgTypeEnum.LABEL_PHOTO
            if (pathType == PhotoMsg.PathType.WEB) {
                return pLabelWeb;
            } else if (pathType == PhotoMsg.PathType.ABSOLUTE) {
                return pLabelAbs;
            } else {
                return pLabelRel;
            }
        }
    }

    //#endregion


    //#region  修改内容   folderPath fileName alt remark

    /**
     * 修改图片的 文件路径
     *
     * @param photoMsg   修改对象
     * @param folderPath 文件路径
     * @return
     */
    public static PhotoMsg updateFolderPath(PhotoMsg photoMsg, String folderPath) {
        TextDiv textDiv = (TextDiv) photoMsg;
        String originalText = textDiv.getOriginalText();
        System.out.println("修改内容：" + originalText);
        Matcher matcher = getPhotoMsgPattern(photoMsg).matcher(originalText);
        matcher.matches();

        //  获取到捕获数组的边界
        int start = matcher.start("folderPath");
        int end = matcher.end("folderPath");
        //  修改内容
        String text = originalText.substring(0, start) + folderPath + originalText.substring(end);
        //  修改对象
        textDiv.setOriginalText(text);
        photoMsg.setPhotoPath(folderPath);
        System.out.println("\t\t\t\t修改结果：" + text);
        return photoMsg;
    }

    /**
     * 修改图片名
     *
     * @param photoMsg
     * @param fileName
     * @return
     */
    public static PhotoMsg updatePhotoName(PhotoMsg photoMsg, String fileName) {
        TextDiv textDiv = (TextDiv) photoMsg;
        String originalText = textDiv.getOriginalText();
        Matcher matcher = getPhotoMsgPattern(photoMsg).matcher(originalText);

        //  获取到捕获数组的边界
        int start = matcher.start("fileName");
        int end = matcher.end("fileName");
        //  修改内容
        String text = originalText.substring(0, start) + fileName + originalText.substring(end);
        //  修改对象
        textDiv.setOriginalText(text);
        photoMsg.setPhotoName(fileName);
        return photoMsg;
    }

    /**
     * 修改图片 备注
     *
     * @param photoMsg
     * @param fileName
     * @return
     */
    public static PhotoMsg updatePhotoRemark(PhotoMsg photoMsg, String fileName) {
        TextDiv textDiv = (TextDiv) photoMsg;
        String originalText = textDiv.getOriginalText();

        //  获取到捕获数组的边界
        int start, end;

        if (textDiv.getTextType() == TextDiv.MsgTypeEnum.IMG) {
            Matcher matcher = getPhotoMsgPattern(photoMsg).matcher(originalText);
            start = matcher.start("remark");
            end = matcher.end("remark");
        } else {
            Matcher matcher = Pattern.compile(".*" + RegexStore.ALT_PHOTO_LABEL + ".*").matcher(originalText);
            start = matcher.start("alt");
            end = matcher.end("alt");
        }

        //  修改内容
        String text = originalText.substring(0, start) + fileName + originalText.substring(end);
        //  修改对象
        textDiv.setOriginalText(text);
        photoMsg.setRemark(fileName);
        return photoMsg;
    }


    //#endregion

}
