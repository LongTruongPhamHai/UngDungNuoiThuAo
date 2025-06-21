package model;

public class StudyLog {
    private String studylogid, userid, type, time, date;
    private Long score, duration;

    public StudyLog(){}

    public StudyLog(String userid, String type, String time, String date, Long score, Long duration) {
        this.userid = userid;
        this.type = type;
        this.time = time;
        this.date = date;
        this.score = score;
        this.duration = duration;
    }

    public String getStudylogid() {
        return studylogid;
    }

    public void setStudylogid(String studylogid) {
        this.studylogid = studylogid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
