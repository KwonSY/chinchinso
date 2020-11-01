package com.pumkit.chin.chinchinso;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.io.ByteStreams;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pumkit.chin.widget.OkHttpClientSingleton;
import com.pumkit.chin.widget.RealPathUtil;
import com.pumkit.chin.widget.SessionManager;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class JoinActivity2 extends AppCompatActivity {

    private SessionManager session;
    private OkHttpClient httpClient;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    private static final String TAG = "PhotoUpload-Sample";
    private static final String serverAddress = Statics.opt_url;

    private int image_num;
    private String user_name, my_id, id_firebase, main, token;

    // 제목
//    private EditText mTitle;
    // 이미지 뷰
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6;
    // 선택된 이미지
    private Uri mImageUri;

    private RequestQueue mQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join2);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            FirebaseUser user_fire = firebaseAuth.getCurrentUser();
            id_firebase = user_fire.getUid();
            token = FirebaseInstanceId.getInstance().getToken();
        }

        Intent intent = getIntent();
        my_id = intent.getStringExtra("my_id");
//        user_name = intent.getStringExtra("user_name");
        id_firebase = intent.getStringExtra("id_firebase");
//        my_id = "89";
//        id_firebase = "\t2AhAwKxQqCQ79rVkPRh7OtNBxWH2";
        Log.e("abc", "my_id in Join2 = " + my_id);

//        token = FirebaseInstanceId.getInstance().getToken();
//
//        UserVo userVo = new UserVo(username, my_id, token);
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        Log.e("abc", "user.getUid() = " + user.getUid());
//
//        databaseReference.child("users").child(user.getUid()).setValue(userVo);
//
//        id_firebase = user.getUid();



        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        imageView5 = (ImageView) findViewById(R.id.imageView5);
        imageView6 = (ImageView) findViewById(R.id.imageView6);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_num = 1;
                selectImage(view);
            }
        });
        imageView1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_num = 2;
                selectImage(view);
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_num = 3;
                selectImage(view);
            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_num = 4;
                selectImage(view);
            }
        });

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_num = 5;
                selectImage(view);
            }
        });

        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_num = 6;
                selectImage(view);
            }
        });

        Button btn_goBlind = (Button) findViewById(R.id.btn_goBlind);
        btn_goBlind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                uploadContents(view); //vvvvvvvvvvvvvvvv

                Intent intent = new Intent(JoinActivity2.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("my_id", my_id);
                startActivity(intent);
                finish();
            }
        });

        mQueue = Volley.newRequestQueue(this);

    }

    // 사진 고르기
    private static final int PICK_IMAGE_REQUEST = 1;

    public void selectImage(View v) {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("image/*");
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                GALLERY_PHOTO);
    }


    public final int GALLERY_PHOTO = 2;
    Bitmap newbitmap;
    private Uri fileUri;
    String realPath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "result Code : " + resultCode);

        if (requestCode == GALLERY_PHOTO) {

            if (resultCode == RESULT_OK) {
                // SDK < API 11
                Log.e("abc", "sdk = " + Build.VERSION.SDK_INT);
                if (Build.VERSION.SDK_INT < 11) {

                    try {
                        realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(
                                JoinActivity2.this,
                                data.getData());
                        setTextViews(Build.VERSION.SDK_INT, data.getData()
                                .getPath(), realPath);

                        mImageUri = data.getData();
                        if (image_num == 1) {
                            imageView1.setImageURI(mImageUri);
                        } else if (image_num == 2) {
                            imageView2.setImageURI(mImageUri);
                        } else if (image_num == 3) {
                            imageView3.setImageURI(mImageUri);
                        } else if (image_num == 4) {
                            imageView4.setImageURI(mImageUri);
                        } else if (image_num == 5) {
                            imageView5.setImageURI(mImageUri);
                        } else if (image_num == 2) {
                            imageView6.setImageURI(mImageUri);
                        }


                        Log.e("abc", "case sdk below 11 = " + Build.VERSION.SDK_INT);
                    } catch (Exception e) {

                        e.printStackTrace();
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver()
                                .query(selectedImage, filePathColumn, null,
                                        null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor
                                .getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();

                        newbitmap = BitmapFactory.decodeFile(filePath);
                        if (image_num == 1) {
                            imageView1.setImageBitmap(newbitmap);
                        } else if (image_num == 2) {
                            imageView2.setImageBitmap(newbitmap);
                        } else if (image_num == 3) {
                            imageView3.setImageBitmap(newbitmap);
                        } else if (image_num == 4) {
                            imageView4.setImageBitmap(newbitmap);
                        } else if (image_num == 5) {
                            imageView5.setImageBitmap(newbitmap);
                        } else if (image_num == 6) {
                            imageView6.setImageBitmap(newbitmap);
                        }

                    }
                } else if (Build.VERSION.SDK_INT < 19) {
                    // SDK >= 11 && SDK < 19

                    try {
                        realPath = RealPathUtil.getRealPathFromURI_API11to18(
                                JoinActivity2.this,
                                data.getData());
                        setTextViews(Build.VERSION.SDK_INT, data.getData()
                                .getPath(), realPath);

                        mImageUri = data.getData();
                        Log.e("abc", "case sdk below 17 = " + Build.VERSION.SDK_INT);
                    } catch (Exception e1) {

                        e1.printStackTrace();
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver()
                                .query(selectedImage, filePathColumn, null,
                                        null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor
                                .getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();

                        newbitmap = BitmapFactory.decodeFile(filePath);
//                        imageView1.setImageBitmap(newbitmap);
                        if (image_num == 1) {
                            imageView1.setImageBitmap(newbitmap);
                        } else if (image_num == 2) {
                            imageView2.setImageBitmap(newbitmap);
                        } else if (image_num == 3) {
                            imageView3.setImageBitmap(newbitmap);
                        } else if (image_num == 4) {
                            imageView4.setImageBitmap(newbitmap);
                        } else if (image_num == 5) {
                            imageView5.setImageBitmap(newbitmap);
                        } else if (image_num == 6) {
                            imageView6.setImageBitmap(newbitmap);
                        }

                    }
                } else {
                    // SDK  >= 19
                    try {
//                        realPath = RealPathUtil.getRealPathFromURI_API19(
//                                JoinActivity2.this,
//                                data.getData());
//
//                        setTextViews(Build.VERSION.SDK_INT, data.getData()
//                                .getPath(), realPath);

                        mImageUri = data.getData();
                        if (image_num == 1) {
                            imageView1.setImageURI(mImageUri);
                        } else if (image_num == 2) {
                            imageView2.setImageURI(mImageUri);
                        } else if (image_num == 3) {
                            imageView3.setImageURI(mImageUri);
                        } else if (image_num == 4) {
                            imageView4.setImageURI(mImageUri);
                        } else if (image_num == 5) {
                            imageView5.setImageURI(mImageUri);
                        } else if (image_num == 6) {
                            imageView6.setImageURI(mImageUri);
                        }
                        Log.e("abc", "case sdk upper 19 = " + Build.VERSION.SDK_INT);
                    } catch (Exception e) {

                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver()
                                .query(selectedImage, filePathColumn, null,
                                        null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor
                                .getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();

                        newbitmap = BitmapFactory.decodeFile(filePath);
//                        imageView1.setImageBitmap(newbitmap);
                        if (image_num == 1) {
                            imageView1.setImageBitmap(newbitmap);
                        } else if (image_num == 2) {
                            imageView2.setImageBitmap(newbitmap);
                        } else if (image_num == 3) {
                            imageView3.setImageBitmap(newbitmap);
                        } else if (image_num == 4) {
                            imageView4.setImageBitmap(newbitmap);
                        } else if (image_num == 5) {
                            imageView5.setImageBitmap(newbitmap);
                        } else if (image_num == 6) {
                            imageView6.setImageBitmap(newbitmap);
                        }

                    }
                }
                // end
                if (image_num == 1) {
                    main = "1";
                    uploadContents(imageView1);
                } else if (image_num == 2) {
                    main = "0";
                    uploadContents(imageView2);
                } else if (image_num == 3) {
                    main = "0";
                    uploadContents(imageView3);
                } else if (image_num == 4) {
                    main = "0";
                    uploadContents(imageView4);
                } else if (image_num == 5) {
                    main = "0";
                    uploadContents(imageView5);
                } else if (image_num == 6) {
                    main = "0";
                    uploadContents(imageView6);
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(), "잠시 후 재시도해주세요.", Toast.LENGTH_SHORT).show();
            }

        }

//        if ( resultCode != Activity.RESULT_OK ) {
//            Toast.makeText(this, "file choose cancelled", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "file choose cancelled");
//            return;
//        }
//
//        if ( PICK_IMAGE_REQUEST == requestCode ) {
//            try {
//                Log.d(TAG, "data : " + data);
//                mImageUri = data.getData();
//                // 화면에 이미지 출력
//                imageView1.setImageURI(mImageUri);
//            } catch (Exception e) {
//                Log.e(TAG, "URISyntaxException", e);
//                e.printStackTrace();
//            }
//        }

    }

    private void setTextViews(int sdk, String uriPath, String realPath) {

        Uri uriFromPath = Uri.fromFile(new File(realPath));

        fileUri = uriFromPath;

        try {
            newbitmap = BitmapFactory.decodeStream(getContentResolver()
                    .openInputStream(fileUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


//        imageView1.setImageBitmap(newbitmap);
        imageView1.setImageURI(fileUri);

        Log.d("Status", "Build.VERSION.SDK_INT:" + sdk);
        Log.d("Status", "URI Path:" + fileUri);
        Log.d("Status", "Real Path: " + realPath);

    }

    public void uploadContents(View v) {
//        String title = mTitle.getText().toString();
//        if ( title.length() < 1 ) {
//            Toast.makeText(this, "글을 입력해주세요", Toast.LENGTH_SHORT).show();
//            return;
//        }

        ProfileUploadRequest req = new ProfileUploadRequest(com.android.volley.Request.Method.POST, serverAddress, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response);
                Toast.makeText(JoinActivity2.this, "친친소에 사용될 사진이 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
//                imageView1.setImageBitmap(null);
                if (main.equals("1")) {
                    new UpdateFireTask().execute();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "ErrorResponse", error);
            }
        });
        if (mImageUri != null) {
            req.addFileUpload("uploadedfile", mImageUri);
            req.addStringUpload("opt", "upload_profile");
            req.addStringUpload("my_id", my_id);
            req.addStringUpload("id_firebase", id_firebase);
            req.addStringUpload("main", main);
        }

        mQueue.add(req);


//        Intent intent = new Intent(JoinActivity2.this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("my_id", my_id);
//        startActivity(intent);
//        finish();
    }

    class ProfileUploadRequest extends StringRequest {

        public ProfileUploadRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        // 파일 업로드
        private Map<String, Uri> fileUploads = new HashMap<String, Uri>();

        // 키-밸류 업로드
        private Map<String, String> stringUploads = new HashMap<String, String>();


        public void addFileUpload(String param, Uri uri) {
            fileUploads.put(param, uri);
        }

        public void addStringUpload(String param, String content) {
            stringUploads.put(param, content);
        }

        String boundary = "XXXYYYZZZ";
        String lineEnd = "\r\n";

        @Override
        public byte[] getBody() throws AuthFailureError {
            Log.d(TAG, "getBody works in MultlpartRequest");
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(os);

                for (String key : stringUploads.keySet()) {
                    dos.writeBytes("--" + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                    dos.writeBytes("Content-Type: text/plain; charset-UTF-8" + lineEnd);
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(stringUploads.get(key));
                    dos.writeBytes(lineEnd);
                }

                for (String key : fileUploads.keySet()) {
                    dos.writeBytes("--" + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + key + "\"" + lineEnd);
                    dos.writeBytes("Content-Type: application/octet-stream" + lineEnd);
                    dos.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                    dos.writeBytes(lineEnd);

                    Uri uri = fileUploads.get(key);
                    InputStream is = getContentResolver().openInputStream(uri);
                    byte[] fileData = ByteStreams.toByteArray(is);
                    dos.write(fileData);
                    dos.writeBytes(lineEnd);
                }

                dos.writeBytes("--" + boundary + "--" + lineEnd);
                dos.flush();
                dos.close();

                return os.toByteArray();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public String getBodyContentType() {
            return "multipart/form-data; boundary=" + boundary;
        }
    }

    //업데이트 메인프로필
    private class UpdateFireTask extends AsyncTask<Void, Void, Void> {
        String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... params) {
//            FormBody body = new FormBody.Builder()
//                    .add("opt", "upload_profile")
//                    .add("my_id", "")
//                    .add("main", "1")
//                    .build();

            RequestBody body = new FormBody.Builder()
                    .add("opt", "update_firebase")
                    .add("my_id", my_id)
                    .add("id_firebase", id_firebase)
                    .add("token", token)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();
                    Log.e("abc", "UpdateFireTask = " + bodyStr);
                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "UpdateFireTask root = " + obj);

                    result = obj.getString("result");
//                    if (result.equals("0")) {
//                        my_id = obj.getString("my_id");
//                        user_name = obj.getString("user_name");
//                        email = obj.getString("email");
//                        myGender = obj.getString("gender");
//                    } else {
//                    }

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
//                UserVo userVo = new UserVo();
//                UserVo userVo2 = new UserVo();

//                Object objUser = mDatabase.child("users").setValue(userVo);
                FirebaseUser user_fire = firebaseAuth.getCurrentUser();
                String myFireId = user_fire.getUid();

//                String key = mDatabase.child("users").child(myFireId).child("img_url").push().getKey();
//                UserVo userVo2 = new UserVo(objUser.);
//                Map<String, Object> userValues = userVo2.toMap();
//
//                Map<String, Object> childUpdates = new HashMap<>();
//                childUpdates.put("/users" + key, userValues);

                Map<String, Object> taskMap = new HashMap<String, Object>();
                taskMap.put("img_url", "xxx");

                mDatabase.child("users").child(myFireId).updateChildren(taskMap);
            } else {

            }

        }
    }
}