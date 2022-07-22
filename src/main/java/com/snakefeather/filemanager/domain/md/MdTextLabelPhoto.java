package com.snakefeather.filemanager.domain.md;

import com.snakefeather.filemanager.domain.TextDiv;

import java.nio.file.Path;

public class MdTextLabelPhoto extends TextDiv implements PhotoMsg {


    //   web地址、绝对地址、相对地址
    private PhotoMsg.PathType pathType = PathType.RELATIVE;

    //   图片所处的文件夹
    private String folderPath = null;
    //   图片名
    private String fileName = null;
    //   备注  remark
    private String alt = null;

    public MdTextLabelPhoto() {
    }

    public MdTextLabelPhoto(PathType pathType, String folderPath, String fileName, String altStr) {
        super(null, -1, null, MsgTypeEnum.LABEL_PHOTO);     //  创建临时的TextDiv对象。
        this.pathType = pathType;
        this.folderPath = folderPath;
        this.fileName = fileName;
        this.alt = altStr;
    }

    @Override
    public void decorate(TextDiv textDiv) {
        this.filePath = textDiv.getFilePath();
        this.lineNumber = textDiv.getLineNumber();
        this.originalText = textDiv.getOriginalText();
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
    }

    @Override
    public PathType getPathType() {
        return pathType;
    }

    @Override
    public void setPathType(PathType pathType) {

    }

    @Override
    public String getRemark() {
        return alt;
    }

    @Override
    public void setRemark(String remark) {
        this.alt = remark;
    }

}
