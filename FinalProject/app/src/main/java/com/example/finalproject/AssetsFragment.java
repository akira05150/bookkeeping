package com.example.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

//import
public class AssetsFragment extends Fragment {


    static final String db_name = "testDB";  //資料庫名稱
    static final String tb_name = "test";    //資料表名稱
    Cursor cur;
    SQLiteDatabase db;
    MyDBOpenHelper dbhelper;
    int total;

    TextView acost, dcost, tcost;

    SimpleCursorAdapter adapter;
    ListView lv;
    static final String[] FROMS = new String[] { "ASSET_TYPE", "ASSET_COST"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assets, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        acost = view.findViewById(R.id.txv_acost);
        acost.setTextColor(Color.parseColor("#2196F3"));
        dcost = view.findViewById(R.id.txv_dcost);
        dcost.setTextColor(Color.parseColor("#F44336"));
        tcost = view.findViewById(R.id.txv_tcost);
        lv = view.findViewById(R.id.lv);

        //計算收入金額
        dbhelper = new MyDBOpenHelper(view.getContext());
        db = dbhelper.getReadableDatabase();
        cur = db.rawQuery("SELECT SUM(cost) AS ATOTAL FROM "+ tb_name,null);
        if (cur.moveToFirst()) {
            total = cur.getInt(cur.getColumnIndex("ATOTAL"));
        }
        if(total>=0){
            String str = ""+total;
            acost.setText(str);
            dcost.setText("0");
            tcost.setText(str);
            tcost.setTextColor(Color.parseColor("#2196F3"));
        }
        else{
            total = Math.abs(total); //正數化
            String str = ""+total;
            acost.setText("0");
            dcost.setText(str);
            tcost.setText(str);
            tcost.setTextColor(Color.parseColor("#F44336"));
        }

//        cur = db.rawQuery("SELECT asset AS ASSET_TYPE, SUM(cost) AS ASSET_COST FROM "+ tb_name +
//                               " GROUP BY asset",null);
        cur = db.rawQuery("SELECT *, SUM(cost) AS TC FROM "+ tb_name
                +" GROUP BY asset ORDER BY TC DESC",null);
        cur.moveToFirst();

        //建立adapter物件
        adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.item_asset,
                cur,
                new String[] {"asset","TC"},
                new int[] {R.id.txv_atype, R.id.txv_atypecost},
                0);
        lv.setAdapter(adapter);


        return view;
    }
}

