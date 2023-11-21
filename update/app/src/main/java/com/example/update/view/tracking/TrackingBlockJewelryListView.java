package com.example.update.view.tracking;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.update.R;
import com.example.update.api.TrackingApi;
import com.example.update.entity.Jewelry;

import java.util.ArrayList;
import java.util.List;

public class TrackingBlockJewelryListView extends ConstraintLayout {
    private static final String TAG = TrackingBlockJewelryListView.class.getSimpleName();

    private Context context;
    private  String element;

    private int showView;
    private TrackingBlockJewelryListView trackingBlockJewelryListView;

    private TrackingBlockJewelryListViewAdapter trackingBlockJewelryListViewAdapter;

    private TextView tracking_block_jewelry_list_number;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ListView listView;

    private EditText search;

    private String searchStr;


    private List<Jewelry> jewelryList = null;
    private List<Jewelry> dataList = new ArrayList<>();


    public TrackingBlockJewelryListView(Context context) {
        super(context);
        this.context = context;
        initView(context);

    }
    public TrackingBlockJewelryListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);

    }
    public TrackingBlockJewelryListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TrackingBlockJewelryListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    private void initView(Context context) {
        trackingBlockJewelryListView = (TrackingBlockJewelryListView) LayoutInflater.from(context).inflate(R.layout.tracking_block_jewelry_list, this,true);
        tracking_block_jewelry_list_number = (TextView) trackingBlockJewelryListView.findViewById(R.id.tracking_block_jewelry_list_number);
        swipeRefreshLayout = (SwipeRefreshLayout)  trackingBlockJewelryListView.findViewById(R.id.tracking_block_jewelry_list_refresh);
        search = (EditText)trackingBlockJewelryListView.findViewById(R.id.tracking_block_jewelry_list_search);
        listView = (ListView) trackingBlockJewelryListView.findViewById(R.id.tracking_block_jewelry_list_result);
        initList();
        initSearch();
        initRefresh();
    }

    private void initSearch(){
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){//搜索按键action
//                    hideKeyboard(getContext(),search);
                    InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                    searchStr = search.getText().toString();
                    dataList.clear();
                    tracking_block_jewelry_list_number.setText("搜索中");
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getList(searchStr);
                            trackingBlockJewelryListView.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (trackingBlockJewelryListViewAdapter != null) {
                                        trackingBlockJewelryListViewAdapter.notifyDataSetChanged();
                                    } else {
                                        trackingBlockJewelryListViewAdapter = new TrackingBlockJewelryListViewAdapter(context, dataList);
                                        listView.setAdapter(trackingBlockJewelryListViewAdapter);
                                    }
                                    tracking_block_jewelry_list_number.setText("件数:" + dataList.size());
                                }
                            });
                            Log.e("error",String.valueOf(dataList.size()));
                        }
                    });
                    thread.start();
                    //通知ListView更改数据源

                }
                return false;
            }
        });
    }
    private void initRefresh(){
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#0000FF"));
//        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.d3_bg_gray);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchStr = search.getText().toString();
                dataList.clear();
                setAllEnabled(false);
                tracking_block_jewelry_list_number.setText("搜索中");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getList(searchStr);
                        trackingBlockJewelryListView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (trackingBlockJewelryListViewAdapter != null) {
                                    trackingBlockJewelryListViewAdapter.notifyDataSetChanged();
                                } else {
                                    trackingBlockJewelryListViewAdapter = new TrackingBlockJewelryListViewAdapter(context, dataList);
                                    listView.setAdapter(trackingBlockJewelryListViewAdapter);
                                }
                                setAllEnabled(true);
                                tracking_block_jewelry_list_number.setText("件数:" + dataList.size());
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                        Log.e("error",String.valueOf(dataList.size()));
                    }
                });
                thread.start();
            }
        });
    }

    private void initList(){
        dataList.clear();
        setAllEnabled(false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getList(searchStr);
                trackingBlockJewelryListView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (trackingBlockJewelryListViewAdapter != null) {
                            trackingBlockJewelryListViewAdapter.notifyDataSetChanged();
                        } else {
                            trackingBlockJewelryListViewAdapter = new TrackingBlockJewelryListViewAdapter(context, dataList);
                            listView.setAdapter(trackingBlockJewelryListViewAdapter);
                        }
                        setAllEnabled(true);
                        tracking_block_jewelry_list_number.setText("件数:" + dataList.size());
                    }
                });
                Log.e("error",String.valueOf(dataList.size()));
            }
        });
        thread.start();
    }

    private void getList(String searchStr){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("user","");
        if(TextUtils.isEmpty(searchStr)){
            while (jewelryList == null){
                jewelryList = TrackingApi.getBlockJewelryListBySearch(user,"");
            }
        }
        else {
            while (jewelryList == null){
                jewelryList = TrackingApi.getBlockJewelryListBySearch(user,searchStr);
            }
        }

        for(Jewelry jewelry: jewelryList){
            dataList.add(jewelry);
        }
        jewelryList = null;
    }


    private void setAllEnabled(boolean enabled){
        search.setEnabled(enabled);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.HeaderBar);
        //获取title_text属性
        //获取show_views属性，如果没有设置时默认为0x26
//        showView = mTypedArray.getInt(R.styleable.HeaderBar_show_views, 0x26);
//        text_center.setTextColor(mTypedArray.getColor(R.styleable.HeaderBar_title_text_color, Color.WHITE));
//        mTypedArray.recycle();
//        showView(showView);
    }

    private void showView(int showView) {
        //将showView转换为二进制数，根据不同位置上的值设置对应View的显示或者隐藏。
        Long data = Long.valueOf(Integer.toBinaryString(showView));
    }

    private void toastMessage(String message){
        trackingBlockJewelryListView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }
        });
    }

}



