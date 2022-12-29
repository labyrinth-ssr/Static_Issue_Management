package org.example.Entity;


import org.example.SonarConfig.SonarIssues;

import java.util.List;

public class SonarRules {
    String type_id;
    String description;
    String severity;
    String lang;
    String type;

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static void setSonarRules(List<SonarRules> sonarRules, List<SonarIssues> sonarIssues){
        for (SonarIssues sonarIssue : sonarIssues) {
            SonarRules sonarRule = new SonarRules();
            sonarRule.setType_id(sonarIssue.getTypeId());
            sonarRule.setDescription(sonarIssue.getMessage());
            sonarRule.setLang("java");
            sonarRule.setSeverity(sonarIssue.getSeverity());
            sonarRule.setType(sonarIssue.getType());

            sonarRules.add(sonarRule);

        }
    }

    public SonarRules(String id, String description, String severity, String lang, String type) {
        this.type_id= id;
        this.description = description;
        this.severity = severity;
        this.lang = lang;
        this.type = type;
    }

    public SonarRules() {
    }
}
