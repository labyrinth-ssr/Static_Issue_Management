package org.example.Query.Value;

public class TimeValue {
    String time;
    public String getTime() {
        return time;
    }

    public void setTime(Long time) {
        long time_duration = time;
        long diffSeconds = time_duration % 60;
        long diffMinutes = time_duration / (60) % 60;
        long diffHours = time_duration / (60 * 60) % 24;
        long diffDays = time_duration / (24 * 60 * 60);
        StringBuilder s = new StringBuilder();
        if(diffDays > 0) {
            s.append(diffDays).append("天");
            s.append(diffHours).append("时");
        }else if(diffHours > 0) s.append(diffHours).append("时");
        s.append(diffMinutes).append("分").append(diffSeconds).append("秒");
        this.time = s.toString();
    }
}
