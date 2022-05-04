package com.example.tictactoe;

import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class play_online extends AppCompatActivity {

    private ImageButton bt[]=new ImageButton[9];
    private TextView tv,p1,p2,timer1;
    private ImageButton tb;

    private CountDownTimer countDownTimer1;
    private static final long START_TIME_IN_MILLIS = 20000;
    private long timeLeftInMillis1 = START_TIME_IN_MILLIS;
    private boolean isTimerRunning1;

    private MediaPlayer backgroudSong;
    private MediaPlayer finishSong;
    private MediaPlayer moveSong;

    private boolean soundOn=true;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String play_as,key,name_other_player,key_other_player;

    private int j;

    private int s[]={0,0,0,0,0,0,0,0,0};
    private boolean is_move;
    private int move;
    private int counter;
    private String game_result="null";
    private boolean game_over;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_online);

        game_over=false;
        counter=0;

        backgroudSong=MediaPlayer.create(play_online.this,R.raw.ring);
        finishSong=MediaPlayer.create(play_online.this,R.raw.over);
        moveSong=MediaPlayer.create(play_online.this,R.raw.move);
        backgroudSong.setLooping(true);
        backgroudSong.seekTo(0);
        backgroudSong.start();

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
        timer1=findViewById(R.id.timer1);
        tb=findViewById(R.id.tb);
        p1.setBackgroundColor(Color.parseColor("#ff66ff"));

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        key=getIntent().getExtras().getString("key");
        play_as=getIntent().getExtras().getString("play_as");
        key_other_player=getIntent().getExtras().getString("player_key");
        name_other_player=getIntent().getExtras().getString("player_name");

        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map map=(Map)snapshot.getValue();
                String name=""+map.get("name");
                if(play_as.equals("Player 1")){
                    p1.setText(name);
                    p2.setText(name_other_player);
                }
                else{
                    p2.setText(name);
                    p1.setText(name_other_player);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(play_as.equals("Player 1"))
            startTimer1();

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

        Map<String,Object> map1=new HashMap<>();
        map1.put("move",-1);
        map1.put("counter",0);

        if(play_as.equals("Player 1"))
        {
            map1.put("is_move",true);
        }
        else
        {
            map1.put("is_move",false);
        }
        map1.put("game_result","null");

        databaseReference.child(key).child("game").setValue(map1);

        databaseReference.child(key).child("game").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Map map = (Map) snapshot.getValue();
                    boolean is_move = Boolean.parseBoolean(map.get("is_move").toString());
                    int move = Integer.parseInt(map.get("move").toString());
                    int counter = Integer.parseInt(map.get("counter").toString());
                    String game_result = map.get("game_result").toString();

                    if (!game_result.equals("null") ) {
                        game_over=true;
                        tv.setText(game_result);
                        p1.setBackgroundColor(Color.parseColor("#fdff9c"));
                        p2.setBackgroundColor(Color.parseColor("#fdff9c"));
                        if (isTimerRunning1)
                            pauseTimer1();
                        isTimerRunning1=true;
                        backgroudSong.pause();
                        finishSong.seekTo(0);
                        finishSong.start();
                        for(int i=0;i<9;++i)
                            s[i]=0;
                    }
                    else if (move != -1) {
                        moveSong.start();

                        if (is_move) {
                            startTimer1();
                        } else {
                            resetTimer1();
                            pauseTimer1();
                        }

                        int row = move / 3;
                        int col = move - (row * 3);

                        if ((!is_move && play_as.equals("Player 1")) || (is_move && play_as.equals("Player 2"))) {
                            s[move] = 1;
                            bt[move].setImageResource(R.drawable.right0);
                            p1.setBackgroundColor(Color.parseColor("#fdff9c"));
                            p2.setBackgroundColor(Color.parseColor("#ff66ff"));
                        } else if ((is_move && play_as.equals("Player 1")) || (!is_move && play_as.equals("Player 2"))) {
                            s[move] = 2;
                            bt[move].setImageResource(R.drawable.cross0);
                            p1.setBackgroundColor(Color.parseColor("#ff66ff"));
                            p2.setBackgroundColor(Color.parseColor("#fdff9c"));
                        }

                        if(!is_move)
                        {
                            int temp[]=new int[9];
                            for(int i=0;i<9;++i)
                            {
                                temp[i]=s[i];
                            }
                            Runnable progressRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    int i;
                                    for(i=0;i<9;++i)
                                    {
                                        if(temp[i]!=s[i])
                                            break;
                                    }
                                    if(i==9 && !game_over)
                                    {
                                        databaseReference.child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Map map=(Map) task.getResult().getValue();
                                                    String name=map.get("name").toString();
                                                    Map<String, Object> update = new HashMap<>();
                                                    update.put(key + "/game/game_result", name + " Won!!!");
                                                    update.put(key_other_player + "/game/game_result", name + " Won!!!");
                                                    databaseReference.updateChildren(update);
                                                }
                                            }
                                        });

                                    }
                                }
                            };

                            Handler pdCanceller = new Handler();
                            pdCanceller.postDelayed(progressRunnable, 25000);
                        }

                        if (s[(row * 3) + (col + 1) % 3] == s[move] && s[(row * 3) + (col + 2) % 3] == s[move]) {
                            winSetup(move, (row * 3) + (col + 1) % 3, (row * 3) + (col + 2) % 3, s[move]);
                        } else if (s[((row + 1) % 3) * 3 + col] == s[move] && s[((row + 2) % 3) * 3 + col] == s[move]) {
                            winSetup(move, ((row + 1) % 3) * 3 + col, ((row + 2) % 3) * 3 + col, s[move]);
                        } else if (s[0] == s[move] && s[4] == s[move] && s[8] == s[move]) {
                            winSetup(0, 4, 8, s[move]);
                        } else if (s[2] == s[move] && s[4] == s[move] && s[6] == s[move]) {
                            winSetup(2, 4, 6, s[move]);
                        }

                        if (counter == 9 && !game_over) {
                            Map<String, Object> update = new HashMap<>();
                            update.put(key + "/game/game_result", "Draw!");
                            update.put(key_other_player + "/game/game_result", "Draw!");
                            databaseReference.updateChildren(update);
                            game_over = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        for(int i=0;i<9;++i)
        {
            bt[i].setOnClickListener(v -> {
                ImageButton b = (ImageButton)v;
                for(j =0; j <9; j++)
                {
                    if(b==bt[j])
                        break;
                }
                int row=j/3;
                int col=j-(row*3);

                databaseReference.child(key).child("game").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Map map=(Map) task.getResult().getValue();
                            is_move=Boolean.parseBoolean(map.get("is_move").toString());
                            counter=Integer.parseInt(map.get("counter").toString());
                            game_result=map.get("game_result").toString();

                            if(s[j]==0 && game_result.equals("null") && is_move)
                            {
                                move=j;
                                counter++;

                                Map<String,Object> temp=new HashMap<>();
                                temp.put("move",move);
                                temp.put("counter",counter);
                                temp.put("is_move",false);
                                temp.put("game_result","null");
                                databaseReference.child(key).child("game").updateChildren(temp);

                                Map<String,Object> temp1=new HashMap<>();
                                temp1.put("move",move);
                                temp1.put("counter",counter);
                                temp1.put("is_move",true);
                                temp1.put("game_result","null");
                                databaseReference.child(key_other_player).child("game").updateChildren(temp1);
                            }
                        }
                    }
                });
            });
        }

    }

    void winSetup(int a,int b,int c,int player)
    {
        bt[a].setBackgroundColor(Color.parseColor("#ffff00"));
        bt[b].setBackgroundColor(Color.parseColor("#ffff00"));
        bt[c].setBackgroundColor(Color.parseColor("#ffff00"));
        game_over=true;
        if((player == 1 && play_as.equals("Player 1")) || (player==2 && play_as.equals("Player 2")))
        {
            databaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map map = (Map) snapshot.getValue();
                        String name = "" + map.get("name");
                        Map<String, Object> update = new HashMap<>();
                        update.put(key + "/game/game_result", name + " Won!!!");
                        update.put(key_other_player + "/game/game_result", name + " Won!!!");
                        databaseReference.updateChildren(update);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            Map<String, Object> update = new HashMap<>();
            update.put(key+"/game/game_result", name_other_player+" Won!!!");
            update.put(key_other_player+"/game/game_result", name_other_player+" Won!!!");
            databaseReference.updateChildren(update);
        }
    }

    private void startTimer1() {
        countDownTimer1 = new CountDownTimer(timeLeftInMillis1, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis1 = millisUntilFinished;
                updateCountDownText(timer1,timeLeftInMillis1);
            }

            @Override
            public void onFinish() {
                isTimerRunning1 = false;
                game_over=true;

                Map<String, Object> update = new HashMap<>();
                update.put(key+"/game/game_result", name_other_player+" Won!!!");
                update.put(key_other_player+"/game/game_result", name_other_player+" Won!!!");
                databaseReference.updateChildren(update);
            }
        }.start();

        isTimerRunning1 = true;
    }

    private void pauseTimer1() {
        countDownTimer1.cancel();
        isTimerRunning1 = false;
    }

    private void resetTimer1() {
        timeLeftInMillis1 = START_TIME_IN_MILLIS;
        updateCountDownText(timer1,timeLeftInMillis1);
    }

    private void updateCountDownText(TextView textView,long timeLeftInMillis) {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        textView.setText(timeLeftFormatted);
    }

    @Override
    public void onBackPressed() {
        databaseReference.child(key).child("game").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    Map map=(Map) task.getResult().getValue();
                    game_result=map.get("game_result").toString();

                    if(game_result.equals("null"))
                    {
                        AlertDialog.Builder builder=new AlertDialog.Builder(play_online.this);
                        builder.setMessage("You will loose the game if you leave!\nContinue");
                        builder.setCancelable(false);
                        builder.setTitle("Alert Dialog");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<String, Object> update = new HashMap<>();
                                update.put(key+"/game/game_result", name_other_player+" Won!!!");
                                update.put(key_other_player+"/game/game_result", name_other_player+" Won!!!");
                                databaseReference.updateChildren(update);
                                play_online.super.onBackPressed();
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
                    else
                    {
                        play_online.super.onBackPressed();
                    }
                }
            }
        });
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