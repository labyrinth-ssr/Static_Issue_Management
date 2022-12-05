package org.example;

public class want {
    String repo_name;

    public String getRepo_name() {
        return repo_name;
    }

    public void setRepo_name(String repo_name) {
        this.repo_name = repo_name;
    }

    @Override
    public String toString() {
        return "want{" +
                "repo_name='" + repo_name + '\'' +
                '}';
    }
}
