package com.example.update.view.tracking;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.update.R;
import com.example.update.api.HomeApi;
import com.example.update.api.TrackingApi;
import com.example.update.entity.Jewelry;
import com.example.update.entity.NotificationOfTracking;
import com.example.update.entity.Rank_jewelry;
import com.example.update.view.JewelryHomeView;
import com.example.update.view.ListViewAdapter;
import com.example.update.view.rank.RankListViewAdapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TrackingJewelryListView extends ConstraintLayout {
    private static final String TAG = TrackingJewelryListView.class.getSimpleName();

    private Context context;
    private  String element;

    private int showView;
    private TrackingJewelryListView trackingJewelryListView;

    private TrackingJewelryListViewAdapter trackingJewelryListViewAdapter;

    private TextView tracking_jewelry_list_number;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ListView listView;

    private EditText search;

    private TextView footText;

    private String searchStr;


    private Map<String,Object> result = null;
    private List<Jewelry> dataList = new ArrayList<>();

    private int jewelryCount = 20;

    private int jewelryNumber = 0;

    //状态:初始化中
    private final int initializing = 0;

    //状态:检索中
    private final int searching = 1;

    //状态:刷新中
    private final int refreshing = 2;


    //状态:加载更多中
    private final int loading = 3;

    //状态:正常
    private final int common = 4;

    private final int blocking = 5;

    private final int deleting = 6;


    private int flag;


    public TrackingJewelryListView(Context context) {
        super(context);
        this.context = context;
        initView(context);

    }
    public TrackingJewelryListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);

    }
    public TrackingJewelryListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TrackingJewelryListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    private void initView(Context context) {
        trackingJewelryListView = (TrackingJewelryListView) LayoutInflater.from(context).inflate(R.layout.tracking_jewelry_list, this,true);
        tracking_jewelry_list_number = (TextView) trackingJewelryListView.findViewById(R.id.tracking_jewelry_list_number);
        swipeRefreshLayout = (SwipeRefreshLayout)  trackingJewelryListView.findViewById(R.id.tracking_jewelry_list_refresh);
        search = (EditText)trackingJewelryListView.findViewById(R.id.tracking_jewelry_list_search);
        listView = (ListView) trackingJewelryListView.findViewById(R.id.tracking_jewelry_list_result);
        footText = (TextView) LayoutInflater.from(context).inflate(R.layout.foot_text, null);
        initList();
        initSearch();
        initRefresh();
        initListView();
    }

    private void initListView(){

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    // 滚动停止时
                    case SCROLL_STATE_IDLE:
                        // 判断是否滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            if(flag != common){
                                return;
                            }
                            if (dataList.size() < jewelryNumber) {
                                searchStr = search.getText().toString();
                                if(dataList.size() == 0){
                                    footText.setText("没有更多饰品");
                                    footText.setTextColor(getResources().getColor(R.color.gray_CDCDCD));
                                    footText.setTextSize(12);
                                }
                                else {
                                    flag = loading;
                                    setAllEnabled(false);
                                    swipeRefreshLayout.setEnabled(false);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getList(searchStr, dataList.get(dataList.size() - 1).getC5ID());
                                            trackingJewelryListView.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (trackingJewelryListViewAdapter != null) {
                                                        trackingJewelryListViewAdapter.notifyDataSetChanged();
                                                    } else {
                                                        trackingJewelryListViewAdapter = new TrackingJewelryListViewAdapter(context, dataList);
                                                        listView.setAdapter(trackingJewelryListViewAdapter);
                                                        listView.addFooterView(footText);
                                                    }
                                                    tracking_jewelry_list_number.setText("件数:" + jewelryNumber);
                                                    setAllEnabled(true);
                                                    swipeRefreshLayout.setEnabled(true);
                                                    if(dataList.size() >= jewelryNumber){
                                                        footText.setText("没有更多饰品");
                                                        footText.setTextColor(getResources().getColor(R.color.gray_CDCDCD));
                                                        footText.setTextSize(12);
                                                    }else {
                                                        footText.setText("正在加载中");
                                                        footText.setTextColor(getResources().getColor(R.color.green_268C36));
                                                        footText.setTextSize(13);
                                                    }
                                                    flag = common;

                                                }
                                            });
                                        }
                                    }).start();
                                }
                                // 数据变化后通知adapter
                            } else {
                                // 模拟没有跟多数据了
                                footText.setText("没有更多饰品");
                                footText.setTextColor(getResources().getColor(R.color.gray_CDCDCD));
                                footText.setTextSize(12);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        final String items[] = {"删除", "拉黑"};
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int[] location = new  int[2] ;
                view.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = context.getSharedPreferences("user", context.MODE_PRIVATE);
                        String user = sharedPreferences.getString("user","");
                        if(flag != common){
                            return;
                        }
                        if(items[which].equals("拉黑")){

                            flag = blocking;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Object object = TrackingApi.blockJewelry(user,dataList.get(position).getC5ID());
                                    if(object instanceof String){
                                        dataList.remove(position);
                                        jewelryNumber--;
                                        trackingJewelryListView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (trackingJewelryListViewAdapter != null) {
                                                    trackingJewelryListViewAdapter.notifyDataSetChanged();
                                                } else {
                                                    trackingJewelryListViewAdapter = new TrackingJewelryListViewAdapter(context, dataList);
                                                    listView.setAdapter(trackingJewelryListViewAdapter);
                                                    listView.addFooterView(footText);
                                                }
                                                tracking_jewelry_list_number.setText("件数:" + jewelryNumber);
                                                flag = common;
                                            }
                                        });
                                    }
                                    else if(object instanceof NotificationOfTracking){
                                        toastMessage(((NotificationOfTracking) object).getMessage());
                                    }
                                }
                            }).start();
                        }
                        else if(items[which].equals("删除")){
                            flag = deleting;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Object object = TrackingApi.deleteJewelry(user,dataList.get(position).getC5ID());
                                    if(object instanceof String){
                                        dataList.remove(position);
                                        jewelryNumber--;
                                        trackingJewelryListView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (trackingJewelryListViewAdapter != null) {
                                                    trackingJewelryListViewAdapter.notifyDataSetChanged();
                                                } else {
                                                    trackingJewelryListViewAdapter = new TrackingJewelryListViewAdapter(context, dataList);
                                                    listView.setAdapter(trackingJewelryListViewAdapter);
                                                    listView.addFooterView(footText);
                                                }
                                                tracking_jewelry_list_number.setText("件数:" + jewelryNumber);
                                                flag = common;
                                            }
                                        });
                                    }
                                    else if(object instanceof NotificationOfTracking){
                                        toastMessage(((NotificationOfTracking) object).getMessage());
                                    }
                                }
                            }).start();
                        }
                    }
                }).create();
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
                lp.x = x + 300; // 新位置X坐标
                lp.y = y - 120; // 新位置Y坐标
                dialogWindow.setAttributes(lp);
                dialogWindow.setDimAmount(0f);
                dialog.show();
                DisplayMetrics dm2 = getResources().getDisplayMetrics();
                dialog.getWindow().setLayout(dm2.widthPixels - lp.x + 50,420);
                return false;
            }
        });

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
                    if(flag != common){
                        return false;
                    }
                    flag = searching;
                    dataList.clear();
                    trackingJewelryListViewAdapter.notifyDataSetChanged();
                    footText.setVisibility(View.GONE);
                    setAllEnabled(false);
                    swipeRefreshLayout.setEnabled(false);
                    tracking_jewelry_list_number.setText("搜索中");
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getList(searchStr,"-");
                            trackingJewelryListView.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (trackingJewelryListViewAdapter != null) {
                                        trackingJewelryListViewAdapter.notifyDataSetChanged();
                                    } else {
                                        trackingJewelryListViewAdapter = new TrackingJewelryListViewAdapter(context, dataList);
                                        listView.setAdapter(trackingJewelryListViewAdapter);
                                        listView.addFooterView(footText);
                                    }
                                    setAllEnabled(true);
                                    swipeRefreshLayout.setEnabled(true);
                                    if(dataList.size() >= jewelryNumber){
                                        footText.setText("没有更多饰品");
                                        footText.setTextColor(getResources().getColor(R.color.gray_CDCDCD));
                                        footText.setTextSize(12);
                                    }else {
                                        footText.setText("正在加载中");
                                        footText.setTextColor(getResources().getColor(R.color.green_268C36));
                                        footText.setTextSize(13);
                                    }
                                    footText.setVisibility(View.VISIBLE);
                                    tracking_jewelry_list_number.setText("件数:" + jewelryNumber);
                                    flag = common;
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
                if(flag != common){
                    return;
                }
                flag = refreshing;
                dataList.clear();
                trackingJewelryListViewAdapter.notifyDataSetChanged();
                footText.setVisibility(View.GONE);
                setAllEnabled(false);
                tracking_jewelry_list_number.setText("搜索中");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getList(searchStr,"-");
                        trackingJewelryListView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (trackingJewelryListViewAdapter != null) {
                                    trackingJewelryListViewAdapter.notifyDataSetChanged();
                                } else {
                                    trackingJewelryListViewAdapter = new TrackingJewelryListViewAdapter(context, dataList);
                                    listView.setAdapter(trackingJewelryListViewAdapter);
                                    listView.addFooterView(footText);
                                }
                                setAllEnabled(true);
                                if(dataList.size() >= jewelryNumber){
                                    footText.setText("没有更多饰品");
                                    footText.setTextColor(getResources().getColor(R.color.gray_CDCDCD));
                                    footText.setTextSize(12);
                                }else {
                                    footText.setText("正在加载中");
                                    footText.setTextColor(getResources().getColor(R.color.green_268C36));
                                    footText.setTextSize(13);
                                }
                                footText.setVisibility(View.VISIBLE);
                                tracking_jewelry_list_number.setText("件数:" + jewelryNumber);
                                swipeRefreshLayout.setRefreshing(false);
                                flag = common;
                            }
                        });
                    }
                });
                thread.start();
            }
        });
    }

    private void initList(){
        dataList.clear();
        setAllEnabled(false);
        swipeRefreshLayout.setEnabled(false);
        flag = initializing;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getList(searchStr,"-");
                trackingJewelryListView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (trackingJewelryListViewAdapter != null) {
                            trackingJewelryListViewAdapter.notifyDataSetChanged();
                        } else {
                            trackingJewelryListViewAdapter = new TrackingJewelryListViewAdapter(context, dataList);
                            listView.setAdapter(trackingJewelryListViewAdapter);
                            listView.addFooterView(footText);
                        }
                        setAllEnabled(true);
                        swipeRefreshLayout.setEnabled(true);
                        if(dataList.size() >= jewelryNumber){
                            footText.setText("没有更多饰品");
                            footText.setTextColor(getResources().getColor(R.color.gray_CDCDCD));
                            footText.setTextSize(12);
                        }else {
                            footText.setText("正在加载中");
                            footText.setTextColor(getResources().getColor(R.color.green_268C36));
                            footText.setTextSize(13);
                        }
                        tracking_jewelry_list_number.setText("件数:" + jewelryNumber);
                        flag = common;
                    }
                });
                Log.e("error",String.valueOf(dataList.size()));
            }
        });
        thread.start();
    }

    private void getList(String searchStr,String offset){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("user","");
        if(TextUtils.isEmpty(searchStr)){
            while (result == null){
                result = TrackingApi.getJewelryListBySearch(user,"",offset,jewelryCount,dataList.size());
            }
        }
        else {
            while (result == null){
                result = TrackingApi.getJewelryListBySearch(user,searchStr,offset,jewelryCount,dataList.size());
            }
        }
        jewelryNumber = (int)result.get("number");
        List<Jewelry> jewelryList = (List<Jewelry>)result.get("jewelryList");
        for(Jewelry jewelry: jewelryList){
            dataList.add(jewelry);
        }
        result = null;
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
        trackingJewelryListView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }
        });
    }

}


