package edu.nwmissouri.beam.pagerank;

import java.io.Serializable;

public class VotingPage implements Serializable {
    private String voterName = "";
    private double voterRank = 0.0;
    private Integer contributorVotes = 0;

    public VotingPage(String voterName, Integer contributorVotes) {
        this.voterName = voterName;
        this.contributorVotes = contributorVotes;
    }

    public VotingPage(String voterName, double voterRank) {
        this.voterName = voterName;
        this.voterRank = voterRank;
    }

    public VotingPage(String voterName, double voterRank, Integer contributorVotes) {
        this.voterName = voterName;
        this.voterRank = voterRank;
        this.contributorVotes = contributorVotes;
    }

    public String getVoterName() {
        return voterName;
    }

    public void setVoterName(String voterName) {
        this.voterName = voterName;
    }

    public double getVoterRank() {
        return voterRank;
    }

    public void setVoterRank(double voterRank) {
        this.voterRank = voterRank;
    }

    public Integer getContributorVotes() {
        return contributorVotes;
    }

    public void setContributorVotes(Integer contributorVotes) {
        this.contributorVotes = contributorVotes;
    }

    @Override
    public String toString() {
        return String.format("%s %.4f %s", voterName, voterRank, contributorVotes);
    }

}