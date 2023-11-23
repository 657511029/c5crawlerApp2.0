package com.example.update.service;

import android.app.Service;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import android.os.IBinder;


import androidx.annotation.Nullable;

import com.example.update.R;


public class FloatingWindowService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化 WindowManager

    }
    private void initFloatWindow(){
//        int ballSize = DensityUtil.dip2px(this, 45);
//        Drawable ballIcon = BackGroudSeletor.getdrawble("ic_floatball", this);
//        FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon);
//        //设置悬浮球的位置
//        //FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon,FloatBallCfg.Gravity.LEFT_CENTER);
//        //设置悬浮球不半隐藏
//        //ballCfg.setHideHalfLater(false);
//        int menuSize = DensityUtil.dip2px(this, 180);
//        int menuItemSize = DensityUtil.dip2px(this, 40);
//        FloatMenuCfg menuCfg = new FloatMenuCfg(menuSize, menuItemSize);
//        FloatBallManager mFloatballManager = new FloatBallManager(getApplicationContext(), ballCfg, menuCfg);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        // 移除悬浮窗 View
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
