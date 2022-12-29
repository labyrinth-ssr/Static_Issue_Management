package org.example.Entity;

public class Commit_Inst {
    String commit_id;
    String inst_id;

    public String getInst_id() {
        return inst_id;
    }

    public void setInst_id(String inst_id) {
        this.inst_id = inst_id;
    }

    public String getCommit_id() {
        return commit_id;
    }

    public void setCommit_id(String commit_id) {
        this.commit_id = commit_id;
    }

    public Commit_Inst(String inst_id, String commit_id) {
        this.inst_id = inst_id;
        this.commit_id = commit_id;
    }

    public Commit_Inst(){

    }
}


