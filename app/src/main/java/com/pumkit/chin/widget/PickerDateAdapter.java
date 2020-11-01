package com.pumkit.chin.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.chinchinso.Statics;
import com.pumkit.chin.vo.DatedListData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PickerDateAdapter extends BaseAdapter {

    public static ArrayList<DatedListData> listViewItemList = new ArrayList<DatedListData>() ;

    public PickerDateAdapter(ArrayList<DatedListData> listViewItemList) {
//        if (i == -1) {
//            this.listViewItemList = null;
//        } else {
//            this.listViewItemList = listViewItemList.get(i);
//            Log.e("abc", "listViewItemList = " + listViewItemList.get(i).get(0));
//        }

        this.listViewItemList = listViewItemList;
    }

    @Override
    public int getCount() {
//        Log.e("abc", "데이터 어댑터 = " + listViewItemList.size());
        return listViewItemList.size();
//        return count;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_row_datedlist, parent, false);
        }

        ImageView img_match = (ImageView) convertView.findViewById(R.id.img_match);
        TextView match_name = (TextView) convertView.findViewById(R.id.match_name);
        ImageView icon_arrow_right = (ImageView) convertView.findViewById(R.id.icon_arrow_right);
        ImageView img_blind = (ImageView) convertView.findViewById(R.id.img_blind);
        TextView blind_name = (TextView) convertView.findViewById(R.id.blind_name);

        DatedListData mData = listViewItemList.get(position);

        if (mData.getMatch_pic() == "") {
            img_match.setBackgroundResource(0);
            Picasso.with(context).load(R.drawable.stub_image).placeholder(R.drawable.stub_image).error(R.drawable.stub_image).tag(context).transform(new CircleTransform()).into(img_match);
            Picasso.with(context).load(R.drawable.stub_image).placeholder(R.drawable.stub_image).error(R.drawable.stub_image).tag(context).transform(new CircleTransform()).into(icon_arrow_right);
        } else {
            Picasso.with(context).load(Statics.main_url + mData.getMatch_pic()).placeholder(R.drawable.stub_image).error(R.drawable.stub_image).fit().tag(context).transform(new CircleTransform()).into(img_match);
        }

        match_name.setText(mData.getMatch_name());

        if (mData.getBlind_pic() == "") {
            img_blind.setBackgroundResource(0);
            Picasso.with(context).load(R.drawable.stub_image).placeholder(R.drawable.stub_image).error(R.drawable.stub_image).tag(context).transform(new CircleTransform()).into(img_blind);
        } else {
            Picasso.with(context).load(Statics.main_url + mData.getBlind_pic()).placeholder(R.drawable.stub_image).error(R.drawable.stub_image).fit().tag(context).transform(new CircleTransform()).into(img_blind);
        }
        blind_name.setText(mData.getBlind_name());

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

//    public void addItem(String match_id, String match_name, String match_pic, String blind_id, String blind_name, String blind_pic, String blind_fire) {
//        DatedListData item = new DatedListData(match_id, match_name, match_pic, blind_id, blind_name, blind_pic, blind_fire);
//
////        item.setMatch_id(match_id);
//
//        listViewItemList.add(item);
//    }

}