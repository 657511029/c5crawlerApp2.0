package com.example.update.view.tracking;

import android.app.AlertDialog;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TrackingAddJewelryListView extends ConstraintLayout {
    private static final String TAG = TrackingAddJewelryListView.class.getSimpleName();

    private Context context;
    private  String element;

    private int showView;
    private TrackingAddJewelryListView trackingAddJewelryListView;

    private TrackingAddJewelryListViewAdapter trackingAddJewelryListViewAdapter;

    private TextView tracking_add_jewelry_list_number;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ListView listView;

    private EditText search;

    private String searchStr;


    private List<Jewelry> jewelryList = new ArrayList<>();
    private List<Jewelry> dataList = new ArrayList<>();


    public TrackingAddJewelryListView(Context context) {
        super(context);
        this.context = context;
        initView(context);

    }
    public TrackingAddJewelryListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);

    }
    public TrackingAddJewelryListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TrackingAddJewelryListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    private void initView(Context context) {
        trackingAddJewelryListView = (TrackingAddJewelryListView) LayoutInflater.from(context).inflate(R.layout.tracking_add_jewelry_list, this,true);
        tracking_add_jewelry_list_number = (TextView) trackingAddJewelryListView.findViewById(R.id.tracking_add_jewelry_list_number);
        swipeRefreshLayout = (SwipeRefreshLayout)  trackingAddJewelryListView.findViewById(R.id.tracking_add_jewelry_list_refresh);
        search = (EditText)trackingAddJewelryListView.findViewById(R.id.tracking_add_jewelry_list_search);
        listView = (ListView) trackingAddJewelryListView.findViewById(R.id.tracking_add_jewelry_list_result);
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
        final String items[] = {"追踪"};
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
                                if(items[which].equals("追踪")){
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Object object = TrackingApi.addJewelry(user,dataList.get(position));
                                            if(object instanceof String){
                                                toastMessage("饰品:" + dataList.get(position).getJewelryName() + " 成功加入追踪名单");
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

                dialog.getWindow().setLayout(dm2.widthPixels - lp.x + 50,280);
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
                    trackingAddJewelryListViewAdapter.notifyDataSetChanged();
                    setAllEnabled(false);
                    swipeRefreshLayout.setEnabled(false);
                    tracking_add_jewelry_list_number.setText("搜索中");
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getList(searchStr);
                            } catch (UnsupportedEncodingException e) {
                                setAllEnabled(true);
                                swipeRefreshLayout.setEnabled(true);
                                throw new RuntimeException(e);
                            }
                            trackingAddJewelryListView.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (trackingAddJewelryListViewAdapter != null) {
                                        trackingAddJewelryListViewAdapter.notifyDataSetChanged();
                                    } else {
                                        trackingAddJewelryListViewAdapter = new TrackingAddJewelryListViewAdapter(context, dataList);
                                        listView.setAdapter(trackingAddJewelryListViewAdapter);
                                    }
                                    setAllEnabled(true);
                                    swipeRefreshLayout.setEnabled(true);
                                    tracking_add_jewelry_list_number.setText("件数:" + dataList.size());
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
                if(searchStr == null || searchStr.equals("")){
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                dataList.clear();
                trackingAddJewelryListViewAdapter.notifyDataSetChanged();
                setAllEnabled(false);
                tracking_add_jewelry_list_number.setText("搜索中");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getList(searchStr);
                        } catch (UnsupportedEncodingException e) {
                            setAllEnabled(true);
                            swipeRefreshLayout.setRefreshing(false);
                            throw new RuntimeException(e);

                        }
                        trackingAddJewelryListView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (trackingAddJewelryListViewAdapter != null) {
                                    trackingAddJewelryListViewAdapter.notifyDataSetChanged();
                                } else {
                                    trackingAddJewelryListViewAdapter = new TrackingAddJewelryListViewAdapter(context, dataList);
                                    listView.setAdapter(trackingAddJewelryListViewAdapter);
                                }
                                setAllEnabled(true);
                                tracking_add_jewelry_list_number.setText("件数:" + dataList.size());
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


    private void getList(String searchStr) throws UnsupportedEncodingException {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("user","");
        jewelryList = HomeApi.getJewelryList(searchStr);
        for(Jewelry jewelry: jewelryList){
            dataList.add(jewelry);
        }
        jewelryList.clear();
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
        trackingAddJewelryListView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }
        });
    }

}




