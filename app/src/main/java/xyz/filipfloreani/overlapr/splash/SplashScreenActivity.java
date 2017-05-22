package xyz.filipfloreani.overlapr.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncCredentials;
import io.realm.SyncUser;
import xyz.filipfloreani.overlapr.HomeActivity;
import xyz.filipfloreani.overlapr.OverlaprApplication;
import xyz.filipfloreani.overlapr.R;

public class SplashScreenActivity extends AppCompatActivity implements SyncUser.Callback {

    private static final String ADMIN_USERNAME = "admin@overlapr.com";
    private static final String ADMIN_PWD = "OverlaprAdmin";
    private static final int POST_MILLIS = 1500;

    private TextView splashStatusText;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler = new Handler();

        splashStatusText = (TextView) findViewById(R.id.splash_status);

        loginAdmin();
    }

    private void loginAdmin() {
        SyncCredentials credentials = SyncCredentials.usernamePassword(ADMIN_USERNAME, ADMIN_PWD, false);

        splashStatusText.setText(R.string.connecting_server);
        SyncUser.loginAsync(credentials, OverlaprApplication.AUTH_URL, this);
    }


    // Configure Realm for the current active user
    public void setActiveUser(SyncUser user) {
        SyncConfiguration defaultConfig = new SyncConfiguration.Builder(user, OverlaprApplication.REALM_URL).build();
        Realm.setDefaultConfiguration(defaultConfig);
    }

    @Override
    public void onSuccess(SyncUser user) {
        splashStatusText.setText(R.string.connection_success);

        setActiveUser(user);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                SplashScreenActivity.this.startActivity(intent);
                SplashScreenActivity.this.finish();
            }
        }, POST_MILLIS);
    }

    @Override
    public void onError(ObjectServerError error) {
        String errorMsg;
        switch (error.getErrorCode()) {
            case EXISTING_ACCOUNT:
                errorMsg = "Account already exists";
                break;
            default:
                errorMsg = error.toString();
        }

        splashStatusText.setText(errorMsg);
    }
}
