package org.example.Entity;

import org.example.SonarConfig.SonarIssues;
import org.example.SonarConfig.SonarLocation;
import cn.edu.fudan.issue.util.AstParserUtil;

import java.util.List;
import java.util.UUID;

public class Iss_location {
    String location_id;
    String class_;
    String method;
    Integer start_line;
    Integer end_line;
    Integer start_col;
    Integer end_col;
    String code;


    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


//    public void setInst_id(String inst_id) {
//        this.inst_id = inst_id;
//    }

    public static String getUuidFromLocation(Iss_location location){
        String stringBuilder =""+location.getStart_line()+location.getEnd_line()+location.getStart_col()+location.getEnd_col()+System.currentTimeMillis();
        return UUID.nameUUIDFromBytes(stringBuilder.getBytes()).toString();
    }


    public Iss_location(String class_, String method, Integer start_line, Integer end_line, Integer start_col, Integer end_col, String code, String file_path) {
        String stringBuilder =file_path+start_line+end_line+start_col+end_col + System.currentTimeMillis() + Math.random();
        this.location_id = UUID.nameUUIDFromBytes(stringBuilder.getBytes()).toString();
        this.class_ = class_;
        this.method = method;
        this.start_line = start_line;
        this.end_line = end_line;
        this.start_col = start_col;
        this.end_col = end_col;
        this.code = code;
    }

    public Iss_location() {
    }


    @Override
    public String toString() {
        return "Iss_location{" +
                "location_id='" + location_id + '\'' +
                ", class_='" + class_ + '\'' +
                ", method='" + method + '\'' +
                ", start_line=" + start_line +
                ", end_line=" + end_line +
                ", start_col=" + start_col +
                ", end_col=" + end_col +
                ", code='" + code + '\'' +
                '}';
    }
}
