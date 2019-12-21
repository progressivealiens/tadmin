package com.techsalt.tadmin.views.activity;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.techsalt.tadmin.R;
import com.techsalt.tadmin.helper.PrefData;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (PrefData.readBooleanPref(PrefData.PREF_LOGINSTATUS)){
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                }else{
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                }
                finish();
            }
        },3000);

    }
}
