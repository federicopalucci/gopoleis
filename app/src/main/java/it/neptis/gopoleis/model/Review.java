package it.neptis.gopoleis.model;

import android.support.annotation.NonNull;

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

    public boolean isWasVoted() {
        return wasVoted;
    }

    public void setWasVoted(boolean wasVoted) {
        this.wasVoted = wasVoted;
    }

    public boolean isWasVotedPositively() {
        return wasVotedPositively;
    }

    public void setWasVotedPositively(boolean wasVotedPositively) {
        this.wasVotedPositively = wasVotedPositively;
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

    public void setPlayer(String player) {
        this.player = player;
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

    public void setReview(String review) {
        this.review = review;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

}
