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

public class single_player extends AppCompatActivity {

    private long backPressedTime;
    private ImageButton bt[]=new ImageButton[9];
    private TextView tv,p1;
    private Button bt2;
    private ImageButton tb;
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
    private int singlePlayerGamesPlayed,singlePlayerGamesWon,singlePlayerGamesDraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        backgroudSong=MediaPlayer.create(single_player.this,R.raw.ring);
        finishSong=MediaPlayer.create(single_player.this,R.raw.over);
        moveSong=MediaPlayer.create(single_player.this,R.raw.move);
        backgroudSong.setLooping(true);
        backgroudSong.seekTo(0);
        backgroudSong.start();

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        player1=getIntent().getExtras().getString("data1");
        player2=getIntent().getExtras().getString("data2");
        key=getIntent().getExtras().getString("key");
        name=getIntent().getExtras().getString("name");
        singlePlayerGamesPlayed=getIntent().getExtras().getInt("singlePlayerGamesPlayed");
        singlePlayerGamesDraw=getIntent().getExtras().getInt("singlePlayerGamesDraw");

        databaseReference.child(key).child("single_player_games_played").setValue(++singlePlayerGamesPlayed);

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
        bt2=findViewById(R.id.bt2);
        tb=findViewById(R.id.tb);

        p1.setText(player1 + " vs " + player2);

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
                game_over=false;
                counter=0;
                tv.setText("");
                if(player1.equals("COMPUTER"))
                {
                    int computerTurn = computerMove(s);
                    s[computerTurn] = 2;
                    bt[computerTurn].setImageResource(R.drawable.right0);
                    counter++;
                }
                databaseReference.child(key).child("single_player_games_played").setValue(++singlePlayerGamesPlayed);
                finishSong.pause();
                backgroudSong.seekTo(0);
                backgroudSong.start();
            }
        });

        if(player1.equals("COMPUTER"))
        {
            int computerTurn = computerMove(s);
            s[computerTurn] = 2;
            bt[computerTurn].setImageResource(R.drawable.right0);
            counter++;
        }

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
                if(s[j]==0 && !game_over)
                {
                    s[j] = 1;
                    moveSong.start();
                    if(counter%2==0)
                        bt[j].setImageResource(R.drawable.right0);
                    else
                        bt[j].setImageResource(R.drawable.cross0);
                    counter++;
                    int computerTurn = computerMove(s);
                    if (computerTurn != -1)
                    {
                        int row=computerTurn/3;
                        int col=computerTurn-(row*3);
                        s[computerTurn] = 2;
                        if(counter%2!=0)
                            bt[computerTurn].setImageResource(R.drawable.cross0);
                        else
                            bt[computerTurn].setImageResource(R.drawable.right0);
                        counter++;
                        if(s[(row*3)+(col+1)%3]==2 && s[(row*3)+(col+2)%3]==2)
                        {
                            winSetup(computerTurn,(row*3)+(col+1)%3,(row*3)+(col+2)%3,2,player1,player2);
                        }
                        else if(s[((row+1)%3)*3+col]==2 && s[((row+2)%3)*3+col]==2)
                        {
                            winSetup(computerTurn,((row+1)%3)*3+col,((row+2)%3)*3+col,2,player1,player2);
                        }
                        else if(s[0]==2 && s[4]==2 && s[8]==2)
                        {
                            winSetup(0,4,8,2,player1,player2);
                        }
                        else if(s[2]==2 && s[4]==2 && s[6]==2)
                        {
                            winSetup(2,4,6,2,player1,player2);
                        }
                    }
                    if(counter==9 && game_over==false)
                    {
                        tv.setText("Draw!");
                        databaseReference.child(key).child("single_player_games_draw").setValue(++singlePlayerGamesDraw);
                        game_over=true;
                    }
                    if(game_over)
                    {
                        backgroudSong.pause();
                        finishSong.seekTo(0);
                        finishSong.start();
                    }
                }
            });
        }
    }

    void winSetup(int a,int b,int c,int player,String player1,String player2)
    {
        bt[a].setBackgroundColor(Color.parseColor("#ffff00"));
        bt[b].setBackgroundColor(Color.parseColor("#ffff00"));
        bt[c].setBackgroundColor(Color.parseColor("#ffff00"));
        game_over=true;
        if(player==1)
            tv.setText(name + " Won!!!");
        else
            tv.setText("COMPUTER"+" Won!!!");
    }

    int computerMove(int s[])
    {
        int bestVal=-1000;
        int move=-1;
        for(int i=0;i<9;++i)
        {
            if(s[i]==0)
            {
                s[i]=2;
                int moveVal=minimax(s,0,false);
                s[i]=0;
                if(moveVal>bestVal)
                {
                    move=i;
                    bestVal=moveVal;
                }
            }
        }
        return move;
    }

    int minimax(int s[],int depth,boolean isComputer)
    {
        if(hasComputerWon())
            return 10;
        if(hasPlayerWon())
            return -10;
        if(isAvailable()==false)
            return 0;

        if(isComputer)//computer's move
        {
            int maxEval=-1000;
            for(int i=0;i<9;++i)
            {
                if(s[i]==0)
                {
                    s[i]=2;
                    int eval=minimax(s,depth+1,false);
                    maxEval=Math.max(maxEval,eval);
                    s[i]=0;
                }
            }
            return maxEval;
        }
        else//player's move
        {
            int minEval=1000;
            for(int i=0;i<9;++i)
            {
                if(s[i]==0)
                {
                    s[i]=1;
                    int eval=minimax(s,depth+1,true);
                    minEval=Math.min(minEval,eval);
                    s[i]=0;
                }
            }
            return minEval;
        }
    }

    boolean hasPlayerWon()
    {
        if((s[0]==1 && s[1]==1 && s[2]==1) || (s[3]==1 && s[4]==1 && s[5]==1) || (s[6]==1 && s[7]==1 && s[8]==1)
                || (s[0]==1 && s[3]==1 && s[6]==1) || (s[1]==1 && s[4]==1 && s[7]==1) || (s[2]==1 && s[5]==1 && s[8]==1)
                || (s[0]==1 && s[4]==1 && s[8]==1) || (s[2]==1 && s[4]==1 && s[6]==1))
        {
            return true;
        }
        return false;
    }

    boolean hasComputerWon()
    {
        if((s[0]==2 && s[1]==2 && s[2]==2) || (s[3]==2 && s[4]==2 && s[5]==2) || (s[6]==2 && s[7]==2 && s[8]==2)
                || (s[0]==2 && s[3]==2 && s[6]==2) || (s[1]==2 && s[4]==2 && s[7]==2) || (s[2]==2 && s[5]==2 && s[8]==2)
                || (s[0]==2 && s[4]==2 && s[8]==2) || (s[2]==2 && s[4]==2 && s[6]==2))
        {
            return true;
        }
        return false;
    }

    boolean isAvailable()
    {
        for(int i=0;i<9;++i)
            if(s[i]==0)
                return true;
        return false;
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