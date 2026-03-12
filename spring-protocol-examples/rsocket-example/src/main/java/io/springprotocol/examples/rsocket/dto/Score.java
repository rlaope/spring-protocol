package io.springprotocol.examples.rsocket.dto;

public class Score {

    private String matchId;
    private String team;
    private int points;

    public Score() {
    }

    public Score(String matchId, String team, int points) {
        this.matchId = matchId;
        this.team = team;
        this.points = points;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
