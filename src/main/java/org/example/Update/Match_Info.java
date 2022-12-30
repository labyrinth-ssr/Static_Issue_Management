package org.example.Update;

public class Match_Info {
    String case_id;
    String case_status;
    String commit_id_last;
    String commit_id_disappear;
    String inst_id_last;
    String type_id;
    String message;
    String file_name;

    public String getCase_id() {
        return case_id;
    }

    public void setCase_id(String case_id) {
        this.case_id = case_id;
    }

    public String getCase_status() {
        return case_status;
    }

    public void setCase_status(String case_status) {
        this.case_status = case_status;
    }

    public String getCommit_id_last() {
        return commit_id_last;
    }

    public void setCommit_id_last(String commit_id_last) {
        this.commit_id_last = commit_id_last;
    }

    public String getCommit_id_disappear() {
        return commit_id_disappear;
    }

    public void setCommit_id_disappear(String commit_id_disappear) {
        this.commit_id_disappear = commit_id_disappear;
    }

    public String getInst_id_last() {
        return inst_id_last;
    }

    public void setInst_id_last(String inst_id_last) {
        this.inst_id_last = inst_id_last;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Match_Info(String case_id, String case_status, String commit_id_last, String commit_id_disappear, String inst_id_last, String type_id, String message, String file_name) {
        this.case_id = case_id;
        this.case_status = case_status;
        this.commit_id_last = commit_id_last;
        this.commit_id_disappear = commit_id_disappear;
        this.inst_id_last = inst_id_last;
        this.type_id = type_id;
        this.message = message;
        this.file_name = file_name;
    }

    public Match_Info() {
    }
}
