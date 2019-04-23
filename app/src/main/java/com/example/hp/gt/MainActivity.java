package com.example.hp.gt;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hp.gt.Util.Languages;
import com.example.hp.gt.Util.SQLiteDbHelper;
import com.example.hp.gt.entity.Note;
import com.facebook.stetho.Stetho;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.hp.gt.Util.PureNetUtil.getUrlWithQueryString;
import static com.example.hp.gt.Util.PureNetUtil.md5;


public class MainActivity extends AppCompatActivity {
    private String content = "";
    private String source = "";
    private String target = "";
    private Button button1; //翻译按钮
    private Button button2; //标记按钮
    private Button button3; //查看笔记按钮
    private ListView listView; //主页展示历史查询记录
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //数据库可视化工具
        Stetho.initializeWithDefaults(this);

        listView = findViewById(R.id.list_view);
        registerForContextMenu(listView);

        //获取数据库对象
        SQLiteDbHelper helper = new SQLiteDbHelper(getApplicationContext());
        final SQLiteDatabase database = helper.getWritableDatabase();
        final List<Note> notes = new ArrayList<>();
        final List<String> record = new ArrayList<>();



        //主界面show历史记录
        final Thread showHistoryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("开启", "查询历史历史记录");

                Cursor cursor = database.query(SQLiteDbHelper.NOTES, new String[]{"id",
                        "source", "target"}, "ismark=?", new String[]{"0"}, null, null, "id DESC");

                System.out.print(cursor.toString());

                // 不断移动光标获取值
                while (cursor.moveToNext()) {
                    // 直接通过索引获取字段值
                    int id = cursor.getInt(0);
                    // 先获取 name 的索引值，然后再通过索引获取字段值
                    String source = cursor.getString(cursor.getColumnIndex("source"));
                    String target = cursor.getString(cursor.getColumnIndex("target"));
//                    Integer ismark = cursor.getInt(cursor.getColumnIndex("ismark"));
                    Log.e("", "id: " + id + " source: " + source + target + 0);

//                    notes.add(new Note(source, target, 0));

                    record.add("From:      " + source + "     To      " + target);

                    //todo 打开新界面并且传数据
                }

                // 关闭光标
                cursor.close();
            }
        });
        showHistoryThread.start();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, record);
        listView.setAdapter(arrayAdapter);


        button1 = (Button) this.findViewById(R.id.b1);
        button2 = (Button) this.findViewById(R.id.b2);
        button3 = (Button) this.findViewById(R.id.b3);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        EditText editText = (EditText) findViewById(R.id.input);
                        Spinner s1 = (Spinner) findViewById(R.id.source);
                        Spinner s2 = (Spinner) findViewById(R.id.target);
                        content = editText.getText().toString();
                        source = (String) s1.getSelectedItem();
                        target = (String) s2.getSelectedItem();
                        Languages languages = new Languages();
                        String sourceCode = languages.getCode(source);
                        String targetCode = languages.getCode(target);
                        if (content == null || content.length() == 0) {
                            beforeshowResponse("请输入查询内容");
                            Thread.interrupted();
                        }else {
                            beforeshowResponse("正在查询...");
                        }
                        String appKey = "4a29dc4c730f98ab";
                        String salt = String.valueOf(System.currentTimeMillis());
                        String sign = md5(appKey + content + salt + "SaZMzqwIExQhFIs3M8VifpTY7OScLPzn");
                        Map params = new HashMap();
                        params.put("q", content);
                        params.put("from", sourceCode);
                        params.put("to", targetCode);
                        params.put("sign", sign);
                        params.put("salt", salt);
                        params.put("appKey", appKey);

                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(getUrlWithQueryString("https://openapi.youdao.com/api", params))
                                .get()
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            String responseData = response.body().string();
                            showResponse(responseData, database);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                //刷新Listview
                new Thread(showHistoryThread).start();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("b2", "插入数据");
                        //插入标记的数据
                        EditText editText = (EditText) findViewById(R.id.input);
                        EditText editText2 = (EditText) findViewById(R.id.output);
                        ContentValues values = new ContentValues();
                        values.put("source", editText.getText().toString());
                        values.put("target", editText2.getText().toString());
                        values.put("ismark", 1);
                        database.insert(SQLiteDbHelper.NOTES, null, values);
                        Log.e("b2", "插入数据完毕");

                        Cursor cursor = database.query(SQLiteDbHelper.NOTES, new String[]{"id",
                                "source", "target"}, null, null, null, null, "id DESC");
                        int id=0;
                        while (cursor.moveToNext()) {
                            id = cursor.getInt(cursor.getColumnIndex("id"));
                        }
                        database.execSQL(" UPDATE notes SET ismark = 1 WHERE id =" + id);
                    }
                }).start();
            }
        });


        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, NotesActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("menu");//上下文菜单的标题
        menu.add(1, 1, 1, "添加到笔记");
        menu.add(1, 2, 2, "删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Toast.makeText(this, "msg1", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "msg2", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onContextItemSelected(item);
    }


    private void beforeshowResponse(String string) {
        EditText editText2 = (EditText) findViewById(R.id.output);
        editText2.setText(string);
    }

    //show翻译内容并且将查询添加到历史记录中
    private void showResponse(final String response, final SQLiteDatabase database) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Object translation = jsonObject.get("translation");
                    EditText editText2 = (EditText) findViewById(R.id.output);
                    editText2.setText(translation.toString());

                    EditText editText = (EditText) findViewById(R.id.input);
                    ContentValues values = new ContentValues();
                    values.put("source", editText.getText().toString());
                    values.put("target", editText2.getText().toString());
                    values.put("ismark", 0);
                    database.insert(SQLiteDbHelper.NOTES, null, values);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
