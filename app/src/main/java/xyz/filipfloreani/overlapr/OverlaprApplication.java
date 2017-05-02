package xyz.filipfloreani.overlapr;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

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

        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
