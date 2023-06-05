package com.tanxi.sport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.NumberPicker;

public class NightmodeActivity extends WearableActivity {

    NumberPicker numNight,numMorning;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    int night,morning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nightmode);

        numNight=findViewById(R.id.numNight);
        numMorning=findViewById(R.id.numMorning);
        preferences=getSharedPreferences("config",MODE_PRIVATE);
        editor=preferences.edit();

        night=preferences.getInt("night",22);
        morning=preferences.getInt("morning",6);

        numNight.setMaxValue(24);
        numNight.setMinValue(18);
        numNight.setValue(night);
        numMorning.setMaxValue(10);
        numMorning.setMinValue(1);
        numMorning.setValue(morning);

        numNight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                editor.putInt("night",newVal);
                editor.commit();
            }
        });
        numMorning.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                editor.putInt("morning",newVal);
                editor.commit();
            }
        });
    }
}