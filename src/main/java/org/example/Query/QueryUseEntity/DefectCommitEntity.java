package org.example.Query.QueryUseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DefectCommitEntity {
    String commit_hash;
    String committer;
    LocalDateTime commit_time;
    String commit_msg;

    String average_exist_duration;

    Long in_count;
    Long out_count;

    @Override
    public String toString() {
        return  "\033[33mcommit_hash='" + commit_hash + '\'' +
                ", committer='" + committer + '\'' +
                ", commit_time='" + commit_time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + '\'' +
                ", commit_message='" + commit_msg + '\''+
                ", 引入缺陷数:" + in_count +
                ", 解决缺陷数:" + out_count+"\033[m";
    }

    public String getCommit_hash() {
        return commit_hash;
    }

    public void setCommit_hash(String commit_hash) {
        this.commit_hash = commit_hash;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public void setCommit_time(LocalDateTime commit_time) {
        this.commit_time = commit_time;
    }


    public String getCommit_msg() {
        return commit_msg;
    }

    public void setCommit_msg(String commit_msg) {
        this.commit_msg = commit_msg;
    }

    public String getAverage_exist_duration() {
        return average_exist_duration;
    }

    public void setAverage_exist_duration(BigDecimal average_exist_duration) {
        long time_duration = average_exist_duration.longValue();
        long diffSeconds = time_duration % 60;
        long diffMinutes = time_duration / (60) % 60;
        long diffHours = time_duration / (60 * 60) % 24;
        long diffDays = time_duration / (24 * 60 * 60);
        StringBuffer s = new StringBuffer();
        if(diffDays > 0) {
            s.append(diffDays).append("天");
            s.append(diffHours).append("时");
        }else if(diffHours > 0) s.append(diffHours).append("时");
        s.append(diffMinutes).append("分").append(diffSeconds).append("秒");
        this.average_exist_duration = s.toString();
    }

    public Long getIn_count() {
        return in_count;
    }

    public void setIn_count(Long in_count) {
        this.in_count = in_count;
    }

    public Long getOut_count() {
        return out_count;
    }

    public void setOut_count(Long out_count) {
        this.out_count = out_count;
    }
}
