package org.example.Entity;

public class Iss_location {
    Integer inst_id;
    String class_;
    String method;
    Integer start_line;
    Integer end_line;
    Integer start_col;
    Integer end_col;
    Integer line_offset;
    String code;
    int file_id;

    public Integer getInst_id() {
        return inst_id;
    }

    public void setFile_id(int file_id) {
        this.file_id = file_id;
    }

    public String getClass_() {
        return class_;
    }

    public void setClass_(String class_) {
        this.class_ = class_;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getStart_line() {
        return start_line;
    }

    public void setStart_line(Integer start_line) {
        this.start_line = start_line;
    }

    public Integer getEnd_line() {
        return end_line;
    }

    public void setEnd_line(Integer end_line) {
        this.end_line = end_line;
    }

    public Integer getStart_col() {
        return start_col;
    }

    public void setStart_col(Integer start_col) {
        this.start_col = start_col;
    }

    public Integer getEnd_col() {
        return end_col;
    }

    public void setEnd_col(Integer end_col) {
        this.end_col = end_col;
    }

    public Integer getLine_offset() {
        return line_offset;
    }

    public void setLine_offset(Integer line_offset) {
        this.line_offset = line_offset;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getFile_id() {
        return file_id;
    }

    public void setInst_id(Integer inst_id) {
        this.inst_id = inst_id;
    }
}
