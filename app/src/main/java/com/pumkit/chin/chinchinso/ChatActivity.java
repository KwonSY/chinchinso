package com.pumkit.chin.chinchinso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.ServerValue;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pumkit.chin.vo.MemberData;
import com.pumkit.chin.widget.ChatAdapter;
import com.pumkit.chin.vo.ChatData;
import com.pumkit.chin.widget.OkHttpClientSingleton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ChatActivity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 234;
    private Uri filePath;
    private StorageReference storageReference;

    private OkHttpClient httpClient;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    public String my_id = MainActivity.my_id;
    private String fromId, matchId, toId, toUserName, pic1;
    private String partnerId;

    private EditText editText;
    private Button sendButton;

    private ListView listView;
    public ChatAdapter chatAdapter;

    private ChatData chatData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        Intent intent = getIntent();
//        m_idMessages = intent.getStringArrayListExtra("id_message");
        fromId = intent.getStringExtra("myFireId");
        matchId = intent.getStringExtra("matchFireId");
        toId = intent.getStringExtra("yourFireId");
        toUserName = intent.getStringExtra("yourUserName");
        pic1 = intent.getStringExtra("pic1");
        Log.e("abc", "fromId = " + fromId + ", matchId = " + matchId + ", toId = " + toId + ", toUserName = " + toUserName + ", pic1 = " + pic1);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        listView = (ListView) findViewById(R.id.listView);
        chatAdapter = new ChatAdapter();
        listView.setAdapter(chatAdapter);

        final Button btn_send_img = (Button) findViewById(R.id.btn_send_img);
        btn_send_img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if (action == event.ACTION_DOWN) {
                    btn_send_img.getBackground().setAlpha(80);
                } else if (action == event.ACTION_UP) {
                    btn_send_img.getBackground().setAlpha(255);

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select an Image"), PICK_IMAGE_REQUEST);
                }

                return false;
            }
        });

        editText = (EditText) findViewById(R.id.editText);
        sendButton = (Button) findViewById(R.id.btn_send);


//        mDatabase.child("user-match-messages").child(fromId).child(matchId).child(toId).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Map<String, Object> map = (HashMap<String,Object>) dataSnapshot.getValue();
//
//                for ( String id_message : map.keySet() ) {
//                    mDatabase.child("message").addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                            chatData = dataSnapshot.getValue(ChatData.class);
//                            chatData.setToUserName(toUserName);
//
//                            if (chatData.getFromId().equals(fromId) && chatData.getToId().equals(toId)) {
//                                Log.e("abc", "PASSSSING");
//                                chatAdapter.addItem(chatData.getFromId(), chatData.getToId(), chatData.getToUserName(), chatData.getText(), chatData.getTimestampLong(), chatData.getImageUrl(), chatData.getImageWidth(), chatData.getImageHeight(), pic1);
//                                listView.setAdapter(chatAdapter);
//                            } else if (chatData.getFromId().equals(toId) && chatData.getToId().equals(fromId)) {
//                                Log.e("abc", "어랏???");
//                                chatAdapter.addItem(chatData.getFromId(), chatData.getToId(), chatData.getToUserName(), chatData.getText(), chatData.getTimestampLong(), chatData.getImageUrl(), chatData.getImageWidth(), chatData.getImageHeight(), pic1);
//                                listView.setAdapter(chatAdapter);
//                            } else {
//
//                            }
//                        }
//
//                        @Override
//                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                        }
//
//                        @Override
//                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//
//
//
//
//
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        mDatabase.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                chatData = dataSnapshot.getValue(ChatData.class);
                chatData.setToUserName(toUserName);

                if (chatData.getFromId().equals(fromId) && chatData.getToId().equals(toId)) {
                    Log.e("abc", "PASSSSING");
                    chatAdapter.addItem(chatData.getFromId(), chatData.getToId(), chatData.getToUserName(), chatData.getText(), chatData.getTimestampLong(), chatData.getImageUrl(), chatData.getImageWidth(), chatData.getImageHeight(), pic1);
                    listView.setAdapter(chatAdapter);
                } else if (chatData.getFromId().equals(toId) && chatData.getToId().equals(fromId)) {
                    Log.e("abc", "어랏???");
                    chatAdapter.addItem(chatData.getFromId(), chatData.getToId(), chatData.getToUserName(), chatData.getText(), chatData.getTimestampLong(), chatData.getImageUrl(), chatData.getImageWidth(), chatData.getImageHeight(), pic1);
                    listView.setAdapter(chatAdapter);
                } else {

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editText.getText().toString().length() == 0) {

                } else {
                    final String mess = editText.getText().toString();

                    HashMap<String, Object> timestamp = new HashMap<>();
                    timestamp.put("time", ServerValue.TIMESTAMP);

                    ChatData chatData2 = new ChatData(fromId, toId, editText.getText().toString(), timestamp);

                    DatabaseReference id_message = mDatabase.child("messages").push();

//                    mDatabase.child("messages").child(id_message.getKey()).setValue(new ChatData(fromId, toId, editText.getText().toString(), ServerValue.TIMESTAMP));
                    Log.e("abc" , "chatData2 = " + chatData2);
                    mDatabase.child("messages").child(id_message.getKey()).setValue(chatData2);

                    Map<String, Object> taskMap = new HashMap<String, Object>();
                    taskMap.put(id_message.getKey(), 1);

//                    mDatabase.child("user-messages").child(fromId).child(toId).updateChildren(taskMap);
//                    mDatabase.child("user-messages").child(toId).child(fromId).updateChildren(taskMap);
                    mDatabase.child("user-match-messages").child(fromId).child(matchId).child(toId).updateChildren(taskMap);
                    mDatabase.child("user-match-messages").child(toId).child(matchId).child(fromId).updateChildren(taskMap);

//                    mDatabase.child("user-match-messages").child(myFireId).child(match_FireId)


                    chatData2.setToUserName(toUserName);

                    DatabaseReference token_url = mDatabase.child("users").child(toId);

                    token_url.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            MemberData memberData = dataSnapshot.getValue(MemberData.class);

                            FCMParams params = new FCMParams(memberData.getToken(), mess);
                            new ChatFCMTask().execute(params);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    editText.setText("");
                }

            }
        });

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

    private String format, imageUrl;

    private void uploadFile(final String extension, final int width, final int height) {
        if (filePath != null) {
            Log.e("abc", "xx0 filePath = " + filePath);

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
            format = s.format(new Date());

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("application/octet-stream")
                    .build();

            StorageReference riversRef = storageReference.child("message_images/" + my_id + "_chat_" + format + extension);
            riversRef.putFile(filePath, metadata)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Save in Firebase
                            imageUrl = taskSnapshot.getDownloadUrl().toString();
                            Log.e("abc", "xxx ChatDDDData0 imageUrl = " + imageUrl);

                            HashMap<String, Object> timestamp = new HashMap<>();
                            timestamp.put("time", ServerValue.TIMESTAMP);
                            ChatData chatData = new ChatData(fromId, toId, imageUrl, timestamp, width, height);
                            Log.e("abc", "xxx ChatDDDData1 = " + fromId);
                            Log.e("abc", "xxx ChatDDDData2 = " + imageUrl);
                            Log.e("abc", "xxx ChatDDDData3 = " + width);

                            DatabaseReference id_message = mDatabase.child("messages").push();

                            mDatabase.child("messages").child(id_message.getKey()).setValue(chatData);

                            Map<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put(id_message.getKey(), 1);

                            mDatabase.child("user-messages").child(fromId).child(toId).updateChildren(taskMap);
                            mDatabase.child("user-messages").child(toId).child(fromId).updateChildren(taskMap);

                            chatData.setToUserName(toUserName);

                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "사진 업로드 성공", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.e("abc", "업로드 중 사진 사이즈 = " + taskSnapshot.getTotalByteCount());
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage(((int) progress) + "% 업로드 중...");
                        }
                    });
        } else {
            //display a error toast
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                imageView.setImageBitmap(bitmap);

                String extension = null;
                int imageWidth = 0, imageHeight = 0;

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(filePath, filePathColumn, null, null, null);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath2 = cursor.getString(columnIndex);
                    extension = filePath2.substring(filePath2.lastIndexOf("."));
                    Log.e("abc", "xx1 after sub = " + extension);

//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inJustDecodeBounds = true;
//                    BitmapFactory.decodeFile(new File(filePath.getPath()).getAbsolutePath(), options);
                    imageWidth = bitmap.getWidth();
                    imageHeight = bitmap.getHeight();
                }

                Log.e("abc", "xxx onActivityResult = " + "uploadFile PASSING");

                uploadFile(extension, imageWidth, imageHeight);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // FCM to 상대방
    private class FCMParams {
        String token;
        String message;

        FCMParams(String token, String message) {
            this.token = token;
            this.message = message;
        }
    }

    private class ChatFCMTask extends AsyncTask<FCMParams, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(FCMParams... params) {
            if (params[0].token == null || params[0].token.equals("null")) {

            } else {
                FormBody body = new FormBody.Builder()
                        .add("token", params[0].token)
                        .add("message", params[0].message)
                        .build();

                Request request = new Request.Builder().url(Statics.main_url+"fcm/push_chat.php").post(body).build();

                try {
                    okhttp3.Response response = httpClient.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String bodyStr = response.body().string();
                        Log.e("FCM", "FCM_obj 444 = " + bodyStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }
}