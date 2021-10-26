package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
        //implements BillFragment.callbackValue {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bottom Nav 設定
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new BillFragment()).commit();

        //網頁檢視資料庫
        Stetho.initializeWithDefaults(this);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    //用switch分析被按下的item
                    switch(item.getItemId()){
                        case R.id.nav_bill:
                            selectedFragment = new BillFragment();
                            break;
                        case R.id.nav_statistics:
                            selectedFragment = new StatisticsFragTab();
                            break;
                        case R.id.nav_assets:
                            selectedFragment = new AssetsFragment();
                            break;
//                        case R.id.nav_setting:
//                            selectedFragment = new SettingFragment();
//                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };

    /*
    @Override
    public void sendDataOfRecord(String data) {//
        Intent intent = new Intent();
        intent.putExtra("新增更新", data);
        intent.setClass(this, AddbillActivity.class);
        startActivity(intent);
    }*/
}