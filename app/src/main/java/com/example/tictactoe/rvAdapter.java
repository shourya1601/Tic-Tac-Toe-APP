package com.example.tictactoe;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class rvAdapter extends FirebaseRecyclerAdapter<model_listitem, rvAdapter.rvViewholder> {

    ProgressDialog progressDialog;

    public rvAdapter(@NonNull FirebaseRecyclerOptions<model_listitem> options, ProgressDialog progressDialog)
    {
        super(options);
        this.progressDialog=progressDialog;
    }

    @Override
    public void onDataChanged() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void
    onBindViewHolder(@NonNull rvViewholder holder, int position, @NonNull model_listitem model)
    {
        holder.tvname.setText(model.getName());
        holder.tvplayed.setText("Played: "+(model.getSingle_player_games_played()+model.getTwo_player_games_played())+"  ");
        holder.tvwon.setText("Won: "+model.getTwo_player_games_won()+"  ");
        holder.tvdraw.setText("Draw: "+(model.getSingle_player_games_draw()+model.getTwo_player_games_draw())+"  ");
        String image_url=model.getImage();
        if(image_url.equals("null"))
            holder.ivimage.setBackgroundResource(R.drawable.default_image);
        else
            Picasso.get().load(image_url).into(holder.ivimage);
    }

    @NonNull
    @Override
    public rvViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, parent, false);
        return new rvViewholder(view);
    }

    class rvViewholder extends RecyclerView.ViewHolder {

        TextView tvname, tvplayed, tvwon, tvdraw;
        ImageView ivimage;
        public rvViewholder(@NonNull View itemView)
        {
            super(itemView);

            ivimage = itemView.findViewById(R.id.image);
            tvname = itemView.findViewById(R.id.name);
            tvplayed = itemView.findViewById(R.id.played);
            tvwon = itemView.findViewById(R.id.won);
            tvdraw =itemView.findViewById(R.id.draw);
        }
    }
}
