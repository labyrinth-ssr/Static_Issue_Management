package org.example.Entity;

import org.example.SonarConfig.SonarIssues;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Iss_instance {
    String inst_id;
    String type_id;
    String commit_hash;
    Date commit_time;
    String committer;
    String file_path;
    String description;

    public void setInst_id(String inst_id) {
        this.inst_id = inst_id;
    }

    public String getInst_id() {
        return inst_id;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getCommit_hash() {
        return commit_hash;
    }

    public void setCommit_hash(String commit_hash) {
        this.commit_hash = commit_hash;
    }

    public Date getCommit_time() {
        return commit_time;
    }

    public void setCommit_time(Date commit_time) {
        this.commit_time = commit_time;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static void setInstance(List<Iss_instance> res,List<SonarIssues> sonarIssues, Commit commit){
            for (SonarIssues sonarIssues1:sonarIssues) {
                Iss_instance iss_instance=new Iss_instance();
                iss_instance.inst_id = sonarIssues1.getId();
                iss_instance.commit_hash=commit.getCommit_hash();
                iss_instance.commit_time=commit.getCommit_time();
                iss_instance.committer=commit.getCommitter();
                iss_instance.file_path=sonarIssues1.getFilePath();
                iss_instance.description=sonarIssues1.getMessage();
                iss_instance.type_id=sonarIssues1.getTypeId();
                res.add(iss_instance);
            }
    }
};
