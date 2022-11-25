package org.example.Entity;

public class Iss_match {
    String inst_id;
    String parent_inst_id;
    String commit_hash;
    String parent_commit_hash;
    String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

