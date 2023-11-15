package com.example.update.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.update.R;
import com.example.update.entity.Rank_jewelry;
import com.example.update.view.rank.RankListViewAdapter;

import java.util.List;
import java.util.Map;

public class InfoArrayAdapter extends BaseAdapter {
    private Context context;//上下文对象
    private List<Map<String,String>> dataList;//ListView显示的数据
    public InfoArrayAdapter(@NonNull Context context,@NonNull List<Map<String,String>> dataList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.modifyinfo_list_item, null);
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
        viewHolder.info_list_item_name.setText(name);
        viewHolder.info_list_item_message.setText(message);
        return convertView;
    }
    private final class ViewHolder {


        TextView info_list_item_name;

        TextView info_list_item_message;


        /**
         * 构造器
         *
         * @param view 视图组件（ListView的子项视图）
         */
        ViewHolder(View view) {

            info_list_item_name = (TextView) view.findViewById(R.id.info_list_item_name);
            info_list_item_message = (TextView) view.findViewById(R.id.info_list_item_message);
        }
    }


}
