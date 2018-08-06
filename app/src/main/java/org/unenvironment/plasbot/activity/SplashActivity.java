package org.unenvironment.plasbot.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.unenvironment.plasbot.R;
import org.unenvironment.plasbot.config.Constants;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean authorised = sharedpreferences.getBoolean("authorised", false);
        boolean show_intro = sharedpreferences.getBoolean("show_intro", true);
        Intent intent;
        if(show_intro){
            intent = new Intent(this, IntroductionActivity.class);
        }else {

            if (authorised) {
                intent = new Intent(this, MainActivity.class);
            } else {
                intent = new Intent(this, AuthorizationActivity.class);
            }
        }

        startActivity(intent);
        finish();
    }
}
