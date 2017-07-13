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

    private List<Heritage> heritages;
    private Stage stage;

    public List<Heritage> getHeritages() {
        return heritages;
    }

    public void setHeritages(List<Heritage> heritages) {
        this.heritages = heritages;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

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