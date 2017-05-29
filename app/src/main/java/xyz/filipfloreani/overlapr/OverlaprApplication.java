package xyz.filipfloreani.overlapr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by filipfloreani on 17/03/2017.
 */
public class OverlaprApplication extends Application {

    public static final String AUTH_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/auth";
    public static final String REALM_URL = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/realm-overlapr";

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

        SharedPreferences sp = getSharedPreferences(HomeActivity.SHARED_PREF_HOME_ACTIVITY, Context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }
}
