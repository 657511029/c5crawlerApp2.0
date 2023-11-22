package com.example.update.view.hangknife;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.update.MainActivity;
import com.example.update.R;
import com.example.update.api.HomeApi;
import com.example.update.entity.Hangknife_jewelry;
import com.example.update.entity.Rank_jewelry;
import com.example.update.view.rank.RankListViewAdapter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HangknifeView extends ConstraintLayout {
    private static final String TAG = HangknifeView.class.getSimpleName();

    private Context context;
    private  String element;

    private HangknifeView hangknifeView;

    private List<Hangknife_jewelry> hangknife_jewelryList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

    private ListView listView;

    private EditText hangknife_edits_change;

    private EditText hangknife_edits_min;

    private EditText hangknife_edits_max;

    private Button hangknife_edits_search;

    private Button hangknife_edits_clean;
    private int showView;
    private HangknifeListViewAdapter hangknifeListViewAdapter;

    private List<Hangknife_jewelry> dataList = new ArrayList<>();




    public HangknifeView(Context context){
        super(context);
        this.context = context;
        initView(context);

    }
    public HangknifeView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    public HangknifeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HangknifeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    private void initView(Context context){
        hangknifeView = (HangknifeView) LayoutInflater.from(context).inflate(R.layout.hangknife, this,true);
        initEdits();
        swipeRefreshLayout = (SwipeRefreshLayout)hangknifeView.findViewById(R.id.hangknife_list_refresh);
        initRefresh();
        listView = (ListView) hangknifeView.findViewById(R.id.hangknife_list_result);
        initList();
    }
    private void initRefresh(){
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#0000FF"));
//        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.d3_bg_gray);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(hangknife_edits_min.getText().toString() == null || hangknife_edits_min.getText().toString().equals("")){
                    toastMessage("最小价格不能为空");
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                if(hangknife_edits_max.getText().toString() == null || hangknife_edits_max.getText().toString().equals("")){
                    toastMessage("最大价格不能为空");
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                if(hangknife_edits_change.getText().toString() == null || hangknife_edits_change.getText().toString().equals("")){
                    toastMessage("最小日交易量不能为空");
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                if(Integer.parseInt(hangknife_edits_min.getText().toString()) >= Integer.parseInt(hangknife_edits_max.getText().toString())){
                    toastMessage("最小价格不能大于等于最大价格");
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                setAllEnabled(false);
                hangknife_jewelryList.clear();
                dataList.clear();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getList();
                        hangknifeView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (hangknifeListViewAdapter != null) {
                                    hangknifeListViewAdapter.notifyDataSetChanged();
                                } else {
                                    hangknifeListViewAdapter = new HangknifeListViewAdapter(context, dataList);
                                    listView.setAdapter(hangknifeListViewAdapter);
                                }
                                setAllEnabled(true);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                        Log.e("error",String.valueOf(hangknife_jewelryList.size()));
                    }
                });
                thread.start();
            }
        });
    }

    private  void initList(){
        dataList.clear();
        //设置ListView的适配器
        hangknifeListViewAdapter = new HangknifeListViewAdapter(context, dataList);
        listView.setAdapter(hangknifeListViewAdapter);
    }
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.HeaderBar);
    }
    @SuppressLint("ResourceType")
    private void initEdits(){
        hangknife_edits_min = hangknifeView.findViewById(R.id.hangknife_edits_min);
        hangknife_edits_max = hangknifeView.findViewById(R.id.hangknife_edits_max);
        hangknife_edits_change = hangknifeView.findViewById(R.id.hangknife_edits_change);
        hangknife_edits_search = hangknifeView.findViewById(R.id.hangknife_edits_search);
        hangknife_edits_search.setOnClickListener(new MyClickListener());
        hangknife_edits_clean = hangknifeView.findViewById(R.id.hangknife_edits_clean);
        hangknife_edits_clean.setOnClickListener(new MyCleanClickListener());
    }

    private void setAllEnabled(boolean enabled){
        hangknife_edits_min.setEnabled(enabled);
        hangknife_edits_max.setEnabled(enabled);
        hangknife_edits_change.setEnabled(enabled);
        hangknife_edits_search.setEnabled(enabled);
        hangknife_edits_clean.setEnabled(enabled);
    }
    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick (View v) {
            if(hangknife_edits_min.getText().toString() == null || hangknife_edits_min.getText().toString().equals("")){
                toastMessage("最小价格不能为空");
                return;
            }
            if(hangknife_edits_max.getText().toString() == null || hangknife_edits_max.getText().toString().equals("")){
                toastMessage("最大价格不能为空");
                return;
            }
            if(hangknife_edits_change.getText().toString() == null || hangknife_edits_change.getText().toString().equals("")){
                toastMessage("最小日交易量不能为空");
                return;
            }
            if(Integer.parseInt(hangknife_edits_min.getText().toString()) >= Integer.parseInt(hangknife_edits_max.getText().toString())){
                toastMessage("最小价格不能大于等于最大价格");
                return;
            }
            setAllEnabled(false);
            swipeRefreshLayout.setEnabled(false);
            hangknife_jewelryList.clear();
            dataList.clear();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    getList();
                    hangknifeView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (hangknifeListViewAdapter != null) {
                                hangknifeListViewAdapter.notifyDataSetChanged();
                            } else {
                                hangknifeListViewAdapter = new HangknifeListViewAdapter(context, dataList);
                                listView.setAdapter(hangknifeListViewAdapter);
                            }
                            setAllEnabled(true);
                            swipeRefreshLayout.setEnabled(true);
                        }
                    });
                    Log.e("error",String.valueOf(hangknife_jewelryList.size()));
                }
            });
            thread.start();
        }
    }

    class MyCleanClickListener implements View.OnClickListener {

        @Override
        public void onClick (View v) {
            hangknife_edits_min.setText(null);
            hangknife_edits_max.setText(null);
            hangknife_edits_change.setText(null);

            setAllEnabled(false);
            swipeRefreshLayout.setEnabled(false);
            hangknife_jewelryList.clear();
            dataList.clear();

            if (hangknifeListViewAdapter != null) {
                hangknifeListViewAdapter.notifyDataSetChanged();
            } else {
                hangknifeListViewAdapter = new HangknifeListViewAdapter(context, dataList);
                listView.setAdapter(hangknifeListViewAdapter);
            }
            setAllEnabled(true);
            swipeRefreshLayout.setEnabled(true);
        }
    }

    private void getList(){
        try {
                hangknife_jewelryList = HomeApi.getHangknifeJewelryList(
                        hangknife_edits_min.getText().toString(),
                        hangknife_edits_max.getText().toString(),
                        hangknife_edits_change.getText().toString()
                );
            Hangknife_jewelry hangknife_Jewelry = new Hangknife_jewelry();
            hangknife_Jewelry.setJewelryName("饰品名称");
            hangknife_Jewelry.setMin_sell("最低售价");
            hangknife_Jewelry.setTrade_count_day("日交易量");
            hangknife_Jewelry.setFast_scale("挂刀比例");
            dataList.add(hangknife_Jewelry);
            for(Hangknife_jewelry hangknifeJewelry: hangknife_jewelryList){
                dataList.add(hangknifeJewelry);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void showView(int showView) {
        //将showView转换为二进制数，根据不同位置上的值设置对应View的显示或者隐藏。
        Long data = Long.valueOf(Integer.toBinaryString(showView));
    }

    private void toastMessage(String message){
        hangknifeView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }
        });

    }

}


