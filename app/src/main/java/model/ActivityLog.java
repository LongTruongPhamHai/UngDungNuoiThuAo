package model;

public class ActivityLog {
    private String actlogid, datetime, date, time, type, userid;
    private int duration, distance, step, score;

    public ActivityLog(){}

    public ActivityLog(String datetime, String date, String time, String type, String userid, int duration, int distance, int step, int score) {
        this.datetime = datetime;
        this.date = date;
        this.time = time;
        this.type = type;
        this.userid = userid;
        this.duration = duration;
        this.distance = distance;
        this.step = step;
        this.score = score;
    }

    public String getActlogid() {
        return actlogid;
    }

    public void setActlogid(String actlogid) {
        this.actlogid = actlogid;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
