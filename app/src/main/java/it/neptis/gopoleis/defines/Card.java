package it.neptis.gopoleis.defines;

import android.util.Log;

import it.neptis.gopoleis.R;

public class Card {

    private String code, cost, name, description;

    public Card(String cod, String cost, String n, String d) {
        this.code = cod;
        this.cost = cost;
        this.name = n;
        this.description = d;
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

}