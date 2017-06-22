package com.example.anna.neptis.defines;

/**
 * Created by Sapienza on 17/06/2017.
 */

public class GameManager {

    private static GameManager mInstance = null;

    private String game1SessionCode;
    private String game2SessionCode;
    private String game3SessionCode;
    private String game4SessionCode;

    private GameManager(){
        this.game1SessionCode = "";
        this.game2SessionCode = "";
        this.game3SessionCode = "";
        this.game4SessionCode = "";
    }

    public static GameManager getInstance(){
        if (mInstance == null){
            mInstance = new GameManager();
        }
        return mInstance;
    }

    public String getGame1SessionCode() {
        return game1SessionCode;
    }

    public void setGame1SessionCode(String game1SessionCode) {
        this.game1SessionCode = game1SessionCode;
    }

    public String getGame2SessionCode() {
        return game2SessionCode;
    }

    public void setGame2SessionCode(String game2SessionCode) {
        this.game2SessionCode = game2SessionCode;
    }

    public String getGame3SessionCode() {
        return game3SessionCode;
    }

    public void setGame3SessionCode(String game3SessionCode) {
        this.game3SessionCode = game3SessionCode;
    }

    public String getGame4SessionCode() {
        return game4SessionCode;
    }

    public void setGame4SessionCode(String game4SessionCode) {
        this.game4SessionCode = game4SessionCode;
    }
}