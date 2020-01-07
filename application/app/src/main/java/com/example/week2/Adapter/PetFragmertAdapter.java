package com.example.week2.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week2.Fragments.PetFragment;
import com.example.week2.R;
import com.example.week2.RecordActivity;
import com.example.week2.WalkActivity;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.ContextCompat.startActivity;

public class PetFragmertAdapter extends RecyclerView.Adapter<PetFragmertAdapter.ViewHolder> {

    private List<String> indicator;
    private Activity activity;

    public PetFragmertAdapter(Activity activity){
        List<String> tempIndicator = new ArrayList<String>();
        tempIndicator.add("Go to Walk");
        tempIndicator.add("Check Walking Record");
        this.indicator = tempIndicator;
        this.activity = activity;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.pet_fragment_image);
        }
    }

    @NonNull
    @Override
    public PetFragmertAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.pet_frag_recycler_view_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PetFragmertAdapter.ViewHolder holder, int position) {
        if(position % 2 == 0 )
        {
            holder.image.setImageResource(R.drawable.start_walking_image_icon);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity.getApplicationContext(), WalkActivity.class);
                    activity.startActivity(intent);
                }
            });
        }
        else{
            holder.image.setImageResource(R.drawable.set_record_image_icon);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity.getApplicationContext(), RecordActivity.class);
                    activity.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return indicator.size();
    }
}
