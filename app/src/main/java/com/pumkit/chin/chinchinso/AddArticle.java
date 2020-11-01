package com.pumkit.chin.chinchinso;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.io.ByteStreams;
import com.pumkit.chin.widget.RealPathUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class AddArticle extends Activity {

    String my_id = MainActivity.my_id;
    String main = "0";
//    EditText edit_article;

    private ImageView upload_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);

//        edit_article = (EditText) findViewById(article);
        Switch switch_main = (Switch) findViewById(R.id.switch_main);
        switch_main.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    main = "1";
                } else {
                    main = "0";
                }
            }
        });

        upload_pic = (ImageView) findViewById(R.id.upload_pic);
        upload_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(v);
            }
        });

        Button btn_upload = (Button) findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uploadContents(v);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        mQueue = Volley.newRequestQueue(this);

//        ImageView btn_back = (ImageView) findViewById(R.id.btn_back);
//        btn_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
        final ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    btn_back.setBackgroundResource(R.drawable.border_transparent);
                } else if (action == MotionEvent.ACTION_UP) {
                    btn_back.setBackgroundResource(0);

                    onBackPressed();
                } else if (action == MotionEvent.ACTION_CANCEL) {
                    btn_back.setBackgroundResource(0);
                }

                return true;
            }
        });
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

    private Uri mImageUri;

    private RequestQueue mQueue;

    public final int GALLERY_PHOTO = 2;
    Bitmap newbitmap;
    private Uri fileUri;
    String realPath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("abc", "result Code : " + resultCode);

        if (requestCode == GALLERY_PHOTO) {

            if (resultCode == RESULT_OK) {
                // SDK < API 11
                Log.e("abc", "sdk = " + Build.VERSION.SDK_INT);
                if (Build.VERSION.SDK_INT < 11) {

                    try {
                        realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(
                                AddArticle.this,
                                data.getData());
                        setTextViews(Build.VERSION.SDK_INT, data.getData()
                                .getPath(), realPath);

                        mImageUri = data.getData();
                        upload_pic.setImageURI(mImageUri);

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
                        upload_pic.setImageBitmap(newbitmap);

                    }
                }
                // SDK >= 11 && SDK < 19
                else if (Build.VERSION.SDK_INT < 19) {

                    try {
                        realPath = RealPathUtil.getRealPathFromURI_API11to18(
                                AddArticle.this,
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
                        upload_pic.setImageBitmap(newbitmap);

                    }
                }
                // SDK  >= 19
                else {
                    try {
//                        realPath = RealPathUtil.getRealPathFromURI_API19(
//                                JoinActivity2.this,
//                                data.getData());
//
//                        setTextViews(Build.VERSION.SDK_INT, data.getData()
//                                .getPath(), realPath);

                        mImageUri = data.getData();
                        upload_pic.setImageURI(mImageUri);
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
                        upload_pic.setImageBitmap(newbitmap);

                    }
                }
                // end

            } else if (resultCode == RESULT_CANCELED) {

                Toast.makeText(getApplicationContext(), "취소되었습니다.",
                        Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),

                        "Oops!! Failed to pick Image", Toast.LENGTH_SHORT).show();
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
//                upload_pic.setImageURI(mImageUri);
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

//        upload_pic.setImageBitmap(newbitmap);
        upload_pic.setImageURI(fileUri);

        Log.d("Status", "Build.VERSION.SDK_INT:" + sdk);
        Log.d("Status", "URI Path:" + fileUri);
        Log.d("Status", "Real Path: " + realPath);

    }

    public void uploadContents(View v) throws UnsupportedEncodingException {
//        String article = edit_article.getText().toString().trim();
        String article = "";
        article = URLEncoder.encode(article, "utf-8");
        Log.e("Article_abc", "article = " + article);

//        if (article.length() < 1) {
//            Toast.makeText(this, "당신의 프로필에 글과 해쉬를 작성해주세요", Toast.LENGTH_SHORT).show();
//            return;
//        } else {
            MoviePosterUploadRequest req = new MoviePosterUploadRequest(Request.Method.POST, Statics.opt_url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(AddArticle.this, "프로필 등록 완료", Toast.LENGTH_SHORT).show();
                    upload_pic.setImageBitmap(null);
//                mTitle.setText("");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("abc", "ErrorResponse", error);
                }
            });
            if (mImageUri != null) {
                req.addFileUpload("uploadedfile", mImageUri);
                req.addStringUpload("opt", "upload_profile");
                req.addStringUpload("my_id", my_id);
                req.addStringUpload("main", main);
//                req.addStringUpload("text", article);
//                req.addStringUpload("position", "1");

                mQueue.add(req);

                Handler hd = new Handler();
                hd.postDelayed(new Waithandler(), 1200); // 3초 후에 hd Handler 실행
            } else {
                Toast.makeText(this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();

//                req.addFileUpload("uploadedfile", mImageUri);
//                req.addStringUpload("opt", "upload_article");
//                req.addStringUpload("my_id", my_id);
//                req.addStringUpload("text", article);
//                req.addStringUpload("position", "1");
            }


//        }


    }

    class MoviePosterUploadRequest extends StringRequest {

        public MoviePosterUploadRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
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

//    public void google_img(String[] args) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, JSONException {
//        // TODO Auto-generated method stub
//
//        String query = "각시탈";
//        URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="+URLEncoder.encode(query, "UTF-8")+
//                "&userip=211.202.27.138&rsz=8");
//        URLConnection connection = url.openConnection();
//        connection.addRequestProperty("Referer", "www.google.com");
//
//        String line;
//        StringBuilder builder = new StringBuilder();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        while((line = reader.readLine()) != null) {
//            builder.append(line);
//        }
//        System.out.println(builder);
//        JSONObject json = new JSONObject(builder.toString());
//        Log.e("Google_abc", "G_barack" + json);
//    }

    private class Waithandler implements Runnable {
        public void run() {
            Intent intent = new Intent(AddArticle.this, MyImageList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra("setCurrent", 3);
//            MainActivity.header_yn = "n";
            startActivity(intent);
            finish();
        }
    }
}