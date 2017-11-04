package it.neptis.gopoleis.model;

import com.google.android.gms.maps.model.LatLng;

public class Stage {

    private int code;
    private String title, curiosity;
    private LatLng coordinates;
    private Question question;
    private boolean isFinal;
    private boolean hintUnlocked;

    public Stage(int code, String title, String curiosity, LatLng coordinates, Question question, boolean isFinal, boolean hintUnlocked) {
        this.code = code;
        this.title = title;
        this.curiosity = curiosity;
        this.coordinates = coordinates;
        this.question = question;
        this.isFinal = isFinal;
        this.hintUnlocked = hintUnlocked;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCuriosity() {
        return curiosity;
    }

    public Question getQuestion() {
        return question;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public boolean isHintUnlocked() {
        return hintUnlocked;
    }

    public void setHintUnlocked(boolean hintUnlocked) {
        this.hintUnlocked = hintUnlocked;
    }

}