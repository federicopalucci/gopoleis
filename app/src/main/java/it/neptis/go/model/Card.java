package it.neptis.go.model;

public class Card {

    private String code, rarity, name, description, filepath;

    public Card(String cod, String rarity, String n, String d, String filepath) {
        this.code = cod;
        this.rarity = rarity;
        this.name = n;
        this.description = d;
        this.filepath = filepath;
    }

    public Card(String rarity, String n, String d) {
        this.rarity = rarity;
        this.name = n;
        this.description = d;
    }

    public String getCode() {
        return this.code;
    }

    public String getRarity() {
        return this.rarity;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getFilepath() {
        return this.filepath;
    }

}