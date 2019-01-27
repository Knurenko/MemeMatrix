package com.homedev.cometomyrise.memematrix;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DrawView drawView = new DrawView(this);
        drawView.mContext = getApplicationContext();

        //landscape or portrait orientation only
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //fullscreen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //no actionbar+title
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        try {
            getSupportActionBar().hide();
            getActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setContentView(drawView);
    }
}
