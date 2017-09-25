package it.neptis.gopoleis.model;

public class Medal {

    private int code;
    private String name;
    private String filePath;
    private int category;

    public Medal(int code, String name, String filePath, int category) {
        this.code = code;
        this.name = name;
        this.filePath = filePath;
        this.category = category;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

}
