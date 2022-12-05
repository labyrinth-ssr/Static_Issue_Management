package org.example.QueryUseEntity;

public class DefectTypeEntity {
    String type_id;
    String description;
    String average_exist_duration;

    @Override
    public String toString() {
        return "\t" +
                "type_id='" + type_id + '\'' +
                ", description='" + description + '\'' +
                ", average_exist_duration='" + average_exist_duration + '\'';
    }

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

    public String getAverage_exist_duration() {
        return average_exist_duration;
    }

    public void setAverage_exist_duration(String average_exist_duration) {
        this.average_exist_duration = average_exist_duration;
    }
}
