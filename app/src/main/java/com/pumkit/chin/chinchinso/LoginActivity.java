package com.pumkit.chin.chinchinso;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.pumkit.chin.widget.Encryption;
import com.pumkit.chin.widget.OkHttpClientSingleton;
import com.pumkit.chin.widget.SessionManager;

import org.json.JSONObject;

import java.io.InputStream;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.pumkit.chin.chinchinso.R.id.edit_phone;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private SessionManager session;

    private OkHttpClient httpClient;

    private String my_id, myGender;
    private String userName;
    private String userId;
    private String profileUrl;


    private EditText edit_id;
    private EditText edit_password;
    private String login_id;
    private String phone, email, password;

    private InputStream is = null;
    private String json = "";

    private String result;

    private ProgressDialog progressDialog;

//    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
//        if(firebaseAuth.getCurrentUser() != null) {
//            finish();
//
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//        }

        session = new SessionManager(getApplicationContext());

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        // 헤쉬키를 가져온다
//        getAppKeyHash();

//        callback = new SessionCallback();                  // 이 두개의 함수 중요함
//        Session.getCurrentSession().addCallback(callback);
//        Session.getCurrentSession().checkAndImplicitOpen();

        edit_id = (EditText) findViewById(edit_phone);
        edit_password = (EditText) findViewById(R.id.edit_password);

        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String sConvertText;
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            String phoneNum = telManager.getLine1Number();
            Log.e("abc", "phoneNum = " + phoneNum);
            sConvertText= "010" + phoneNum.substring(phoneNum.length()-8, phoneNum.length());
        } catch (NullPointerException ex) {
            sConvertText = "";
        }
        edit_id.setText(sConvertText);

        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(onClickListener);

        Button btn_join = (Button) findViewById(R.id.btn_join);
        btn_join.setOnClickListener(onClickListener);

//        Button btn_facebook_login = (Button) findViewById(R.id.btn_facebook_login);
//        btn_facebook_login.setOnClickListener(onClickListener);

        progressDialog = new ProgressDialog(this);


        //facebook
//        callbackManager = CallbackManager.Factory.create();

//        Button btn_facebook_login = (Button) findViewById(R.id.btn_facebook_login);
////        btn_facebook_login.setOnClickListener(onClickListener);
//        btn_facebook_login.setReadPermissions(Arrays.asList("public_profile", "email"));
//        btn_facebook_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        Log.v("result",object.toString());
//                    }
//                });
//
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id,name,email,gender,birthday");
//                graphRequest.setParameters(parameters);
//                graphRequest.executeAsync();
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.e("LoginErr",error.toString());
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_login:
                    userLogin();

                    break;
                case R.id.btn_join:
                    Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
//                case R.id.btn_facebook_login:
//
//                    break;
            }
        }
    };

    private void userLogin() {
        email = edit_id.getText().toString().trim();
        password = edit_password.getText().toString().trim();

        if (TextUtils.isEmpty(edit_id.getText().toString())) {
            Toast.makeText(this, "이메일을 입력하세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
            return;
        }

        new LoginTask().execute();
    }

    private class LoginTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(email.contains("@")) {
                phone = "empty";
                email = edit_id.getText().toString();
                Log.e("abc", "phne emp phone1 = " + phone + ", email = "+ email);
            } else {
                phone = edit_id.getText().toString();
                email = "empty";
                Log.e("abc", "email emp phone2 = " + phone + ", email = "+ email);
            }

            password = edit_password.getText().toString();

            Encryption.setPassword(password);
            Encryption.encryption(password);
            password = Encryption.getPassword();
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "login")
                    .add("phone", phone)
                    .add("email", email)
                    .add("password", password)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "Login root = " + obj);

                    result = obj.getString("result");
                    my_id = obj.getString("sid");

                    if (result.equals("0")) {
                        email = obj.getString("email");
                        myGender = obj.getString("gender");
                        Log.e("abc", "Login myGender = " + myGender);
                    } else if (result.equals("1")) {

                    } else {
////                        progressBar.dismiss();
//
//                        Toast.makeText(getApplicationContext(), "이미 가입한 휴대전화 번호 입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
////                isNetworkStat(LoginActivity.this);
////                buildDialog(LoginActivity.this).show();
//                Log.e("abc", "isConnected(LoginActivity.this) = " + MainActivity.isConnected(LoginActivity.this));
//                if (MainActivity.isConnected(LoginActivity.this) == false) {
//                    MainActivity.buildDialog(LoginActivity.this).show();
////                    buildDialog(LoginActivity.this).show();
//                } else {
//
//                }

                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (result.equals("0")) {

                progressDialog.setMessage("로그인중...");
                progressDialog.show();

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressDialog.dismiss();
                                Log.e("abc", "progressDialog myGender = " + myGender);
                                session.createLoginSession(my_id, myGender);

                                if(task.isSuccessful()){
                                    finish();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                    intent.putExtra("type", type); //vvvvvvvvvvvvvvvvvvvvvvvvvvvv
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

//                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }

                            }
                        });

            } else if (result.equals("1")) {
                //Toast.makeText(getBaseContext(), "비밀번호를 맞지않습니다.", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public boolean isNetworkStat( Context context ) {
        ConnectivityManager manager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo lte_4g = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
        boolean blte_4g = false;
        if(lte_4g != null)
            blte_4g = lte_4g.isConnected();
        if( mobile != null ) {
            if (mobile.isConnected() || wifi.isConnected() || blte_4g)
                return true;
        } else {
            if ( wifi.isConnected() || blte_4g )
                return true;
        }

        AlertDialog.Builder dlg = new AlertDialog.Builder(context);
        dlg.setTitle("네트워크 오류");
        dlg.setMessage("네트워크 상태를 확인해 주십시요.");
        dlg.setIcon(R.drawable.ic_tab_call);
        dlg.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        dlg.show();
        return false;
    }












/*

    // JSON    opt == "login"
    public String POST(String url, LoginVo loginVo) {
        InputStream inputStream = null;
        String result = "";
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

//            // 3. build jsonObject
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.accumulate("result", loginVo.getResult());
//            jsonObject.accumulate("result_data", loginVo.getResult_data());
//
//            // 4. convert JSONObject to JSON to String
//            json = jsonObject.toString();

            // ** Alternative way to convert erson object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(loginVo);

            // 5. set json to StringEntity
//            StringEntity se = new StringEntity(json);

            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("opt", "login"));
            params.add(new BasicNameValuePair("phone", edit_phone.getText().toString()));
            params.add(new BasicNameValuePair("password", edit_password.getText().toString()));

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,
                    HTTP.UTF_8);
            httpPost.setEntity(ent);


            // 6. set httpPost Entity
//            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
//            httpPost.setHeader("Accept", "application/json");
//            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);

//                JSONObject obj = new JSONObject(result);
//                str_result = obj.getString("result");
//                str_result_data = obj.getString("result_data");
            } else {
                result = "Did not work!";
            }

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }


        // 11. return result
        return result;
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

//    @Override
//    public void onClick(View view) {
//
//        switch(view.getId()){
//            case R.id.btnPost:
//                if(!validate())
//                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
//                // call AsynTask to perform network operation on separate thread
//                new HttpAsyncTask().execute("http://hmkcode.appspot.com/jsonservlet");
//                break;
//        }
//
//    }









    private class LoginTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            loginVo = new LoginVo();
//            loginVo.setResult(edit.getText().toString());
//            loginVo.setResult_data(etName.getText().toString());
//            loginVo.setName(etName.getText().toString());
//            loginVo.setCountry(etCountry.getText().toString());
//            loginVo.setTwitter(etTwitter.getText().toString());

            return POST(urls[0], loginVo);
//            return doInBackground();
        }

        @Override
        protected void onPostExecute(String result) {

            progressDialog.setMessage("로그인중...");
            progressDialog.show();
            Log.e("abc", "phone@gmail.com = " + phone + ", password = " + password);
            String email = phone+"@gmail.com";

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.e("abc", "progressDialog dismiss = " + task);
                            progressDialog.dismiss();

                            if(task.isSuccessful()){
                                Log.e("abc", "finish and run ");
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }

                        }
                    });

            try {
                Log.e("abc", "result in LOGIN : " + result);
                JSONObject obj = new JSONObject(result);

                str_result = obj.getString("result");
                str_result_data = obj.getString("result_data");

//                Log.e("abc", "loginVo.getResult() : " + loginVo.getResult());
                Log.e("abc", "str_result = " + str_result + ", str_result_data = " + str_result_data);
                if (str_result.equals("0")) {

                    session.createLoginSession(str_result_data);

//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                    finish();


                } else if (str_result.equals("1")) {
                    Log.e("abc", "xxx =zzzs " + str_result);
                    //Toast.makeText(getBaseContext(), "비밀번호를 맞지않습니다.", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }

    }

    private boolean validate() {
        if (edit_phone.getText().toString().trim().equals(""))
            return false;
//        else if(etCountry.getText().toString().trim().equals(""))
//            return false;
        else if (edit_password.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        result = bufferedReader.readLine();
//        while((line = bufferedReader.readLine()) != null) {
//            result += line;
//            Log.e("abc", "xxx line :" + line);
//        }

        inputStream.close();

        return result;
    }
*/

    // Connection WiFi
    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Internet Disconnection");
        builder.setMessage("네트워크 연결을 재시도해주세요.");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
//        builder.show();

        return builder;
    }
}