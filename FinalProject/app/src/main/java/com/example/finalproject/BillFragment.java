package com.example.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;


public class BillFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {

    /*介面傳值
    private callbackValue Interface_callback; //回调接口，用来和activity互通信息

    public interface callbackValue
    {
        public void sendDataOfRecord(String data);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Interface_callback = (callbackValue) activity;
    }*/

    static final String tb_name = "test";    //資料表名稱
    //static final String[] FROM = new String[] {"inout", "date", "month","year", "asset", "type", "cost", "content"};
    static final String[] FROMS = new String[] { "date", "date2", "type", "cost", "content"};

    SQLiteDatabase db;  //資料庫
    MyDBOpenHelper dbHelper;

    String inoutStr,dateStr , monthStr , yearStr, asssetStr, typeStr, costStr, contentStr ;

    Cursor cur;
    SimpleCursorAdapter adapter;
    ListView lv;

    TextView txv_income, txv_outcome, txv_total,txv_incomed, txv_outcomed, txv_totald;
    int totalm, totald, incomem, incomed, outcomem, outcomed;

    TextView txv_time;

    //取得目前時間
    Calendar c = Calendar.getInstance();
    int cyear = c.get(Calendar.YEAR);
    int cmonth = c.get(Calendar.MONTH)+1;
    int cdate = c.get(Calendar.DAY_OF_MONTH);


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();  //隱藏actionbar

        //選擇帳單顯示月份
        txv_time = view.findViewById(R.id.txv_time);
        txv_time.setOnClickListener(this);
        txv_time.setText("< "+ cyear + "/" + cmonth+ getResources().getString(R.string.str_month) + " >");

        lv = view.findViewById(R.id.lv);
        dbHelper = new  MyDBOpenHelper(view.getContext());
        db = dbHelper.getReadableDatabase();

        //顯示收支總額
        txv_income = view.findViewById(R.id.txv_income);
        txv_outcome = view.findViewById(R.id.txv_outcome);
        txv_total = view.findViewById(R.id.txv_total);
        txv_incomed = view.findViewById(R.id.txv_incomed);
        txv_outcomed = view.findViewById(R.id.txv_outcomed);
        txv_totald = view.findViewById(R.id.txv_totald);

        //顯示收支情況
        monthIncome();
        monthOutcome();
        monthTotal();
        dayIncome();
        dayOutcome();
        dayTotal();

        Cursor cur = db.rawQuery("SELECT * FROM " + tb_name +
                " WHERE year == "+ cyear + " AND month == " + cmonth + " ORDER BY date(date2) DESC",null); //查詢

        if(cur.getCount() == 0 || !cur.moveToFirst()){
            Toast.makeText(getActivity()
                    , "沒有資料", Toast.LENGTH_LONG).show();
        }

        //建立adapter物件
        adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.item_linear,
                cur,
                FROMS,
                new int[] {R.id.txv_showd2, R.id.txv_showym2, R.id.txv_type2, R.id.txv_cost2, R.id.txv_content2},
                0);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);
        requery();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Fab_add
        FloatingActionButton fab_add = getView().findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打開新增bill的頁面
                Intent it = new Intent();
                it.setClass(getActivity(), AddbillActivity.class);
                it.putExtra("新增更新","新增");
                startActivity(it);

                //Interface_callback.sendDataOfRecord("新增");
            }
        });


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        cur.moveToPosition(position);

        String[] FROM = new String[] {"inout", "date", "month", "year", "asset", "type", "content", "date2"};
        //打開更新bill的頁面，將值傳入
        Intent it = new Intent();
        it.setClass(getActivity(), AddbillActivity.class);
        it.putExtra("新增更新","更新");

        //將該物件的資料傳出去
        for(int k = 0; k<8; k++){
            String str = cur.getString(cur.getColumnIndex(FROM[k]));
            it.putExtra(FROM[k] , str);
        }
        it.putExtra("cost", cur.getInt(cur.getColumnIndex("cost")));
        it.putExtra("_id", cur.getInt(cur.getColumnIndex("_id")));

        startActivity(it);
        requery();
        //Interface_callback.sendDataOfRecord("更新");

    }

    //刪除
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(getActivity(), "onlongClick", Toast.LENGTH_LONG).show();
        cur.moveToPosition(position);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage(getResources().getString(R.string.str_delete));
        dialog.setNegativeButton(getResources().getString(R.string.str_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requery();
            }
        });

        dialog.setPositiveButton(getResources().getString(R.string.str_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDelete();
            }
        });
        dialog.show();

        return true;
    }

    //重新查詢
    private void requery(){
        //cur = db.rawQuery("SELECT * FROM " + tb_name +
          //      " WHERE year == cyear AND month == cmonth ORDER BY date(date2) DESC",null); //查詢
        cur = db.rawQuery("SELECT * FROM " + tb_name +
                " WHERE year == "+ cyear + " AND month == " + cmonth + " ORDER BY date(date2) DESC",null); //查詢
        adapter.changeCursor(cur);    //更改adapter的Cursor
        adapter.notifyDataSetChanged();
    }


    //刪除
    public void onDelete(){
        db.delete(tb_name, "_id="+cur.getInt(0), null);
        requery();
        monthIncome();
        monthOutcome();
        monthTotal();
        dayIncome();
        dayOutcome();
        dayTotal();
    }

    //選擇月份
    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.txv_time){
            final Calendar today = Calendar.getInstance();//取得目前時間
            int nowyear = today.get(Calendar.YEAR);
            int nowmonth = today.get(Calendar.MONTH);

            //建立年月選擇交談窗
            MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getActivity(),
                    new MonthPickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(int selectedMonth, int selectedYear) {
                            cyear = selectedYear;
                            cmonth = selectedMonth+1;
                            txv_time.setText("< "+ cyear + "/" + cmonth+ getResources().getString(R.string.str_month) + " >");
                            requery();
                            monthIncome();
                            monthOutcome();
                            monthTotal();
                            dayIncome();
                            dayOutcome();
                            dayTotal();
                        }
                    }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

            builder.setActivatedMonth(nowmonth)
                    .setMinYear(2015)
                    .setActivatedYear(nowyear)
                    .setMaxYear(2030)
                    .setMinMonth(Calendar.JANUARY)
                    .setTitle("")
                    .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
                    .build().show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getActivity(), "onResume", Toast.LENGTH_LONG).show();
        requery();
        lv.setAdapter(adapter);
        monthIncome();
        monthOutcome();
        monthTotal();
        dayIncome();
        dayOutcome();
        dayTotal();
    }

    String findinout="";
    //計算收支
    public void monthIncome(){
        findinout = "收入";
        Cursor cur_summ = db.rawQuery("SELECT SUM(cost) AS INCOMEM FROM "+ tb_name +
                " WHERE year == "+ cyear + " AND month == " + cmonth + " AND inout IN('" + findinout + "')",null);
        if (cur_summ.moveToFirst()) {
            incomem = cur_summ.getInt(cur_summ.getColumnIndex("INCOMEM"));
        }
        String im = getResources().getString(R.string.str_income)+"\n" + incomem;
        txv_income.setText(im);
    }

    public void monthOutcome(){
        findinout = "支出";
        Cursor cur_summ = db.rawQuery("SELECT SUM(cost) AS OUTCOMEM FROM "+ tb_name +
                " WHERE year == "+ cyear + " AND month == " + cmonth + " AND inout IN('" + findinout + "')",null);
        if (cur_summ.moveToFirst()) {
            outcomem = cur_summ.getInt(cur_summ.getColumnIndex("OUTCOMEM"));
        }
        String om = getResources().getString(R.string.str_outcome)+"\n" + outcomem;
        txv_outcome.setText(om);
    }

    public void monthTotal(){
        Cursor cur_summ = db.rawQuery("SELECT SUM(cost) AS TOTALM FROM "+ tb_name +
                " WHERE year == "+ cyear + " AND month == " + cmonth,null);
        if (cur_summ.moveToFirst()) {
            totalm = cur_summ.getInt(cur_summ.getColumnIndex("TOTALM"));
        }
        String tm = getResources().getString(R.string.str_total)+"\n" + totalm;
        txv_total.setText(tm);
    }

    public void dayIncome(){
        findinout = "收入";
        Cursor cur_summ = db.rawQuery("SELECT SUM(cost) AS INCOMED FROM "+ tb_name +
                " WHERE year == "+ cyear + " AND month == " + cmonth + " AND date == " + cdate + " AND inout IN('" + findinout + "')",null);
        if (cur_summ.moveToFirst()) {
            incomed = cur_summ.getInt(cur_summ.getColumnIndex("INCOMED"));
        }
        String id = getResources().getString(R.string.str_income)+"\n" + incomed;
        txv_incomed.setText(id);
    }

    public void dayOutcome(){
        findinout = "支出";
        Cursor cur_summ = db.rawQuery("SELECT SUM(cost) AS OUTCOMED FROM "+ tb_name +
                " WHERE year == "+ cyear + " AND month == " + cmonth + " AND date == " + cdate + " AND inout IN('" + findinout + "')",null);
        if (cur_summ.moveToFirst()) {
            outcomed = cur_summ.getInt(cur_summ.getColumnIndex("OUTCOMED"));
        }
        String od = getResources().getString(R.string.str_outcome)+"\n" + outcomed;
        txv_outcomed.setText(od);
    }

    public void dayTotal(){
        Cursor cur_sumd = db.rawQuery("SELECT SUM(cost) AS TOTALD  FROM "+ tb_name +
                " WHERE year == "+ cyear + " AND month == " + cmonth + " AND date == " + cdate,null);
        if (cur_sumd.moveToFirst()) {
            totald = cur_sumd.getInt(cur_sumd.getColumnIndex("TOTALD"));
        }
        String td = getResources().getString(R.string.str_total) +"\n"+ totald;
        txv_totald.setText(td);
    }


}


