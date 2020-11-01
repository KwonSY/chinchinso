package com.pumkit.chin.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.vo.HashData;

import java.util.ArrayList;

public class MyOpenHashAdapter extends BaseAdapter {

    public ArrayList<HashData> listViewItemList = new ArrayList<HashData>() ;

    public MyOpenHashAdapter() {
    }

    public MyOpenHashAdapter(ArrayList<HashData> listViewItemList) {
        this.listViewItemList = listViewItemList;
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_row_hash, parent, false);
        }

        TextView txt_hash_name = (TextView) convertView.findViewById(R.id.txt_hash_name);

        HashData hashData = listViewItemList.get(position);

        if (!hashData.getSid().equals("0")) {
            txt_hash_name.setText("#" + hashData.getHash_name());
            txt_hash_name.setTextColor(ContextCompat.getColor(context, R.color.mainColor));

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) txt_hash_name.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            txt_hash_name.setLayoutParams(layoutParams);
        } else {
            txt_hash_name.setText("#" + "공개할 해쉬를 담아주세요");
            txt_hash_name.setTextColor(Color.parseColor("#0d0d0d"));

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) txt_hash_name.getLayoutParams();
//            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.CENTER_IN_PARENT);
            txt_hash_name.setLayoutParams(layoutParams);
        }

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }
    
    public void addItem(String sid, String hash_name, int open) {
        HashData item = new HashData();

        item.setSid(sid);
        item.setHash_name(hash_name);
        item.setOpen(open);

        listViewItemList.add(item);
    }

    public void clearItemList() {
        listViewItemList.clear();
    }
}