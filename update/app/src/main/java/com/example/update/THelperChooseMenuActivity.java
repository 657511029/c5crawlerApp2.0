package com.example.update;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.update.api.THelperApi;
import com.example.update.view.JewelryListView;
import com.example.update.view.hangknife.HangknifeView;
import com.example.update.view.rank.RankView;
import com.example.update.view.thelper.DateView;
import com.example.update.view.thelper.MinNumberView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class THelperChooseMenuActivity extends AppCompatActivity {

    private Context context;

    private long start;

    private  long end;

    private int count;

    private String searchStr;

    private TextView restart;

    private TextView finish;

    private List<TextView> t_helper_menu_topBar_items;

    private int t_helper_menu_main_itemId;

    private LinearLayout t_helper_menu_main_item;
    private ConstraintLayout t_helper_menu_main_container;


    private DateView dateView;

    private MinNumberView minNumberView;

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

        t_helper_menu_main_container = (ConstraintLayout) findViewById(R.id.THelper_choose_menu_container);

        dateView  = new DateView(context);
        dateView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        minNumberView  = new MinNumberView(context);
        minNumberView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        t_helper_menu_main_container.addView(dateView);

        t_helper_menu_topBar_items = new ArrayList<>();
        t_helper_menu_topBar_items.add((TextView) findViewById(R.id.t_helper_menu_topBar_item1));
        t_helper_menu_topBar_items.add((TextView) findViewById(R.id.t_helper_menu_topBar_item2));
        t_helper_menu_topBar_items.add((TextView) findViewById(R.id.t_helper_menu_topBar_item3));
//        home_topBar_items.add((TextView) findViewById(R.id.home_topBar_item3));
        t_helper_menu_main_itemId = R.id.t_helper_menu_topBar_item1;
        t_helper_menu_main_item = dateView;

        clickItem(R.id.t_helper_menu_topBar_item1);
        setTitleClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickItem(view.getId());
            }
        });
    }
    private void setTitleClickListener(View.OnClickListener onClickListener) {
        for(TextView t_helper_menu_topBar_item: t_helper_menu_topBar_items){
            t_helper_menu_topBar_item.setOnClickListener(onClickListener);
        }
    }

    private void clickItem(int id){
        for(TextView t_helper_menu_topBar_item: t_helper_menu_topBar_items){
            if(t_helper_menu_topBar_item.getId() == id){
//                home_topBar_item.setTypeface(Typeface.DEFAULT_BOLD);
                t_helper_menu_topBar_item.setTextColor(this.getResources().getColor(R.color.black));
                t_helper_menu_topBar_item.setBackground(this.getResources().getDrawable(R.drawable.t_helper_choose_menu_title_active_mask));
            }
            else {
//                home_topBar_item.setTypeface(Typeface.DEFAULT);
                t_helper_menu_topBar_item.setTextColor(this.getResources().getColor(R.color.gray_979797));
                t_helper_menu_topBar_item.setBackground(this.getResources().getDrawable(R.drawable.t_helper_choose_menu_title_mask));
            }
        }
        chooseItem(id);
    }

    private void chooseItem(int id){
        if(id == t_helper_menu_main_itemId){
            return;
        }
        if(id == R.id.t_helper_menu_topBar_item1){
            chooseItem1(R.id.t_helper_menu_topBar_item1);
        }
        else if(id == R.id.t_helper_menu_topBar_item2){
            chooseItem2(R.id.t_helper_menu_topBar_item2);
        }
        else if(id == R.id.t_helper_menu_topBar_item3){
            chooseItem3(R.id.t_helper_menu_topBar_item3);
        }
    }

    private void chooseItem1(int id){
        t_helper_menu_main_container.removeView(t_helper_menu_main_item);
        t_helper_menu_main_container.addView(dateView);
        t_helper_menu_main_item = dateView;
        t_helper_menu_main_itemId = id;
    }

    private void chooseItem2(int id){
        t_helper_menu_main_container.removeView(t_helper_menu_main_item);
        t_helper_menu_main_container.addView(minNumberView);

        t_helper_menu_main_item =  minNumberView;
        t_helper_menu_main_itemId = id;
    }
    private void chooseItem3(int id){
//        home_main_container.removeView(home_main_item);
//        home_main_container.addView(hangknifeView);
//
//        home_main_item =  hangknifeView;
        t_helper_menu_main_itemId = id;
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
                Calendar now = Calendar.getInstance();
                start = dateView.getStartTime();
                end = dateView.getEndTime();
                if(start == -1 || end == -1){
                    toastMessage("未选择日期");
                    return;
                }
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