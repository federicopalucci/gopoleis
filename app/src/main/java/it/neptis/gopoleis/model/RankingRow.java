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

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

}