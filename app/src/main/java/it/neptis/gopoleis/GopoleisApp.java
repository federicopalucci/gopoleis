package it.neptis.gopoleis;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.twitter.sdk.android.core.Twitter;

import java.util.List;

import it.neptis.gopoleis.defines.Heritage;
import it.neptis.gopoleis.defines.Stage;

public class GopoleisApp extends MultiDexApplication {

    private static String TAG = "GopoleisApp";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Initializing MylocationManager");
        MyLocationManager.getInstance(getApplicationContext());

        Twitter.initialize(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

}