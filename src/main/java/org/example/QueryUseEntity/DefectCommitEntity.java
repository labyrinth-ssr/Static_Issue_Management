package org.example.QueryUseEntity;

public class DefectCommitEntity {
    String commit_hash;
    String committer;
    String commit_time;
    String commit_msg;

    @Override
    public String toString() {
        return  "commit_hash='" + commit_hash + '\'' +
                ", committer='" + committer + '\'' +
                ", commit_time='" + commit_time + '\'' +
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

    public String getCommit_time() {
        return commit_time;
    }

    public void setCommit_time(String commit_time) {
        this.commit_time = commit_time;
    }

    public String getCommit_msg() {
        return commit_msg;
    }

    public void setCommit_msg(String commit_msg) {
        this.commit_msg = commit_msg;
    }
}
