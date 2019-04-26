package com.example.hp.gt;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hp.gt.Util.SQLiteDbHelper;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {
    private ListView listView; //显示笔记
    private ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        SQLiteDbHelper helper = new SQLiteDbHelper(getApplicationContext());
        final SQLiteDatabase database = helper.getWritableDatabase();
        final List<String> marked = new ArrayList<>();

        listView = findViewById(R.id.list_notes);
        registerForContextMenu(listView);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("b3", "查询数据");

                Cursor cursor = database.query(SQLiteDbHelper.NOTES, new String[]{"id",
                        "source", "target"}, "ismark=?", new String[]{"1"}, null, null, "id DESC");

                // 不断移动光标获取值
                while (cursor.moveToNext()) {
                    // 直接通过索引获取字段值
                    int id = cursor.getInt(0);
                    // 先获取 name 的索引值，然后再通过索引获取字段值
                    String source = cursor.getString(cursor.getColumnIndex("source"));
                    Log.e("", "id: " + id + " source: " + source);
                    String s1 = cursor.getString(cursor.getColumnIndex("source"));
                    String t1 = cursor.getString(cursor.getColumnIndex("target"));
                    marked.add("From:      " + s1 + "     To      " + t1);
                }
                // 关闭光标
                cursor.close();
            }
        }).start();
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, marked);
        listView.setAdapter(arrayAdapter);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("menu");//上下文菜单的标题
        menu.add(1, 1, 1, "删除");
        menu.add(1, 2, 2, "清空");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:

                Toast.makeText(this, "你记住了么就删删删", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "不允许！", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onContextItemSelected(item);
    }



}
