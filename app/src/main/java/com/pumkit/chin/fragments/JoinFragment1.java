package com.pumkit.chin.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.pumkit.chin.chinchinso.JoinActivity;
import com.pumkit.chin.chinchinso.R;

import static android.content.Context.TELEPHONY_SERVICE;

public class JoinFragment1 extends Fragment {

    private TelephonyManager telManager;
//    private OkHttpClient httpClient;

//    private FirebaseAuth firebaseAuth;
//    private DatabaseReference databaseReference;


    private String sConvertText;
//    private String sConvertText, password, phone;
//    private String email = null;

    private VideoView videoView;
    private EditText edit_email, edit_phone, edit_password;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        telManager = (TelephonyManager) getActivity().getSystemService(TELEPHONY_SERVICE);

        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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
            sConvertText= "010" + phoneNum.substring(phoneNum.length()-8, phoneNum.length());
        } catch (NullPointerException ex) {
            sConvertText = "휴대폰번호가 없습니다.";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join1, container,
                false);
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



//        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

//        responseList = new ArrayList<String>();

        edit_password = (EditText) getView().findViewById(R.id.edit_password);
        edit_email = (EditText) getView().findViewById(R.id.edit_email);
        edit_phone = (EditText) getView().findViewById(R.id.edit_phone);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        edit_phone.setText(sConvertText);

        Button btn_go_join1_2 = (Button) getView().findViewById(R.id.btn_go_join1_2);
        btn_go_join1_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                username = edit_userNm.getText().toString();
                JoinActivity.email = edit_email.getText().toString().trim();
                JoinActivity.phone = edit_phone.getText().toString().trim();
                JoinActivity.psw = edit_password.getText().toString().trim();
//                age = edit_age.getText().toString();
//                school = auto_school.getText().toString();

                if (JoinActivity.psw.length() < 6 || JoinActivity.phone.length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), "비밀번호를 6자 이상으로 설정해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    if (isValidEmail(JoinActivity.email)) {
//                        new JoinTask().execute();
//                        Intent intent = new Intent(getActivity(), JoinActivity1_2.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra("email",email);
//                        intent.putExtra("phone",phone);
//                        intent.putExtra("password",password);
//                        startActivity(intent);
                        JoinActivity.mViewPager.setCurrentItem(1);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "올바른 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}