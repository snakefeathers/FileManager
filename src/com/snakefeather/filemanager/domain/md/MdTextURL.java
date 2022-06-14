package com.snakefeather.filemanager.domain.md;

import com.snakefeather.filemanager.domain.TextDiv;
import com.snakefeather.filemanager.regex.RegexStore;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 计划存储Markdown文件读出的信息，之后写入。
 */
public class MdTextURL extends TextDiv {


    //#region   图片信息

    //   url备注   或  alt信息
    private String remark = null;

    //   网络地址还是  绝对、相对地址
    private boolean isWeb = false;

    //   图片文件路径     //  相对地址的，根据文件路径，转换为绝对地址。 // 绝对地址的，不用改变。
    private Path photoPath = null;
    //   图片网络路径
    private URL photoUrl = null;
    //#endregion

    public MdTextURL(Path filePath, long lineNumber, String originalText) {
        super(filePath, lineNumber, originalText, MsgTypeEnum.URL);
        setOriginalText(originalText);
    }



    /**
     *   设置URL原文，   同时从其中提取出信息来
     *
     * @param originalText
     */
    public void setOriginalText(String originalText) {
        //   正则匹配
        Matcher matcher = Pattern.compile(".*" + RegexStore.PHOTO_URL + ".*").matcher(originalText);
        //  调用matches（）方法，顺便做个保底异常
        if (!matcher.matches()) throw new NullPointerException("图片URL：MgMsg无法匹配。" + "原文：" + originalText);
        //   根据是web路径还是绝对或相对路径，决定要创建Path还是URL。
        isWeb = originalText.matches(".*" + RegexStore.WEB_PHOTO_PATH + ".*");
        if (isWeb) {
            //  是web路径
            try {
                this.photoUrl = new URL(matcher.group("folderPath"));
            } catch (MalformedURLException e) {
                throw new NullPointerException("图片URL：创建URL路径失败。" + "原文：" + originalText);
            }
            this.primaryText = Paths.get(this.photoUrl.getPath())
                    .getFileName().toString();       //  文本ID为图片名
            //  URL 直接调用getFile()方法，实际返回的是类似于相对路径。（测试中完全相同。）
            //      不如直接调用getPath()方法，获取到这种路径。
            //       之后使用Path类，获取到文件名

        } else {
            //  绝对路径或相对路径
            this.photoPath = Paths.get(matcher.group("folderPath"));
            if (!photoPath.isAbsolute()) {
                //   不是绝对路径，转为绝对路径
                photoPath = filePath.resolve(photoPath).normalize();
            }
            //  文本ID为图片名   //  虽然正则中也有捕获 文件名，不过正则表达式有待完善，会经常修改，还是以后在补吧。
            this.primaryText = this.photoPath.getFileName().toString();
        }
    }


    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setPhotoPath(Path photoPath) {
        this.photoPath = photoPath;
        isWeb = false;
    }

    public void setPhotoUrl(URL photoUrl) {
        this.photoUrl = photoUrl;
        isWeb = true;
    }



    public String getRemark() {
        return remark;
    }

    public boolean sWeb() {
        return isWeb;
    }

    public Path getPhotoPath() {
        return photoPath;
    }

    public URL getPhotoUrl() {
        return photoUrl;
    }


}
