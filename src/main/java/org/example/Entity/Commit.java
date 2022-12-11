package org.example.Entity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Commit implements Cloneable{
    String commit_hash;
    String repo_path;
    String committer;
    Date commit_time;
    String commit_msg;
    String committer_email;
    String parent_commit_hash;

    public String getRepo_path() {
        return repo_path;
    }

    public void setRepo_path(String repo_path) {
        this.repo_path = repo_path;
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

    public Date getCommit_time() {
        return commit_time;
    }

    public void setCommit_time(LocalDateTime commit_time) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        this.commit_time = Date.from(commit_time.atZone(defaultZoneId).toInstant());
    }

    public void setCommit_time_(Date commit_time) {
        this.commit_time = commit_time;
    }

    public String getCommit_msg() {
        return commit_msg;
    }

    public void setCommit_msg(String commit_msg) {
        this.commit_msg = commit_msg;
    }

    public String getCommitter_email() {
        return committer_email;
    }

    public void setCommitter_email(String committer_email) {
        this.committer_email = committer_email;
    }

    public String getParent_commit_hash() {
        return parent_commit_hash;
    }

    public void setParent_commit_hash(String parent_commit_hash) {
        this.parent_commit_hash = parent_commit_hash;
    }

    @Override
    public String toString() {
        return "commit_hash='" + commit_hash + '\'' +
                ", commit_time='" + commit_time + '\'' +
                ", commit_msg='" + commit_msg + '\'' +
                ", committer='" + committer + '\'' +
                ", committer_email='" + committer_email + '\'' +
                ", parent_commit_hash='" + parent_commit_hash + '\'' +
                ", repo_path='" + repo_path + '\'';
    }

    @Override
    public Commit clone() throws CloneNotSupportedException {
        return (Commit)super.clone();
    }

    public void setCommit(Commit commit,String repo_path){
        this.commit_hash=commit.getCommit_hash();
        this.commit_msg=commit.getCommit_msg();
        this.committer=commit.getCommitter();
        this.commit_time=commit.getCommit_time();
        this.parent_commit_hash=commit.getParent_commit_hash();
        this.repo_path=repo_path;
        this.committer_email=commit.getCommitter_email();
    }
}
