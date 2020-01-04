package com.example.week2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week2.DetailActivity;
import com.example.week2.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Uri> galleryList = null;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;

        public MyViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.img);
        }

        @Override
        public  void onClick(View view){
//            img = view.findViewById(R.id.img);
//            img.setMaxHeight(500);
//            img.setMaxWidth(400);
            if(view.getId() == R.id.img){
                Log.d("t", "-----------");
            }
        }
    }

    public MyAdapter(Context context, ArrayList<Uri> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.cell_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
//        myViewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        myViewHolder.img.setImageURI(galleryList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(context, DetailActivity.class);
                //intent.putExtra("uri", );
                intent.putExtra("uri", galleryList.get(position).toString());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return galleryList.size();
    }
}

