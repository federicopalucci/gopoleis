package it.neptis.gopoleis.model;

public class Card {

    private String code, cost, name, description, filepath;

    public Card(String cod, String cost, String n, String d, String filepath) {
        this.code = cod;
        this.cost = cost;
        this.name = n;
        this.description = d;
        this.filepath = filepath;
    }

    public Card(String cost, String n, String d) {
        this.cost = cost;
        this.name = n;
        this.description = d;
    }

    public String getCode() {
        return this.code;
    }

    public String getCost() {
        return this.cost;
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