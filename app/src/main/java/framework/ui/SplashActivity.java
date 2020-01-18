package framework.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.jona.schiffeversenken.MainActivity;
import com.jona.schiffeversenken.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null)
            getActionBar().hide();
        setContentView(R.layout.activity_splash);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(750);
                    startMenu();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        timer.start();
    }

    private void startMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onPause() {
        super.onPause();
        finish();
    }

}
