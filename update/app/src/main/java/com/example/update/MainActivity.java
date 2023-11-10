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
import com.iammert.library.readablebottombar.ReadableBottomBar;

public class MainActivity extends AppCompatActivity {

    private ReadableBottomBar readableBottomBar;

    private ConstraintLayout constraintLayout;



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
                ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.home_container);
                HomeView homeView = new HomeView(context);
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                homeView.setLayoutParams(layoutParams);
                constraintLayout.addView(homeView);

            }
        });
    }

    public void initBottomBar(){
        readableBottomBar = (ReadableBottomBar) findViewById(R.id.bottomBar);
        readableBottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {

            }
        });
        readableBottomBar.selectItem(3);
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