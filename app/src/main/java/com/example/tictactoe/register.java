package com.example.tictactoe;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {

    ImageView ivPhoto;
    EditText etName,etEmail,etPassword;
    Button bRegister;

    private final int IMG_REQUEST=1;

    private Uri path;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        ivPhoto=findViewById(R.id.ivPhoto);
        etName=findViewById(R.id.etName);
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        bRegister=findViewById(R.id.bRegister);

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,IMG_REQUEST);
            }
        });

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name=etName.getText().toString();
                String email=etEmail.getText().toString();
                String password=etPassword.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name))
                {
                    Toast.makeText(register.this,"Enter all details",Toast.LENGTH_LONG).show();
                }
                else
                {
                    progressDialog=new ProgressDialog(register.this);
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    firebaseAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful())
                                    {
                                        Map<String,Object> map=new HashMap<>();
                                        map.put("email",email);
                                        map.put("name",name);
                                        map.put("single_player_games_played",0);
                                        map.put("two_player_games_played",0);
                                        map.put("two_player_games_won",0);
                                        map.put("single_player_games_draw",0);
                                        map.put("two_player_games_draw",0);
                                        map.put("play_online",false);
                                        map.put("play_online_time",Long.MAX_VALUE);
                                        String key=databaseReference.push().getKey();
                                        databaseReference.child(key).setValue(map);
                                        uploadImage(key);
                                        Toast.makeText(register.this,"Registration Successful",Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(register.this,"Registration Unsuccessful",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMG_REQUEST && data!=null) {
            Bitmap bm=null;
            path=data.getData();
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ivPhoto.setImageBitmap(bm);
        }
    }

    void uploadImage(String key)
    {
        if(path!=null)
        {
            StorageReference reference=storageReference.child(key+"."+getFileExtension());
            reference.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            databaseReference.child(key).child("image").setValue(uri.toString());
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(register.this,"Uploading Image Failed!",Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {
            databaseReference.child(key).child("image").setValue("null");
        }
    }

    String getFileExtension() {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(path));
    }
}