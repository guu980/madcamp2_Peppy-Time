package com.example.week2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week2.R;

import java.util.ArrayList;

public class PathAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> path;
    private Context context;

    class PathViewHolder extends RecyclerView.ViewHolder {
        private TextView pathName;

        public PathViewHolder(@NonNull View itemView) {
            super(itemView);
            pathName = itemView.findViewById(R.id.path_row);
        }
    }

    public PathAdapter(ArrayList<String> path, Context context) {
        this.path = path;
        this.context = context;
    }

    public void setAdapter(ArrayList<String> path) {
        this.path = path;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.path_rows, parent, false);
        return new PathViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PathViewHolder viewHolder = (PathViewHolder) holder;
        viewHolder.pathName.setText(path.get(position));
        viewHolder.pathName.setTextColor(ContextCompat.getColor(context, R.color.standard_text_color));
    }

    @Override
    public int getItemCount() {
        if (path == null) {
            return 0;
        }
        return path.size();
    }
}
