package org.example.Entity;

public class Repository {

    String repo_name;
    String path;


    public String getRepo_name() {
        return repo_name;
    }

    public void setRepo_name(String repo_name) {
        this.repo_name = repo_name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void pathToName(){
        String[] temp = path.split("\\\\");
        this.repo_name=temp[temp.length-1];
    }
}
