package model;

import java.io.Serializable;

public class User {
    private String userid;
    private String email, username, password;
    private int exp = 0, level = 1;

    public User() {
    }

    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.exp = 0;
        this.level = 1;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void gainLevel() {
        while (this.exp >= 100) {
            this.exp -= 100;
            this.level++;
        }
    }

    public void userStudyEnglish() {
        this.exp += 10;
        gainLevel();
    }

    public void userTest(int score) {
        if (score >= 7) this.exp += score;
        else this.exp += 2;
        gainLevel();
    }

    public void userRun(int step) {
        this.exp += (int) Math.round((float) step / 10);
        gainLevel();
    }

    public void userBicycle(int distance) {
        this.exp += (distance / 10) * 2;
        gainLevel();
    }

    public void userTimePractice(int duration) {
        this.exp += (int) Math.round((float) duration / 60);
        gainLevel();
    }

    public void userPlayGame(int score) {
        // Win = 10; Loss = 0;
        this.exp += score;
        gainLevel();
    }
}