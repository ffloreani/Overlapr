package xyz.filipfloreani.overlapr.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import xyz.filipfloreani.overlapr.HomeActivity;
import xyz.filipfloreani.overlapr.R;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int POST_MILLIS = 1000;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_splash_screen);

        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(SplashScreenActivity.this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                SplashScreenActivity.this.startActivity(intent, bundle);
                SplashScreenActivity.this.finish();
            }
        }, POST_MILLIS);
    }
}
