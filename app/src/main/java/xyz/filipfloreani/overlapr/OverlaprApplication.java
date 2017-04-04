package xyz.filipfloreani.overlapr;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by filipfloreani on 17/03/2017.
 */
public class OverlaprApplication extends Application {

    private OverlaprApplication overlaprInstance;

    public OverlaprApplication getOverlaprInstance() {
        if (overlaprInstance == null) {
            overlaprInstance = this;
        }
        return overlaprInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
