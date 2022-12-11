package org.example.QueryUseEntity;

import java.math.BigDecimal;

public class DefectEntity {
    String exist_duration;
    Long case_id;
    String inst_id;
    String file_path;
    Long start_line;
    Long end_line;
    Long start_col;
    Long end_col;
    String class_;
    String method;
    String code;

    @Override
    public String toString() {
        return "\t\t" +
                "inst_id='" + inst_id + '\'' + ", case_id='" + case_id + '\'' + ", exist_duration='" + exist_duration +'\'' +
                "\n\t\t\t" +
                "file_path='" + file_path + '\'' +
                ", start_line='" + start_line + '\'' +
                ", end_line='" + end_line + '\'' +
                ", start_col='" + start_col + '\'' +
                ", end_col='" + end_col + '\'' +
                "\n\t\t\t" +
                "class_='" + class_ + '\'' +
                ", method='" + method + '\'' +
                "\n\t\t\t" +
                "code='" + code + '\'';
    }

    public String getInst_id() {
        return inst_id;
    }

    public void setInst_id(String inst_id) {
        this.inst_id = inst_id;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public Long getStart_line() {
        return start_line;
    }

    public void setStart_line(Long start_line) {
        this.start_line = start_line;
    }

    public Long getEnd_line() {
        return end_line;
    }

    public void setEnd_line(Long end_line) {
        this.end_line = end_line;
    }

    public Long getStart_col() {
        return start_col;
    }

    public void setStart_col(Long start_col) {
        this.start_col = start_col;
    }

    public Long getEnd_col() {
        return end_col;
    }

    public void setEnd_col(Long end_col) {
        this.end_col = end_col;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExist_duration() {
        return exist_duration;
    }

    public void setExist_duration(Long exist_duration) {
        long diffSeconds = exist_duration % 60;
        long diffMinutes = exist_duration / (60) % 60;
        long diffHours = exist_duration / (60 * 60) % 24;
        long diffDays = exist_duration / (24 * 60 * 60);
        StringBuffer s = new StringBuffer();
        if(diffDays > 0) {
            s.append(diffDays).append("天");
            s.append(diffHours).append("时");
        }else if(diffHours > 0) s.append(diffHours).append("时");
        s.append(diffMinutes).append("分").append(diffSeconds).append("秒");
        this.exist_duration = s.toString();
    }

    public Long getCase_id() {
        return case_id;
    }

    public void setCase_id(Long case_id) {
        this.case_id = case_id;
    }
}
