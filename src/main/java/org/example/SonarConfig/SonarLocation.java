package org.example.SonarConfig;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.UUID;

public class SonarLocation {
    String startLine;
    String endLine;
    String startOffset;
    String endOffset;

    public String getStartLine() {
        return startLine;
    }

    public void setStartLine(String startLine) {
        this.startLine = startLine;
    }

    public String getEndLine() {
        return endLine;
    }

    public void setEndLine(String endLine) {
        this.endLine = endLine;
    }

    public String getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(String startOffset) {
        this.startOffset = startOffset;
    }

    public String getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(String endOffset) {
        this.endOffset = endOffset;
    }

    @Override
    public String toString() {
        return "[" +
                startLine +
                ", " + endLine +
                ", " + startOffset +
                ", " + endOffset +
                ']';
    }

    public String getUuid(){
        return UUID.nameUUIDFromBytes((startLine +" "+ endLine +" "+ startOffset +" "+ endOffset).getBytes()).toString();
    }
}
