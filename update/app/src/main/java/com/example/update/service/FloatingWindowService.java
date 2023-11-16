package com.example.update.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.example.update.R;

public class FloatingWindowService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;

    private int lastX;
    private int lastY;
    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化 WindowManager
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        // 创建悬浮窗 View
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.cockroach, null);

        // 设置悬浮窗 View 的参数
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.START | Gravity.TOP;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;


        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
//                startActivityForResult(intent, 1);
            } else {
                //TODO do something you need
                DisplayMetrics metrics = new DisplayMetrics();
                mWindowManager.getDefaultDisplay().getMetrics(metrics);
                params.x = metrics.widthPixels/2;
                params.y = metrics.heightPixels/2;
                mFloatingView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        //此方法获取的是手指相对于view的左上角的x,y值
                        int x = (int) event.getRawX();
                        int y = (int) event.getRawY();
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                lastX = x;
                                lastY = y;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                int offsetX = x - lastX;
                                int offsetY = y - lastY;
                                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mFloatingView.getLayoutParams();
                                layoutParams.x += offsetX;
                                layoutParams.y += offsetY;
                                WindowManager windowManager = (WindowManager) mFloatingView.getContext().getSystemService(Context.WINDOW_SERVICE);
                                windowManager.updateViewLayout(mFloatingView, layoutParams);
//                                mFloatingView.layout(mFloatingView.getLeft() + offsetX, mFloatingView.getTop() + offsetY, mFloatingView.getRight() + offsetX, mFloatingView.getBottom() + offsetY);
                                break;
                        }

                        return true;
                    }
                });
                mWindowManager.addView(mFloatingView, params);
            }
        }
        // 添加悬浮窗 View
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        // 移除悬浮窗 View
        if (mFloatingView != null) {
            mWindowManager.removeView(mFloatingView);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
