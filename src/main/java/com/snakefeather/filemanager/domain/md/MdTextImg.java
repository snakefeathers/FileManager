package com.snakefeather.filemanager.domain.md;

import com.snakefeather.filemanager.domain.TextDiv;

import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * 计划存储Markdown文件读出的信息，之后写入。
 * 相对地址：![img](photos/535.jpg)
 * 绝对地址：![image-20220313173812070](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20220313173812070.png)
 * 网络地址：![img](https://lab.huaweicloud.com/img/tc/sp-im/1519/1567681665-step-1.png)
 * ![运行结果](/photos/image-20211221213605502.png)
 */
public class MdTextImg extends TextDiv implements PhotoMsg {


    //#region   图片信息


    //   web地址、绝对地址、相对地址
    private PhotoMsg.PathType pathType = PathType.RELATIVE;

    //   图片所处的文件夹
    private String folderPath = null;
    //   图片名
    private String fileName = null;
    //   备注  remark
    private String remark = null;

    Pattern pattern = null;

    public MdTextImg(Path filePath, long lineNumber, String originalText, MsgTypeEnum textType) {
        super(filePath, lineNumber, originalText, textType);
    }

    public MdTextImg() {
    }

    /**
     * 结合merger()方法使用。
     *
     * @param pathType
     * @param folderPath
     * @param fileName
     * @param remark
     */
    public MdTextImg(PathType pathType, String folderPath, String fileName, String remark) {
        super(null, -1, null, MsgTypeEnum.IMG);     //  创建临时的TextDiv对象。
        this.pathType = pathType;
        this.folderPath = folderPath;
        this.fileName = fileName;
        this.remark = remark;
    }

    @Override
    public void decorate(TextDiv textDiv) {
        this.filePath = textDiv.getFilePath();
        this.lineNumber = textDiv.getLineNumber();
        this.originalText = textDiv.getOriginalText();
    }


    //#endregion


    /**
     * 设置URL原文，   同时从其中提取出信息来
     *
     * @param originalText
     */
    public void setOriginalText(String originalText) {
        this.originalText = originalText;
//        this.originalText = originalText;
//        //   正则匹配
//        Matcher matcher = Pattern.compile(".*" + RegexStore.EASY_PHOTO_IMG + ".*").matcher(originalText);
//        //  调用matches（）方法，顺便做个保底异常
//        if (!matcher.matches()) throw new NullPointerException("图片URL：MgMsg无法匹配。" + "原文：" + originalText);
//        //   根据是web路径还是绝对或相对路径，决定要创建Path还是URL。
//        isWeb = originalText.matches(".*" + RegexStore.WEB_PHOTO_IMG + ".*");
//        if (isWeb) {
//            //  是web路径
//            try {
//                this.photoUrl = new URL(matcher.group("folderPath"));
//            } catch (MalformedURLException e) {
//                throw new NullPointerException("图片URL：创建URL路径失败。" + "原文：" + originalText);
//            }
//            this.primaryText = Paths.get(this.photoUrl.getPath())
//                    .getFileName().toString();       //  文本ID为图片名
//            //  URL 直接调用getFile()方法，实际返回的是类似于相对路径。（测试中完全相同。）
//            //      不如直接调用getPath()方法，获取到这种路径。
//            //       之后使用Path类，获取到文件名
//
//        } else {
//            //  绝对路径或相对路径
//            this.photoPath = Paths.get(matcher.group("folderPath")+matcher.group("fileName"));
//            if (!photoPath.isAbsolute()) {
//                //   不是绝对路径，转为绝对路径
//                photoPath = filePath.resolve(photoPath).normalize();
//            }
//            //  文本ID为图片名   //  虽然正则中也有捕获 文件名，不过正则表达式有待完善，会经常修改，还是以后在补吧。
//            this.primaryText = this.photoPath.getFileName().toString();
//        }
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }


    @Override
    public String getPhotoName() {
        return fileName;
    }

    @Override
    public void setPhotoName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getPhotoPath() {
        return folderPath;
    }

    @Override
    public void setPhotoPath(String folderPath) {
        this.folderPath = folderPath;
    }

    @Override
    public PathType getPathType() {
        return pathType;
    }

    @Override
    public void setPathType(PathType pathType) {
        this.pathType = pathType;
    }


}
