package com.raymond.redditdownloader;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.LinkedList;


public class MainActivity extends AppCompatActivity {
    final Fragment fragmentDownload = new DownloadsFragment();
    final Fragment fragmentHistory = new HistoryFragment();
    final Fragment fragmentSettings = new SettingsFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragmentDownload;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fragments initialize
        fm.beginTransaction().add(R.id.main_container, fragmentDownload, "fragmentDownload").commit();
        fm.beginTransaction().add(R.id.main_container, fragmentHistory, "fragmentHistory").hide(fragmentHistory).commit();
        fm.beginTransaction().add(R.id.main_container, fragmentSettings, "fragmentSettings").hide(fragmentSettings).commit();
        fm.executePendingTransactions(); //


        // Bottom Nav Bar
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        // Theme
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String themePref = settings.getString("theme", "");
        setTheme(themePref);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_downloads:
                    fm.beginTransaction().hide(active).show(fragmentDownload).commit();
                    active = fragmentDownload;
                    item.setChecked(true);
                    return true;
                case R.id.action_history:
                    fm.beginTransaction().hide(active).show(fragmentHistory).commit();
                    active = fragmentHistory;
                    item.setChecked(true);
                    return true;
                case R.id.action_settings:
                    fm.beginTransaction().hide(active).show(fragmentSettings).commit();
                    active = fragmentSettings;
                    item.setChecked(true);
                    return true;
                }
            return false;
        }
    };

    private void setTheme(String themeSetting) {
        View view = getWindow().getDecorView();
        switch (themeSetting) {
            case "auto":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                view.setSystemUiVisibility(0); // Resets icon color in status bar to default
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // Sets windowLightStatusBar = true
                break;
        }
    }
    }
