package com.pumkit.chin.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.pumkit.chin.chinchinso.MainActivity;
import com.pumkit.chin.chinchinso.PayHeartActivity;
import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.chinchinso.ResetPswActivity;
import com.pumkit.chin.chinchinso.Statics;
import com.pumkit.chin.widget.OkHttpClientSingleton;
import com.pumkit.chin.widget.SessionManager;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SettingFragment extends Fragment {

    private OkHttpClient httpClient;
    private FirebaseAuth firebaseAuth;
    private SessionManager session;

    private boolean bool_alarm_yn;
    private Switch switch_alarm;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        firebaseAuth = FirebaseAuth.getInstance();
        session = new SessionManager(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        initControls();
    }

    private void initControls() {

        switch_alarm = (Switch) getActivity().findViewById(R.id.switch_alarm);
        switch_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str_yn;
                if (isChecked) {
                    str_yn = "y";
                    bool_alarm_yn = true;
                    new UpdateAlarmYnTask().execute("y");
                } else {
                    str_yn = "n";
                    bool_alarm_yn = false;
                    new UpdateAlarmYnTask().execute("n");
                }

                new UpdateAlarmYnTask().execute(str_yn);
            }
        });

        TextView btn_pay_heart = (TextView) getActivity().findViewById(R.id.btn_pay_heart);
        btn_pay_heart.setOnClickListener(onClickListener);

        TextView reset_password = getActivity().findViewById(R.id.reset_password);
        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new ResetPswTask().execute();

                Intent intent = new Intent(getActivity(), ResetPswActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("my_id", MainActivity.my_id);
                startActivity(intent);
            }
        });

        Button btn_logout = getActivity().findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();

                session.logoutUser();

                getActivity().finish();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("my_id", MainActivity.my_id);
                startActivity(intent);
            }
        });

        MainActivity.setLogoClick(getView());
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_pay_heart:
                    Intent intent = new Intent(getActivity(), PayHeartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        new MySettingTask().execute();
    }

    private class MySettingTask extends AsyncTask<String, Void, Void> {
        String alarm;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "my_setting")
                    .add("my_id", MainActivity.my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "Alarm obj = " + obj);

                    alarm = obj.getString("alarm");
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
            if (alarm.equals("y")) {
                bool_alarm_yn = true;
                switch_alarm.setChecked(bool_alarm_yn);
            } else {
                bool_alarm_yn = false;
                switch_alarm.setChecked(bool_alarm_yn);
            }
//            switch_alarm.setChecked(bool_alarm_yn);
//            setTextPublicYn(bool_public_yn);
        }
    }

    private class UpdateAlarmYnTask extends AsyncTask<String, Void, Void> {
        String yn;

        @Override
        protected Void doInBackground(String... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "update_alarm_yn")
                    .add("my_id", MainActivity.my_id)
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
//            if (yn.equals("y")) {
//                bool_public_yn = true;
//            } else {
//                bool_public_yn = false;
//            }
//
//            setTextPublicYn(bool_public_yn);
        }
    }
}