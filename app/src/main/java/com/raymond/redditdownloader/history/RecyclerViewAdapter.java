package com.raymond.redditdownloader.history;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raymond.redditdownloader.R;

import java.util.LinkedList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final LinkedList<String> mDataString;
    private final LayoutInflater layoutInflater;


    // data is passed into the constructor
    public RecyclerViewAdapter(Context context, LinkedList<String> data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.mDataString = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = layoutInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(mItemView, this);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
            String mCurrent = mDataString.get(position);
            holder.wordItemView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return mDataString.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView wordItemView;
        public final ImageButton moreVert;
        final RecyclerViewAdapter mAdapter;

        public ViewHolder(final View itemView, RecyclerViewAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.word);
            moreVert = itemView.findViewById(R.id.moreVert);
            this.mAdapter = adapter;

            moreVert.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(moreVert.getContext(), itemView);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.copyLink:
                                    ClipboardManager clipboardManager = (ClipboardManager) moreVert.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("url", "https://www.reddit.com" + wordItemView.getText());
                                    clipboardManager.setPrimaryClip(clip);
                                    Toast.makeText(moreVert.getContext(), "URL copied to clipboard!", Toast.LENGTH_SHORT).show();
                                    return true;

                            }
                            return false;
                        }
                    });
                    popupMenu.inflate(R.menu.popup);
                    popupMenu.setGravity(Gravity.RIGHT);
                    popupMenu.setForceShowIcon(true);
                    popupMenu.show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            int mPosition = getLayoutPosition(); //  The position of the element clicked

        }
    }
}
