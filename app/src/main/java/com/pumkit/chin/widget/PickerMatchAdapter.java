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
import com.pumkit.chin.vo.MatchData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.pumkit.chin.chinchinso.R.id.img_match;
import static com.pumkit.chin.chinchinso.R.id.match_name;

public class PickerMatchAdapter extends BaseAdapter {

    public ArrayList<MatchData> listViewItemList = new ArrayList<MatchData>() ;

    public PickerMatchAdapter(ArrayList<MatchData> listViewItemList) {
        this.listViewItemList = listViewItemList;
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    private class ViewHolder {
//        RelativeLayout layout_matchmaker;
        ImageView img_match;
        TextView match_name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_row_match, parent, false);
        }

        holder = new ViewHolder();
//        holder.layout_matchmaker = (RelativeLayout) convertView.findViewById(R.id.layout_matchmaker);
        holder.img_match = (ImageView) convertView.findViewById(R.id.img_match);
        holder.match_name = (TextView) convertView.findViewById(R.id.match_name);

        MatchData matchData = listViewItemList.get(position);

        Log.e("abc", convertView.getTag() + "adadad tMatch_img() = " + matchData.getMatch_img());
        if (matchData.getMatch_img() == "") {
            Log.e("abc", "비었다 ");
            holder.img_match.setBackgroundResource(0);
            Picasso.with(context).load(R.drawable.stub_image).placeholder(R.drawable.stub_image).error(R.drawable.stub_image).fit().tag(context).transform(new CircleTransform()).into(holder.img_match);
        } else {
            Picasso.with(context).load(Statics.main_url+matchData.getMatch_img()).placeholder(R.drawable.stub_image).error(R.drawable.stub_image).fit().tag(context).transform(new CircleTransform()).into(holder.img_match);
        }

        if (matchData.getUser_name() == "") {
            holder.match_name.setText("");
        } else {
            holder.match_name.setText(matchData.getUser_name());
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

    public void addItem(String sid, String user_name, int cnt_blind) {
        MatchData item = new MatchData();

        item.setSid(sid);
        item.setUser_name(user_name);
        item.setCnt_blind(cnt_blind);

        listViewItemList.add(item);
    }

}