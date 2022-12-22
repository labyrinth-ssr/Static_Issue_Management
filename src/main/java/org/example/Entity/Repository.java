package org.example.Entity;

public class Repository {

    String repo_path;

    public String getRepo_path() {
        return repo_path;
    }

    public void setRepo_path(String repo_path) {
        this.repo_path = repo_path;
    }

    public String pathToName(){
        String[] temp = repo_path.split("/");
        return temp[temp.length-1];
    }

    @Override
    public String toString() {
        return "repo_name='" + pathToName() + '\'' +
                ", path='" + repo_path + '\'';
    }

    public Repository(String repo_path) {
        this.repo_path = repo_path;
    }

    public Repository() {
    }
}
