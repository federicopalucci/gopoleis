package it.neptis.gopoleis.defines;

import java.util.List;

public class Path {

    private int code;
    private String title;
    private Question question;
    private Heritage heritage;
    private List<Stage> stages;

    public Path(int code, String title, Question question, Heritage heritage, List<Stage> stages) {
        this.code = code;
        this.title = title;
        this.question = question;
        this.heritage = heritage;
        this.stages = stages;
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

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Heritage getHeritage() {
        return heritage;
    }

    public void setHeritage(Heritage heritage) {
        this.heritage = heritage;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

}