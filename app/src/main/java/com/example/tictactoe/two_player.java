package com.example.tictactoe;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class two_player extends AppCompatActivity {

    private long backPressedTime;
    private ImageButton bt[]=new ImageButton[9];
    private TextView tv,p1,p2;
    private Button bt2;
    private ImageButton tb;
    private int player=1;
    private int counter=0;
    private boolean game_over=false;
    private int s[]={0,0,0,0,0,0,0,0,0};

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private MediaPlayer backgroudSong;
    private MediaPlayer finishSong;
    private MediaPlayer moveSong;

    private boolean soundOn=true;

    private String player1,player2,key,name;
    private int twoPlayerGamesPlayed,twoPlayerGamesWon,twoPlayerGamesDraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player);

        backgroudSong=MediaPlayer.create(two_player.this,R.raw.ring);
        finishSong=MediaPlayer.create(two_player.this,R.raw.over);
        moveSong=MediaPlayer.create(two_player.this,R.raw.move);
        backgroudSong.setLooping(true);
        backgroudSong.seekTo(0);
        backgroudSong.start();

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        player1=getIntent().getExtras().getString("data1");
        player2=getIntent().getExtras().getString("data2");
        key=getIntent().getExtras().getString("key");
        name=getIntent().getExtras().getString("name");
        twoPlayerGamesPlayed=getIntent().getExtras().getInt("twoPlayerGamesPlayed");
        twoPlayerGamesWon=getIntent().getExtras().getInt("twoPlayerGamesWon");
        twoPlayerGamesDraw=getIntent().getExtras().getInt("twoPlayerGamesDraw");

        databaseReference.child(key).child("two_player_games_played").setValue(++twoPlayerGamesPlayed);

        bt[0]=findViewById(R.id.b1);
        bt[1]=findViewById(R.id.b2);
        bt[2]=findViewById(R.id.b3);
        bt[3]=findViewById(R.id.b4);
        bt[4]=findViewById(R.id.b5);
        bt[5]=findViewById(R.id.b6);
        bt[6]=findViewById(R.id.b7);
        bt[7]=findViewById(R.id.b8);
        bt[8]=findViewById(R.id.b9);
        tv=findViewById(R.id.tv);
        p1=findViewById(R.id.p1);
        p2=findViewById(R.id.p2);
        bt2=findViewById(R.id.bt2);
        tb=findViewById(R.id.tb);
        p1.setBackgroundColor(Color.parseColor("#ff66ff"));

        p1.setText(player1);
        p2.setText(player2);

        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(soundOn)
                {
                    backgroudSong.setVolume(0,0);
                    finishSong.setVolume(0,0);
                    moveSong.setVolume(0,0);
                    tb.setBackgroundResource(R.drawable.ic_baseline_volume_off_24);
                    soundOn=false;
                }
                else
                {
                    backgroudSong.setVolume(1,1);
                    finishSong.setVolume(1,1);
                    moveSong.setVolume(1,1);
                    tb.setBackgroundResource(R.drawable.ic_baseline_volume_up_24);
                    soundOn=true;
                }
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<9;++i){
                    s[i]=0;
                    bt[i].setImageResource(android.R.color.transparent);
                    bt[i].setBackgroundColor(Color.parseColor("#d9d7d2"));
                }
                player=1;
                game_over=false;
                counter=0;
                tv.setText("");
                p1.setBackgroundColor(Color.parseColor("#ff66ff"));
                p2.setBackgroundColor(Color.parseColor("#fdff9c"));
                databaseReference.child(key).child("two_player_games_played").setValue(++twoPlayerGamesPlayed);
                finishSong.pause();
                backgroudSong.seekTo(0);
                backgroudSong.start();
            }
        });

        for(int i=0;i<9;++i)
        {
            bt[i].setOnClickListener(v -> {
                ImageButton b = (ImageButton)v;
                int j;
                for(j =0; j <9; j++)
                {
                    if(b==bt[j])
                        break;
                }
                int row=j/3;
                int col=j-(row*3);
                if (s[j] == 0 && !game_over)
                {
                    s[j]=player;
                    moveSong.start();
                    if(s[(row*3)+(col+1)%3]==player && s[(row*3)+(col+2)%3]==player)
                    {
                        winSetup(j,(row*3)+(col+1)%3,(row*3)+(col+2)%3,player);
                    }
                    else if(s[((row+1)%3)*3+col]==player && s[((row+2)%3)*3+col]==player)
                    {
                        winSetup(j,((row+1)%3)*3+col,((row+2)%3)*3+col,player);
                    }
                    else if(s[0]==player && s[4]==player && s[8]==player)
                    {
                        winSetup(0,4,8,player);
                    }
                    else if(s[2]==player && s[4]==player && s[6]==player)
                    {
                        winSetup(2,4,6,player);
                    }
                    if(player==1) {
                        bt[j].setImageResource(R.drawable.right0);
                        player=2;
                        p1.setBackgroundColor(Color.parseColor("#fdff9c"));
                        p2.setBackgroundColor(Color.parseColor("#ff66ff"));
                    }
                    else {
                        bt[j].setImageResource(R.drawable.cross0);
                        player=1;
                        p1.setBackgroundColor(Color.parseColor("#ff66ff"));
                        p2.setBackgroundColor(Color.parseColor("#fdff9c"));
                    }
                    counter++;
                    if(counter==9 && !game_over) {
                        tv.setText("Draw!");
                        databaseReference.child(key).child("two_player_games_draw").setValue(++twoPlayerGamesDraw);
                        game_over=true;
                    }
                    if(game_over)
                    {
                        backgroudSong.pause();
                        finishSong.seekTo(0);
                        finishSong.start();
                        p1.setBackgroundColor(Color.parseColor("#fdff9c"));
                        p2.setBackgroundColor(Color.parseColor("#fdff9c"));
                    }
                }
            });
        }
    }

    void winSetup(int a,int b,int c,int player)
    {
        bt[a].setBackgroundColor(Color.parseColor("#ffff00"));
        bt[b].setBackgroundColor(Color.parseColor("#ffff00"));
        bt[c].setBackgroundColor(Color.parseColor("#ffff00"));
        game_over=true;
        if(player==1){
            tv.setText(player1+" Won!!!");
            if(player1.equals(name))
            {
                databaseReference.child(key).child("two_player_games_won").setValue(++twoPlayerGamesWon);
            }
        }
        else {
            tv.setText(player2 + " Won!!!");
            if(player2.equals(name))
            {
                databaseReference.child(key).child("two_player_games_won").setValue(++twoPlayerGamesWon);
            }
        }
    }

    @Override
    protected void onResume() {
        if(!game_over)
            backgroudSong.start();
        finishSong.start();
        finishSong.seekTo(0);
        finishSong.pause();
        super.onResume();
    }

    @Override
    protected void onStop() {
        backgroudSong.pause();
        finishSong.pause();
        super.onStop();
    }
}