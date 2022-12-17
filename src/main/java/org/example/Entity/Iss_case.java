package org.example.Entity;

import SonarConfig.SonarIssues;
import SonarConfig.SonarLocation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Iss_case {
    String case_id;
    String type_id;
    String commit_id_new;
    String commit_id_last;
    String commit_id_disappear;
    String case_status;

    public String getCommit_id_disappear() {
        return commit_id_disappear;
    }

    public void setCommit_id_disappear(String commit_id_disappear) {
        this.commit_id_disappear = commit_id_disappear;
    }

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

    public String getCommit_id_last() {
        return commit_id_last;
    }

    public void setCommit_id_last(String commit_id_last) {
        this.commit_id_last = commit_id_last;
    }

    public String getCommit_id_new() {
        return commit_id_new;
    }

    public void setCommit_id_new(String commit_id_new) {
        this.commit_id_new = commit_id_new;
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
        String stringBuilder =iss_case.getCommit_id_new()+iss_case.getCommit_id_last()+ String.valueOf(i);
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


