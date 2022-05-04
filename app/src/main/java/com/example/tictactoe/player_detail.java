package com.example.tictactoe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class player_detail extends AppCompatActivity {

    EditText et1,et2;
    Button b;
    int flag1=0,flag2=0;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String s1,s2;

    String key;
    boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        et1=findViewById(R.id.et1);
        et2=findViewById(R.id.et2);
        b=findViewById(R.id.bShow);

        String mode=getIntent().getExtras().getString("mode");
        key=getIntent().getExtras().getString("key");

        final CharSequence players[]={"Player 1","Player 2"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Play as:");

        builder.setSingleChoiceItems(players, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map map=(Map)snapshot.getValue();
                        String name=map.get("name").toString();
                        if(players[which].equals("Player 1"))
                        {
                            setName(et1,name);
                            if(mode.equals("Single Player"))
                            {
                                setName(et2,"COMPUTER");
                            }
                        }
                        else
                        {
                            setName(et2,name);
                            if(mode.equals("Single Player"))
                            {
                                setName(et1,"COMPUTER");
                            }
                        }
                        dialog.cancel();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        AlertDialog alert=builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(false);
        alert.show();

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=false;
                s1=et1.getText().toString();
                s2=et2.getText().toString();
                for(int i=0;i<s1.length();++i)
                    if((s1.charAt(i)>='a' || s1.charAt(i)>='A') && (s1.charAt(i)<='z' || s1.charAt(i)<='Z'))
                    {
                        flag1=1;
                        break;
                    }
                for(int i=0;i<s2.length();++i)
                    if((s2.charAt(i)>='a' || s2.charAt(i)>='A') && (s2.charAt(i)<='z' || s2.charAt(i)<='Z'))
                    {
                        flag2=1;
                        break;
                    }
                if(s1.equals(s2))
                {
                    s1+="(I)";
                    s2+="(II)";
                }
                if(flag1==1 && flag2==1) {
                    if(mode.equals("Single Player"))
                    {
                        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(flag==false) {
                                    flag=true;
                                    Map map = (Map) snapshot.getValue();
                                    String name=map.get("name").toString();
                                    int singlePlayerGamesPlayed = Integer.parseInt(map.get("single_player_games_played").toString());
                                    int singlePlayerGamesDraw = Integer.parseInt(map.get("single_player_games_draw").toString());
                                    Intent intent = new Intent(player_detail.this, single_player.class);
                                    Bundle bdl = new Bundle();
                                    bdl.putString("data1", s1);
                                    bdl.putString("data2", s2);
                                    bdl.putString("key", key);
                                    bdl.putString("name",name);
                                    bdl.putInt("singlePlayerGamesPlayed", singlePlayerGamesPlayed);
                                    bdl.putInt("singlePlayerGamesDraw", singlePlayerGamesDraw);
                                    intent.putExtras(bdl);
                                    startActivity(intent);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else
                    {
                        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(flag==false) {
                                    flag=true;
                                    Map map = (Map) snapshot.getValue();
                                    String name=map.get("name").toString();
                                    int twoPlayerGamesPlayed = Integer.parseInt(map.get("two_player_games_played").toString());
                                    int twoPlayerGamesWon = Integer.parseInt(map.get("two_player_games_won").toString());
                                    int twoPlayerGamesDraw = Integer.parseInt(map.get("two_player_games_draw").toString());
                                    Intent intent = new Intent(player_detail.this, two_player.class);
                                    Bundle bdl = new Bundle();
                                    bdl.putString("data1", s1);
                                    bdl.putString("data2", s2);
                                    bdl.putString("key", key);
                                    bdl.putString("name",name);
                                    bdl.putInt("twoPlayerGamesPlayed", twoPlayerGamesPlayed);
                                    bdl.putInt("twoPlayerGamesWon", twoPlayerGamesWon);
                                    bdl.putInt("twoPlayerGamesDraw", twoPlayerGamesDraw);
                                    intent.putExtras(bdl);
                                    startActivity(intent);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                else
                    Toast.makeText(player_detail.this,"Name must contain atleast one letter",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setName(EditText editText,String name)
    {
        editText.setText(name);
        editText.setFocusable(false);
        editText.setClickable(false);
        editText.setFocusableInTouchMode(false);
    }

}