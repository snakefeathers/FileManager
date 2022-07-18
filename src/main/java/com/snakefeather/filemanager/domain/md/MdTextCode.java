package com.snakefeather.filemanager.domain.md;

import com.snakefeather.filemanager.domain.TextDiv;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MdTextCode extends TextDiv {


    private List<TextDiv> childs = new ArrayList<>();
    // 开始行号
    protected long startLineCount;
    // 结尾行号
    protected long endLineCount;


    public MdTextCode(Path filePath, long lineNumber, String originalText) {
        super(filePath, lineNumber, originalText, TextDiv.MsgTypeEnum.CODE);
        startLineCount = lineNumber;
        endLineCount = lineNumber;
        childs.add(new TextDiv(filePath, lineNumber, originalText));
    }

    public void add(String str) {
        childs.add(new TextDiv(super.filePath, ++endLineCount, originalText));
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (childs.size() > 0) {
            stringBuilder.append(childs.get(0).toString());
            for (int i = 1; i < childs.size(); i++) {
                stringBuilder.append("\n" + childs.get(i).toString());
            }
        }
        return stringBuilder.toString();
    }
}
