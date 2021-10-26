package com.example.finalproject;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDBOpenHelper extends SQLiteOpenHelper {

    static final String db_name = "testDB";  //資料庫名稱
    static final String tb_name = "test";    //資料表名稱

    public MyDBOpenHelper(Context context){
        super(context,db_name,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
        String createTable = "CREATE TABLE IF NOT EXISTS " + tb_name +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "inout VARCHAR(16), " + //收支
                "date VARCHAR(16), " +  //日月年
                "month VARCHAR(16), " +
                "year VARCHAR(16), " +
                "asset VARCHAR(16), " + //資產(帳戶現金)
                "type VARCHAR(16), " +  //類型
                "cost VARCHAR(16), " +  //金額
                "content VARCHAR(50), " +
                "date2 TEXT)";
        */

        String createTable = "CREATE TABLE IF NOT EXISTS " + tb_name +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "inout VARCHAR(16), " + //收支
                "date VARCHAR(16), " +  //日月年
                "month VARCHAR(16), " +
                "year VARCHAR(16), " +
                "asset VARCHAR(16), " + //資產(帳戶現金)
                "type VARCHAR(16), " +  //類型
                "cost INTEGER, " +  //金額
                "content VARCHAR(50), " +
                "date2 TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // TODO Auto-generated method stub

    }
}

