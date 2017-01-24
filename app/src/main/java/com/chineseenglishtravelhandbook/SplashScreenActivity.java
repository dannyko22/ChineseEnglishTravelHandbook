package com.chineseenglishtravelhandbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Danny on 23/01/2017.
 */

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}