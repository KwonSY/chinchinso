package com.pumkit.chin.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.vo.HashData;

import java.util.ArrayList;

public class MyHashAdapter extends BaseAdapter {

    public ArrayList<HashData> listViewItemList = new ArrayList<HashData>() ;

    public MyHashAdapter() {
    }

    public MyHashAdapter(ArrayList<HashData> listViewItemList) {
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

        txt_hash_name.setText("#" + hashData.getHash_name());

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