package com.example.update;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.update.api.THelperApi;

import java.text.ParseException;
import java.util.Calendar;

public class THelperChooseMenuActivity extends AppCompatActivity {

    private Context context;

    private long start;

    private  long end;

    private int count;

    private String searchStr;

    private TextView restart;

    private TextView finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thelper_choose_menu);
        try {
            initData();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initComponent();
    }

    private void initData() throws ParseException {
        context = THelperChooseMenuActivity.this;
        Intent intent = getIntent();
        Calendar now = Calendar.getInstance();
        start = intent.getLongExtra("start", THelperApi.getDataTime(now.get(Calendar.YEAR),now.get(Calendar.MONTH) + 1,now.get(Calendar.DAY_OF_MONTH) - 1,"冬令时"));
        end = intent.getLongExtra("end",THelperApi.getDataTime(now.get(Calendar.YEAR),now.get(Calendar.MONTH) + 1,now.get(Calendar.DAY_OF_MONTH),"冬令时"));
        count = intent.getIntExtra("count",500);
        searchStr = intent.getStringExtra("searchStr");
    }

    private void initComponent(){
       restart = (TextView) findViewById(R.id.THelper_choose_menu_buttons_restart);
       initRestart();
       finish = (TextView) findViewById(R.id.THelper_choose_menu_buttons_finish);
       initFinish();
    }

    private void initRestart(){
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                try {
                    start = THelperApi.getDataTime(now.get(Calendar.YEAR),now.get(Calendar.MONTH) + 1,now.get(Calendar.DAY_OF_MONTH) - 1,"冬令时");
                    end = THelperApi.getDataTime(now.get(Calendar.YEAR),now.get(Calendar.MONTH) + 1,now.get(Calendar.DAY_OF_MONTH),"冬令时");
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                count = 500;
                searchStr = "";
                Intent intent = new Intent(THelperChooseMenuActivity.this,THelperActivity.class);
                intent.putExtra("start",start);
                intent.putExtra("end",end);
                intent.putExtra("searchStr",searchStr);
                intent.putExtra("count",count);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initFinish(){
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(THelperChooseMenuActivity.this,THelperActivity.class);
                intent.putExtra("start",start);
                intent.putExtra("end",end);
                intent.putExtra("searchStr",searchStr);
                intent.putExtra("count",count);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.t_helper_choose_out);
    }

    private void toastMessage(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }
        });
    }
}