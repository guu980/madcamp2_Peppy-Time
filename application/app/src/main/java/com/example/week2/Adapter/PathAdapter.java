package com.example.week2.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week2.R;

import java.util.ArrayList;

public class PathAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> path;

    class PathViewHolder extends RecyclerView.ViewHolder {
        private TextView pathName;

        public PathViewHolder(@NonNull View itemView) {
            super(itemView);
            pathName = itemView.findViewById(R.id.path_row);
        }
    }

    public PathAdapter(ArrayList<String> path) {
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
    }

    @Override
    public int getItemCount() {
        return path.size();
    }
}
