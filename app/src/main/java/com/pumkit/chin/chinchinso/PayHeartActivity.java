package com.pumkit.chin.chinchinso;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.pumkit.chin.widget.OkHttpClientSingleton;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class PayHeartActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private OkHttpClient httpClient;

    // PRODUCT & SUBSCRIPTION IDS
    //"android.test.purchased"
    private static final String PRODUCT_ID = "com.pumkit.chin.chinchinso";
    private static final String SUBSCRIPTION_ID = "com.pumkit.chin.chinchinso.subs1";
    private static final String LICENSE_KEY = null; // PUT YOUR MERCHANT KEY HERE;
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID=null;

    private BillingProcessor bp;

    TextView txt_have_heart;

    private String cnt_heart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payheart);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();


        txt_have_heart = (TextView) findViewById(R.id.txt_have_heart);


        if(!BillingProcessor.isIabServiceAvailable(this)) {
            //In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16
            showToast("플레이스토어 버전이 너무 낮습니다. 업그레이드 해주세요.");
        }

//        bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
//            @Override
//            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
//                showToast("onProductPurchased: " + productId);
//                updateTextViews();
//            }
//            @Override
//            public void onBillingError(int errorCode, @Nullable Throwable error) {
//                showToast("onBillingError: " + Integer.toString(errorCode));
//            }
//            @Override
//            public void onBillingInitialized() {
//                showToast("onBillingInitialized");
//                readyToPurchase = true;
//                updateTextViews();
//            }
//            @Override
//            public void onPurchaseHistoryRestored() {
//                showToast("onPurchaseHistoryRestored");
//                for(String sku : bp.listOwnedProducts())
//                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
//                for(String sku : bp.listOwnedSubscriptions())
//                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
//                updateTextViews();
//            }
//        });

        bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID,this);


        TextView btn_charge1 = (TextView) findViewById(R.id.btn_charge1);
        btn_charge1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.purchase(PayHeartActivity.this, PRODUCT_ID);
            }
        });

        TextView btn_charge3 = (TextView) findViewById(R.id.btn_charge3);
        btn_charge3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetHeartTask().execute();
            }
        });

        TextView btn_charge4 = (TextView) findViewById(R.id.btn_charge4);
        btn_charge4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PayHeartTask().execute();
            }
        });



        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(onClickListener);
        btn_back.setOnTouchListener(mOnTouchListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new AccountTask().execute();
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

//    IInAppBillingService mService;
//
//    ServiceConnection mServiceConn = new ServiceConnection() {
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mService = null;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name,
//                                       IBinder service) {
//            mService = IInAppBillingService.Stub.asInterface(service);
//        }
//    };

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
//        Toast.makeText(this, "친친소에서 xxx를 구매하셨습니다.", Toast.LENGTH_SHORT).show();
        showToast("친친소 하트로 새로운 인연을 만나보세요.");
    }

    @Override
    public void onPurchaseHistoryRestored() {
        showToast("onPurchaseHistoryRestored");
        for(String sku : bp.listOwnedProducts())
            Log.d("abc", "Owned Managed Product: " + sku);
        for(String sku : bp.listOwnedSubscriptions())
            Log.d("abc", "Owned Subscription: " + sku);
//        updateTextViews();
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        showToast("구매 중 오류가 발생하였습니다.");
    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCOde, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCOde, data)) {
            super.onActivityResult(requestCode, resultCOde, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

//    private void updateTextViews() {
//        TextView text = (TextView)findViewById(R.id.productIdTextView);
//        text.setText(String.format("%s is%s purchased", PRODUCT_ID, bp.isPurchased(PRODUCT_ID) ? "" : " not"));
//        text = (TextView)findViewById(R.id.subscriptionIdTextView);
//        text.setText(String.format("%s is%s subscribed", SUBSCRIPTION_ID, bp.isSubscribed(SUBSCRIPTION_ID) ? "" : " not"));
//    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    //    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mService != null) {
//            unbindService(mServiceConn);
//        }
//    }


    private class AccountTask extends AsyncTask<Void, Void, Void> {



        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "account")
                    .add("user_id", MainActivity.my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "Profile obj = " + obj);

                    cnt_heart = obj.getString("cnt_heart");
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
            txt_have_heart.setText(cnt_heart);
        }
    }

    private class GetHeartTask extends AsyncTask<Void, Void, Void> {
        String result;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "heart_charge")
                    .add("my_id", MainActivity.my_id)
                    .add("heart", "50")
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "Profile obj = " + obj);

                    result = obj.getString("result");
                    if (result.equals("0")) {
                        cnt_heart = obj.getString("cnt_heart");
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
            txt_have_heart.setText(cnt_heart);
        }
    }

    private class PayHeartTask extends AsyncTask<Void, Void, Void> {

        String result;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "heart_use")
                    .add("my_id", MainActivity.my_id)
//                    .add("heart", "2")
                    .add("page_id", "14")
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "Profile obj = " + obj);

                    result = obj.getString("result");
                    if (result.equals("0")) {
                        cnt_heart = obj.getString("cnt_heart");
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
            txt_have_heart.setText(cnt_heart);
        }
    }

}