package com.pumkit.chin.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.pumkit.chin.chinchinso.MainActivity;
import com.pumkit.chin.chinchinso.ProfileActivity;
import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.chinchinso.Statics;
import com.pumkit.chin.vo.PublicDateListData;
import com.pumkit.chin.widget.OkHttpClientSingleton;
import com.pumkit.chin.widget.PublicDateListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OpenDateFragment extends Fragment {

    private OkHttpClient httpClient;

    private GridView public_list;
    public PublicDateListAdapter publicDateListAdapter;
    private PublicDateListData publicDateListData;

    private RelativeLayout layout_public_n;
    private Button btn_go_public;

    public OpenDateFragment() {
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
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_opendate, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initControls();
    }

    private void initControls() {
        public_list = (GridView) getView().findViewById(R.id.public_list);
        publicDateListAdapter = new PublicDateListAdapter();
        public_list.setAdapter(publicDateListAdapter);

        public_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                publicDateListData = (PublicDateListData) parent.getItemAtPosition(position);

//                Intent intent = new Intent(getActivity(), OnePublicDate.class);
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("match_id", publicDateListData.getMatch_id());
//                intent.putExtra("match_name", publicDateListData.getMatch_name());
//                intent.putExtra("match_pic", publicDateListData.getMatch_pic());
//                intent.putExtra("blind_id", publicDateListData.getBlind_id());
//                intent.putExtra("blind_name", publicDateListData.getBlind_name());
//                intent.putExtra("blind_pic", publicDateListData.getBlind_pic());
//                intent.putExtra("recomm", publicDateListData.getRecomm());

                intent.putExtra("user_id", publicDateListData.getBlind_id());
                startActivity(intent);
            }
        });

        layout_public_n = (RelativeLayout) getView().findViewById(R.id.layout_public_n);

//        ImageView logo_chinchin = (ImageView) getActivity().findViewById(R.id.logo_chinchin);
//        logo_chinchin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment fragment = new DateFragment();
//
//                MainActivity.changeFragment(getView(), fragment);
//            }
//        });
        MainActivity.setLogoClick(getView());

        btn_go_public = (Button) getView().findViewById(R.id.btn_go_public);
        btn_go_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StartPublicDateTask().execute();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        new PublicDateTask().execute();
    }

    public JSONObject public_obj;
    private String public_yn;

    private class PublicDateTask extends AsyncTask<Void, Void, Void> {
        String hash_name = "";

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            FormBody body = new FormBody.Builder()
                    .add("opt", "public_date")
                    .add("my_id", MainActivity.my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();
                    Log.e("abc", "bodyStr : " + bodyStr);
                    public_obj = new JSONObject(bodyStr);

                    public_yn = public_obj.getString("public_yn");
//                    public_auto = public_obj.getString("public_auto");

                    JSONArray arr = public_obj.getJSONArray("public_date");
                    for (int i=0; i<arr.length(); i++) {
                        JSONObject obj2 = arr.getJSONObject(i);

                        JSONObject match_obj = obj2.getJSONObject("match");
                        String match_id = match_obj.getString("sid");
                        String match_name = match_obj.getString("user_name");
                        String match_pic = match_obj.getString("pic1");

                        JSONObject blind_obj = obj2.getJSONObject("blind");
                        String blind_id = blind_obj.getString("sid");
                        String blind_name = blind_obj.getString("user_name");
                        String blind_pic = blind_obj.getString("pic1");
                        String log_yn = blind_obj.getString("log_yn");
                        JSONArray hash_arr = blind_obj.getJSONArray("hash");
                        for (int j=0; j<hash_arr.length(); j++) {
                            JSONObject hash_obj = hash_arr.getJSONObject(j);
                            hash_name += "#" + hash_obj.getString("hash_name");
                        }

                        publicDateListAdapter.addItem(match_id, match_name, match_pic, blind_id, blind_name, blind_pic, hash_name);
                    }

                } else {

                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (public_yn.equals("n")) {
                public_list.setVisibility(View.GONE);
                layout_public_n.setVisibility(View.VISIBLE);
            } else if (public_yn.equals("y")) {
                public_list.setVisibility(View.VISIBLE);
                layout_public_n.setVisibility(View.GONE);

                public_list.setAdapter(publicDateListAdapter);
                publicDateListAdapter.notifyDataSetChanged();
            }

        }
    }

    private class StartPublicDateTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            FormBody body = new FormBody.Builder()
                    .add("opt", "update_publicdate_yn")
                    .add("my_id", MainActivity.my_id)
                    .add("yn", "y")
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);

                    String result = obj.getString("result");
                    String yn = obj.getString("yn");
                } else {

                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new PublicDateTask().execute();
        }
    }
}