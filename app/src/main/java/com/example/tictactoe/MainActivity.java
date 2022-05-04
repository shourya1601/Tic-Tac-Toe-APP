package com.example.tictactoe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private long backPressedTime;

    ImageButton single,two,online;
    TextView tvWelcome;

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    String key;
    String email="";
    String play_as;

    boolean player_found=false;
    long time;
    boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog=new ProgressDialog(MainActivity.this);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        online=findViewById(R.id.online);
        single=findViewById(R.id.single);
        two=findViewById(R.id.two);
        tvWelcome=findViewById(R.id.tvWelcome);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View hView = navigationView.getHeaderView(0);
        TextView nav_user = hView.findViewById(R.id.nav_name);
        ImageView nav_image = hView.findViewById(R.id.nav_img);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.nav_stats:
                        Intent intent=new Intent(MainActivity.this, player_stats.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("key",key);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        //close drawer
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_globalstats:
                        Intent intent1=new Intent(MainActivity.this, globalstats.class);
                        startActivity(intent1);
                        //close drawer
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_rules:
                        Intent intent2=new Intent(MainActivity.this, rules.class);
                        startActivity(intent2);
                        //close drawer
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_updatepass:
                        Intent intent3=new Intent(MainActivity.this, updatePass.class);
                        startActivity(intent3);
                        //close drawer
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        finishAffinity();
                        //close drawer
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                return true;
            }
        });

        key=getIntent().getExtras().getString("key");

        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map map=(Map)snapshot.getValue();
                String image_url=map.get("image").toString();
                if(image_url.equals("null"))
                    nav_image.setBackgroundResource(R.drawable.default_image);
                else
                    Picasso.get().load(image_url).into(nav_image);
                String name=""+map.get("name");
                nav_user.setText(name);
                tvWelcome.setText("Welcome\n"+name+"!!!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
        }

        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flag=false;
                player_found=false;
                play_as="null";

                time=System.currentTimeMillis();
                Map<String, Object> update = new HashMap<>();
                update.put("play_online", true);
                update.put("play_online_time", time);
                databaseReference.child(key).updateChildren(update);

                progressDialog.show();
                progressDialog.setCancelable(false);
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                databaseReference.orderByChild("play_online").equalTo(true).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String player_key=ds.getKey();
                            Map map = (Map) ds.getValue();
                            String player_name=map.get("name").toString();
                            String player_email = map.get("email").toString();
                            String player_online=map.get("play_online").toString();
                            long player_time=Long.parseLong(map.get("play_online_time").toString());

                            if (player_found == false
                                    && player_online.equals("true")
                                    && !player_email.equals(email)
                                    && System.currentTimeMillis()<time+10000) {

                                player_found = true;
                                progressDialog.dismiss();

                                Map<String, Object> update = new HashMap<>();
                                update.put(key+"/play_online", false);
                                update.put(player_key+"/play_online", false);
                                databaseReference.updateChildren(update);

                                if(flag==false) {
                                    flag=true;

                                    if(time<player_time)
                                    {
                                        play_as="Player 1";
                                    }
                                    else
                                    {
                                        play_as="Player 2";
                                    }
                                    Intent intent = new Intent(MainActivity.this, play_online.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("key", key);
                                    bundle.putString("play_as",play_as);
                                    bundle.putString("player_key",player_key);
                                    bundle.putString("player_name",player_name);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if(progressDialog.isShowing() && player_found==false) {
                            databaseReference.child(key).child("play_online").setValue(false);
                            Toast.makeText(MainActivity.this,"Try Again Later",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                };

                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 10000);

            }
        });

        /*online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flag=false;
                player_found=false;
                play_as="null";
                player_play_as="null";

                final CharSequence players[]={"Player 1","Player 2"};
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Play as:");

                builder.setSingleChoiceItems(players, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        play_as=players[which].toString();
                        if(play_as.equals("Player 1"))
                            player_play_as="Player 2";
                        else
                            player_play_as="Player 1";

                        time=System.currentTimeMillis();
                        databaseReference.child(key).child("play_online_time").setValue(time);

                        dialog.cancel();

                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        progressDialog.setContentView(R.layout.progress_dialog);
                        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        Map<String, Object> update = new HashMap<>();
                        update.put("play_online", true);
                        update.put("play_online_as_player", play_as);
                        databaseReference.child(key).updateChildren(update);

                        databaseReference.orderByChild("play_online_as_player").equalTo(player_play_as).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {

                                    String player_key=ds.getKey();
                                    Map map = (Map) ds.getValue();
                                    String player_name=map.get("name").toString();
                                    String player_email = map.get("email").toString();
                                    String player_play_as = map.get("play_online_as_player").toString();

                                    if (player_found == false
                                            && !player_play_as.equals(play_as)
                                            && System.currentTimeMillis()<time+10000) {
                                        setTitle(player_email);
                                        player_found = true;
                                        progressDialog.dismiss();

                                        Map<String, Object> update = new HashMap<>();
                                        update.put(key+"/play_online", false);
                                        update.put(key+"/play_online_as_player", "null");
                                        update.put(player_key+"/play_online", false);
                                        update.put(player_key+"/play_online_as_player", "null");
                                        databaseReference.updateChildren(update);

                                        if(flag==false) {
                                            flag=true;
                                            Intent intent = new Intent(MainActivity.this, play_online.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("key", key);
                                            bundle.putString("play_as",play_as);
                                            bundle.putString("player_key",player_key);
                                            bundle.putString("player_name",player_name);
                                            intent.putExtras(bundle);
                                            //startActivity(intent);
                                            Toast.makeText(MainActivity.this,player_name,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Runnable progressRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if(progressDialog.isShowing() && player_found==false) {
                                    databaseReference.child(key).child("play_online").setValue(false);
                                    databaseReference.child(key).child("play_online_as_player").setValue("null");
                                    Toast.makeText(MainActivity.this,"Try Again Later",Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }
                        };

                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 10000);

                    }
                });

                AlertDialog alert=builder.create();
                alert.setCanceledOnTouchOutside(false);
                alert.setCancelable(false);
                alert.show();

            }
        });*/

        single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, player_detail.class);
                Bundle bundle=new Bundle();
                bundle.putString("mode","Single Player");
                bundle.putString("key",key);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, player_detail.class);
                Bundle bundle=new Bundle();
                bundle.putString("mode","Two Player");
                bundle.putString("key",key);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            Toast.makeText(MainActivity.this,"Press again to exit",Toast.LENGTH_SHORT).show();
        }
        backPressedTime=System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        databaseReference.child(key).child("play_online").setValue(false);
        //databaseReference.child(key).child("play_online_as_player").setValue("null");
        if(progressDialog.isShowing())
            progressDialog.dismiss();
        super.onStop();
    }
}