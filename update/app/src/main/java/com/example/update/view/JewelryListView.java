package com.example.update.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.update.R;
import com.example.update.api.HomeApi;
import com.example.update.entity.Jewelry;
import com.example.update.view.loading.LoadingLayout;
import com.example.update.view.tracking.TrackingJewelryListViewAdapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  JewelryListView extends ConstraintLayout {
    private static final String TAG = JewelryListView.class.getSimpleName();

    private Context context;
    private  String element;

    private int showView;

    private JewelryListView jewelryListView;

    private ListViewAdapter listViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    private ListView listView;

    private EditText search;

    private String searchStr;

    private LoadingLayout loadingLayout;
    private List<Jewelry> jewelryList = new ArrayList<>();

    private List<Map<String,Jewelry>> dataList = new ArrayList<>();

    public JewelryListView(Context context) {
        super(context);
        this.context = context;
        initView(context);

    }
    public JewelryListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    public JewelryListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public JewelryListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    private void initView(Context context) {
        jewelryListView = (JewelryListView) LayoutInflater.from(context).inflate(R.layout.jewelry_list, this,true);
        swipeRefreshLayout = (SwipeRefreshLayout) jewelryListView.findViewById(R.id.jewelry_list_refresh);
        listView = (ListView) jewelryListView.findViewById(R.id.jewelry_list_result);
        search = (EditText) jewelryListView.findViewById(R.id.jewelry_list_search);
        loadingLayout = (LoadingLayout) jewelryListView.findViewById(R.id.jewelry_list_loading);

        initList("");
        initSearchExit();
        initRefresh();
//        listView.setSelection(4);
    }

    private  void initList(String keyword){
        dataList.clear();
        //设置ListView的适配器
        listViewAdapter = new ListViewAdapter(context, dataList);
        listView.setAdapter(listViewAdapter);
    }

    private void initSearchExit(){
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){//搜索按键action
//                    hideKeyboard(getContext(),search);
                    InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                    searchStr = search.getText().toString();
                    dataList.clear();
                    listViewAdapter.notifyDataSetChanged();
                    setAllEnabled(false);
                    loadingLayout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setEnabled(false);
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(searchStr)){
                                try {
                                    getDataList(searchStr);
                                } catch (UnsupportedEncodingException | InterruptedException e) {
                                    toastMessage("获取饰品数据失败");
                                }
                            }
                            jewelryListView.post(new Runnable() {
                                @Override
                                public void run() {

                                    loadingLayout.setVisibility(View.GONE);
                                    //通知ListView更改数据源
                                    if (listViewAdapter != null) {
                                        listViewAdapter.notifyDataSetChanged();
                                    } else {
                                        listViewAdapter = new ListViewAdapter(context, dataList);
                                        listView.setAdapter(listViewAdapter);
                                    }
                                    setAllEnabled(true);
                                    swipeRefreshLayout.setEnabled(true);
                                }
                            });

                        }
                    });
                    thread.start();
                }
                return false;
            }
        });
        search.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        search.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue_4875C6)));
                    } else {
                        // For older versions of Android, you can try using the support library
                        ViewCompat.setBackgroundTintList(search, ColorStateList.valueOf(getResources().getColor(R.color.blue_4875C6)));
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        search.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray_C8C8C8)));
                    } else {
                        // For older versions of Android, you can try using the support library
                        ViewCompat.setBackgroundTintList(search, ColorStateList.valueOf(getResources().getColor(R.color.gray_C8C8C8)));
                    }
                }
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
                listViewAdapter.notifyDataSetChanged();
                setAllEnabled(false);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(searchStr)){
                            try {
                                getDataList(searchStr);
                            } catch (UnsupportedEncodingException | InterruptedException e) {
                                toastMessage("获取饰品数据失败");
                            }
                        }
                        jewelryListView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listViewAdapter != null) {
                                    listViewAdapter.notifyDataSetChanged();
                                } else {
                                    listViewAdapter = new ListViewAdapter(context, dataList);
                                    listView.setAdapter(listViewAdapter);
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
    private void getDataList(String keyword) throws UnsupportedEncodingException, InterruptedException {

                try {
                    jewelryList = HomeApi.getJewelryList(keyword);
                    if(jewelryList.size()%2 == 1){
                        Jewelry newJewelry = new Jewelry("","","","");
                        jewelryList.add(newJewelry);
                    }
                    for(int i = 0;i < jewelryList.size();i = i+ 2){
                        Jewelry jewelry_left = jewelryList.get(i);
                        Jewelry jewelry_right = jewelryList.get(i + 1);
                        Map data = new HashMap();
                        data.put("jewelry_left",jewelry_left);
                        data.put("jewelry_right",jewelry_right);
                        dataList.add(data);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
    }
    private void toastMessage(String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
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

    private void setAllEnabled(boolean enabled){
        search.setEnabled(enabled);
    }
    private void showView(int showView) {
        //将showView转换为二进制数，根据不同位置上的值设置对应View的显示或者隐藏。
        Long data = Long.valueOf(Integer.toBinaryString(showView));
    }

}

