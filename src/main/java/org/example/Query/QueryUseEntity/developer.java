package org.example.Query.QueryUseEntity;

public class developer {
    String committer;
    String committer_email;

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public String getCommitter_email() {
        return committer_email;
    }

    public void setCommitter_email(String committer_email) {
        this.committer_email = committer_email;
    }

    @Override
    public String toString() {
        return "committer='" + committer + '\'' +
                ", committer_email='" + committer_email + '\'';
    }
}
