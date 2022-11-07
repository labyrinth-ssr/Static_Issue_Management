package org.example.Entity;

public class Issue {
    String key;
    String rule;
    String severity;
    String component;
    String project;
    String hash;
    String type;
    String message;
    Location location;
    String create_date;
    String update_date;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public  static class Location{
        int start_line;
        int end_line;
        int start_offset;
        int end_offset;

        public int getStart_line() {
            return start_line;
        }

        public void setStart_line(int start_line) {
            this.start_line = start_line;
        }

        public int getEnd_line() {
            return end_line;
        }

        public void setEnd_line(int end_line) {
            this.end_line = end_line;
        }

        public int getStart_offset() {
            return start_offset;
        }

        public void setStart_offset(int start_offset) {
            this.start_offset = start_offset;
        }

        public int getEnd_offset() {
            return end_offset;
        }

        public void setEnd_offset(int end_offset) {
            this.end_offset = end_offset;
        }
    }
}
