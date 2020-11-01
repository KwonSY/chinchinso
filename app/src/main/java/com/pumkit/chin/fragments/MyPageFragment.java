package com.pumkit.chin.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pumkit.chin.chinchinso.MainActivity;
import com.pumkit.chin.chinchinso.ProfileActivity;
import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.chinchinso.RejectActivity;
import com.pumkit.chin.chinchinso.Statics;
import com.pumkit.chin.widget.CircleTransform;
import com.pumkit.chin.widget.MyPokeAdapter;
import com.pumkit.chin.widget.OkHttpClientSingleton;
import com.pumkit.chin.widget.PokeToMeAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MyPageFragment extends Fragment {

    private OkHttpClient httpClient;

    private String my_id = MainActivity.my_id;
    private String my_name, my_pic, status, public_yn;
    private boolean bool_status, bool_public_yn;

    private ArrayList<String> phones_arr = new ArrayList<>();
    private ArrayList<String> names_arr = new ArrayList<>();

    private Switch switch_status, switch_public_yn;
    //    private TextView txt_status;
    LinearLayout layout_poke_to_me;
    private PokeToMeAdapter mPokeToMeAdapter;
    private MyPokeAdapter myPokeAdapter;

    LinearLayout layout_my_poke;


    public MyPageFragment() {
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


        return inflater.inflate(R.layout.fragment_mypage, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initControls();
    }

    private void initControls() {
        phones_arr = MainActivity.phones_arr;
        names_arr = MainActivity.names_arr;

        // Making notification bar transparent
//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }

        ImageView img_profile = (ImageView) getActivity().findViewById(R.id.img_profile);
        img_profile.setOnClickListener(onClickListener);

        switch_status = (Switch) getActivity().findViewById(R.id.switch_status);
        switch_public_yn = (Switch) getActivity().findViewById(R.id.switch_public_yn);
        switch_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str_yn;
                if (isChecked) {
//                    bool_status = true;
                    str_yn = "y";
                } else {
//                    bool_status = false;
                    str_yn = "n";
                    new UpdatePublicDateYnTask().execute(str_yn);
                }

                new UpdateDateYnTask().execute(str_yn);
            }
        });
        switch_public_yn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (bool_status) {
                    String str_yn;
                    if (isChecked) {
                        str_yn = "y";
                    } else {
                        str_yn = "n";
                    }

                    new UpdatePublicDateYnTask().execute(str_yn);
                } else {
                    Toast.makeText(getActivity(), "소개팅 가능 상태를 우선 ON 해주세요.", Toast.LENGTH_SHORT).show();
                    switch_public_yn.setChecked(false);
                }
            }
        });


        layout_poke_to_me = (LinearLayout) getActivity().findViewById(R.id.layout_poke_to_me);
        ListView listView_poke_to_me = (ListView) getActivity().findViewById(R.id.listView_poke_to_me);
        mPokeToMeAdapter = new PokeToMeAdapter();
        listView_poke_to_me.setAdapter(mPokeToMeAdapter);
//        listView_poke_to_me.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                HashData hashData = (HashData) parent.getItemAtPosition(position);
////                new UpdateHashOpenTask().execute(hashData.getSid(), "0");
//                MemberData memberData = (MemberData) parent.getItemAtPosition(position);
//
//                Intent intent = new Intent(getContext(), ChatActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("myFireId", MainActivity.myFireId);
//                intent.putExtra("matchFireId", memberData.getId_firebase());
//                intent.putExtra("yourFireId", memberData.getId_firebase());
//                intent.putExtra("yourUserName", memberData.getUser_name());
//                intent.putExtra("pic1", memberData.getPic1());
//            }
//        });

        layout_my_poke = (LinearLayout) getActivity().findViewById(R.id.layout_my_poke);
        ListView listView_my_poke = (ListView) getActivity().findViewById(R.id.listView_my_poke);
        myPokeAdapter = new MyPokeAdapter();
        listView_my_poke.setAdapter(myPokeAdapter);
//        listView_my_poke.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });

        Button btn_reject = (Button) getActivity().findViewById(R.id.btn_reject);
        btn_reject.setOnClickListener(onClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        new MyPageTask().execute();
    }

    private String txt_switch_public_n = "모두의 소개팅을 참가하지 않습니다.";

    public void setTextStatus(boolean yn) {
        if (yn == true) {
            switch_status.setTextColor(Color.parseColor("#F1594A"));
            switch_status.setText("소개팅이 진행중입니다.");
            switch_status.setChecked(true);
        } else {
            switch_status.setTextColor(Color.parseColor("#CAC5C6"));
            switch_status.setText("소개팅을 잠시 쉽니다. (커플이 되었어요)");

            switch_status.setChecked(false);
            switch_public_yn.setChecked(false);
            switch_public_yn.setTextColor(Color.parseColor("#CAC5C6"));
            switch_public_yn.setText(txt_switch_public_n);
        }
    }

    public void setTextPublicYn(boolean yn) {
        if (yn == true) {
            switch_public_yn.setTextColor(Color.parseColor("#F1594A"));
            switch_public_yn.setText("모두의 소개팅을 참가합니다.");
            switch_public_yn.setChecked(true);
        } else {
            switch_public_yn.setTextColor(Color.parseColor("#CAC5C6"));
            switch_public_yn.setText(txt_switch_public_n);
            switch_public_yn.setChecked(false);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_reject:
                    myPokeAdapter.clearItemList();

                    Intent intent = new Intent(getActivity(), RejectActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("phones_arr", phones_arr);
                    intent.putExtra("names_arr", names_arr);
                    startActivity(intent);

                    break;
                case R.id.img_profile:
                    myPokeAdapter.clearItemList();

                    Intent intent2 = new Intent(getActivity(), ProfileActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent2.putExtra("user_id", my_id);
                    intent2.putExtra("match_id", "");
                    startActivity(intent2);

                    break;
            }
        }
    };

//    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            ImageButton b = (ImageButton) v;
//
//            int action = event.getAction();
//
//            if (action == MotionEvent.ACTION_DOWN) {
//                b.setBackgroundResource(R.drawable.border_transparent);
//            } else if (action == MotionEvent.ACTION_UP) {
//                b.setBackgroundResource(0);
//
//                onBackPressed();
//            } else if (action == MotionEvent.ACTION_CANCEL) {
//                b.setBackgroundResource(0);
//            }
//            return false;
//        }
//    };


    private class MyPageTask extends AsyncTask<Void, Void, Void> {
        ImageView img_profile;
        TextView profile_name, title_poke_to_me, title_my_poke;
        int cnt_poke_to_me, cnt_my_poke;

        @Override
        protected void onPreExecute() {
            img_profile = (ImageView) getActivity().findViewById(R.id.img_profile);
            profile_name = (TextView) getActivity().findViewById(R.id.txt_name);

            title_poke_to_me = (TextView) getActivity().findViewById(R.id.title_poke_to_me);
            title_my_poke = (TextView) getActivity().findViewById(R.id.title_my_poke);
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "my_page")
                    .add("my_id", my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "MyPageTask obj = " + obj);

                    my_name = obj.getString("user_name");
                    my_pic = obj.getString("pic1");
                    status = obj.getString("status");
                    public_yn = obj.getString("public_yn");
                    Log.e("abc", "public_yn = " + public_yn);
                    if (status.equals("y")) {
                        bool_status = true;
                    } else {
                        bool_status = false;
                    }
                    if (public_yn.equals("y")) {
                        bool_public_yn = true;
                    } else {
                        bool_public_yn = false;
                    }

                    String s_cnt_poke_to_me = obj.getString("cnt_poke_to_me");
                    cnt_poke_to_me = Integer.parseInt(s_cnt_poke_to_me);
                    JSONArray arr = obj.getJSONArray("poke_to_me");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj2 = arr.getJSONObject(i);

                        JSONObject obj_match = obj2.getJSONObject("match");
                        String match_id = obj_match.getString("sid");
                        String match_name = obj_match.getString("user_name");
                        String match_pic = obj_match.getString("pic1");
                        String match_fire = obj_match.getString("id_firebase");

                        JSONObject obj_from = obj2.getJSONObject("from");
                        String blind_id = obj_from.getString("sid");
                        String blind_name = obj_from.getString("user_name");
                        String blind_pic = obj_from.getString("pic1");
                        String blind_time_poke = obj_from.getString("time_poke");

                        mPokeToMeAdapter.addItem(blind_id, blind_name, blind_pic, blind_time_poke, match_id);
                    }

                    String s_cnt_my_poke = obj.getString("cnt_my_poke");
                    cnt_my_poke = Integer.parseInt(s_cnt_my_poke);
                    JSONArray arr3 = obj.getJSONArray("my_poke");
                    for (int i=0; i<arr3.length(); i++) {
                        JSONObject obj3 = arr3.getJSONObject(i);

                        JSONObject obj_match = obj3.getJSONObject("match");
                        String match_id = obj_match.getString("sid");
                        String match_name = obj_match.getString("user_name");
                        String match_pic = obj_match.getString("pic1");
                        String match_fire = obj_match.getString("id_firebase");

                        JSONObject obj_to = obj3.getJSONObject("to");
                        String blind_id = obj_to.getString("sid");
                        String blind_name = obj_to.getString("user_name");
                        String blind_pic = obj_to.getString("pic1");
                        String blind_time_poke = obj_to.getString("time_poke");

                        myPokeAdapter.addItem(match_id, blind_id, blind_name, blind_pic, blind_time_poke);
                    }
                } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Picasso.with(getContext()).load(Statics.main_url + my_pic).placeholder(R.drawable.icon_noprofile_circle_m).transform(new CircleTransform()).into(img_profile);
            profile_name.setText(my_name + "님의 프로필");

//            Log.e("abc", "public_yn2 = " + public_yn);

//            switch_status.setChecked(bool_status);
            setTextStatus(bool_status);
//            switch_public_yn.setChecked(bool_public_yn);
            setTextPublicYn(bool_public_yn);

            mPokeToMeAdapter.notifyDataSetChanged();
            myPokeAdapter.notifyDataSetChanged();

            if (cnt_poke_to_me == 0) {
                layout_poke_to_me.setVisibility(View.GONE);
                title_poke_to_me.setVisibility(View.GONE);
            }

            if (cnt_my_poke == 0) {
                layout_my_poke.setVisibility(View.GONE);
                title_my_poke.setVisibility(View.GONE);
            }
        }
    }

    private class UpdateDateYnTask extends AsyncTask<String, Void, Void> {
        String yn;

        @Override
        protected Void doInBackground(String... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "update_date_yn")
                    .add("my_id", my_id)
                    .add("yn", params[0])
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "UpdateDateYnTask obj = " + obj);

                    String result = obj.getString("result");
                    yn = obj.getString("yn");
                } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (yn.equals("y")) {
                bool_status = true;
            } else {
                bool_status = false;
            }

            setTextStatus(bool_status);
        }
    }

    private class UpdatePublicDateYnTask extends AsyncTask<String, Void, Void> {
        String yn;

        @Override
        protected Void doInBackground(String... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "update_publicdate_yn")
                    .add("my_id", my_id)
                    .add("yn", params[0])
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "UPublicYnTask obj = " + obj);

                    String result = obj.getString("result");
                    yn = obj.getString("yn");
                } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (yn.equals("y")) {
                bool_public_yn = true;
            } else {
                bool_public_yn = false;
            }

            setTextPublicYn(bool_public_yn);
        }
    }
}