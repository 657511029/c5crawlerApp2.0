package com.example.update.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.example.update.R;

import java.util.List;
import java.util.Map;

public class InfoItemArrayAdapter extends BaseAdapter {
    private Context context;//上下文对象
    private List<Map<String,String>> dataList;//ListView显示的数据

    public InfoItemArrayAdapter(@NonNull Context context, @NonNull List<Map<String,String>> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    //每个子项被滚动到屏幕内的时候会被调用
    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        //判断是否有缓存
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.modifyinfoitem_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //得到缓存的布局
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Map<String,String> map = dataList.get(position);
        String name = "";
        String message = "";
        for(Map.Entry<String,String> entry: map.entrySet()){
            name += entry.getKey() + "\n";
            message += entry.getValue() + "\n";
        }
        name = name.trim();
        message = message.trim();
        viewHolder.modifyInfoItem_list_item_name.setText(name);
        viewHolder.modifyInfoItem_list_item_message.setText(message);
        return convertView;
    }
    private final class ViewHolder {


        TextView modifyInfoItem_list_item_name;

        EditText modifyInfoItem_list_item_message;


        /**
         * 构造器
         *
         * @param view 视图组件（ListView的子项视图）
         */
        ViewHolder(View view) {

            modifyInfoItem_list_item_name = (TextView) view.findViewById(R.id.modifyInfoItem_list_item_name);
            modifyInfoItem_list_item_message = (EditText) view.findViewById(R.id.modifyInfoItem_list_item_message);
            modifyInfoItem_list_item_message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            modifyInfoItem_list_item_message.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.blue_4875C6)));
                        } else {
                            // For older versions of Android, you can try using the support library
                            ViewCompat.setBackgroundTintList(modifyInfoItem_list_item_message, ColorStateList.valueOf(context.getResources().getColor(R.color.blue_4875C6)));
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            modifyInfoItem_list_item_message.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.gray_C8C8C8)));
                        } else {
                            // For older versions of Android, you can try using the support library
                            ViewCompat.setBackgroundTintList(modifyInfoItem_list_item_message, ColorStateList.valueOf(context.getResources().getColor(R.color.gray_C8C8C8)));
                        }
                    }
                }
            });
        }
    }


}

