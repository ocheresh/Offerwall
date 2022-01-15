package com.ocheresh.offerwall;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import android.os.Bundle;
import android.app.ProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import org.json.*;

import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static WebView webView;
    private Button butNext;
    public static int counter;
    private ProgressDialog progDailog;

    Retrofit retrofit;
    MessagesApi messagesApi;
    String answer;

    List<String> paths;
    List<Object> objects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progDailog = ProgressDialog.show(this, "Loading","Please wait...", true);
        progDailog.setCancelable(false);

        webView = (WebView) findViewById(R.id.webView);
        webView.clearCache(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progDailog.show();
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                progDailog.dismiss();
            }
        });


        butNext = (Button)findViewById(R.id.button);

        answer = null;
        paths = new ArrayList<String>();
        objects = new ArrayList<Object>();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://demo3005513.mockable.io/api/v1/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        messagesApi = retrofit.create(MessagesApi.class);

        counter = 0;

        try {
            Call<String> messages = messagesApi.messages("entities/getAllIds");
            messages.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    answer = response.body();
                    if (answer != null) {
                        Log.e("Answer: ", answer);
                        try {
                            JSONObject obj = new JSONObject(answer);
                            JSONArray arr = obj.getJSONArray("data");
                            for (int i = 0; i < arr.length(); i++) {
                                String post_id = arr.getJSONObject(i).getString("id");
                                paths.add(post_id);
                                createObject("object/" + post_id, messagesApi, objects);
                            }
                        }
                        catch(Exception e){
                            Log.e("Answer error json: ", e.getMessage().toString());
                        }
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("Answer : ", t.getMessage());
                }
            });
        }
        catch(Exception e) {
            Log.e("Answer Error: ", e.getMessage().toString());
        }

        butNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    webView.clearCache(true);
                    webView.clearView();
                    webView.clearAnimation();
                    webView.reload();
                    nextloadwebview(objects);
                } catch (Exception e) { }
            }
        });
    }

    public void createObject(String path, MessagesApi messagesApi, List<Object> objects){
        try {
            Call<String> messages = messagesApi.messages(path);
            messages.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String answer = response.body();
                    Object temp = new Object(answer);
                    Log.e("Answer Object : ", temp.getid() + "  " + temp.gettype() + " " + temp.gettype_info());
                    objects.add(temp);
                    if (objects.size() == 1)
                        loadwebview(temp);
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("Answer error create Object: ", t.getMessage());
                }
            });
        }
        catch(Exception e) {
            Log.e("Answer Error create Object:: ", e.getMessage().toString());
        }
    }

    public void loadwebview(Object obj) {
        Log.e("Answer " + " " + String.valueOf(counter) + " :", obj.gettype());
        counter++;
        switch (obj.gettype()){
            case ("text"):
                webView.loadData(obj.gettype_info(), "text/html; charset=utf-8", "utf-8");
                break;
            case ("webview"):
                webView.loadUrl(obj.gettype_info());
                break;
            case ("image"):
                webView.loadUrl(obj.gettype_info());
                break;
            default:
                webView.loadData("Идет загрузка...", "text/html; charset=utf-8", "utf-8");
                break;
        }
    }

    public void nextloadwebview(List<Object> objects){
        if (counter >= objects.size())
            counter = 0;
        if (counter < objects.size())
            loadwebview(objects.get(counter));
    }
}