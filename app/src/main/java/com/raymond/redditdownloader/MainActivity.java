package com.raymond.redditdownloader;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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


public class MainActivity extends AppCompatActivity {
    final Context context = this;
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
        fm.beginTransaction().add(R.id.main_container, fragmentDownload, "1").commit();
        fm.beginTransaction().add(R.id.main_container, fragmentHistory, "2").hide(fragmentHistory).commit();
        fm.beginTransaction().add(R.id.main_container, fragmentSettings, "3").hide(fragmentSettings).commit();



        // Bottom Nav Bar
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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

    }
