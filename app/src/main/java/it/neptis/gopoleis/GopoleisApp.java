package it.neptis.gopoleis;

import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.twitter.sdk.android.core.Twitter;

import java.util.List;

import it.neptis.gopoleis.defines.Heritage;
import it.neptis.gopoleis.defines.Path;

public class GopoleisApp extends MultiDexApplication {

    private List<Heritage> heritages;
    private Path path;

    public List<Heritage> getHeritages() {
        return heritages;
    }

    public void setHeritages(List<Heritage> heritages) {
        this.heritages = heritages;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Twitter.initialize(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

}
