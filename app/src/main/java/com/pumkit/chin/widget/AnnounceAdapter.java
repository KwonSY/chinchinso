package com.pumkit.chin.widget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.vo.AnnounceData;

import java.util.ArrayList;

public class AnnounceAdapter extends BaseExpandableListAdapter {

//    public ArrayList<AnnounceData> listViewItemList = new ArrayList<AnnounceData>();

    private ArrayList<AnnounceData> groupList =  new ArrayList<AnnounceData>();
    private ArrayList<AnnounceData> childList = new ArrayList<AnnounceData>();
//    private HashMap<Integer, ArrayList<AnnounceData>> mChildHashMap = new HashMap<>();
    private LayoutInflater inflater = null;
    private ViewHolder viewHolder = null;

    public AnnounceAdapter() {

    }

//    public AnnounceAdapter(Context c, ArrayList<AnnounceData> groupList, HashMap<String, ArrayList<AnnounceData>> mChildHashMap) {
//        super();
//        this.inflater = LayoutInflater.from(c);
//        this.groupList = groupList;
////        this.childList = childList;
//        this.mChildHashMap = mChildHashMap;
//    }

    @Override
    public int getGroupCount() {
        if (groupList == null)
            return 0;

        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//        Log.e("abc", "mChildHashMap.size() = " + mChildHashMap.size());
//        if (mChildHashMap.size() == 0)
//            return 0;
//
//        return mChildHashMap.get(groupPosition).size();
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
//        return mChildHashMap.get(groupList.get(groupPosition)).get(childPosition);
        return childList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public class ViewHolder {
        TextView txt_title, txt_date;
        TextView txt_text;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//        final ViewHolder viewHolder;
//        final int pos = position;
        final Context context = parent.getContext();

//        View v = convertView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_row_announce, parent, false);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        viewHolder = new ViewHolder();
//        if (v == null) {
//            viewHolder = new ViewHolder();
//            v = inflater.inflate(R.layout.item_row_announce, parent, false);
//            viewHolder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);
//            viewHolder.txt_date = (TextView) convertView.findViewById(R.id.txt_date);
//            v.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder)v.getTag();
//        }

        viewHolder = new ViewHolder();
        viewHolder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);
        viewHolder.txt_date = (TextView) convertView.findViewById(R.id.txt_date);

        Log.e("abc", "groupPosition = " + groupPosition);

        AnnounceData announceData = groupList.get(groupPosition);

        if(isExpanded) {
            //그룹이 펼쳤을 때
            //holder.
        } else {
            //그룹이 닫혔을 때
        }

//        Date date = new Date(announceData.getDate());
//        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
//        String dateString = announceData.getDate();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
//        Date convertedDate = new Date();
//        try {
//            convertedDate = dateFormat.parse(dateString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        viewHolder.txt_title.setText(announceData.getTitle());
        viewHolder.txt_date.setText(announceData.getDate());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//        final ViewHolder viewHolder;
//        View v = convertView;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_row_announce_child, parent, false);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder = new ViewHolder();
//        if(v == null){
//            viewHolder = new ViewHolder();
//            v = inflater.inflate(R.layout.item_row_announce_child, null);
            viewHolder.txt_text = (TextView) convertView.findViewById(R.id.txt_text);
//            v.setTag(viewHolder);
//        }else{
//            viewHolder = (ViewHolder)v.getTag();
//        }


//        Log.e("abc", "mChildHashMap = " + mChildHashMap);
//        AnnounceData announceData = mChildHashMap.get(groupPosition).get(groupPosition);
        AnnounceData announceData = childList.get(groupPosition);


//        viewHolder.txt_text.setText(getChild(groupPosition, childPosition));
        viewHolder.txt_text.setText(announceData.getText());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void addItem(int sid, String title, String text, String date) {
        date = date.substring(2, 10);

        AnnounceData item = new AnnounceData();

        item.setSid(sid);
        item.setTitle(title);
        item.setText(text);
        item.setDate(date);

        groupList.add(item);
        childList.add(item);
    }

}