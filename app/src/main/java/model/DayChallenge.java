package model;

import java.util.List;

public class DayChallenge {
    private String datechallenge;
    private List<String> challengeidlist;

    public DayChallenge() {}

    public DayChallenge(String datechallenge, List<String> challengeidlist) {
        this.datechallenge = datechallenge;
        this.challengeidlist = challengeidlist;
    }

    public String getDatechallenge() {
        return datechallenge;
    }

    public void setDatechallenge(String datechallenge) {
        this.datechallenge = datechallenge;
    }

    public List<String> getChallengeidlist() {
        return challengeidlist;
    }

    public void setChallengeidlist(List<String> challengeidlist) {
        this.challengeidlist = challengeidlist;
    }
}
