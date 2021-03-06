package com.snakefeather.filemanager.domain;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 采用 递归组合 的结构。
 * 目前来看，日常元素是一层，代码块，流程图可能用到二层，递归组合有点多余。
 * 不过在不影响开发的基础上，增加了可扩展性，是值得的。
 */
public  class TextDiv {

    //#region   文本位置信息

    //  信息所属文件
    protected Path filePath = null;
    //  信息行号 —— 起始行号
    protected long lineNumber = -1;
    //  信息原文
    protected String originalText = null;
    //  信息类型
    protected TextDiv.MsgTypeEnum textType = null;
    //  主要信息
    protected String primaryText = null;


    //   子类引用    //  为了针对代码块，流程图.采用递归组合
    protected List<TextDiv> childs = new ArrayList<>();
    //   父类引用   //  存储父类的引用
    protected TextDiv parent = null;
    //   容器的引用
    protected FileTextList fileList = null;
    //   占几行
    protected int lineCount = 0;

    //#endregion

    public TextDiv() {
    }

    public TextDiv(Path filePath, long lineNumber, String originalText, MsgTypeEnum textType) {
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.originalText = originalText;
        this.textType = textType;
        this.primaryText = originalText;        //  抽象类就用 原文 来表示 主要信息 。
    }
    public TextDiv(Path filePath,  FileTextList fileList ,long lineNumber, String originalText, MsgTypeEnum textType) {
        this.filePath = filePath;
        this.fileList = fileList;
        this.lineNumber = lineNumber;
        this.originalText = originalText;
        this.textType = textType;
        this.primaryText = originalText;        //  抽象类就用 原文 来表示 主要信息 。
    }
    // 装饰器 使用
    public TextDiv(Path filePath, long lineNumber, String originalText) {
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.originalText = originalText;
        this.primaryText = originalText;        //  抽象类就用 原文 来表示 主要信息 。
    }

    //   信息枚举类型
    public enum MsgTypeEnum {
        IMG,            //  ![img](photos/535.jpg)
        LABEL_PHOTO,      //  <img src="photos\依赖倒转原则.png" style="zoom:80%;" />
        CODE,           //  代码块
        ORDINARY;       //  普通文本
    }

    //  暂时有些多余。先写着。
    //  插入元素
    public void insert(TextDiv textDiv) {
        childs.add(textDiv);
        lineCount += textDiv.lineCount;
    }

    public void insert(TextDiv textDiv, int index) {
        childs.set(index, textDiv);
        lineCount += textDiv.lineCount;
    }

    //  移除元素
    public TextDiv remove(TextDiv textDiv) {
        int ind = childs.indexOf(textDiv);
        if (ind >= 0) {
            TextDiv temp = childs.get(ind);
            childs.remove(ind);
            lineCount -= textDiv.lineCount;
            return temp;
        } else {
            return null;
        }
    }
    //  获取要返回给前端的文本。    图片路径啥的可能要预处理，留下可修改的空间。
    public String toWebStr(){
        return originalText;
    }
    //  获取要写入到文件的文本
    public String toLocalStr(){
        return originalText;
    }


    //#region   通用方法  Getter() 与 toString()

    public Path getFilePath() {
        return filePath;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public MsgTypeEnum getTextType() {
        return textType;
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public List<TextDiv> getChilds() {
        return childs;
    }

    public TextDiv getParent() {
        return parent;
    }

    public int getLineCount() {
        return lineCount;
    }

    public FileTextList getFileList() {
        return fileList;
    }


    @Override
    public String toString() {
        return originalText;
    }

    //#endregion

}
