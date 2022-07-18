package com.snakefeather.filemanager.domain;

public interface TextOutput {

    //  图片链接在本地需要用绝对地址或相对地址。  在web需要网络地址。
    String toWebHtml();

    String toFileHtml();
}
