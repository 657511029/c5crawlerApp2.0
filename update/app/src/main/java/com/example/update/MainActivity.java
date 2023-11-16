package com.example.update;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.update.service.FloatingWindowService;
import com.example.update.service.TrackingService;
import com.example.update.view.HomeView;
import com.example.update.view.JewelryListView;
import com.example.update.view.info.InfoView;
import com.iammert.library.readablebottombar.ReadableBottomBar;

public class MainActivity extends AppCompatActivity {

    private ReadableBottomBar readableBottomBar;

    private ConstraintLayout constraintLayout;

    private HomeView homeView;

    private InfoView infoView;

    private int mainPoint;

    private ConstraintLayout mainView;


    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initComponent();
        initNotificationChannel();
    }
    private void initData(){
        context = getApplicationContext();
    }

    private void initComponent(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initBottomBar();
                constraintLayout = (ConstraintLayout) findViewById(R.id.home_container);
                homeView = new HomeView(context);
                ConstraintLayout.LayoutParams layoutParams_home = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                homeView.setLayoutParams(layoutParams_home);

                infoView = new InfoView(context);
                ConstraintLayout.LayoutParams layoutParams_info = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                infoView.setLayoutParams(layoutParams_info);

                Intent intent = getIntent();
                int point = intent.getIntExtra("point",0);
                constraintLayout.addView(homeView);
                mainPoint = 0;
                mainView = homeView;
//                readableBottomBar.selectItem(point);
//                switch (point){
//                    case 0:
//                        constraintLayout.addView(homeView);
//                        mainPoint = 0;
//                        mainView = homeView;
//                        break;
//                    case 1:
//                        toastMessage("工程师正在努力建设中");
//                        break;
//                    case 2:
//                        toastMessage("工程师正在努力建设中");
//                        break;
//                    case 3:
//                        constraintLayout.addView(infoView);
//                        mainPoint = 3;
//                        mainView = infoView;
//                        break;
//                }


            }
        });
    }

    public void initBottomBar(){
        readableBottomBar = (ReadableBottomBar) findViewById(R.id.bottomBar);
        readableBottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {
                if(i == mainPoint){
                    return;
                }
                switch (i){
                    case 0:
                        constraintLayout.removeView(mainView);
                        constraintLayout.addView(homeView);
                        mainPoint = i;
                        mainView = homeView;
                        break;

                    case 1:
                        toastMessage("工程师正在努力建设中");
                        break;

                    case 2:
                        toastMessage("工程师正在努力建设中");
                        break;
                    case 3:
                        constraintLayout.removeView(mainView);
                        constraintLayout.addView(infoView);
                        mainPoint = i;
                        mainView = infoView;
                        break;
                }
            }
        });

    }
    @Override
    protected void onDestroy() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        //获取Editor对象的引用
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //将获取过来的值放入文件
        editor.putString("track", "false");
        editor.commit();
        Intent intent = new Intent(this, FloatingWindowService.class);
        Intent intent2 = new Intent(this, TrackingService.class);
        stopService(intent);
        stopService(intent2);
        super.onDestroy();
    }

    private void toastMessage(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void initNotificationChannel(){
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        String highChannelId = "重要通知渠道";
        if(notificationManager.getNotificationChannel(highChannelId) == null){
            NotificationChannel highChannel = new NotificationChannel(highChannelId,"捡漏通知", NotificationManager.IMPORTANCE_HIGH);
            highChannel.setDescription("捡漏通知");
            notificationManager.createNotificationChannel(highChannel);
        }
        String defaultChannelId = "默认通知渠道";
        if(notificationManager.getNotificationChannel(defaultChannelId) == null){
            NotificationChannel defaultChannel = new NotificationChannel(defaultChannelId,"运行通知", NotificationManager.IMPORTANCE_DEFAULT);
            defaultChannel.setDescription("运行通知");
            notificationManager.createNotificationChannel(defaultChannel);
        }
    }
}