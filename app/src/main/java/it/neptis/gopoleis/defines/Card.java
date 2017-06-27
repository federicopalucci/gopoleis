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

    public Integer getCardImage(String i) {
        switch (i) {
            case "card0001":
                return R.drawable.colosseo;
            case "card0002":
                return R.drawable.faro_di_alessandria;
            case "card0003":
                return R.drawable.machu_picchu;
            case "card0004":
                return R.drawable.london_bridge;
            case "card0005":
                return R.drawable.via_appia;
            case "card0006":
                return R.drawable.isola_tiberina;
            case "card0007":
                return R.drawable.partenone;
            case "card0008":
                return R.drawable.stile_dorico;
            case "card0009":
                return R.drawable.stile_ionico;
            case "card0010":
                return R.drawable.stile_corinzio;
            case "card0011":
                return R.drawable.arco_a_tutto_sesto;
            case "card0012":
                return R.drawable.arco_a_sesto_acuto;
            case "card0013":
                return R.drawable.stile_gotico;
            case "card0014":
                return R.drawable.basilica_di_san_pietro;
            case "card0015":
                return R.drawable.basilica_san_giovanni;
            case "card0016":
                return R.drawable.piazza_del_popolo;
            case "card0017":
                return R.drawable.altare_della_patria;
            case "card0018":
                return R.drawable.balcone_piazza_venezia;
            case "card0019":
                return R.drawable.museo_maxxi;
            case "card0020":
                return R.drawable.stadio_olimpico;

            default:
                Log.d("Prova img: ", i);
                Integer card_image = R.drawable.card;
                return card_image;
        }
    }

}