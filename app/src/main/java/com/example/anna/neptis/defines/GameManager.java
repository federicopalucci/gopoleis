package com.example.anna.neptis.defines;

/**
 * Created by Sapienza on 17/06/2017.
 */

public class GameManager {
    private static GameManager mInstance = null;

    private User mUser;

    private GameManager(){
        mUser = new User();
    }

    public static GameManager getInstance(){
        if (mInstance == null){
            mInstance = new GameManager();
        }
        return mInstance;
    }

    public User getUser(){
        return this.mUser;
    }

    public void setUser(User value){
        mUser = value;
    }
}
