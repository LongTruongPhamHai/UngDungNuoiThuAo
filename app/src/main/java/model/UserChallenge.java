package model;

public class UserChallenge {
    private String userchallengeid, challengeid, userid, date, type;
    private int progress, max;
    private boolean iscomplete;

    public UserChallenge() {}

    public UserChallenge(String challengeid, String userid, String date, String type, int progress, int max) {
        this.challengeid = challengeid;
        this.userid = userid;
        this.date = date;
        this.type = type;
        this.progress = progress;
        this.max = max;
        this.iscomplete = (progress >= max);
    }

    public String getUserchallengeid() {
        return userchallengeid;
    }

    public void setUserchallengeid(String userchallengeid) {
        this.userchallengeid = userchallengeid;
    }

    public String getChallengeid() {
        return challengeid;
    }

    public void setChallengeid(String challengeid) {
        this.challengeid = challengeid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public boolean isIscomplete() {
        return iscomplete;
    }

    public void setIscomplete(boolean iscomplete) {
        this.iscomplete = iscomplete;
    }
}
