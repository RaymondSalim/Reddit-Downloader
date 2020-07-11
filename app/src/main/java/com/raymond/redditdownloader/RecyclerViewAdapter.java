package com.raymond.redditdownloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private LinkedList<String> mData;
    private LayoutInflater layoutInflater;

    // data is passed into the constructor
    public RecyclerViewAdapter(Context context, LinkedList<String> data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.mData = data;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = layoutInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        String mCurrent = mData.get(position);
        holder.wordItemView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView wordItemView;
        final RecyclerViewAdapter mAdapter;

        public ViewHolder(View itemView, RecyclerViewAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.word );
            this.mAdapter = adapter;

        }
    }
}
