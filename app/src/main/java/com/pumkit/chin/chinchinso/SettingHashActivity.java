package com.pumkit.chin.chinchinso;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pumkit.chin.vo.HashData;
import com.pumkit.chin.widget.FamousHashAdapter;
import com.pumkit.chin.widget.MyHashAdapter;
import com.pumkit.chin.widget.MyOpenHashAdapter;
import com.pumkit.chin.widget.OkHttpClientSingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SettingHashActivity extends AppCompatActivity {

    private OkHttpClient httpClient;

    private MyOpenHashAdapter myOpenHashAdapter;
    private MyHashAdapter myHashAdapter;
    private FamousHashAdapter famousHashAdapter;

    int cnt_openhash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_hash);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        TextView title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setText("소개팅 해쉬관리");

        final ListView listViewOpenHash = (ListView) findViewById(R.id.listView_openhash);
        myOpenHashAdapter = new MyOpenHashAdapter();
        listViewOpenHash.setAdapter(myOpenHashAdapter);
        listViewOpenHash.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final HashData hashData = (HashData) parent.getItemAtPosition(position);

                if (cnt_openhash <= 1) {
                    Toast.makeText(SettingHashActivity.this, "노출하고자 하는 프로필 해쉬를 1개이상 설정해주세요.", Toast.LENGTH_SHORT).show();
                } else {
//                    String hash_name = hashData.getHash_name().replace("#", "");

                    Animation fadeOut = AnimationUtils.loadAnimation(SettingHashActivity.this, R.anim.slowfadeout2);
                    view.findViewById(R.id.txt_hash_name).setAnimation(fadeOut);
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
//                            Toast.makeText(SettingHashActivity.this, "테스트.", Toast.LENGTH_SHORT).show();
//                            view.findViewById(R.id.txt_hash_name).setAnimation(animation);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            new UpdateHashOpenTask().execute(hashData.getSid(), "0");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
            }
        });

        ListView listViewOwnedHash = (ListView) findViewById(R.id.listView_owned_hash);
        myHashAdapter = new MyHashAdapter();
        listViewOwnedHash.setAdapter(myHashAdapter);
        listViewOwnedHash.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashData hashData = (HashData) parent.getItemAtPosition(position);

                if (cnt_openhash > 2) {
                    Toast.makeText(SettingHashActivity.this, "프로필해쉬는 3개까지 설정하세요.", Toast.LENGTH_SHORT).show();
                }else {
                    String hash_name = hashData.getHash_name().replace("#", "");
                    new UpdateHashOpenTask().execute(hashData.getSid(), "1");
                }
            }
        });

        GridView gridViewFamousHash = (GridView) findViewById(R.id.gridView_famouse_hash) ;
        famousHashAdapter = new FamousHashAdapter();
        gridViewFamousHash.setAdapter(famousHashAdapter);
        gridViewFamousHash.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashData hashData = (HashData) parent.getItemAtPosition(position);

                new GetHashTask().execute(hashData.getSid());
            }
        });

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(onClickListener);
        btn_back.setOnTouchListener(mOnTouchListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new MyHashesTask().execute();
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

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private class MyHashesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            cnt_openhash = 0;

            myOpenHashAdapter.clearItemList();
            myHashAdapter.clearItemList();
            famousHashAdapter.clearItemList();
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "my_hash")
                    .add("my_id", MainActivity.my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "마이해쉬돌아감 : " + obj);
                    JSONArray hash_arr = obj.getJSONArray("my_hash");
                    for (int i=0; i<hash_arr.length(); i++) {
                        JSONObject obj2 = hash_arr.getJSONObject(i);

                        String sid = obj2.getString("sid");
                        String hash_name = obj2.getString("hash_name");
                        String open = obj2.getString("open");
                        int i_open = Integer.parseInt(open);

                        if (open.equals("1")) {
                            cnt_openhash++;

                            myOpenHashAdapter.addItem(sid, hash_name, i_open);
                        } else {
                            if (myOpenHashAdapter.getCount() < 3) {
                                myOpenHashAdapter.addItem("0", "프로필해쉬를 담거나 수정해주세요.", 1);
                            }
                            myHashAdapter.addItem(sid, hash_name, i_open);
                        }
                    }

                    JSONArray hash_arr2 = obj.getJSONArray("famous_hash");
                    for (int j=0; j<hash_arr2.length(); j++) {
                        JSONObject obj3 = hash_arr2.getJSONObject(j);

                        String sid = obj3.getString("sid");
                        String hash_name = obj3.getString("hash_name");

                        famousHashAdapter.addItem(sid, hash_name);
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
//            if (!hash1.equals(null)) {
//                txt_hash_name1.setText(hash1);
//            }
//            if (hash2 != null) {
//                txt_hash_name2.setText(hash2);
//            }
//            if (hash3 != null) {
//                txt_hash_name3.setText(hash3);
//            }
            myOpenHashAdapter.notifyDataSetChanged();
            myHashAdapter.notifyDataSetChanged();
            famousHashAdapter.notifyDataSetChanged();
        }
    }

    private class UpdateHashOpenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;

            FormBody body = new FormBody.Builder()
                    .add("opt", "update_hash_open")
                    .add("my_id", MainActivity.my_id)
                    .add("hash_id", params[0])
                    .add("open", params[1])
                    .build();
            Log.e("abc", "공개1 : " + params[0]);
            Log.e("abc", "공개2 : " + params[1]);
            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "해쉬공개설정 : " + obj);
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
            if (result.equals("0")) {
                new MyHashesTask().execute();
            }
        }
    }

    private class GetHashTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "get_hash")
                    .add("my_id", MainActivity.my_id)
                    .add("hash_id", params[0])
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();
                    Log.d("abc", "bodyStr : " + bodyStr);
                    JSONObject obj = new JSONObject(bodyStr);

                    String result = obj.getString("result");
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
            new MyHashesTask().execute();
        }
    }

//    private class DelHashTask extends AsyncTask<String, Void, Void> {
//        @Override
//        protected Void doInBackground(String... params) {
//            FormBody body = new FormBody.Builder()
//                    .add("opt", "get_hash")
//                    .add("my_id", MainActivity.my_id)
//                    .add("hash_id", params[0])
//                    .build();
//
//            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();
//
//            try {
//                okhttp3.Response response = httpClient.newCall(request).execute();
//                if (response.isSuccessful()) {
//                    String bodyStr = response.body().string();
//
//                    JSONObject obj = new JSONObject(bodyStr);
//
//                    String result = obj.getString("result");
//                } else {
////                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
//                }
//
//            } catch (Exception e) {
//                Log.e("abc", "Error : " + e.getMessage());
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            new MyHashesTask().execute();
//        }
//    }

}