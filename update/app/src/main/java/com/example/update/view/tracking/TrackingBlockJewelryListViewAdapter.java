package com.example.update.view.tracking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.update.R;
import com.example.update.entity.Jewelry;

import java.util.List;

public class TrackingBlockJewelryListViewAdapter extends BaseAdapter {

    private Context context;//上下文对象
    private List<Jewelry> dataList;//ListView显示的数据

    /**
     * 构造器
     *
     * @param context  上下文对象
     * @param dataList 数据
     */
    public TrackingBlockJewelryListViewAdapter(Context context, List<Jewelry> dataList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.tracking_block_jewelry_list_item, null);
            viewHolder = new TrackingBlockJewelryListViewAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //得到缓存的布局
            viewHolder = (TrackingBlockJewelryListViewAdapter.ViewHolder) convertView.getTag();
        }
        Jewelry jewelry = dataList.get(position);
        viewHolder.tracking_block_jewelry_list_item_image.setImageBitmap(jewelry.getBitmap());
        viewHolder.tracking_block_jewelry_list_item_name.setText(jewelry.getJewelryName());

        return convertView;
    }

    /**
     * ViewHolder类
     */
    private final class ViewHolder {

        ImageView tracking_block_jewelry_list_item_image;

        TextView tracking_block_jewelry_list_item_name;



        /**
         * 构造器
         *
         * @param view 视图组件（ListView的子项视图）
         */
        ViewHolder(View view) {
            tracking_block_jewelry_list_item_image = (ImageView) view.findViewById(R.id.tracking_block_jewelry_list_item_image);
            tracking_block_jewelry_list_item_name = (TextView) view.findViewById(R.id.tracking_block_jewelry_list_item_name);

        }
    }
}
