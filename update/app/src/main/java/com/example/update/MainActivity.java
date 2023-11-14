package com.example.update;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        readableBottomBar.selectItem(1);
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

                constraintLayout.addView(homeView);
                mainPoint = 0;
                mainView = homeView;

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

    private void toastMessage(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
            }
        });

    }
}