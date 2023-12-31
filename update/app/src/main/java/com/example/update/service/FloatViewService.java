package com.example.update.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.update.MainActivity;
import com.example.update.R;
import com.example.update.SettingActivity;
import com.example.update.TrackingActivity;

public class FloatViewService extends Service {

    private static final String TAG = "FloatViewService";
    // 定义浮动窗口布局
    private LinearLayout mFloatLayout;

    private LinearLayout mFloatLayout_large;
    private WindowManager.LayoutParams wmParams;
    // 创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;

//    private ImageButton go_main;
    private ImageButton float_ball;

    private ConstraintLayout float_ball_large;

    private ImageButton go_tracking;


//    private LinearLayout toucher_layout;

    private int screenHeight;
    private int screenWidth;

    private int oldX = 0;

    private int oldY = 0;
    private MyCountDownTimer myCountDownTimer;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        createFloatView();
        myCountDownTimer = new MyCountDownTimer(2500, 1000); //设置计时2.5s
        myCountDownTimer.start();
    }

    @SuppressWarnings("static-access")
    @SuppressLint("InflateParams")
    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        // 通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);

        Display display = mWindowManager.getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);
        screenWidth = point.x;
        screenHeight = point.y;
        Log.i("qqq", "screenWidth------: " + screenWidth + "\n" + "screenHeight---" + screenHeight);

        // 设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        // 设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 调整悬浮窗显示的停靠位置为右侧底部
        wmParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;
        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        // 获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.toucherlayout, null);
        mFloatLayout_large = (LinearLayout) inflater.inflate(R.layout.toucherlayout_large, null);


        // 添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        // 浮动窗口按钮

//        go_main = (ImageButton) mFloatLayout.findViewById(R.id.go_main);
        float_ball = (ImageButton) mFloatLayout.findViewById(R.id.float_ball);
        float_ball_large = (ConstraintLayout) mFloatLayout_large.findViewById(R.id.float_ball_large);
        go_tracking = (ImageButton)float_ball_large.findViewById(R.id.go_tracking);

        //UNSPECIFIED是未指定模式
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

//        mFloatLayout_large.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        float_ball.setOnTouchListener(new View.OnTouchListener() {

            private float rawX;
            private float rawY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        if (wmParams.x <= 0) {
//                            //在屏幕右侧
//                            wmParams.x = 0;
//                        } else if(wmParams.x >= screenWidth){
//                            wmParams.x = screenWidth;
//                        }
//                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
//                        Log.i("qqq", "onTouch------------------------------ACTION_DOWN: ");
                        mFloatLayout.setAlpha(1.0f);//设置其透明度
                        myCountDownTimer.cancel();//取消计时
                        rawX = event.getRawX();
                        rawY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
//                        Log.i("qqq", "onTouch------------------------------ACTION_MOVE: ");
                        // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        int distanceX = (int) (event.getRawX() - rawX);
                        int distanceY = (int) (event.getRawY() - rawY);
                        //mFloatView.getMeasuredWidth()和mFloatView.getMeasuredHeight()都是100
                        wmParams.x = wmParams.x - distanceX;
                        wmParams.y = wmParams.y - distanceY;
                        // 刷新
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        rawX = event.getRawX();
                        rawY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        myCountDownTimer.start();//重新开始计时
                        if (wmParams.x < screenWidth / 2) {
                            //在屏幕右侧
                            wmParams.x = 0;
                            wmParams.y = wmParams.y - 0;
                        } else {
                            wmParams.x = screenWidth;
                            wmParams.y = wmParams.y - 0;
                        }
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        break;
                }
                return false;//此处必须返回false，否则OnClickListener获取不到监听
            }
        });


        float_ball.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldX = wmParams.x;
                oldY = wmParams.y;
                mWindowManager.removeView(mFloatLayout);
                displayLarge();
            }
        });

        go_tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindowManager.removeView(mFloatLayout_large);
                wmParams.x = oldX;
                wmParams.y = oldY;
                mWindowManager.addView(mFloatLayout,wmParams);
                Intent intent = new Intent(FloatViewService.this, TrackingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }


    private void displayLarge(){
        WindowManager.LayoutParams newWmParams = new WindowManager.LayoutParams();
        newWmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        newWmParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        // 设置图片格式，效果为背景透明
        newWmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
//        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 调整悬浮窗显示的停靠位置为右侧底部
//        newWmParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
//        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
//        newWmParams.x = screenWidth/2 - 212;
//        newWmParams.y = screenHeight/2 - 200;
        newWmParams.gravity = Gravity.CENTER;
        newWmParams.x = 0;
        newWmParams.y = 0;
        // 设置悬浮窗口长宽数据
        newWmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        newWmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowManager.addView(mFloatLayout_large,newWmParams);
        mFloatLayout_large.setOnTouchListener(new View.OnTouchListener() {
              @Override
              public boolean onTouch(View v, MotionEvent event) {
                  mWindowManager.removeView(mFloatLayout_large);
                  wmParams.x = oldX;
                  wmParams.y = oldY;
                  mWindowManager.addView(mFloatLayout,wmParams);
                  return false;
              }
        });
    }
    public static String getTopActivity(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getShortClassName();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatLayout != null) {
            // 移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            mFloatLayout.setAlpha(0.4f);
//            if(wmParams.x == 0){
//                wmParams.x = wmParams.x - mFloatLayout.getWidth();
//            }else if(wmParams.x == screenWidth){
//                wmParams.x = wmParams.x + mFloatLayout.getWidth();
//            }
//            mWindowManager.updateViewLayout(mFloatLayout, wmParams);
        }
    }

}

