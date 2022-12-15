package org.example.Entity;

import org.example.SonarConfig.SonarIssues;
import org.example.SonarConfig.SonarLocation;
import cn.edu.fudan.issue.util.AstParserUtil;

import java.util.List;
import java.util.UUID;

public class Iss_location {
    String location_id;
//    String inst_id;
    String class_;
    String method;
    Integer start_line;
    Integer end_line;
    Integer start_col;
    Integer end_col;
    Integer line_offset;
    String code;
    String file_path;

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

//    public String getInst_id() {
//        return inst_id;
//    }

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

    public String getFile_path() {
        return file_path;
    }

//    public void setInst_id(String inst_id) {
//        this.inst_id = inst_id;
//    }

    public static String getUuidFromLocation(Iss_location location){
        String stringBuilder =location.getFile_path()+location.getStart_line()+location.getEnd_line()+location.getStart_col()+location.getEnd_col();
        return UUID.nameUUIDFromBytes(stringBuilder.getBytes()).toString();
    }

    public static void setLocation (List<Iss_location>res,List<SonarIssues> sonarIssues){
        for (SonarIssues sonarIssues1:sonarIssues) {
            for (SonarLocation location:sonarIssues1.getLocation()) {
                Iss_location iss_location = new Iss_location();
                iss_location.setCode("");
//                AstParserUtil.getAllClassNamesInFile()
                iss_location.setClass_("");
                iss_location.setStart_line(Integer.parseInt(location.getStartLine()));
                iss_location.setEnd_line(Integer.parseInt(location.getEndLine()));
                iss_location.setStart_col(Integer.parseInt(location.getStartOffset()));
                iss_location.setEnd_col(Integer.parseInt(location.getEndOffset()));
                iss_location.setFile_path(sonarIssues1.getFilePath().split(":")[1].replace("/","\\"));
//                iss_location.setInst_id(sonarIssues1.getId());
                iss_location.setMethod("");
                iss_location.setLocation_id(getUuidFromLocation(iss_location));
                res.add(iss_location);
            }
            }
        }
}
