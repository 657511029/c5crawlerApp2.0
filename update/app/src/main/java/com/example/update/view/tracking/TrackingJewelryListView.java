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
import android.widget.AdapterView;
import android.widget.EditText;
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

    private String searchStr;


    private List<Jewelry> jewelryList = null;
    private List<Jewelry> dataList = new ArrayList<>();

    int jewelryCount = 0;


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
        initList();
        initSearch();
        initRefresh();
        initListView();
    }

    private void initListView(){
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

                        if(items[which].equals("拉黑")){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Object object = TrackingApi.blockJewelry(user,dataList.get(position).getC5ID());
                                    if(object instanceof String){
                                        dataList.remove(position);
                                        trackingJewelryListView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (trackingJewelryListViewAdapter != null) {
                                                    trackingJewelryListViewAdapter.notifyDataSetChanged();
                                                } else {
                                                    trackingJewelryListViewAdapter = new TrackingJewelryListViewAdapter(context, dataList);
                                                    listView.setAdapter(trackingJewelryListViewAdapter);
                                                }
                                                tracking_jewelry_list_number.setText("件数:" + dataList.size());
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
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Object object = TrackingApi.deleteJewelry(user,dataList.get(position).getC5ID());
                                    if(object instanceof String){
                                        dataList.remove(position);
                                        trackingJewelryListView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (trackingJewelryListViewAdapter != null) {
                                                    trackingJewelryListViewAdapter.notifyDataSetChanged();
                                                } else {
                                                    trackingJewelryListViewAdapter = new TrackingJewelryListViewAdapter(context, dataList);
                                                    listView.setAdapter(trackingJewelryListViewAdapter);
                                                }
                                                tracking_jewelry_list_number.setText("件数:" + dataList.size());
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
                    dataList.clear();
                    setAllEnabled(false);
                    swipeRefreshLayout.setEnabled(false);
                    tracking_jewelry_list_number.setText("搜索中");
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getList(searchStr);
                            trackingJewelryListView.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (trackingJewelryListViewAdapter != null) {
                                        trackingJewelryListViewAdapter.notifyDataSetChanged();
                                    } else {
                                        trackingJewelryListViewAdapter = new TrackingJewelryListViewAdapter(context, dataList);
                                        listView.setAdapter(trackingJewelryListViewAdapter);
                                    }
                                    setAllEnabled(true);
                                    swipeRefreshLayout.setEnabled(true);
                                    tracking_jewelry_list_number.setText("件数:" + dataList.size());
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
                tracking_jewelry_list_number.setText("搜索中");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getList(searchStr);
                        trackingJewelryListView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (trackingJewelryListViewAdapter != null) {
                                    trackingJewelryListViewAdapter.notifyDataSetChanged();
                                } else {
                                    trackingJewelryListViewAdapter = new TrackingJewelryListViewAdapter(context, dataList);
                                    listView.setAdapter(trackingJewelryListViewAdapter);
                                }
                                setAllEnabled(true);
                                tracking_jewelry_list_number.setText("件数:" + dataList.size());
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
        swipeRefreshLayout.setEnabled(false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getList(searchStr);
                trackingJewelryListView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (trackingJewelryListViewAdapter != null) {
                            trackingJewelryListViewAdapter.notifyDataSetChanged();
                        } else {
                            trackingJewelryListViewAdapter = new TrackingJewelryListViewAdapter(context, dataList);
                            listView.setAdapter(trackingJewelryListViewAdapter);
                        }
                        setAllEnabled(true);
                        swipeRefreshLayout.setEnabled(true);
                        tracking_jewelry_list_number.setText("件数:" + dataList.size());
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
                jewelryList = TrackingApi.getJewelryListBySearch(user,"");
            }
        }
        else {
            while (jewelryList == null){
                jewelryList = TrackingApi.getJewelryListBySearch(user,searchStr);
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
        trackingJewelryListView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }
        });
    }

}


