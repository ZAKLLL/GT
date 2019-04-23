package com.example.hp.gt.Util;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

public class Dbutil extends AppCompatActivity {
    //获取数据库对象
    SQLiteDbHelper helper = new SQLiteDbHelper(getApplicationContext());
    final SQLiteDatabase database = helper.getWritableDatabase();


    //添加到笔记
    public void addNote(int id) {

    }

    //删除某一条记录
}
