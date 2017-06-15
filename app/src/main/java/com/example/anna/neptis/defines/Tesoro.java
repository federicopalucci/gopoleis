package com.example.anna.neptis.defines;

import com.example.anna.neptis.R;

/**
 * Created by Anna on 17/10/2016.
 */

public class Tesoro {

    private String code;
    private String latitudine;
    private String longitudine;
    private String info;
    private boolean found = false;  //per verificare se il tesoro è stato trovato
    private Integer image = new Integer(R.drawable.treasures);

    public Tesoro(String c,String lat,String lon,String i){
        this.code = c;
        this.latitudine = lat;
        this.longitudine = lon;
        this.info = i;
    }

    public String getCode() {
        return this.code;
    }

    public String getLatitudine() {
        return this.latitudine;
    }

    public String getLongitudine() {
        return this.longitudine;
    }

    public String getInfo() {
        return this.info;
    }

    public Integer getImage() {
        return this.image;
    }

    public boolean isFound() {
        return this.found;
    }

    public void setFound() {
        this.found = true;
    }


}
