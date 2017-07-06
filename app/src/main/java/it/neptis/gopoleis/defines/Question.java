package it.neptis.gopoleis.defines;

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

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getHintOnSite() {
        return hintOnSite;
    }

    public void setHintOnSite(String hintOnSite) {
        this.hintOnSite = hintOnSite;
    }

    public String getHintByPaying() {
        return hintByPaying;
    }

    public void setHintByPaying(String hintByPaying) {
        this.hintByPaying = hintByPaying;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}