package org.example.Entity;

import org.example.SonarConfig.SonarIssues;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Iss_instance {
    String inst_id;
    String type_id;
    String commit_id;
    String parent_inst_id;
    String case_id;
    String file_path;
//    Date commit_time;
//    String committer;
//    String file_path;
//    String description;

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

    public String getCommit_id() {
        return commit_id;
    }

    public void setCommit_id(String commit_id) {
        this.commit_id = commit_id;
    }
    public String getParent_inst_id() {
        return parent_inst_id;
    }

    public void setParent_inst_id(String parent_inst_id) {
        this.parent_inst_id = parent_inst_id;
    }

    public String getCase_id() {
        return case_id;
    }

    public void setCase_id(String case_id) {
        this.case_id = case_id;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }


    public static void setInstance(List<Iss_instance> res,List<SonarIssues> sonarIssues, Commit commit){
            for (SonarIssues sonarIssues1:sonarIssues) {
                Iss_instance iss_instance=new Iss_instance();
                iss_instance.inst_id = sonarIssues1.getId();
                iss_instance.commit_id=commit.getCommit_id();
                iss_instance.type_id=sonarIssues1.getTypeId() ;
                res.add(iss_instance);
            }
    }

    public Iss_instance(String inst_id, String type_id, String commit_id, String parent_inst_id, String case_id, String file_path) {
        this.inst_id = inst_id;
        this.type_id = type_id;
        this.commit_id = commit_id;
        this.parent_inst_id = parent_inst_id;
        this.case_id = case_id;
        this.file_path = file_path;
    }

    public Iss_instance() {
    }

    @Override
    public String toString() {
        return "Iss_instance{" +
                "inst_id='" + inst_id + '\'' +
                ", type_id='" + type_id + '\'' +
                ", commit_id='" + commit_id + '\'' +
                ", parent_inst_id='" + parent_inst_id + '\'' +
                ", case_id='" + case_id + '\'' +
                ", file_path='" + file_path + '\'' +
                '}';
    }
};
