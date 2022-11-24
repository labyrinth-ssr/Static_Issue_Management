package org.example.Entity;

public class Commit {
    String commit_hash;
    String committer;
    String commit_time;
    String commit_msg;
    String committer_email;
    String parent_commit_hash;

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
        return "Commit{" +
                "commit_hash='" + commit_hash + '\'' +
                ", committer='" + committer + '\'' +
                ", commit_time='" + commit_time + '\'' +
                ", commit_msg='" + commit_msg + '\'' +
                ", committer_email='" + committer_email + '\'' +
                ", parent_commit_hash='" + parent_commit_hash + '\'' +
                "}\n";
    }
}
