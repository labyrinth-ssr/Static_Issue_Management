package org.example.SonarConfig;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;

public class SonarIssues {
    String id;
    @JSONField(name = "rule")
    String typeId;
    String severity;
    @JSONField(name = "component")
    String filePath;
    @JSONField(name = "project")
    String repoId;
    List<SonarLocation> location;//多地址对象
    String message;
    String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getRepoId() {
        return repoId;
    }

    public void setRepoId(String repoId) {
        this.repoId = repoId;
    }

    public List<SonarLocation> getLocation() {
        return location;
    }

    public void setLocation(List<SonarLocation> location) {
        this.location = location;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SonarIssueEntity{" +
                "id='" + id + '\'' +
                ", typeId='" + typeId + '\'' +
                ", severity='" + severity + '\'' +
                ", filePath='" + filePath + '\'' +
                ", repoId='" + repoId + '\'' +
                ", location=" + location.toString() +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
