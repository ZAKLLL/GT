package com.example.hp.gt;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.hp.gt.PureNetUtil.getUrlWithQueryString;
import static com.example.hp.gt.PureNetUtil.md5;


public class MainActivity extends AppCompatActivity {
    String content = "";
    String source = "";
    String target = "";
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button = (Button) this.findViewById(R.id.b1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        EditText editText = (EditText) findViewById(R.id.input);
                        EditText editText2 = (EditText) findViewById(R.id.output);
                        Spinner s1 = (Spinner) findViewById(R.id.source);
                        Spinner s2 = (Spinner) findViewById(R.id.target);
                        content = editText.getText().toString();
                        source = (String) s1.getSelectedItem();
                        target = (String) s2.getSelectedItem();
                        Languages languages = new Languages();
                        String sourceCode = languages.getCode(source);
                        String targetCode = languages.getCode(target);

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
                            showResponse(responseData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                beforeshowResponse();
            }
        });

    }


    private void beforeshowResponse() {
        EditText editText2 = (EditText) findViewById(R.id.output);
        editText2.setText("正在查询...");
    }

    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Object translation = jsonObject.get("translation");
                    EditText editText2 = (EditText) findViewById(R.id.output);
                    editText2.setText(translation.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
