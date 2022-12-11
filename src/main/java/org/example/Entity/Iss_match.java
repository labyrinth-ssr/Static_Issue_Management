package org.example.Entity;

import java.util.List;

public class Iss_match {
    String inst_id;
    String parent_inst_id;
    String case_id;
    String commit_hash;
    String parent_commit_hash;

    public String getCase_id() {
        return case_id;
    }

    public void setCase_id(String case_id) {
        this.case_id = case_id;
    }

    public String getInst_id() {
        return inst_id;
    }

    public void setInst_id(String inst_id) {
        this.inst_id = inst_id;
    }

    public String getParent_inst_id() {
        return parent_inst_id;
    }

    public void setParent_inst_id(String parent_inst_id) {
        this.parent_inst_id = parent_inst_id;
    }

    public String getCommit_hash() {
        return commit_hash;
    }

    public void setCommit_hash(String commit_hash) {
        this.commit_hash = commit_hash;
    }

    public String getParent_commit_hash() {
        return parent_commit_hash;
    }

    public void setParent_commit_hash(String parent_commit_hash) {
        this.parent_commit_hash = parent_commit_hash;
    }

    public static String instIdLookUpCaseId(List<Iss_match>iss_matchList,String inst_id){
        for (Iss_match iss_match:iss_matchList) {
            if (iss_match.inst_id==inst_id){
                return iss_match.case_id;
            }
        }
        System.out.println("given case id not found case id");
        return "";
    }
}

