/*
 * Copyright (C) 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tanxi.sport;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ConfigItem {
    private final String mItemName;
    private final int mViewType;
    private final Class mClass;
    private int mEnable;


    public ConfigItem(String itemName, Class<? extends Activity> clazz,int enable) {
        mItemName = itemName;
        mViewType = SampleAppConstants.NORMAL;
        mClass = clazz;
        mEnable=enable;
    }

    public String getItemName() {
        return mItemName;
    }

    public int getmEnable() {
        return mEnable;
    }

    public int getViewType() {
        return mViewType;
    }

    public void launchActivity(Context context) {
        Intent intent = new Intent(context, mClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
