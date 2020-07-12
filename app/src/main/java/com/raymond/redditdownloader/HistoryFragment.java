package com.raymond.redditdownloader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

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
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("history", Context.MODE_PRIVATE);
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


    }
}