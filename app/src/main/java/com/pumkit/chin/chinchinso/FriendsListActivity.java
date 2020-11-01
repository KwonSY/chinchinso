package com.pumkit.chin.chinchinso;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pumkit.chin.vo.MemberData;
import com.pumkit.chin.widget.OkHttpClientSingleton;
import com.pumkit.chin.widget.OneManAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FriendsListActivity extends AppCompatActivity {

    private OkHttpClient httpClient;

    private OneManAdapter mOneManAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendslist);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        TextView title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setText("친구들 목록");

        ListView listViewOwnedHash = (ListView) findViewById(R.id.listView_friend);
        mOneManAdapter = new OneManAdapter();
        listViewOwnedHash.setAdapter(mOneManAdapter);
        listViewOwnedHash.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MemberData memberData = (MemberData) parent.getItemAtPosition(position);

                new ChoosePublicFrTask().execute(memberData.getSid(), memberData.getUser_name());
            }
        });

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(onClickListener);
        btn_back.setOnTouchListener(mOnTouchListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new ListFriendsTask().execute();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_back:
                    onBackPressed();

                    break;
            }
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageButton b = (ImageButton) v;

            int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                b.setBackgroundResource(R.drawable.border_transparent);
            } else if (action == MotionEvent.ACTION_UP) {
                b.setBackgroundResource(0);

                onBackPressed();
            } else if (action == MotionEvent.ACTION_CANCEL) {
                b.setBackgroundResource(0);
            }
            return false;
        }
    };

    private class ListFriendsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "list_friend")
                    .add("my_id", MainActivity.my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "MyHashesTask obj : " + obj);
                    JSONArray hash_arr = obj.getJSONArray("friends");
                    for (int i=0; i<hash_arr.length(); i++) {
                        JSONObject obj2 = hash_arr.getJSONObject(i);

                        String sid = obj2.getString("sid");
                        String user_name = obj2.getString("user_name");
                        String pic = obj2.getString("pic1");
                        mOneManAdapter.addItem(sid, user_name, pic);
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
            mOneManAdapter.notifyDataSetChanged();
        }
    }

    private class ChoosePublicFrTask extends AsyncTask<String, Void, String> {
        String fr_name;
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            fr_name = params[1];

            FormBody body = new FormBody.Builder()
                    .add("opt", "choose_public_friend")
                    .add("my_id", MainActivity.my_id)
                    .add("date1", params[0])
                    .build();

//            RequestBody body = new FormBody.Builder()
//                    .add("opt", "choose_public_friend")
//                    .add("my_id", MainActivity.my_id)
//                    .add("date1", params[0])
//                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);

                    result = obj.getString("result");
                } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
//            mOneManAdapter.notifyDataSetChanged();
            if (result.equals("0")) {
                onBackPressed();
                Toast.makeText(getApplicationContext(), fr_name + "님을 '모두의소개팅'에 추천친구로 지정하였습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "일시적인 네트워크 오류가 발생하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}