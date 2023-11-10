package com.example.update.view.rank;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.update.R;
import com.example.update.api.HomeApi;
import com.example.update.entity.Jewelry;
import com.example.update.entity.Rank_jewelry;
import com.example.update.view.HomeView;
import com.example.update.view.JewelryHomeView;
import com.example.update.view.JewelryListView;
import com.example.update.view.ListViewAdapter;

import org.json.JSONException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;


public class RankView extends ConstraintLayout {
    private static final String TAG = RankView.class.getSimpleName();

    private Context context;
    private  String element;

    private RankView rankView;
    private Map<String,String> spinner_value_map;

    private List<Rank_jewelry>rank_jewelryList = new ArrayList<>();
    private Spinner spinner_platform;



    private Spinner spinner_type;


    private Spinner spinner_sort;

    private Spinner spinner_mode;


    private Spinner spinner_day;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ListView listView;
    private int showView;
    private RankListViewAdapter rankListViewAdapter;

    private int spinnerCount;

    private List<Rank_jewelry> dataList = new ArrayList<>();




    public RankView(Context context){
        super(context);
        this.context = context;
        initView(context);

    }
    public RankView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    public RankView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RankView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    private void initView(Context context){
        rankView = (RankView) LayoutInflater.from(context).inflate(R.layout.rank, this,true);
        initMap();
        initSpinner();
        swipeRefreshLayout = (SwipeRefreshLayout)rankView.findViewById(R.id.rank_list_refresh);
        initRefresh();
        listView = (ListView) rankView.findViewById(R.id.rank_list_result);
        initList();
    }
    private void initRefresh(){
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#0000FF"));
//        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.d3_bg_gray);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                spinnerCount++;
                setAllEnabled(false);
                if(spinnerCount < 5){
                    return;
                }
                rank_jewelryList.clear();
                dataList.clear();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getList();
                        rankView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (rankListViewAdapter != null) {
                                    rankListViewAdapter.notifyDataSetChanged();
                                } else {
                                    rankListViewAdapter = new RankListViewAdapter(context, dataList);
                                    listView.setAdapter(rankListViewAdapter);
                                }
                                setAllEnabled(true);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                        Log.e("error",String.valueOf(rank_jewelryList.size()));
                    }
                });
                thread.start();
            }
        });
    }

    private  void initList(){
        dataList.clear();
        //设置ListView的适配器
        rankListViewAdapter = new RankListViewAdapter(context, dataList);
        listView.setAdapter(rankListViewAdapter);
    }
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.HeaderBar);
    }

    private void initMap(){
        spinner_value_map = new HashMap<>();
        spinner_value_map.put("Buff","1");
        spinner_value_map.put("悠悠有品","2");
        spinner_value_map.put("出售价格","min_sell");
        spinner_value_map.put("出售数量","sell_count");
        spinner_value_map.put("差额","1");
        spinner_value_map.put("比例","2");
        spinner_value_map.put("无对比","3");
        spinner_value_map.put("正序","1");
        spinner_value_map.put("倒序","2");
        spinner_value_map.put("一天","1");
        spinner_value_map.put("一周","7");
        spinner_value_map.put("一月","30");
        spinner_value_map.put("三月","90");
    }
    @SuppressLint("ResourceType")
    private void initSpinner(){
        spinnerCount = 0;
        //声明一个下拉列表的数组适配器
//        ArrayAdapter<String> starAdapter = new ArrayAdapter<String>(context, R.drawable.spinner_item_select,platform_Array);
//        //设置数组适配器的布局样式
//        starAdapter.setDropDownViewResource(R.drawable.spinner_item_dropdown);
        //从布局文件中获取名叫sp_dialog的下拉框
        spinner_platform = rankView.findViewById(R.id.spinner_platform);
        spinner_type = rankView.findViewById(R.id.spinner_type);
        spinner_sort = rankView.findViewById(R.id.spinner_sort);
        spinner_mode = rankView.findViewById(R.id.spinner_mode);
        spinner_day = rankView.findViewById(R.id.spinner_day);
        //设置下拉框的数组适配器
//        sp.setAdapter(starAdapter);
        //设置下拉框默认的显示第一项
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        spinner_platform.setOnItemSelectedListener(new MySelectedListener());
        spinner_type.setOnItemSelectedListener(new MySelectedListener());
        spinner_sort.setOnItemSelectedListener(new MySelectedListener());
        spinner_mode.setOnItemSelectedListener(new MySelectedListener());
        spinner_day.setOnItemSelectedListener(new MySelectedListener());



    }

    private void setAllEnabled(boolean enabled){
        spinner_platform.setEnabled(enabled);
        spinner_type.setEnabled(enabled);
        spinner_sort.setEnabled(enabled);
        spinner_mode.setEnabled(enabled);
        spinner_day.setEnabled(enabled);
    }
    class MySelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            spinnerCount++;
            setAllEnabled(false);
            if(spinnerCount < 5){
                return;
            }
            rank_jewelryList.clear();
            dataList.clear();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    getList();
                    rankView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (rankListViewAdapter != null) {
                                rankListViewAdapter.notifyDataSetChanged();
                            } else {
                                rankListViewAdapter = new RankListViewAdapter(context, dataList);
                                listView.setAdapter(rankListViewAdapter);
                            }
                            setAllEnabled(true);
                        }
                    });
                    Log.e("error",String.valueOf(rank_jewelryList.size()));
                }
            });
            thread.start();
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    private void getList(){
        try {
            while (rank_jewelryList.size() == 0){
                rank_jewelryList = HomeApi.getRankJewelryList(context,spinner_value_map.get(spinner_platform.getSelectedItem().toString()),
                        spinner_value_map.get(spinner_type.getSelectedItem().toString()),
                        spinner_value_map.get(spinner_sort.getSelectedItem().toString()),
                        spinner_value_map.get(spinner_mode.getSelectedItem().toString()),
                        spinner_value_map.get(spinner_day.getSelectedItem().toString()));
            }
            for(Rank_jewelry rankJewelry: rank_jewelryList){
                dataList.add(rankJewelry);
            }
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    private void showView(int showView) {
        //将showView转换为二进制数，根据不同位置上的值设置对应View的显示或者隐藏。
        Long data = Long.valueOf(Integer.toBinaryString(showView));
    }

}

