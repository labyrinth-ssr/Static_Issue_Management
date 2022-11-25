package org.example.Entity;

public class Iss_match {
    Integer inst_id;
    Integer parent_inst_id;
    String commit_hash;
    String parent_commit_hash;
    String status;
    Integer case_id;

    public Integer getInst_id() {
        return inst_id;
    }

    public void setInst_id(Integer inst_id) {
        this.inst_id = inst_id;
    }

    public Integer getParent_inst_id() {
        return parent_inst_id;
    }

    public void setParent_inst_id(Integer parent_inst_id) {
        this.parent_inst_id = parent_inst_id;
    }

    public Integer getCase_id() {
        return case_id;
    }

    public void setCase_id(Integer case_id) {
        this.case_id = case_id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

