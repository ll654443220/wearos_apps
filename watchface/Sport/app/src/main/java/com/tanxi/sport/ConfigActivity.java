package com.tanxi.sport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ConfigActivity extends WearableActivity {
    private ScrollView select;
    private TextView appname,logic;
    private Switch night,animate;
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
        initRecycler();
//
//        appname.setTextSize(25);
//        logic.setTextSize(25);
//        night.setTextSize(25);
//        animate.setTextSize(25);
//
//        if ("true".equals(preferences.getString("isNight",""))){
//            night.setChecked(true);
//        }else {
//            night.setChecked(false);
//        }
//        if ("true".equals(preferences.getString("isAnimate",""))){
//            animate.setChecked(true);
//        }else {
//            animate.setChecked(false);
//        }
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
                        getString(R.string.app_name),
                        AboutActivity.class));
        items.add(
                new ConfigItem(
                        getString(R.string.color),
                        ColorActivity.class));
        items.add(
                new ConfigItem(
                        getString(R.string.night),
                        NightmodeActivity.class));
        items.add(
                new ConfigItem(
                        getString(R.string.animate),
                        AnimateActivity.class));
        appListAdapter = new MenuRecyclerViewAdapter(this, items);
        scalingScrollLayoutCallback = new ScalingScrollLayoutCallback();
        recyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, scalingScrollLayoutCallback));
        recyclerView.setEdgeItemsCenteringEnabled(true);
        recyclerView.setAdapter(appListAdapter);
        recyclerView.requestFocus();
    }
}