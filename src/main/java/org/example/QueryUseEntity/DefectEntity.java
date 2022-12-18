package org.example.QueryUseEntity;

import java.math.BigDecimal;

public class DefectEntity {
    String exist_duration;
    String case_id;
    String inst_id;
    String file_path;
    String class_;
    String method;
    String code;

    Long start_line;
    Long start_col;
    String status;


    public String toString(boolean list) {
        return list ? "\t\t" +
                "inst_id='" + inst_id + '\'' + ", case_id='" + case_id + '\'' + ", exist_duration='" + exist_duration +'\'' +
                "\n\t\t\t" +
                "file_path='" + file_path + '\'' + (start_line == null ?"":" 第"+start_line+"行"+start_col+"列")+
                "\n\t\t\t" +
                "class='" + class_ + '\'' +
                ", method='" + method + '\'' +
                "\n\t\t\t" + status + "\n\t\t\t" +
                "code=\n\033[31m" + code + "\033[m"
                :
                "\t\t" + "inst_id='" + inst_id + '\'' + ", status='" + status + '\'' + ", exist_duration='" + exist_duration +'\''+", file_path='" + file_path + '\'' + (start_line == null ?"":" 第"+start_line+"行"+start_col+"列");
    }
    public String getExist_duration() {
        return exist_duration;
    }

    public void setExist_duration(Long exist_duration) {
        long time_duration = exist_duration.longValue();
        long diffSeconds = time_duration % 60;
        long diffMinutes = time_duration / (60) % 60;
        long diffHours = time_duration / (60 * 60) % 24;
        long diffDays = time_duration / (24 * 60 * 60);
        StringBuffer s = new StringBuffer();
        if(diffDays > 0) {
            s.append(diffDays).append("天");
            s.append(diffHours).append("时");
        }else if(diffHours > 0) s.append(diffHours).append("时");
        s.append(diffMinutes).append("分").append(diffSeconds).append("秒");
        this.exist_duration = s.toString();
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


    public String getCase_id() {
        return case_id;
    }

    public void setCase_id(String case_id) {
        this.case_id = case_id;
    }

    public void setExist_duration(String exist_duration) {
        this.exist_duration = exist_duration;
    }

    public Long getStart_line() {
        return start_line;
    }

    public void setStart_line(Long start_line) {
        this.start_line = start_line;
    }

    public Long getStart_col() {
        return start_col;
    }

    public void setStart_col(Long start_col) {
        this.start_col = start_col;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
