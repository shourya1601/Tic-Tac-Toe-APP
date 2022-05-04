package com.example.tictactoe;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class updatePass extends AppCompatActivity {

    EditText newPass,oldPass;
    Button bUpdate;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pass);

        oldPass=findViewById(R.id.etOldPassword);
        newPass=findViewById(R.id.etPassword);
        bUpdate=findViewById(R.id.update);

        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog=new ProgressDialog(updatePass.this);
                progressDialog.show();
                progressDialog.setCancelable(false);
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email=user.getEmail();
                String newPassword = newPass.getText().toString();
                String oldPassword = oldPass.getText().toString();

                AuthCredential credential = EmailAuthProvider.getCredential(email,oldPassword);

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            progressDialog.dismiss();
                                            if (task.isSuccessful()) {

                                                AlertDialog.Builder builder=new AlertDialog.Builder(updatePass.this);
                                                builder.setMessage("You will be signed out. The application will close.");
                                                builder.setCancelable(false);
                                                builder.setTitle("Alert Dialog");

                                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        FirebaseAuth.getInstance().signOut();
                                                        finishAffinity();
                                                    }
                                                });

                                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });

                                                builder.show();

                                            }
                                            else {
                                                Toast.makeText(updatePass.this,"Updation unsuccessful",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                                else {
                                    progressDialog.dismiss();
                                    Toast.makeText(updatePass.this,"Updation unsuccessful",Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });
    }
}