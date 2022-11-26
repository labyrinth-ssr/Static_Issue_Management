package org.example.Entity;

import SonarConfig.SonarIssues;

import java.util.ArrayList;
import java.util.List;

public class Iss_case {
    Integer case_id;
    String type_id;
    String commit_hash_new;
    String commiter_new;
    String time_new;
    String commit_hash_last;
    String filepath_last;
    String commit_hash_disappear;
    String committer_disappear;
    String time_disappear;
    String create_time;
    String update_time;
    String case_status;

    public Integer getCase_id() {
        return case_id;
    }

    public void setCase_id(Integer case_id) {
        this.case_id = case_id;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getCommit_hash_new() {
        return commit_hash_new;
    }

    public void setCommit_hash_new(String commit_hash_new) {
        this.commit_hash_new = commit_hash_new;
    }

    public String getCommiter_new() {
        return commiter_new;
    }

    public void setCommiter_new(String commiter_new) {
        this.commiter_new = commiter_new;
    }

    public String getTime_new() {
        return time_new;
    }

    public void setTime_new(String time_new) {
        this.time_new = time_new;
    }

    public String getCommit_hash_last() {
        return commit_hash_last;
    }

    public void setCommit_hash_last(String commit_hash_last) {
        this.commit_hash_last = commit_hash_last;
    }

    public String getFilepath_last() {
        return filepath_last;
    }

    public void setFilepath_last(String filepath_last) {
        this.filepath_last = filepath_last;
    }

    public String getCommit_hash_disappear() {
        return commit_hash_disappear;
    }

    public void setCommitter_disappear(String committer_disappear) {
        this.committer_disappear = committer_disappear;
    }

    public String getCommitter_disappear() {
        return committer_disappear;
    }

    public void setCommit_hash_disappear(String commit_hash_disappear) {
        this.commit_hash_disappear = commit_hash_disappear;
    }

    public String getTime_disappear() {
        return time_disappear;
    }

    public void setTime_disappear(String time_disappear) {
        this.time_disappear = time_disappear;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getCase_status() {
        return case_status;
    }

    public void setCase_status(String case_status) {
        this.case_status = case_status;
    }

//    public static List<Iss_case> setIss_case(List<Iss_match>matchList, List<SonarIssues>sonarIssuesList){
//        List<Iss_case> iss_caseList = new ArrayList<>();
//        for (Iss_match iss_match:matchList) {
//            Iss_case iss_case = new Iss_case();
//            iss_case.setCase_status(iss_case.getCase_status());
//            iss_case.setType_id(iss_match.getInst_id());
//
//        }
//    }
}


