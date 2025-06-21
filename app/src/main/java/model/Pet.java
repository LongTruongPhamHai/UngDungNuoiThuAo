package model;

import java.io.Serializable;

public class Pet implements Serializable {

    private String petid, userid, petname;
    private int iqscore, physicalscore, spiritscore, overallscore;

    public Pet() {}

    public Pet(String petname, String userid)
    {
        this.petname = petname;
        this.iqscore = 50;
        this.physicalscore = 50;
        this.spiritscore = 50;
        this.overallscore = 50;
        this.userid = userid;
    }

    public String getPetid() {
        return petid;
    }

    public void setPetid(String petid) {
        this.petid = petid;
    }

    public String getPetname() {
        return petname;
    }

    public void setPetname(String petname) {
        this.petname = petname;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getIqStatus() {
        if (iqscore < 50) return "Kém";
        else if (iqscore < 60) return "Trung bình";
        else if (iqscore < 85) return "Tốt";
        else return "Xuất sắc";
    }

    public String getPhysicalStatus() {
        if (physicalscore < 50) return "Yếu";
        else if (physicalscore < 60) return "Trung bình";
        else if (physicalscore < 85) return "Khỏe";
        return "Rất khỏe";
    }

    public String getSpiritStatus() {
        if (spiritscore < 50) return "Mệt mỏi";
        else if (spiritscore < 60) return "Ổn";
        else if (spiritscore < 85) return "Vui vẻ";
        return "Thư giãn";
    }

    public String getOverallStatus() {
        int overallscore = getOverallscore();
        if (overallscore < 50) return "Buồn";
        else if (overallscore < 60) return "Bình thường";
        else if (overallscore < 85) return "Hạnh phúc";
        else return "Vui vẻ";
    }

    public int getIqscore() {
        return iqscore;
    }

    public void setIqscore(int iqscore) {
        this.iqscore = iqscore;
    }

    public int getPhysicalscore() {
        return physicalscore;
    }

    public void setPhysicalscore(int physicalscore) {
        this.physicalscore = physicalscore;
    }

    public int getSpiritscore() {
        return spiritscore;
    }

    public void setSpiritscore(int spiritscore) {
        this.spiritscore = spiritscore;
    }

    public int getOverallscore() {
        return (iqscore + physicalscore + spiritscore) / 3;
    }

    public void setOverallscore(int overallscore) {
        this.overallscore = overallscore;
    }

    public String getIqColor() {
        int iqscore = getIqscore();
        if(iqscore < 50)    return "#616161";
        else if(iqscore < 60)   return "#1E88E5";
        else if(iqscore < 85)   return "#1565C0";
        else return "#FF8F00";
    }

    public String getPhysicalColor() {
        if (physicalscore < 50) return "#D32F2F";
        else if (physicalscore < 60) return "#8D6E63";
        else if (physicalscore < 85) return "#388E3C";
        return "#2E7D32";
    }

    public String getSpiritColor() {
        if (spiritscore < 50) return "#6D4C41";
        else if (spiritscore < 60) return "#8E24AA";
        else if (spiritscore < 85) return "#7B1FA2";
        return "#9C27B0";
    }

    public String getOverallColor() {
        int overallscore = getOverallscore();
        if (overallscore < 50) return "#424242";
        else if (overallscore < 60) return "#5D4037";
        else if (overallscore < 85) return "#00838F";
        else return "#E91E63";
    }

    public void petEnglish() {
        this.iqscore = Math.min(100, this.iqscore += 10);
        this.spiritscore = Math.min(100, this.spiritscore += 2);
    }

    public void petTest(int score) {
        if (score >= 7) this.iqscore = Math.min(100, this.iqscore += score);
        this.spiritscore = Math.min(100, this.spiritscore += (int) score / 4);
    }

    public void petSelfStudy(int duration) {
        this.iqscore = Math.min(100, this.iqscore += (duration / 60));
        this.spiritscore = Math.min(100, this.spiritscore += Math.min((duration / 60) * 2, 4));
    }

    public void petRun(int step) {
        this.iqscore = Math.min(100, this.iqscore += Math.min((step / 10), 2));
        this.physicalscore = Math.min(100, this.physicalscore += (int) Math.round((float) step / 10));
        this.spiritscore = Math.min(100, this.spiritscore += Math.min((step / 10) * 2, 4));
    }

    public void petBicycle(int distance) {
        this.iqscore = Math.min(100, this.iqscore += Math.min((distance / 10), 2));
        this.physicalscore = Math.min(100, this.physicalscore += Math.round((float) distance / 10) * 2);
        this.spiritscore = Math.min(100, this.spiritscore += Math.min((distance / 10) * 2, 4));
    }

    public void petYoga(int duration) {
        this.iqscore = Math.min(100, this.iqscore += Math.min((duration / 60), 2));
        this.physicalscore = Math.min(100, this.physicalscore += (int) Math.round((float) duration / 60));
        this.spiritscore = Math.min(100, this.spiritscore += Math.min((duration / 60) * 2, 4));
    }

    public void petPlayGame(int score) {
        this.iqscore = Math.min(100, this.iqscore += (score / 4));
        this.spiritscore = Math.min(100, this.spiritscore += score);
    }

    public void petOffline() {
        this.iqscore = Math.max(0, this.iqscore -= 5);
        this.physicalscore = Math.max(0, this.physicalscore -= 5);
        this.spiritscore = Math.max(0, this.spiritscore -= 5);
    }
}
