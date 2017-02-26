package cs4347.group1.fauxstringinstrument;

import android.app.Application;

import timber.log.Timber;

public class FauxStringApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
