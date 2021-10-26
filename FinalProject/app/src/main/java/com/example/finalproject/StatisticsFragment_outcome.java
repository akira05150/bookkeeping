package com.example.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;


//import
public class StatisticsFragment_outcome extends Fragment {

    @NonNull
    public static Fragment newInstance() {
        return new StatisticsFragment_outcome();
    }

    @SuppressWarnings("FieldCanBeLocal")
    private PieChart chart;

    // Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");

    static final String db_name = "testDB";  //資料庫名稱
    static final String tb_name = "test";    //資料表名稱
    SQLiteDatabase db;
    MyDBOpenHelper dbHelper;
    Cursor cur;
    String inorout;

    //取得目前時間
    Calendar c = Calendar.getInstance();
    int cyear = c.get(Calendar.YEAR);
    int cmonth = c.get(Calendar.MONTH)+1;
    //int cdate = c.get(Calendar.DAY_OF_MONTH);

    TextView txv;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_outcome, container, false);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        //先有資料
        dbHelper = new  MyDBOpenHelper(view.getContext());
        db = dbHelper.getReadableDatabase();

        //預設支出
        inorout = getResources().getString(R.string.str_outcome);

        //尋找當月支出:分類、總和
//        cur = db.rawQuery("SELECT type AS TYPEc, SUM(cost) AS COSTc FROM "+ tb_name +
//                " WHERE year == "+ cyear + " AND month == " + cmonth +
//                " AND inout IN('"+ inorout +"') GROUP BY type  ORDER BY COSTc",null);
//
//        int typecount = cur.getCount();

        int typecount = query(cyear,cmonth,inorout);

        //建立支出類型、類型消費額
        String []TYPE = new String[typecount];
        float []COST = new float[typecount];
        String str="";
        int i = 0;
        if(cur.moveToFirst()){
            do{
                str = cur.getString((cur.getColumnIndex("TYPEc")));
                if(str.equals("")){
                    str = getResources().getString(R.string.str_else);
                }
                TYPE[i] = str;
                COST[i] = cur.getInt((cur.getColumnIndex("COSTc")));
                //str += TYPE[i]+"\t"+COST[i]+"\n";
                i++;
            }while(cur.moveToNext());
        }

        //建立消費比例
        float []percent = new float[typecount];
        int total = 0;
        for(int j =0; j<typecount; j++){//總額
            total += COST[j];
        }
        for(int j =0; j<typecount; j++){//比例
            percent[j] = COST[j]/total;
            //str += percent[j]+"\n";
        }
        //顯示支出
        int itotal = Math.abs(total);
        String t = "$"+itotal;

        //
        txv = view.findViewById(R.id.txv);
        txv.setText(cyear+"/"+cmonth+getResources().getString(R.string.str_moutcome));

        //繪製圓餅圖
        chart = view.findViewById(R.id.pieChart1);
        chart.setUsePercentValues(true);

        chart.setCenterText(generateCenterText(t)); //設定中間文字

        chart.setCenterTextSize(16f);
        chart.setEntryLabelColor(Color.BLACK);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(16,10,16,0);

        // radius of the center hole in percent of maximum radius
        chart.animateX(1000);//動畫
        chart.setHoleRadius(30f);
        chart.setTransparentCircleRadius(35f);

        Legend l = chart.getLegend();
        l.setTextSize(14f);
        //l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        //l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);

        chart.setData(generatePieData(typecount, TYPE, percent));  //設定chart資料
        chart.invalidate();  //重繪


        return view;
    }

    public int query(int cyear, int cmonth, String inorout){

        if(cmonth==0){//年
            cur = db.rawQuery("SELECT type AS TYPEc, SUM(cost) AS COSTc FROM "+ tb_name +
                    " WHERE year == "+ cyear +
                    " AND inout IN('"+ inorout +"') GROUP BY type  ORDER BY COSTc",null);

        }
        else{//月
            cur = db.rawQuery("SELECT type AS TYPEc, SUM(cost) AS COSTc FROM "+ tb_name +
                    " WHERE year == "+ cyear + " AND month == " + cmonth +
                    " AND inout IN('"+ inorout +"') GROUP BY type  ORDER BY COSTc",null);
        }

        //int typecount = cur.getCount();
        return cur.getCount();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.picker_item,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        final Calendar today = Calendar.getInstance();//取得目前時間
        int nowyear = today.get(Calendar.YEAR);
        int nowmonth = today.get(Calendar.MONTH);


        switch (item.getItemId()) {

//            case R.id.week:
//                //建立週曆交談窗
//
//
//                return true;
            case R.id.yearmonth:
                //建立年月選擇交談窗
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getActivity(),
                        new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                cyear = selectedYear;
                                cmonth = selectedMonth+1;
                                txv.setText(cyear+"/"+cmonth+getResources().getString(R.string.str_moutcome));

                                int typecount = query(cyear,cmonth,inorout);
                                //建立支出類型、類型消費額
                                String []TYPE = new String[typecount];
                                float []COST = new float[typecount];
                                String str="";
                                int i = 0;
                                if(cur.moveToFirst()){
                                    do{
                                        str = cur.getString((cur.getColumnIndex("TYPEc")));
                                        if(str.equals("")){
                                            str = getResources().getString(R.string.str_else);
                                        }
                                        TYPE[i] = str;
                                        COST[i] = cur.getInt((cur.getColumnIndex("COSTc")));
                                        //str += TYPE[i]+"\t"+COST[i]+"\n";
                                        i++;
                                    }while(cur.moveToNext());
                                }

                                //建立消費比例
                                float []percent = new float[typecount];
                                int total = 0;
                                for(int j =0; j<typecount; j++){//總額
                                    total += COST[j];
                                }
                                for(int j =0; j<typecount; j++){//比例
                                    percent[j] = COST[j]/total;
                                    //str += percent[j]+"\n";
                                }
                                //顯示支出
                                int itotal = Math.abs(total);
                                String t = "$"+itotal;

                                //重繪
                                chart.setCenterText(generateCenterText(t));
                                chart.setData(generatePieData(typecount, TYPE, percent));
                                chart.invalidate();
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

                return true;

            case R.id.year:
                //建立年曆交談窗
                MonthPickerDialog.Builder builder1 = new MonthPickerDialog.Builder(getActivity(),
                        new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                cyear = selectedYear;
                                cmonth = 0;
                                txv.setText(cyear + getResources().getString(R.string.str_youtcome));

                                int typecount = query(cyear,cmonth,inorout);
                                //建立支出類型、類型消費額
                                String []TYPE = new String[typecount];
                                float []COST = new float[typecount];
                                String str="";
                                int i = 0;
                                if(cur.moveToFirst()){
                                    do{
                                        str = cur.getString((cur.getColumnIndex("TYPEc")));
                                        if(str.equals("")){
                                            str = getResources().getString(R.string.str_else);
                                        }
                                        TYPE[i] = str;
                                        COST[i] = cur.getInt((cur.getColumnIndex("COSTc")));
                                        //str += TYPE[i]+"\t"+COST[i]+"\n";
                                        i++;
                                    }while(cur.moveToNext());
                                }

                                //建立消費比例
                                float []percent = new float[typecount];
                                int total = 0;
                                for(int j =0; j<typecount; j++){//總額
                                    total += COST[j];
                                }
                                for(int j =0; j<typecount; j++){//比例
                                    percent[j] = COST[j]/total;
                                    //str += percent[j]+"\n";
                                }
                                //顯示支出
                                int itotal = Math.abs(total);
                                String t = "$"+itotal;

                                //重繪
                                chart.setCenterText(generateCenterText(t));
                                chart.setData(generatePieData(typecount, TYPE, percent));
                                chart.invalidate();
                            }
                        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

                builder1.setMinYear(2015)
                        .setActivatedYear(nowyear)
                        .setMaxYear(2030)
                        .setTitle("")
                        .showYearOnly()     //只顯示年
                        .build().show();



                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private SpannableString generateCenterText(String t) {
        SpannableString s = new SpannableString(getResources().getString(R.string.str_outcome)+"\n"+t);//為何長度不夠就搞我==
        //s.setSpan(new RelativeSizeSpan(2f), 0, 8, 0);
        //s.setSpan(new ForegroundColorSpan(Color.GRAY), 8, s.length(), 0);
        return s;
    }

    protected PieData generatePieData(int count, String[] TYPE, float[] percent) {

        //int count = 7;

        ArrayList<PieEntry> entries1 = new ArrayList<PieEntry>();

        for(int i = 0; i < count; i++) {
            entries1.add(new PieEntry( percent[i], TYPE[i]));
        }

        PieDataSet ds1 = new PieDataSet(entries1, "");
        ds1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ds1.setSliceSpace(2f);
        ds1.setDrawValues(true);
        ds1.setValueFormatter(new PercentFormatter());
        ds1.setValueTextColor(Color.BLACK);
        ds1.setValueTextSize(16f);

        PieData d = new PieData(ds1);
        //d.setValueTypeface(tf);

        return d;
    }






}

