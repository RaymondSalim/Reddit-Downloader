package com.raymond.redditdownloader.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.raymond.redditdownloader.R;
import com.raymond.redditdownloader.history.RecyclerViewAdapter;

import java.util.LinkedList;

public class HistoryFragment extends Fragment {
    public RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;
    public LinkedList<String> linkedList = new LinkedList<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        final SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("history", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonText = sharedPreferences.getString("key", null);
        if (jsonText != null) {
            linkedList = gson.fromJson(jsonText, LinkedList.class);
        }


        // RecyclerView Initialize
        // Get a handle to the RecyclerView
        recyclerView = view.findViewById(R.id.recyclerview);
        // Create and supply data to be displayed
        mAdapter = new RecyclerViewAdapter(getActivity(), linkedList);
        // Connect the adapter with the RecyclerView
        recyclerView.setAdapter(mAdapter);
        // Give the recyclerview a default layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        Toolbar toolbar = view.findViewById(R.id.toolbarHistory);
        toolbar.inflateMenu(R.menu.history);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case (R.id.clearHistory):
                        sharedPreferences.edit().remove("key").commit();
                        linkedList.clear();
                        mAdapter.notifyDataSetChanged();

                        Toast.makeText(getContext(), "History deleted", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });


    }

}