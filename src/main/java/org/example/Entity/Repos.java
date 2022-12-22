package org.example.Entity;

public class Repos {
    String repo_path;
    String latest_commit_id;
    Long commit_num;

    public String getRepo_path() {
        return repo_path;
    }

    public void setRepo_path(String repo_path) {
        this.repo_path = repo_path;
    }

    public String getLatest_commit_id() {
        return latest_commit_id;
    }

    public void setLatest_commit_id(String latest_commit_id) {
        this.latest_commit_id = latest_commit_id;
    }

    public Long getCommit_num() {
        return commit_num == null?0:commit_num;
    }

    public void setCommit_num(Long commit_num) {
        this.commit_num = commit_num;
    }

    public Repos() {
    }

    public Repos(Commit c){
        repo_path = c.getRepo_path();
        latest_commit_id = c.getCommit_id();
    }
}
