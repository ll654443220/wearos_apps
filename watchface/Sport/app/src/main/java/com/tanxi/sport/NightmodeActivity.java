package com.tanxi.sport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

public class NightmodeActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nightmode);
    }
}