package com.example.tictactoe;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class player_stats extends AppCompatActivity {

    TextView tvName,tvEmail;
    ImageView ivImage;
    ListView lv;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_stats);

        progressDialog=new ProgressDialog(player_stats.this);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        tvName=findViewById(R.id.name);
        tvEmail=findViewById(R.id.email);
        ivImage=findViewById(R.id.image);
        lv=findViewById(R.id.lView);

        String key=getIntent().getExtras().getString("key");

        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Map map = (Map) snapshot.getValue();
                    String name = map.get("name").toString();
                    tvName.setText(name);
                    String email = map.get("email").toString();
                    tvEmail.setText(email);
                    String image = map.get("image").toString();
                    if(image.equals("null"))
                        ivImage.setBackgroundResource(R.drawable.default_image);
                    else
                        Picasso.get().load(image).into(ivImage);
                    int singlePlayerGamesPlayed = Integer.parseInt(map.get("single_player_games_played").toString());
                    int singlePlayerGamesDraw = Integer.parseInt(map.get("single_player_games_draw").toString());
                    int singlePlayerGamesWon = 0;
                    int singlePlayerGamesLoss = singlePlayerGamesPlayed - (singlePlayerGamesDraw + singlePlayerGamesWon);
                    int twoPlayerGamesPlayed = Integer.parseInt(map.get("two_player_games_played").toString());
                    int twoPlayerGamesWon = Integer.parseInt(map.get("two_player_games_won").toString());
                    int twoPlayerGamesDraw = Integer.parseInt(map.get("two_player_games_draw").toString());
                    int twoPlayerGamesLoss = twoPlayerGamesPlayed - (twoPlayerGamesDraw + twoPlayerGamesWon);
                    int totalGamesPlayed = singlePlayerGamesPlayed + twoPlayerGamesPlayed;
                    int totalGamesWon = singlePlayerGamesWon + twoPlayerGamesWon;
                    int totalGamesDraw = singlePlayerGamesDraw + twoPlayerGamesDraw;
                    int totalGamesLoss = singlePlayerGamesLoss + twoPlayerGamesLoss;

                    String data[] = {
                            "Total Games Played: " + totalGamesPlayed,
                            "Total Games Won: " + totalGamesWon,
                            "Total Games Draw: " + totalGamesDraw,
                            "Total Games Loss: " + totalGamesLoss,
                            "Two Player Games Played: " + twoPlayerGamesPlayed,
                            "Two Player Games Won: " + twoPlayerGamesWon,
                            "Two Player Games Draw: " + twoPlayerGamesDraw,
                            "Two Player Games Loss: " + twoPlayerGamesLoss,
                            "Single Player Games Played: " + singlePlayerGamesPlayed,
                            "Single Player Games Won: " + singlePlayerGamesWon,
                            "Single Player Games Draw: " + singlePlayerGamesDraw,
                            "Single Player Games Loss: " + singlePlayerGamesLoss
                    };

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(player_stats.this, android.R.layout.simple_list_item_1, data);
                    lv.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}