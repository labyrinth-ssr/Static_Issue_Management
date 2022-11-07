package org.example;

import java.util.List;

public class Issues {
    Integer id;

    String key;
    String rule;
    String severity;
    String component;
    String project;
    Integer line;
    String hash;
    String textRange;
    String flows;
    String status;
    String message;
    String effort;
    String debt;
    String author;
    String tags;
    String transitions;
    String actions;
    String comments;
    String creationDate;
    String updateDate;
    String type;
    boolean quickFixAvailable;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTextRange() {
        return textRange;
    }

    public void setTextRange(String textRange) {
        this.textRange = textRange;
    }


    public String getFlows() {
        return flows;
    }

    public void setFlows(String flows) {
        this.flows = flows;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEffort() {
        return effort;
    }

    public void setEffort(String effort) {
        this.effort = effort;
    }

    public String getDebt() {
        return debt;
    }

    public void setDebt(String debt) {
        this.debt = debt;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTransitions() {
        return transitions;
    }

    public void setTransitions(String transitions) {
        this.transitions = transitions;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getQuickFixAvailable() {
        return quickFixAvailable;
    }

    public void setQuickFixAvailable(boolean quickFixAvailable) {
        this.quickFixAvailable = quickFixAvailable;
    }

    @Override
    public String toString() {
        return "Issues{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", rule='" + rule + '\'' +
                ", severity='" + severity + '\'' +
                ", component='" + component + '\'' +
                ", project='" + project + '\'' +
                ", line=" + line +
                ", hash='" + hash + '\'' +
                ", textRange='" + textRange + '\'' +
                ", flows='" + flows + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", effort='" + effort + '\'' +
                ", debt='" + debt + '\'' +
                ", author='" + author + '\'' +
                ", tags='" + tags + '\'' +
                ", transitions='" + transitions + '\'' +
                ", actions='" + actions + '\'' +
                ", comments='" + comments + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", type='" + type + '\'' +
                ", quickFixAvailable=" + quickFixAvailable +
                '}';
    }
}
