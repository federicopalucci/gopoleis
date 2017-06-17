package com.example.anna.neptis.defines;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Created by Anna on 31/10/2016.
 */

public class User {

    private GoogleSignInAccount googleSignInAccount;

    private TwitterSession twitterSession;

    boolean loggedWithGoogle, loggedWithFacebook, loggedWithTwitter;

    public User(){
        loggedWithGoogle = false;
        loggedWithFacebook = false;
        loggedWithTwitter = false;
    }

    public GoogleSignInAccount getGoogleSignInAccount() {
        return googleSignInAccount;
    }

    public void setGoogleSignInAccount(GoogleSignInAccount googleSignInAccount) {
        this.googleSignInAccount = googleSignInAccount;
        loggedWithGoogle = true;
    }

    public TwitterSession getTwitterSession() {
        return twitterSession;
    }

    public void setTwitterSession(TwitterSession twitterSession) {
        this.twitterSession = twitterSession;
        loggedWithTwitter = true;
    }

    public boolean isLoggedWithGoogle(){return loggedWithGoogle;}
    public boolean isLoggedWithFacebook(){return loggedWithFacebook;}
    public boolean isLoggedWithTwitter(){return loggedWithTwitter;}

}
