package xyz.filipfloreani.overlapr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.stetho.Stetho;

import io.realm.Realm;

/**
 * Created by filipfloreani on 17/03/2017.
 */
public class OverlaprApplication extends Application {

    public static final String AUTH_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/auth";
    public static final String REALM_URL = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/realmtasks";

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
        Stetho.initializeWithDefaults(this);

        SharedPreferences sp = getSharedPreferences(HomeActivity.SHARED_PREF_HOME_ACTIVITY, Context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }
}
