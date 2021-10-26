package com.example.finalproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;


@RequiresApi(api = Build.VERSION_CODES.N)
public class AddbillActivity extends AppCompatActivity
            implements DatePickerDialog.OnDateSetListener,View.OnClickListener{


    //顯示當前日期
    Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);
    int weekday = c.get(Calendar.DAY_OF_WEEK);

    TextView setdate; // = (TextView) findViewById(R.id.txv_setdate);//find不能寫在onCreate前
    EditText setasset, settype, setcost, setcontent;
    Button btn_income, btn_outcome, btn_cash, btn_account, btn_new;

    SQLiteDatabase db;  //資料庫
    static final String tb_name = "test";    //資料表名稱
    static final String[] FROM = new String[] {"inout", "date", "month","year", "asset", "type", "cost", "content", "date2"}; //資料表欄位
    String inorout="", sdate, smonth, syear;
    int costdecide; //決定正負數/收入支出

    String inorup;//決定新增或更新
    int id;//更新用的


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbill);
        getSupportActionBar().show();

        setdate = (TextView) findViewById(R.id.txv_setdate);
        //設定系統日期
        setDate(setdate);

        setasset = (EditText) findViewById(R.id.edtasset);
        settype = (EditText) findViewById(R.id.edttype);
        setcost = (EditText) findViewById(R.id.edtcost);
        setcontent = (EditText) findViewById(R.id.edtcontent);

        //按鈕們
        //Button btn_income = (Button) findViewById(R.id.btn_income);
        btn_income = (Button) findViewById(R.id.btn_income);
        btn_outcome = (Button) findViewById(R.id.btn_outcome);
        btn_cash = (Button) findViewById(R.id.btn_cash);
        btn_account = (Button) findViewById(R.id.btn_account);
        btn_new = (Button) findViewById(R.id.btn_new);

        btn_income.setOnClickListener(this);
        btn_outcome.setOnClickListener(this);
        btn_cash.setOnClickListener(this);
        btn_account.setOnClickListener(this);
        btn_new.setOnClickListener(this);

        //資料庫
        MyDBOpenHelper dbHelper = new MyDBOpenHelper(this);
        db = dbHelper.getWritableDatabase();

        //檢查為新增或更新頁面
        Intent it = getIntent();
        inorup = it.getStringExtra("新增更新");


        if(inorup.equals("新增")){
            btn_new.setText(getResources().getString(R.string.str_add));
        }

        else if(inorup.equals("更新")){//設定更新介面

            btn_new.setText(getResources().getString(R.string.str_update));
            String[] FROMU = new String[] {"inout", "date", "month", "year", "asset", "type", "content", "date2"};
            /*String str="";
            for(int i = 0; i<8; i++) {
                str += FROMU[i]+"\t" + it.getStringExtra(FROMU[i])+"\n";
            }
            int co = it.getIntExtra("cost",0);
            id = it.getIntExtra("_id",0);
            str += "cost\t"+ co + "\n_id\t" + id;
            txv.setText(str);*/

            //資料編號
            id = it.getIntExtra("_id",0);
            //收支
            inorout = it.getStringExtra(FROMU[0]);
            if(inorout.equals(getResources().getString(R.string.str_income))){
                btn_income.setTextColor(Color.BLUE);
                btn_outcome.setTextColor(Color.DKGRAY);
                costdecide = 1;
            }
            else if(inorout.equals(getResources().getString(R.string.str_outcome))){
                btn_income.setTextColor(Color.DKGRAY);
                btn_outcome.setTextColor(Color.RED);
                costdecide = -1;
            }
            //日期
            syear = it.getStringExtra(FROMU[3]);
            smonth= it.getStringExtra(FROMU[2]);
            sdate = it.getStringExtra(FROMU[1]);
            setdate.setText(syear+"/"+smonth+"/"+sdate);
            //資產
            setasset.setText(it.getStringExtra(FROMU[4]));
            //類型
            settype.setText(it.getStringExtra(FROMU[5]));
            //金額
            int cos = it.getIntExtra("cost",0);
            String sc = "" + Math.abs(cos);;
            setcost.setText(sc);
            //內容
            setcontent.setText(it.getStringExtra(FROMU[6]));
        }

    }

    public void addData(String inout, String date, String month,String year,String asset,String type, int cost, String content) {
        ContentValues cv = new ContentValues(9);  //建立含n個項目之物件

        String date2;
        cv.put(FROM[0], inout);
        cv.put(FROM[1], date);
        cv.put(FROM[2], month);
        cv.put(FROM[3], year);
        cv.put(FROM[4], asset);
        cv.put(FROM[5], type);
        cv.put(FROM[6], cost);
        cv.put(FROM[7], content);

        if(month.length()==1)
            month = 0+month;
        if(date.length()==1)
            date = 0+date;
        date2 = year+"-"+month+"-"+ date;
        cv.put(FROM[8], date2);

        db.insert(tb_name, null, cv);
    }

    private void update(String inout, String date, String month,String year,String asset,String type, int cost, String content, int id){
        ContentValues cv = new ContentValues(9);  //建立含n個項目之物件

        String date2;
        cv.put(FROM[0], inout);
        cv.put(FROM[1], date);
        cv.put(FROM[2], month);
        cv.put(FROM[3], year);
        cv.put(FROM[4], asset);
        cv.put(FROM[5], type);
        cv.put(FROM[6], cost);
        cv.put(FROM[7], content);

        if(month.length()==1)
            month = 0+month;
        if(date.length()==1)
            date = 0+date;
        date2 = year+"-"+month+"-"+ date;
        cv.put(FROM[8], date2);

        db.update(tb_name, cv, "_id="+id, null);
    }

    //按鈕事件
    public void onClick(View v) {
        //按按鈕+1
        if (v.getId() == R.id.btn_income) {
            Toast.makeText(this,  getResources().getString(R.string.str_income), Toast.LENGTH_SHORT).show();
            btn_income.setTextColor(Color.BLUE);
            btn_outcome.setTextColor(Color.DKGRAY);
            inorout = "收入";
            costdecide = 1;

        } else if (v.getId() == R.id.btn_outcome) {
            Toast.makeText(this,  getResources().getString(R.string.str_outcome), Toast.LENGTH_SHORT).show();
            btn_income.setTextColor(Color.DKGRAY);
            btn_outcome.setTextColor(Color.RED);
            inorout = "支出";
            costdecide = -1;
        }
        else if (v.getId() == R.id.btn_cash) {
            String str = getResources().getString(R.string.str_cash);
            Toast.makeText(this,  str, Toast.LENGTH_SHORT).show();
            setasset.setText(str);
        }
        else if (v.getId() == R.id.btn_account) {
            String str = getResources().getString(R.string.str_account);
            Toast.makeText(this,  str, Toast.LENGTH_SHORT).show();
            setasset.setText(str);
        }
        else if (v.getId() == R.id.btn_new) {
            //將資料寫進資料庫
            String sasset = setasset.getText().toString();
            String stype = settype.getText().toString();
            String scost = setcost.getText().toString();
            String scontent= setcontent.getText().toString();
            int cost = 0;

            if(costdecide == -1){//支出
                try {
                        cost = Integer.parseInt(scost);
                        cost = -cost;
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, getResources().getString(R.string.str_costerr) , Toast.LENGTH_SHORT).show();
                }
            }
            else if(costdecide == 1) {//收入
                try {
                    cost = Integer.parseInt(scost);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, getResources().getString(R.string.str_costerr) , Toast.LENGTH_SHORT).show();
                }
            }

            if(inorout.length()==0){
                Toast.makeText(this, getResources().getString(R.string.str_inorout) , Toast.LENGTH_SHORT).show();
            }
            else if(scost.length()==0){
                Toast.makeText(this, getResources().getString(R.string.str_inputcost) , Toast.LENGTH_SHORT).show();
            }
            else{
                if(inorup.equals(getResources().getString(R.string.str_add))){
                    if(sasset.length()==0){
                        sasset = getResources().getString(R.string.str_else);
                    }
                    addData(inorout, sdate, smonth, syear, sasset, stype, cost, scontent);
                }
                else if(inorup.equals(getResources().getString(R.string.str_update))) {
                    if(sasset.length()==0){
                        sasset = getResources().getString(R.string.str_else);
                    }
                    update(inorout, sdate, smonth, syear, sasset, stype, cost, scontent,id);
                }
                finish();
            }
        }
    }

    public void setDate (TextView view){
        String wd = "";
        switch (weekday){
            case 0:
                wd = "";
                break;
            case 1:
                wd = "日";
                break;
            case 2:
                 wd = "一";
                 break;
            case 3:
                wd = "二";
                break;
            case 4:
                wd = "三";
                break;
            case 5:
                wd = "四";
                break;
            case 6:
                wd = "五";
                break;
            case 7:
                wd = "六";
                break;
        }
        month++;
        view.setText( year+"/"+month+"/"+day+"("+ wd +")");
        syear =""+ year;
        smonth=""+ month;
        sdate =""+ day;
        //Date today = Calendar.getInstance().getTime();//getting date
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");//formating according to my need
        //String date = formatter.format(today);
        //view.setText(date);
    }

    //選時間
    public void pick(View v) {
        new DatePickerDialog(this, this,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)).show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // setdate.setText(year+"/"+(month+1)+"/"+day+"("+ wd +")");
        month++;
        setdate.setText(year+"/"+month+"/"+day);
        syear =""+ year;
        smonth=""+ month;
        sdate =""+ day;
    }
}