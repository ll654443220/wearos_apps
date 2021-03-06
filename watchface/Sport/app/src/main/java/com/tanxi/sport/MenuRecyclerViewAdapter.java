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

import android.content.Context;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.List;

public class MenuRecyclerViewAdapter extends RecyclerView.Adapter<MenuRecyclerViewAdapter.Holder>{
    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<ConfigItem> mItems;
    public interface OnRecyclerViewListener{
        void onItemTouch(View view, int position, MotionEvent motionEvent);
    }
    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener mOnRecyclerViewListener){
        this.onRecyclerViewListener=mOnRecyclerViewListener;
    }

    public MenuRecyclerViewAdapter(Context context, List<ConfigItem> items) {
        this.mContext = context;
        this.mItems = items;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        /* Add check for viewType here if used.
        See LongListRecyclerViewAdapter for an example. */

        return new Holder(mInflater.inflate(R.layout.item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        if (mItems.isEmpty()) {
            return;
        }
        final ConfigItem item = mItems.get(position);

        if (item.getViewType() == SampleAppConstants.HEADER_FOOTER) {
            return;
        }

        holder.bind(item);

        // Start new activity on click of specific item.
        final int pos = position;
//        holder.itemView.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                });
//        if (onRecyclerViewListener !=null){
//            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    onRecyclerViewListener.onItemTouch(holder.itemView,pos,motionEvent);
//                    return true;
//                }
//            });
//        }
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Vibrator vibrator= (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    vibrator.vibrate(15);
                }
                if (motionEvent.getAction()==MotionEvent.ACTION_UP){
                    vibrator.vibrate(15);
                    mItems.get(pos).launchActivity(mContext);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getViewType();
    }

    static class Holder extends ViewHolder {
        TextView mTextView;

        public Holder(final View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.icon_text_view);
        }

        /** Bind appItem info to main screen (displays the item).
         * @param item*/
        public void bind(ConfigItem item) {
            mTextView.setText(item.getItemName());
        }
    }
}
