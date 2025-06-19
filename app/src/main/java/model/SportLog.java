package model;

public class SportLog {
    private String sportlogid, userid, type, time, date;
    private Long distance, step, duration;

//    Ham get, set, khoi tao...

    public SportLog() {
    }

    public SportLog(String userid, String type, String time, String date, Long distance, Long step, Long duration) {
        this.userid = userid;
        this.type = type;
        this.time = time;
        this.date = date;
        this.distance = distance;
        this.step = step;
        this.duration = duration;
    }

    public String getSportlogid() {
        return sportlogid;
    }

    public void setSportlogid(String sportlogid) {
        this.sportlogid = sportlogid;
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

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Long getStep() {
        return step;
    }

    public void setStep(Long step) {
        this.step = step;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
