package org.example.QueryUseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DefectCommitEntity {
    String commit_hash;
    String committer;
    LocalDateTime commit_time;
    String commit_msg;

    @Override
    public String toString() {
        return  "commit_hash='" + commit_hash + '\'' +
                ", committer='" + committer + '\'' +
                ", commit_time='" + commit_time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + '\'' +
                ", commit_message='" + commit_msg + '\'';
    }

    public String getCommit_hash() {
        return commit_hash;
    }

    public void setCommit_hash(String commit_hash) {
        this.commit_hash = commit_hash;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public void setCommit_time(LocalDateTime commit_time) {
        this.commit_time = commit_time;
    }


    public String getCommit_msg() {
        return commit_msg;
    }

    public void setCommit_msg(String commit_msg) {
        this.commit_msg = commit_msg;
    }
}
