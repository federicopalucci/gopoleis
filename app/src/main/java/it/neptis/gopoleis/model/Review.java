package it.neptis.gopoleis.model;

public class Review {

    private String code, player, heritage, review;
    private int likes, dislikes;
    private boolean wasVoted, wasVotedPositively;

    public Review(String code, String player, String heritage, String review, int likes, int dislikes, boolean wasVoted, boolean wasVotedPositively) {
        this.code = code;
        this.player = player;
        this.heritage = heritage;
        this.review = review;
        this.likes = likes;
        this.dislikes = dislikes;
        this.wasVoted = wasVoted;
        this.wasVotedPositively = wasVotedPositively;
    }

    public boolean wasVoted() {
        return wasVoted;
    }

    public boolean wasVotedPositively() {
        return wasVotedPositively;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPlayer() {
        return player;
    }

    public String getHeritage() {
        return heritage;
    }

    public void setHeritage(String heritage) {
        this.heritage = heritage;
    }

    public String getReview() {
        return review;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

}