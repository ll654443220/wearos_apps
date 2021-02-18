package com.tanxi.sport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class ColorActivity extends WearableActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private EditText hour_r,hour_g,hour_b;
    private EditText minute_r,minute_g,minute_b;
    private View hour_color,minute_color;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        preferences=getSharedPreferences("config",MODE_PRIVATE);
        editor=preferences.edit();
    }
}