package com.tanxi.sport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.InputDeviceCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewConfigurationCompat;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ConfigActivity extends WearableActivity {
    private ScrollView select;
    private TextView appname,logic;
    private int night,animate;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    WearableRecyclerView recyclerView;
    MenuRecyclerViewAdapter appListAdapter;
    ScalingScrollLayoutCallback scalingScrollLayoutCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

//        select=findViewById(R.id.select);
//        appname=findViewById(R.id.appname);
//        logic=findViewById(R.id.logic);
//        night=findViewById(R.id.night);
//        animate=findViewById(R.id.animate);
        preferences=getSharedPreferences("config",MODE_PRIVATE);
        editor=preferences.edit();
        recyclerView=findViewById(R.id.main_rv);
//
//        appname.setTextSize(25);
//        logic.setTextSize(25);
//        night.setTextSize(25);
//        animate.setTextSize(25);
//
        if ("true".equals(preferences.getString("isNight",""))){
            night=1;
        }else {
            night=0;
        }
        if ("true".equals(preferences.getString("isAnimate",""))){
            animate=1;
        }else {
            animate=0;
        }
        initRecycler();
//
//        night.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    editor.putString("isNight", "true");
//                    editor.apply();
//                } else {
//                    editor.putString("isNight", "false");
//                    editor.apply();
//                }
//            }
//        });
//        animate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    editor.putString("isAnimate", "true");
//                    editor.apply();
//                } else {
//                    editor.putString("isAnimate", "false");
//                    editor.apply();
//                }
//            }
//        });
    }

    private void initRecycler() {
        final List<ConfigItem> items = new ArrayList<ConfigItem>();
        items.add(
                new ConfigItem(
                        getString(R.string.animate),
                        AnimateActivity.class,animate));
        items.add(
                new ConfigItem(
                        getString(R.string.color),
                        ColorActivity.class,2));
        items.add(
                new ConfigItem(
                        getString(R.string.night),
                        NightmodeActivity.class,night));
        items.add(
                new ConfigItem(
                        getString(R.string.about),
                        AboutActivity.class,2));
        appListAdapter = new MenuRecyclerViewAdapter(this, items);
        scalingScrollLayoutCallback = new ScalingScrollLayoutCallback();
        recyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, scalingScrollLayoutCallback));
        recyclerView.setEdgeItemsCenteringEnabled(true);
        recyclerView.setAdapter(appListAdapter);
        recyclerView.requestFocus();
        LinearSnapHelper linearSnapHelper=new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_SCROLL &&
                        ev.isFromSource(InputDeviceCompat.SOURCE_ROTARY_ENCODER)
                ) {
                    // Don't forget the negation here
                    float delta = -ev.getAxisValue(MotionEventCompat.AXIS_SCROLL) *
                            ViewConfigurationCompat.getScaledVerticalScrollFactor(
                                    ViewConfiguration.get(ConfigActivity.this), ConfigActivity.this
                            );

                    // Swap these axes to scroll horizontally instead
                    v.scrollBy(0, Math.round(delta));

                    return true;
                }
                return false;
            }
        });
    }
}