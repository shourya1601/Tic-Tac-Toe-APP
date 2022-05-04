package com.example.tictactoe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {

    private long backPressedTime;
    EditText etEmail,etPassword;
    Button bLogin,bRegister;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String key;
    boolean flag=false;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        bLogin=findViewById(R.id.bLogin);
        bRegister=findViewById(R.id.bRegister);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(login.this,register.class);
                startActivity(intent);
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flag=false;

                String email=etEmail.getText().toString();
                String password=etPassword.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {
                    Toast.makeText(login.this,"Enter all details",Toast.LENGTH_LONG).show();
                }
                else
                {
                    progressDialog=new ProgressDialog(login.this);
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    firebaseAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful())
                                    {
                                        databaseReference.orderByChild("email")
                                                .equalTo(email)
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                                    {
                                                        if(flag==false) {
                                                            flag=true;
                                                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                                                key = childSnapshot.getKey();
                                                                Intent intent = new Intent(login.this, MainActivity.class);
                                                                Bundle bundle = new Bundle();
                                                                bundle.putString("key", key);
                                                                intent.putExtras(bundle);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                    }
                                    else
                                    {
                                        Toast.makeText(login.this,"Login Failed!!!",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(backPressedTime+2000>System.currentTimeMillis())
        {
            super.onBackPressed();
            finishAffinity();
            return;
        }
        else
        {
            Toast.makeText(login.this,"Press again to exit",Toast.LENGTH_SHORT).show();
        }
        backPressedTime=System.currentTimeMillis();
    }

}

/*
Data is loaded from Firebase asynchronously. Your main code continues to run while the data is loading,
and then when the data is available the onDataChange method is called.
What that means is easiest to see if you add a few log statements:

Log.d("TAG", "Before attaching listener");
mRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
        Log.d("TAG", "Got data");
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        throw databaseError.toException();
    }
});
Log.d("TAG", "After attaching listener");
 */