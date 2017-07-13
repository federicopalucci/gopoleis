package it.neptis.gopoleis.defines;

import java.util.List;

public class Path {

    private int code;
    private String title;
    private Heritage heritage;
    private List<Stage> stages;
    private boolean completed;

    public Path(int code, String title, Heritage heritage, List<Stage> stages, boolean completed) {
        this.code = code;
        this.title = title;
        this.heritage = heritage;
        this.stages = stages;
        this.completed = completed;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}