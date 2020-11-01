package com.pumkit.chin.chinchinso;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.pumkit.chin.vo.RejectData;
import com.pumkit.chin.widget.FrPhonesAdapter;
import com.pumkit.chin.widget.OkHttpClientSingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RejectActivity extends Activity {

    private OkHttpClient httpClient;

    private ArrayList<String> phones_arr = new ArrayList<>();
    private ArrayList<String> names_arr = new ArrayList<>();

    TextView title_fr_list1, btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        Intent intent = getIntent();
        phones_arr = intent.getStringArrayListExtra("phones_arr");
        names_arr = intent.getStringArrayListExtra("names_arr");

        title_fr_list1 = (TextView) findViewById(R.id.title_fr_list1);


        ListView listView_fr = (ListView) findViewById(R.id.listView_fr);
        FrPhonesAdapter frPhonesAdapter = new FrPhonesAdapter(phones_arr, names_arr);
        listView_fr.setAdapter(frPhonesAdapter);
        listView_fr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ListView listView = (ListView) parent;
//
//                String dial = (String) listView.getItemAtPosition(position);
//                Log.e("abc", "dial 33 in rjctact = " + dial);
                RejectData rejectData = (RejectData) parent.getItemAtPosition(position);
                Log.e("abc", "dial 33 in rjctact = " + rejectData.getDial());
                title_fr_list1.setText(rejectData.getDial());
            }
        });

        btn_save = (TextView) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(onClickListener);

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(onClickListener);
        btn_back.setOnTouchListener(mOnTouchListener);
//        Button btn_reject = (Button) findViewById(R.id.btn_reject);
//        btn_reject.setOnClickListener(onClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new rejectFrListTask().execute();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_back:
                    onBackPressed();

                    break;
                case R.id.btn_save:
                    new addRejectTask().execute();

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

    private class rejectFrListTask extends AsyncTask<Void, Void, Void> {
        String cnt_reject, match1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "reject_fr_list")
                    .add("my_id", MainActivity.my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();
//                    Log.e("abc", "bodyStr = " + bodyStr);
                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "JoinCheckTask root = " + obj);

                    cnt_reject = obj.getString("cnt");

                    JSONArray arr = obj.getJSONArray("reject_fr");
                    for (int i=0; i<arr.length(); i++) {

                        if (i==0) {
                            match1 = arr.get(i).toString();
                        } else if(i==1) {

                        } else if(i==2) {

                        } else if(i==3) {

                        } else if(i==4) {

                        }
                    }
                }
            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            title_fr_list1.setText(match1);
        }
    }

    private class addRejectTask extends AsyncTask<Void, Void, Void> {
        String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "add_reject")
//                    .add("user_name", user_name.trim())
//                    .add("email", email.trim())
                    .add("my_id", MainActivity.my_id)
                    .add("match1", title_fr_list1.getText().toString())
                    .add("match2", "")
                    .add("match3", "")
                    .add("match4", "")
                    .add("match5", "")
                    .build();
            Log.e("abc", "title_fr_list1.getText().toString() = " + title_fr_list1.getText().toString());

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();
                    Log.e("abc", "addRejectTask bodyStr = " + bodyStr);
                    JSONObject obj = new JSONObject(bodyStr);

                    result = obj.getString("result");
                }
            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }
}