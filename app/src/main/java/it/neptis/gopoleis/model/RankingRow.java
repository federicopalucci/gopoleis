package it.neptis.gopoleis.model;

public class RankingRow {

    private String player;
    private int amount;

    public RankingRow(String player, int amount) {
        this.player = player;
        this.amount = amount;
    }

    public String getPlayer() {
        return player;
    }

    public int getAmount() {
        return amount;
    }

}