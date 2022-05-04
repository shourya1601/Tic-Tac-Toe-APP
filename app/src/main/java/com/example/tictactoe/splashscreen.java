package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class splashscreen extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    boolean flag=false;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        firebaseAuth= FirebaseAuth.getInstance();

        Thread t=new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(2500);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {

                        String email=user.getEmail();
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
                                                Intent intent = new Intent(splashscreen.this, MainActivity.class);
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
                        Intent msg=new Intent(splashscreen.this,login.class);
                        startActivity(msg);
                    }
                }
            }
        };
        t.start();
    }

    @Override
    public void onBackPressed() {

    }
}