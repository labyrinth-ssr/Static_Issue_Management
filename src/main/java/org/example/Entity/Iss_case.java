package org.example.Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Iss_case {
    String case_id;
//    String type_;
    String type_id;
    String commit_hash_new;
    String committer_new;
    String commit_hash_last;
    String commit_hash_disappear;
    String committer_disappear;
    Date time_disappear;
    Date create_time;
    Date update_time;
    String case_status;

//    public String getType_() {
//        return type_;
//    }
//
//    public void setType_(String type_) {
//        this.type_ = type_;
//    }

    public String getCase_id() {
        return case_id;
    }

    public void setCase_id(String case_id) {
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

    public String getCommitter_new() {
        return committer_new;
    }

    public void setCommitter_new(String committer_new) {
        this.committer_new = committer_new;
    }

    public String getCommit_hash_last() {
        return commit_hash_last;
    }

    public void setCommit_hash_last(String commit_hash_last) {
        this.commit_hash_last = commit_hash_last;
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

    public Date getTime_disappear() {
        return time_disappear;
    }

    public void setTime_disappear(Date time_disappear) {
        this.time_disappear = time_disappear;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public String getCase_status() {
        return case_status;
    }

    public void setCase_status(String case_status) {
        this.case_status = case_status;
    }

    public static Iss_case look_up_case(List<Iss_case> iss_caseList,String case_id){
        for (Iss_case iss_case:iss_caseList) {
            if (case_id == iss_case.case_id){
                return iss_case;
            }

        }
        return null;
    }

    public static String getUuidFromIssueCase(Iss_case iss_case,int i){
        String stringBuilder =iss_case.getCommit_hash_new()+iss_case.getCommit_hash_last()+ String.valueOf(i);
        return UUID.nameUUIDFromBytes(stringBuilder.getBytes()).toString();
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


