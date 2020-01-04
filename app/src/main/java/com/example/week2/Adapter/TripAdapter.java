package com.example.week2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.week2.Data.LandMark;
import com.example.week2.R;
import com.example.week2.TripDetailActivity;
import com.example.week2.XML.getDetailXML;

import java.util.ArrayList;

public class TripAdapter extends RecyclerView.Adapter {

    private ArrayList<LandMark> landMarkArrayList;
    private ArrayList<LandMark> detailArrayList;
    private Context context;
    private getDetailXML getdetailxml;
    String contentid;
    String contenttypeid;

    class TripViewHolder extends RecyclerView.ViewHolder {
        private ImageView mainImage;
        private TextView title;
        private TextView address;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            mainImage = itemView.findViewById(R.id.firstimage);
            title = itemView.findViewById(R.id.title);
            address = itemView.findViewById(R.id.addr1);
        }
    }

    public TripAdapter(ArrayList<LandMark> landMarkArrayList, Context context) {
        this.landMarkArrayList = landMarkArrayList;
        this.context = context;
    }

    public void setAdapter(ArrayList<LandMark> landMarkArrayList) {
        this.landMarkArrayList = landMarkArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_info_rows, parent, false);
        return new TripViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        TripViewHolder tripViewHolder = (TripViewHolder) holder;
        tripViewHolder.title.setText(landMarkArrayList.get(position).getTagList().get("title"));
        tripViewHolder.address.setText(landMarkArrayList.get(position).getTagList().get("addr1"));

        if (landMarkArrayList.get(position).getTagList().get("firstimage") != null) {
            tripViewHolder.mainImage.getLayoutParams().height = 600;
            Glide.with(context).load(landMarkArrayList.get(position).getTagList().get("firstimage")).into(tripViewHolder.mainImage);
        } else {
            tripViewHolder.mainImage.getLayoutParams().height=0;
        }

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(Color.rgb(242, 242, 242));
        } else {
            holder.itemView.setBackgroundColor(Color.rgb(227, 226, 226));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contentid = landMarkArrayList.get(position).getTagList().get("contentid");
                contenttypeid = landMarkArrayList.get(position).getTagList().get("contenttypeid");

                getdetailxml = new getDetailXML(contentid, contenttypeid);
                try{
                    detailArrayList = getdetailxml.execute().get();
                }catch(Exception e){
                }

                Intent intent = new Intent(context, TripDetailActivity.class);
                intent.putExtra("title",landMarkArrayList.get(position).getTagList().get("title"));
                intent.putExtra("addr1", landMarkArrayList.get(position).getTagList().get("addr1"));
                intent.putExtra("firstimage", landMarkArrayList.get(position).getTagList().get("firstimage"));
                intent.putExtra("tel", landMarkArrayList.get(position).getTagList().get("tel"));
                intent.putExtra("mapx", landMarkArrayList.get(position).getTagList().get("mapx"));
                intent.putExtra("mapy", landMarkArrayList.get(position).getTagList().get("mapy"));
                intent.putExtra("overview", detailArrayList.get(0).getTagList().get("overview"));
                if (detailArrayList.get(0).getTagList().get("homepage") != null) {
                    intent.putExtra("homepage", detailArrayList.get(0).getTagList().get("homepage"));
                }

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return landMarkArrayList.size();
    }
}
