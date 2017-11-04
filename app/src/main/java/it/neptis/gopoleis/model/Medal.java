package it.neptis.gopoleis.model;

public class Medal {

    private int code;
    private String name;
    private String filePath;
    private int category;
    private boolean obtained;

    public Medal(int code, String name, String filePath, int category, boolean obtained) {
        this.code = code;
        this.name = name;
        this.filePath = filePath;
        this.category = category;
        this.obtained = obtained;
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

    public int getCategory() {
        return category;
    }

    public boolean isObtained() {
        return obtained;
    }

}