package com.example.update;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.update.adapter.THelperAdapter;
import com.example.update.api.THelperApi;
import com.example.update.entity.OrdersItem;
import com.example.update.view.tracking.TrackingJewelryListViewAdapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class THelperActivity extends AppCompatActivity {

    private Context context;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ListView listView;

    private THelperAdapter tHelperAdapter;

    private EditText search;

    private ImageButton chooseMenu;
    private String searchStr;

    private long start;

    private long end;

    private int count;


    private Map<String, OrdersItem> ordersItemMap = null;

    private List<OrdersItem> dataList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thelper);
        try {
            initData();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        initActionBar("做T助手");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initComponent();
    }

    private void initData() throws ParseException {
        context = THelperActivity.this;
        Intent intent = getIntent();
        Calendar now = Calendar.getInstance();
        start = intent.getLongExtra("start",THelperApi.getDataTime(now.get(Calendar.YEAR),now.get(Calendar.MONTH) + 1,now.get(Calendar.DAY_OF_MONTH) - 1,"冬令时"));
        end = intent.getLongExtra("end",THelperApi.getDataTime(now.get(Calendar.YEAR),now.get(Calendar.MONTH) + 1,now.get(Calendar.DAY_OF_MONTH),"冬令时"));
        count = intent.getIntExtra("count",500);
        searchStr = intent.getStringExtra("searchStr");
    }

    private void initComponent(){
        search = (EditText) findViewById(R.id.THelper_order_list_search);
        search.setText(searchStr);
        chooseMenu = (ImageButton) findViewById(R.id.THelper_choose_menu);
//        int size = getResources().getDimensionPixelSize(R.dimen.chooseMenuSize);
//
//        Log.e("size",String.valueOf(size));
//        search.setWidth(search.getWidth() - size);
//        ConstraintLayout.LayoutParams params_1= (ConstraintLayout.LayoutParams) search.getLayoutParams();
//        Log.e("width",String.valueOf(params_1.width));
//        params_1.width = params_1.width - size;
//        Log.e("width",String.valueOf(params_1.width));
//        search.setLayoutParams(params_1);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.THelper_order_list_refresh);
        listView = (ListView) findViewById(R.id.THelper_order_list_result);
        initChooseMenu();
        initList();
        initRefresh();
    }

    private void initChooseMenu(){
        chooseMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(THelperActivity.this, THelperChooseMenuActivity.class);
                intent.putExtra("start",start);
                intent.putExtra("end",end);
                intent.putExtra("searchStr",searchStr);
                intent.putExtra("count",count);
                startActivity(intent);
                overridePendingTransition(R.anim.t_helper_choose_in, R.anim.t_helper_out);
            }
        });
    }

    private void initList(){
        dataList.clear();
        setAllEnabled(false);
        swipeRefreshLayout.setEnabled(false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getList(searchStr);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (tHelperAdapter != null) {
                            tHelperAdapter.notifyDataSetChanged();
                        } else {
                            tHelperAdapter = new THelperAdapter(context, dataList);
                            listView.setAdapter(tHelperAdapter);
                        }
                        setAllEnabled(true);
                        swipeRefreshLayout.setEnabled(true);
                    }
                });
            }
        });
        thread.start();
    }

    private void getList(String searchStr) throws ParseException {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("user","");
        String token = THelperApi.getToken("17346697622","Lenshanshan521");
//
        if(TextUtils.isEmpty(token)){
            toastMessage("用户C5信息错误");
            return;
        }
//        Calendar now = Calendar.getInstance();
//        long start = THelperApi.getDataTime(now.get(Calendar.YEAR),now.get(Calendar.MONTH) + 1,now.get(Calendar.DAY_OF_MONTH) - dateNumber,"冬令时");
//        long end = THelperApi.getDataTime(now.get(Calendar.YEAR),now.get(Calendar.MONTH) + 1,now.get(Calendar.DAY_OF_MONTH),"冬令时");
//
        while (ordersItemMap == null){
            ordersItemMap = THelperApi.getSellList(start,end,count,token);
        }
        List<OrdersItem> ordersItemList = ordersItemMap.values().stream().collect(Collectors.toList());
        for(int i = 0;i < ordersItemList.size();i++){
            dataList.add(ordersItemList.get(i));
        }
        ordersItemMap = null;
    }

    private void initRefresh(){
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#0000FF"));
//        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.d3_bg_gray);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchStr = search.getText().toString();

                dataList.clear();
                tHelperAdapter.notifyDataSetChanged();
                setAllEnabled(false);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getList(searchStr);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (tHelperAdapter != null) {
                                    tHelperAdapter.notifyDataSetChanged();
                                } else {
                                    tHelperAdapter = new THelperAdapter(context, dataList);
                                    listView.setAdapter(tHelperAdapter);
                                }
                                setAllEnabled(true);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                });
                thread.start();
            }
        });
    }
    private void setAllEnabled(boolean enabled){
        search.setEnabled(enabled);
        chooseMenu.setEnabled(enabled);
    }

    private void initActionBar(String menuTitle){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.title_layout);//设置标题样式
            TextView textView = (TextView) actionBar.getCustomView().findViewById(R.id.display_title);//获取标题布局的textview
            textView.setText(menuTitle);//设置标题名称，menuTitle为String字符串
            actionBar.setHomeButtonEnabled(true);//设置左上角的图标是否可以点击
            actionBar.setDisplayHomeAsUpEnabled(true);//给左上角图标的左边加上一个返回的图标
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_icon_foreground);
            actionBar.setDisplayShowCustomEnabled(true);// 使自定义的普通View能在title栏显示，即actionBar.setCustomView能起作用
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.white)));
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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