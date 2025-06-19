package model;

public class Challenge {
    String challengeid, challengename, challengetype, challengeunit;
    int challengemax;
    /*
    type:   english freestudy   englishTest dictionary  running bicycle yoga    freeexercises
    max:    10      30          5           5           2       4       30      60
    (unit): times   minutes     times       times       km      km      minutes minutes
     */

    /*
    - Hoan thanh 10 bai hoc tieng Anh
    - Tu hoc 30 phut
    - Hoan thanh 5 bai kiem tra tieng Anh
    - Tra tu dien 5 lan
    - Chay bo 2 km
    - Dap xe 4 km
    - Tap yoga 30 phut
    - Tap the duc tu do 40 phut
     */

    /*
    - Complete 10 English lessons
    - Self-study for 30 minutes
    - Complete 5 English tests
    - Look up words in the dictionary 5 times
    - Run 2 kilometers
    - Cycle 4 kilometers
    - Do yoga for 30 minutes
    - Do physical exercises for 60 minutes
    */

    public Challenge() {}

    public Challenge(String challengename, String challengetype, String challengeunit, int challengemax) {
        this.challengename = challengename;
        this.challengetype = challengetype;
        this.challengemax = challengemax;
        this.challengeunit = challengeunit;
    }

    public String getChallengeid() {
        return challengeid;
    }

    public void setChallengeid(String challengeid) {
        this.challengeid = challengeid;
    }

    public String getChallengename() {
        return challengename;
    }

    public void setChallengename(String challengename) {
        this.challengename = challengename;
    }

    public String getChallengetype() {
        return challengetype;
    }

    public void setChallengetype(String challengetype) {
        this.challengetype = challengetype;
    }

    public int getChallengemax() {
        return challengemax;
    }

    public void setChallengemax(int challengemax) {
        this.challengemax = challengemax;
    }

    public String getChallengeunit() {
        return challengeunit;
    }

    public void setChallengeunit(String challengeunit) {
        this.challengeunit = challengeunit;
    }
}
