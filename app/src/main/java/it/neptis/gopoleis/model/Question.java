package it.neptis.gopoleis.model;

public class Question {

    private int code;
    private String question, hintOnSite, hintByPaying, answer;

    public Question(int code, String question, String hintOnSite, String hintByPaying, String answer) {
        this.code = code;
        this.question = question;
        this.hintOnSite = hintOnSite;
        this.hintByPaying = hintByPaying;
        this.answer = answer;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getQuestion() {
        return question;
    }

    public String getHintOnSite() {
        return hintOnSite;
    }

    public String getHintByPaying() {
        return hintByPaying;
    }

    public String getAnswer() {
        return answer;
    }

}