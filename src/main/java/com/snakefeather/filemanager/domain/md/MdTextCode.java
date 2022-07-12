package com.snakefeather.filemanager.domain.md;

import com.snakefeather.filemanager.domain.TextDiv;

import java.nio.file.Path;

public class MdTextCode extends TextDiv {

    // 结尾行号
    protected long endLineNumber;

    public MdTextCode(Path filePath, long lineNumber, String originalText, MsgTypeEnum textType) {
        super(filePath, lineNumber, originalText, textType);
        endLineNumber = lineNumber++;
    }

    public void add(String str){

    }
}
