package it.neptis.gopoleis;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.twitter.sdk.android.core.Twitter;

public class GopoleisApp extends MultiDexApplication {

    private static String TAG = "GopoleisApp";

    @Override
    public void onCreate() {
        super.onCreate();

        Twitter.initialize(this);

        // TODO facebook developer account has been disabled
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

}