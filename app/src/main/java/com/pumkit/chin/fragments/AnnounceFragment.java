package com.pumkit.chin.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.chinchinso.Statics;
import com.pumkit.chin.vo.AnnounceData;
import com.pumkit.chin.widget.AnnounceAdapter;
import com.pumkit.chin.widget.OkHttpClientSingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AnnounceFragment extends Fragment {

    private OkHttpClient httpClient;

    private AnnounceAdapter announceAdapter;

    private ArrayList<AnnounceData> groupList;
    private HashMap<String, ArrayList<AnnounceData>> mChildHashMap;

    ExpandableListView expandableListView;

    public AnnounceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_announce, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        initControls();
    }

    private void initControls() {
//        ListView list_announce = (ListView) getActivity().findViewById(R.id.listView_announce);

        expandableListView = (ExpandableListView) getActivity().findViewById(R.id.expandable_announce);
        announceAdapter = new AnnounceAdapter();
//        announceAdapter = new AnnounceAdapter(getActivity(), groupList, mChildHashMap);
        expandableListView.setAdapter(announceAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return false;
            }
        });

        new AnnounceTask().execute();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private class AnnounceTask extends AsyncTask<Void, Void, Void> {
        String sid, title, text, date;

        @Override
        protected void onPreExecute() {
//            expandableListView = (ExpandableListView) getActivity().findViewById(R.id.expandable_announce);
//            announceAdapter = new AnnounceAdapter(getActivity(), groupList, mChildHashMap);
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "announce")
                    .build();
            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "announce root = " + obj);

                    JSONArray announce_arr = obj.getJSONArray("announce");
//                    cnt_arr = announce_arr.length();
                    for (int i=0; i<announce_arr.length(); i++) {
//                        AnnounceData announceData = new AnnounceData();

                        JSONObject obj2 = announce_arr.getJSONObject(i);
                        sid = obj2.getString("sid");
                        int i_sid = Integer.parseInt(sid);
                        title = obj2.getString("title");
                        text = obj2.getString("text");
                        date = obj2.getString("date");

                        Log.e("abc", "obj2 = " + obj2);

//                        announceData.setSid(sid);
//                        announceData.setTitle(title);
//                        announceData.setText(text);
//                        announceData.setDate(date);

                        announceAdapter.addItem(i_sid, title, text, date);
//                        groupList.add(announceData);
//                        mChildHashMap.put(sid, groupList);
                    }


                }
            } catch (Exception e) {
                Log.e("abc", "Record 실패 = " + e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            expandableListView.setAdapter(announceAdapter);

            announceAdapter.notifyDataSetChanged();
        }
    }
}