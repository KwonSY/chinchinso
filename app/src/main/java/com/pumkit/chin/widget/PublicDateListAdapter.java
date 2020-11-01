package com.pumkit.chin.widget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.chinchinso.Statics;
import com.pumkit.chin.vo.PublicDateListData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PublicDateListAdapter extends BaseAdapter {

    public ArrayList<PublicDateListData> listViewItemList = new ArrayList<PublicDateListData>() ;

    //ArrayList<PublicDateListData> listViewItemList
    public PublicDateListAdapter() {
//        this.listViewItemList = listViewItemList;
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    public class ViewHolder {
        ImageView match_pic;
        TextView match_name, blind_name;
        ImageView blind_pic;
        TextView hash;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_publicdate_list, parent, false);

            holder = new ViewHolder();
            holder.match_pic = (ImageView) convertView.findViewById(R.id.match_pic);
            holder.match_name = (TextView) convertView.findViewById(R.id.match_name);
            holder.blind_pic = (ImageView) convertView.findViewById(R.id.blind_pic);
            holder.blind_name = (TextView) convertView.findViewById(R.id.blind_name);
            holder.hash = (TextView) convertView.findViewById(R.id.hash);
//            holder.recomm = (TextView) convertView.findViewById(R.id.recomm);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PublicDateListData publicDateListData = listViewItemList.get(position);

        Picasso.with(context).load(Statics.main_url + publicDateListData.getMatch_pic()).fit().centerCrop().placeholder(R.drawable.icon_noprofile_m).error(R.drawable.stub_image).transform(new CircleTransform()).into(holder.match_pic);
        holder.match_name.setText(publicDateListData.getMatch_name());
        Log.e("abc", "xxx2 = " + publicDateListData.getBlind_pic());

        if (publicDateListData.getBlind_pic()!=null) {
            Picasso.with(context).load(Statics.main_url + publicDateListData.getBlind_pic())
                    .placeholder(R.drawable.icon_noprofile).error(R.drawable.stub_image).fit().centerCrop().tag(context)
                    .transform(new CircleTransform()).into(holder.blind_pic);
        } else {
            Picasso.with(context).load(R.drawable.icon_noprofile)
                    .placeholder(R.drawable.icon_noprofile).error(R.drawable.stub_image).fit().centerCrop().tag(context)
                    .transform(new CircleTransform()).into(holder.blind_pic);
        }
        holder.blind_name.setText(publicDateListData.getBlind_name());
        holder.hash.setText(publicDateListData.getHash());

//        holder.recomm.setText(publicDateListData.getRecomm());
//        Log.e("abc", "xxx2 = " + publicDateListData.getPic2());
//        if (publicDateListData.getPic2()!=null) {
//            Picasso.with(context).load(publicDateListData.getPic2()).placeholder(R.drawable.icon_noprofile_m).error(R.drawable.stub_image).fit().centerCrop().transform(new CircleTransform()).into(holder.blind_pic2);
//        } else {
//            holder.blind_pic2.setVisibility(View.GONE);
//        }
//        Log.e("abc", "xxx3 = " + publicDateListData.getPic3());
//        if (publicDateListData.getPic3()!=null) {
//            Picasso.with(context).load(publicDateListData.getPic3()).placeholder(R.drawable.icon_noprofile_m).fit().centerCrop().transform(new CircleTransform()).into(holder.blind_pic3);
//        } else {
//            holder.blind_pic3.setVisibility(View.GONE);
//        }

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

    public void addItem(String match_id, String match_name, String match_pic, String blind_id, String blind_name, String blind_pic, String hash_name) {
        PublicDateListData item = new PublicDateListData();

        item.setMatch_id(match_id);
        item.setMatch_name(match_name);
        item.setMatch_pic(match_pic);
        item.setBlind_id(blind_id);
        item.setBlind_name(blind_name);
        item.setBlind_pic(blind_pic);
        item.setHash(hash_name);
//        item.setRecomm(recomm);

        listViewItemList.add(item);
    }
}