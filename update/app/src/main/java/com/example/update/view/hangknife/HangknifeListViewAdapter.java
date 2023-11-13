package com.example.update.view.hangknife;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.update.R;
import com.example.update.entity.Hangknife_jewelry;
import com.example.update.entity.Rank_jewelry;

import java.util.List;

public class HangknifeListViewAdapter extends BaseAdapter {

    private Context context;//上下文对象
    private List<Hangknife_jewelry> dataList;//ListView显示的数据

    /**
     * 构造器
     *
     * @param context  上下文对象
     * @param dataList 数据
     */
    public HangknifeListViewAdapter(Context context, List<Hangknife_jewelry> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //判断是否有缓存
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.hangknife_table_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //得到缓存的布局
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Hangknife_jewelry hangknifeJewelry = dataList.get(position);
        viewHolder.hangknife_item_name.setText(hangknifeJewelry.getJewelryName());
        viewHolder.hangknife_item_min.setText(hangknifeJewelry.getMin_sell());
        viewHolder.hangknife_item_change.setText(String.valueOf(hangknifeJewelry.getTrade_count_day()));
        viewHolder.hangknife_item_fastScale.setText(hangknifeJewelry.getFast_scale());
        return convertView;
    }

    /**
     * ViewHolder类
     */
    private final class ViewHolder {


        TextView hangknife_item_name;

        TextView hangknife_item_min;

        TextView hangknife_item_fastScale;

        TextView hangknife_item_change;


        /**
         * 构造器
         *
         * @param view 视图组件（ListView的子项视图）
         */
        ViewHolder(View view) {
            hangknife_item_name = (TextView) view.findViewById(R.id.hangknife_item_name);
            hangknife_item_min = (TextView) view.findViewById(R.id.hangknife_item_min);
            hangknife_item_fastScale = (TextView) view.findViewById(R.id.hangknife_item_fastScale);
            hangknife_item_change = (TextView) view.findViewById(R.id.hangknife_item_change);
        }
    }
}
