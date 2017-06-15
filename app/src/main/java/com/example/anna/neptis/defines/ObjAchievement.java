package com.example.anna.neptis.defines;

/**
 * Created by lorpe on 03/10/2016.
 */

public class ObjAchievement {
    private String codice;
    private String nome;
    private String descrizione;
    private boolean completato = false; // stelletta grigia

    public ObjAchievement(String c,String n, String d){
        this.codice =c;
        this.nome=n;
        this.descrizione=d;
    }

    public String getCodice(){
        return this.codice;
    }

    public String getNome(){
        return this.nome;
    }

    public String getDescrizione(){
        return this.descrizione;
    }

    public boolean isCompletato(){
        return this.completato;
    }

    public void setCompletato(){
        this.completato = true;
    }
}
