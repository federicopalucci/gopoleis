package it.neptis.gopoleis.model;

import com.google.android.gms.maps.model.LatLng;

public class Stage {

    private int code;
    private String title, curiosity;
    private LatLng coordinates;
    private Question question;
    private Path path;
    private boolean isFinal;
    private boolean hintUnlocked;

    public Stage(int code, String title, String curiosity, LatLng coordinates, Question question, Path path, boolean isFinal) {
        this.code = code;
        this.title = title;
        this.curiosity = curiosity;
        this.coordinates = coordinates;
        this.question = question;
        this.path = path;
        this.isFinal = isFinal;
    }

    public Stage(int code, String title, String curiosity, LatLng coordinates, Question question, Path path, boolean isFinal, boolean hintUnlocked) {
        this.code = code;
        this.title = title;
        this.curiosity = curiosity;
        this.coordinates = coordinates;
        this.question = question;
        this.path = path;
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

    public void setCuriosity(String curiosity) {
        this.curiosity = curiosity;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
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