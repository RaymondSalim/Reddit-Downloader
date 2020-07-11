package com.raymond.redditdownloader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class HistoryFragment extends Fragment {
    public RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;
    public final LinkedList<String> mData = new LinkedList<>();

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
        // RecyclerView Initialize
        // Get a handle to the RecyclerView
        recyclerView = view.findViewById(R.id.recyclerview);
        // Create and supply data to be displayed
        mAdapter = new RecyclerViewAdapter(getContext(), mData);
        // Connect the adapter with the RecyclerView
        recyclerView.setAdapter(mAdapter);
        // Give the recyclerview a default layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO! Save history to sharedpreferences

    }
}