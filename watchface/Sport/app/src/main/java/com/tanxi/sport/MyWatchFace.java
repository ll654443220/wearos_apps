package com.tanxi.sport;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.core.content.res.ResourcesCompat;
import androidx.palette.graphics.Palette;

import android.os.Vibrator;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn"t
 * shown. On devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient
 * mode. The watch face is drawn with less contrast in mute mode.
 * <p>
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
public class MyWatchFace extends CanvasWatchFaceService {

    /*
     * Updates rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<MyWatchFace.Engine> mWeakReference;

        public EngineHandler(MyWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            MyWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        private static final float HOUR_STROKE_WIDTH = 5f;
        private static final float MINUTE_STROKE_WIDTH = 2f;
        private static final float DATE_STROKE_WIDTH = 0f;


        private static final int SHADOW_RADIUS = 6;
        /* Handler to update the time once a second in interactive mode. */
        private final Handler mUpdateTimeHandler = new EngineHandler(this);
        private Calendar mCalendar;
        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };
        private boolean mRegisteredTimeZoneReceiver = false;
        private boolean mMuteMode;
        private float mCenterX;
        private float mCenterY;
        /* Colors for all hands (hour, minute, seconds, ticks) based on photo loaded. */
        private int mWatchHandColor;
        private int mWatchHandShadowColor;
        private Paint mHourPaint;
        private Paint mMinutePaint;
        private Paint mDatePaint;
        private Paint mRectPaint;
        private Paint mNightPaint;
        private boolean mAmbient;
        private boolean mLowBitAmbient;
        private boolean mBurnInProtection;
        private boolean mUpdate,animate;
        private SharedPreferences preferences;
        private SharedPreferences.Editor editor;
        int i;
        private Camera camera=new Camera();
        private Matrix matrix=new Matrix();
        float mCameraRotateX,mCameraRotateY;
        int ani=0;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFace.this)
                    .setAcceptsTapEvents(true)
                    .build());

            mCalendar = Calendar.getInstance();

            initializeBackground();
            initializeWatchFace();
        }

        private void initializeBackground() {
            Paint mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(Color.BLACK);

            /* Extracts colors from background image to improve watchface style. */
        }

        private void initializeWatchFace() {
            /* Set defaults for colors */
            mWatchHandColor = Color.WHITE;
            mWatchHandShadowColor = Color.BLACK;

            Typeface typeface= ResourcesCompat.getFont(MyWatchFace.this,R.font.seguibli);
            Shader mShader = new LinearGradient(0,0,0,300,new int[] {Color.alpha(0),Color.BLACK},null, Shader.TileMode.CLAMP);

            mHourPaint = new Paint();
            mHourPaint.setColor(Color.argb(255,227,193,181));
            mHourPaint.setStrokeWidth(HOUR_STROKE_WIDTH);
            mHourPaint.setAntiAlias(true);
            mHourPaint.setStrokeCap(Paint.Cap.ROUND);
            mHourPaint.setTextSize(280);
            mHourPaint.setTypeface(typeface);
            mHourPaint.setStyle(Paint.Style.STROKE);
            mHourPaint.setTextAlign(Paint.Align.RIGHT);
            mHourPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);

            mMinutePaint = new Paint();
            mMinutePaint.setColor(Color.argb(255,227,193,181));
            mMinutePaint.setStrokeWidth(MINUTE_STROKE_WIDTH);
            mMinutePaint.setAntiAlias(true);
            mMinutePaint.setStrokeCap(Paint.Cap.ROUND);
            mMinutePaint.setTextSize(200);
            mMinutePaint.setTypeface(typeface);
            mMinutePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mMinutePaint.setTextAlign(Paint.Align.RIGHT);
            mMinutePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);

            mDatePaint=new Paint();
            mDatePaint.setColor(mWatchHandColor);
            mDatePaint.setAntiAlias(true);
            mDatePaint.setStrokeCap(Paint.Cap.ROUND);
            mDatePaint.setTextSize(28);
            mDatePaint.setTypeface(typeface);
            mDatePaint.setStyle(Paint.Style.FILL);
            mDatePaint.setTextAlign(Paint.Align.RIGHT);
            mDatePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);

            mRectPaint=new Paint();
            mRectPaint.setShader(mShader);

            mNightPaint=new Paint();
            mNightPaint.setColor(Color.BLACK);
            mNightPaint.setAlpha(200);
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            mAmbient = inAmbientMode;

            updateWatchHandStyle();

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer();
        }

        private void updateWatchHandStyle() {
//            if (mAmbient) {
//                mHourPaint.setColor(Color.WHITE);
//                mMinutePaint.setColor(Color.WHITE);
//
//                mHourPaint.setAntiAlias(false);
//                mMinutePaint.setAntiAlias(false);
//
//                mHourPaint.clearShadowLayer();
//                mMinutePaint.clearShadowLayer();
//
//            } else {
//                mHourPaint.setColor(mWatchHandColor);
//                mMinutePaint.setColor(mWatchHandColor);
//
//                mHourPaint.setAntiAlias(true);
//                mMinutePaint.setAntiAlias(true);
//
//                mHourPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);
//                mMinutePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);
//            }
        }

        @Override
        public void onInterruptionFilterChanged(int interruptionFilter) {
            super.onInterruptionFilterChanged(interruptionFilter);
            boolean inMuteMode = (interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE);

            /* Dim display in mute mode. */
            if (mMuteMode != inMuteMode) {
                mMuteMode = inMuteMode;
                mHourPaint.setAlpha(inMuteMode ? 100 : 255);
                mMinutePaint.setAlpha(inMuteMode ? 100 : 255);
                invalidate();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            /*
             * Find the coordinates of the center point on the screen, and ignore the window
             * insets, so that, on round watches with a "chin", the watch face is centered on the
             * entire screen, not just the usable portion.
             */
            mCenterX = width / 2f;
            mCenterY = height / 2f;

            /*
             * Calculate lengths of different hands based on watch screen size.
             */

            /* Scale loaded background image (more efficient) if surface dimensions change. */

            /*
             * Create a gray version of the image only if it will look nice on the device in
             * ambient mode. That means we don"t want devices that support burn-in
             * protection (slight movements in pixels, not great for images going all the way to
             * edges) and low ambient mode (degrades image quality).
             *
             * Also, if your watch face will know about all images ahead of time (users aren"t
             * selecting their own photos for the watch face), it will be more
             * efficient to create a black/white version (png, etc.) and load that when you need it.
             */
            if (!mBurnInProtection && !mLowBitAmbient) {

            }
        }



        /**
         * Captures tap event (and tap type). The {@link WatchFaceService#TAP_TYPE_TAP} case can be
         * used for implementing specific logic to handle the gesture.
         */
        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    //getCameraRotate(x,y);
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    // TODO: Add code to handle the tap gesture.
                    break;
            }
            invalidate();
        }

        private void getCameraRotate(int x, int y) {
            mUpdate=true;
            ani=1;
            float rotateX=-y+mCenterY;
            float rotateY=x-mCenterX;
            float v = 0;
            float percentX=rotateX/mCenterX;
            float percentY=rotateY/mCenterY;

            if (percentX>0&&percentY>0){
                if (percentX>percentY){
                    v=percentX;
                }else {
                    v=percentY;
                }
            }else if (percentX>0&&percentY<0){
                if (percentX>-percentY){
                    v=percentX;
                }else {
                    v=percentY;
                }
            }else if (percentX<0&&percentY>0){
                if (-percentX>percentY){
                    v=percentX;
                }else {
                    v=percentY;
                }
            }else {
                if (-percentX>-percentY){
                    v=percentX;
                }else {
                    v=percentY;
                }
            }
            if (v<0){
                v=-v;
            }
            if (percentX>1){
                percentX=1;
            }else if (percentX<-1){
                percentX=-1;
            }
            if (percentY>1){
                percentY=1;
            }else if (percentY<-1){
                percentY=-1;
            }
            Log.i("TEXT",""+v);
            Vibrator vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate((long) (150*v));

            mCameraRotateX=percentX*20;
            mCameraRotateY=percentY*20;
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);

            drawBackground(canvas);
            drawWatchFace(canvas);
        }

        private void drawBackground(Canvas canvas) {

            if (mAmbient && (mLowBitAmbient || mBurnInProtection)) {
                canvas.drawColor(Color.BLACK);
            } else if (mAmbient) {
                canvas.drawColor(Color.BLACK);
            } else {
                canvas.drawColor(Color.BLACK);
            }
        }

        private void drawWatchFace(Canvas canvas) {
            int hours,time;
            String timer;
            ContentResolver cv=getContentResolver();
            String strTimeFormat = android.provider.Settings.System.getString(cv,
                                           android.provider.Settings.System.TIME_12_24);
            if(strTimeFormat.equals("24"))
            {
                hours = mCalendar.get(Calendar.HOUR_OF_DAY);
                time=2;
            }
            else {
                hours = mCalendar.get(Calendar.HOUR);
                time=mCalendar.get(Calendar.AM_PM);
            }
            switch (time){
                case 0:
                    timer="AM";
                    break;
                case 1:
                    timer="PM";
                    break;
                case 2:
                    timer="";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + time);
            }
            /*
             * Draw ticks. Usually you will want to bake this directly into the photo, but in
             * cases where you want to allow users to select their own photos, this dynamically
             * creates them on top of the photo.
             */

            /*
             * These calculations reflect the rotation in degrees per unit of time, e.g.,
             * 360 / 60 = 6 and 360 / 12 = 30.
             */
            int date=mCalendar.get(Calendar.DAY_OF_MONTH);
            int mounth=mCalendar.get(Calendar.MONTH);
            int week=mCalendar.get(Calendar.DAY_OF_WEEK);

            int minutes = mCalendar.get(Calendar.MINUTE);

            preferences=getSharedPreferences("config",MODE_PRIVATE);
            editor=preferences.edit();
            if ("true".equals(preferences.getString("isAnimate",""))){
                animate=true;
            }else {
                animate=false;
            }

            int rgbR;
            int rgbG;
            int rgbB;
            rgbR=preferences.getInt("rgbR",227);
            rgbG=preferences.getInt("rgbG",193);
            rgbB=preferences.getInt("rgbB",181);

            String weeks;
            switch (week){
                case 1:
                    weeks="SUN";
                    break;
                case 2:
                    weeks="MON";
                    break;
                case 3:
                    weeks="TUE";
                    break;
                case 4:
                    weeks="WED";
                    break;
                case 5:
                    weeks="THU";
                    break;
                case 6:
                    weeks="FRI";
                    break;
                case 7:
                    weeks="SAT";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + week);
            }
            if (!animate){
                if (mCalendar.get(Calendar.SECOND)==0){
                    mUpdate=false;
                }
            }

            canvas.save();
            /*
             * Save the canvas state before we can begin to rotate it.
             */
            //setCameraRotate(canvas);
            if ("true".equals(preferences.getString("isNight",""))){
                if (time==2){
                    if (hours>=22){
                        mMinutePaint.setColor(Color.WHITE);
                        mHourPaint.setColor(Color.WHITE);
                    }else if (hours<6){
                        mMinutePaint.setColor(Color.WHITE);
                        mHourPaint.setColor(Color.WHITE);
                    }else {
                        mMinutePaint.setColor(Color.argb(255,rgbR,rgbG,rgbB));
                        mHourPaint.setColor(Color.argb(255,rgbR,rgbG,rgbB));
                    }
                }else if (time==1){
                    if (hours>=10){
                        mMinutePaint.setColor(Color.WHITE);
                        mHourPaint.setColor(Color.WHITE);
                    }else {
                        mMinutePaint.setColor(Color.argb(255,rgbR,rgbG,rgbB));
                        mHourPaint.setColor(Color.argb(255,rgbR,rgbG,rgbB));
                    }
                }else {
                    if (hours<6){
                        mMinutePaint.setColor(Color.WHITE);
                        mHourPaint.setColor(Color.WHITE);
                    }else {
                        mMinutePaint.setColor(Color.argb(255,rgbR,rgbG,rgbB));
                        mHourPaint.setColor(Color.argb(255,rgbR,rgbG,rgbB));
                    }
                }

            }else {
                mMinutePaint.setColor(Color.argb(255,rgbR,rgbG,rgbB));
                mHourPaint.setColor(Color.argb(255,rgbR,rgbG,rgbB));
            }

            if (hours<10){
                canvas.drawText("0"+hours,mCenterX*19/10,mCenterY*11/10,mHourPaint);
            }else {
                canvas.drawText(""+hours,mCenterX*19/10,mCenterY*11/10,mHourPaint);
            }
            canvas.drawRoundRect(0,0,mCenterX*2,mCenterY*2,30,30,mRectPaint);
            if (minutes<10){
                canvas.drawText("0"+minutes,mCenterX*37/20,mCenterY*17/10,mMinutePaint);
            }else {
                canvas.drawText(""+minutes,mCenterX*37/20,mCenterY*17/10,mMinutePaint);
            }

            /*
             * Ensure the "seconds" hand is drawn only when we are in interactive mode.
             * Otherwise, we only update the watch face once a minute.
             */
            if (!mAmbient) {
                if (animate){
                    mDatePaint.setAlpha(i*255/30);
                }
                canvas.drawText(timer,mCenterX*4/9,mCenterY*28/20,mDatePaint);
                canvas.drawText(weeks,mCenterX*4/9,mCenterY*31/20,mDatePaint);
                canvas.drawText(date+"",mCenterX*4/9,mCenterY*17/10,mDatePaint);
            }else {
                if (animate){
                    i=0;
                    mUpdate=false;
                }


            }

            if ("true".equals(preferences.getString("isNight",""))){
                if (time==2){
                    if (hours>=22){
                        canvas.drawRect(0,0,mCenterX*2,mCenterY*2,mNightPaint);
                    }else if (hours<6){
                        canvas.drawRect(0,0,mCenterX*2,mCenterY*2,mNightPaint);
                    }
                }else if (time==1){
                    if (hours>=10){
                        canvas.drawRect(0,0,mCenterX*2,mCenterY*2,mNightPaint);
                    }
                }else {
                    if (hours<6){
                        canvas.drawRect(0,0,mCenterX*2,mCenterY*2,mNightPaint);
                    }
                }
            }

            /* Restore the canvas" original orientation. */
            canvas.restore();
        }

        private void setCameraRotate(Canvas canvas) {
            ani++;
            if (0<ani&&ani<3){
            }else {
                mUpdate=false;
                ani=0;
                mCameraRotateX=0;
                mCameraRotateY=0;
            }
            matrix.reset();
            camera.save();
            camera.rotateX(mCameraRotateX);
            camera.rotateY(mCameraRotateY);
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-mCenterX,-mCenterY);
            matrix.postTranslate(mCenterX,mCenterY);
            canvas.concat(matrix);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();
                /* Update time zone in case it changed while we weren"t visible. */
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceiver();
            }

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            MyWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MyWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        /**
         * Starts/stops the {@link #mUpdateTimeHandler} timer based on the state of the watch face.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer
         * should only run in active mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !mAmbient;
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                if (animate){
                    mUpdate=true;
                }
                i++;
                if (i>30){i=30;mUpdate=false;}
                if (mUpdate){
                    mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 20);
                }else {
                    mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 6000);
                }
            }
        }
    }
}