package com.tanxi.sport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ColorActivity extends WearableActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private EditText hour_r,hour_g,hour_b;
    private EditText minute_r,minute_g,minute_b;
    private View hour_color,minute_color;
    int rgbR,rgbG,rgbB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        preferences=getSharedPreferences("config",MODE_PRIVATE);
        editor=preferences.edit();
        hour_r=findViewById(R.id.hour_r);
        hour_g=findViewById(R.id.hour_g);
        hour_b=findViewById(R.id.hour_b);
        hour_color=findViewById(R.id.hour_color);

        hour_r.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                editor.putInt("rgbR",Integer.parseInt(hour_r.getText().toString()));
                editor.commit();
                rgbR=preferences.getInt("rgbR",227);
                rgbG=preferences.getInt("rgbG",193);
                rgbB=preferences.getInt("rgbB",181);
                hour_color.setBackgroundColor(Color.argb(255,rgbR,rgbG,rgbB));
                return false;
            }
        });
        hour_g.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                editor.putInt("rgbG",Integer.parseInt(hour_g.getText().toString()));
                editor.commit();
                rgbR=preferences.getInt("rgbR",227);
                rgbG=preferences.getInt("rgbG",193);
                rgbB=preferences.getInt("rgbB",181);
                hour_color.setBackgroundColor(Color.argb(255,rgbR,rgbG,rgbB));
                return false;
            }
        });
        hour_b.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                editor.putInt("rgbB",Integer.parseInt(hour_b.getText().toString()));
                editor.commit();
                rgbR=preferences.getInt("rgbR",227);
                rgbG=preferences.getInt("rgbG",193);
                rgbB=preferences.getInt("rgbB",181);
                hour_color.setBackgroundColor(Color.argb(255,rgbR,rgbG,rgbB));
                return false;
            }
        });
    }
}