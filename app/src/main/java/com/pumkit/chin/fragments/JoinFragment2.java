package com.pumkit.chin.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pumkit.chin.chinchinso.JoinActivity;
import com.pumkit.chin.chinchinso.JoinActivity2;
import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.chinchinso.Statics;
import com.pumkit.chin.vo.UserData;
import com.pumkit.chin.widget.Encryption;
import com.pumkit.chin.widget.OkHttpClientSingleton;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.pumkit.chin.chinchinso.JoinActivity.age;
import static com.pumkit.chin.chinchinso.JoinActivity.email;
import static com.pumkit.chin.chinchinso.JoinActivity.phone;
import static com.pumkit.chin.chinchinso.JoinActivity.psw;
import static com.pumkit.chin.chinchinso.JoinActivity.user_name;

public class JoinFragment2 extends Fragment {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private OkHttpClient httpClient;

//    private AutoCompleteTextView auto_school;
    private List<String> responseList;

//    private ArrayAdapter<String> adapter;

    private EditText edit_userNm, edit_age;
    private ProgressBar progressBar;

    private String gender = "m";
    private String result, my_id, myGender, id_firebase;
    private String token = JoinActivity.token;

    WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join2, container,
                false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initControls();
    }

    private void initControls() {

//        firebaseAuth = FirebaseAuth.getInstance();
//
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//
//        session = new SessionManager(getApplicationContext());
//
//        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        responseList = new ArrayList<String>();

        edit_userNm = (EditText) getView().findViewById(R.id.edit_userNm);
        edit_age = (EditText) getView().findViewById(R.id.edit_age);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);

        RadioButton radio_male = (RadioButton) getView().findViewById(R.id.radio_male);
        RadioButton radio_female = (RadioButton) getView().findViewById(R.id.radio_female);
        radio_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "m";
            }
        });
        radio_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "f";
            }
        });
        radio_male.setChecked(true);

//        Button btn_licensing = (Button) getView().findViewById(R.id.btn_licensing);
//        btn_licensing.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        Button btn_privacy = (Button) getView().findViewById(R.id.btn_privacy);
//        btn_privacy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        webView = (WebView) getView().findViewById(R.id.webview);

        Button btn_goMain = (Button) getView().findViewById(R.id.btn_go_join2);
        btn_goMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_name = edit_userNm.getText().toString().trim();
                age = edit_age.getText().toString().trim();
//                school = auto_school.getText().toString().trim();

                if (user_name.length() == 0 || psw.length() < 6 || phone.length() == 0 || age.length() == 0) {
//                    Toast.makeText(getActivity().getApplicationContext(), "가입에 필요한 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    if (psw.length() < 6) {
                        Toast.makeText(getActivity().getApplicationContext(), "비밀번호를 6자 이상으로 설정해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (phone.length() == 0) {
                        Toast.makeText(getActivity().getApplicationContext(), "핸드폰 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (age.length() == 0) {
                        Toast.makeText(getActivity().getApplicationContext(), "나이를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (user_name.length() == 0) {
                        Toast.makeText(getActivity().getApplicationContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isValidEmail(email)) {
                        new JoinTask().execute();

//                        progressBar.setVisibility(View.VISIBLE);
//                        loadUrl();
//
//                        Handler hd = new Handler();
//                        hd.postDelayed(new JoinCheckhandler(), 1400);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "올바른 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                    }

                    if (phone.length() != 11) {
                        Toast.makeText(getActivity().getApplicationContext(), "올바른 핸드폰 번호가 아닙니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void loadUrl() {
        String str = null;
        Encryption.setPassword(psw);
        Encryption.encryption(psw);

        try {
            str = "opt=" + URLEncoder.encode("join", "UTF-8");
            str += "&user_name=" + URLEncoder.encode(user_name.trim(), "UTF-8");
            str += "&password=" + URLEncoder.encode(Encryption.getPassword(), "UTF-8");
            str += "&email=" + URLEncoder.encode(email, "UTF-8");
            str += "&phone=" + URLEncoder.encode(phone.trim(), "UTF-8");
            str += "&gender=" + URLEncoder.encode(gender, "UTF-8");
            str += "&age=" + URLEncoder.encode(age.trim(), "UTF-8");
//            str += "&school=" + URLEncoder.encode(school.trim(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.postUrl(Statics.main_url + "json/joinmnf.php", str.getBytes());
        webView.setWebViewClient(new WebViewClientClass());
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    // Join_check 실행
    private class JoinCheckhandler implements Runnable {
        public void run() {
            new JoinCheckTask().execute();
        }
    }


    // Join 가입하기
    private class JoinTask extends AsyncTask<Void, Void, Void> {
        String encryp_psw;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Encryption.setPassword(psw);
            Encryption.encryption(psw);
            encryp_psw = Encryption.getPassword();
            Log.e("abc", "xxx encryp_psw = " + encryp_psw);
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "join")
                    .add("user_name", user_name.trim())
                    .add("password", encryp_psw)
                    .add("email", email.trim())
                    .add("phone", phone.trim())
                    .add("gender", gender)
                    .add("age", age.trim())
//                    .add("id_firebase", id_firebase)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "root = " + obj);

                    result = obj.getString("result");
                    if (result.equals("0")) {
                        my_id = obj.getString("my_id");
                        email = obj.getString("email");
                        myGender = obj.getString("gender");
                    } else {

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
            if (result.equals("0")) {
                JoinActivity.session.createLoginSession(my_id, myGender);

                progressBar.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.GONE);


//            new JoinCheckTask().execute();


                //create user
                firebaseAuth.createUserWithEmailAndPassword(email, encryp_psw)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                Toast.makeText(getActivity(), "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                                progressBar.setVisibility(View.GONE);

                                if (!task.isSuccessful()) {
//                                    Toast.makeText(getActivity(), "Authentication failed." + task.getException(),
//                                            Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getActivity(), "가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                                } else {
//                                    Toast.makeText(getActivity(), "친친소에 오신 것을 환영합니다!" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getActivity(), "친친소에 오신 것을 환영합니다!", Toast.LENGTH_SHORT).show();

                                    Log.e("abc", "token = " + token);
                                    UserData userVo = new UserData(user_name, my_id, token, "");
                                    FirebaseUser user = firebaseAuth.getCurrentUser();

                                    databaseReference.child("users").child(user.getUid()).setValue(userVo);

                                    String id_firebase = user.getUid();

                                    Intent intent = new Intent(getActivity(), JoinActivity2.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("my_id", my_id);
                                    intent.putExtra("id_firebase", id_firebase);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            }
                        });
            } else if (result.equals("1")) {
                Toast.makeText(getActivity().getApplicationContext(), "이미 가입한 휴대전화 번호 입니다.", Toast.LENGTH_SHORT).show();
            } else if (result.equals("2")) {
                Toast.makeText(getActivity().getApplicationContext(), "이미 가입한 이메일 또는 휴대폰번호입니다.", Toast.LENGTH_SHORT).show();
            }

        }
    }


    // Join_check 가입확인
    private class JoinCheckTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "join_check")
//                    .add("user_name", user_name.trim())
//                    .add("email", email.trim())
                    .add("phone", phone.trim())
//                    .add("gender", gender)
//                    .add("age", age.trim())
//                    .add("school", school.trim())
                    .build();
            Log.e("abc", "joincheck phone = " + phone);

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "JoinCheckTask root = " + obj);

                    result = obj.getString("result");
                    if (result.equals("0")) {
                        my_id = obj.getString("my_id");
                        user_name = obj.getString("user_name");
                        email = obj.getString("email");
                        myGender = obj.getString("gender");
                    } else {
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
            if (result.equals("0")) {
                JoinActivity.session.createLoginSession(my_id, myGender);

                Log.e("abc", "email : " + email + ", psw : " + psw);

                firebaseAuth.signInWithEmailAndPassword(email, psw)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if(task.isSuccessful()) {
                            token = FirebaseInstanceId.getInstance().getToken();

                            UserData userVo = new UserData(user_name, my_id, token, "");

                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            databaseReference.child("users").child(user.getUid()).setValue(userVo);
                            id_firebase = user.getUid();
                            Log.e("abc", "id_firebase : " + id_firebase);

                            Intent intent = new Intent(getActivity(), JoinActivity2.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("my_id", my_id);
                            intent.putExtra("id_firebase", id_firebase);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                });


            } else if (result.equals("1")) {

            }

        }
    }

    public boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}