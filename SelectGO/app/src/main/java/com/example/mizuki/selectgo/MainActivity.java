package com.example.mizuki.selectgo;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "HttpExample";

    public Button btn;
    public TextView tv;
    public ListView lv;
    public URL url;
    public Spinner sp_area, sp_type;
    public String data;
    public ArrayAdapter<String> adapterLocation;
    String Option;
    String[] location,gps,type,area;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.button);
        tv = (TextView) findViewById(R.id.textView2);
        sp_area = (Spinner) findViewById(R.id.spinner);
        sp_type = (Spinner) findViewById(R.id.spinner2);

        lv = (ListView)findViewById(R.id.listview);

        orignal_area();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //new一個intent物件，並指定Activity切換的class
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,MapsActivity.class);
                intent .putExtra("GPS",gps[i]);//可放所有基本類別
                intent .putExtra("LCT",location[i]);//可放所有基本類別
                //切換Activity
                startActivity(intent);
            }
        });
                btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gets the URL from the UI's text field.
                //http://yokansoul.ddns.net:25567/android/index.php?area=%E5%8C%97%E6%8A%95%E5%8D%80&type=JOJO%E8%A3%B8%E5%A5%94%E4%BA%8B%E4%BB%B6%E7%B0%BF
                data = "area=" + sp_area.getSelectedItem().toString() + "&type=" + sp_type.getSelectedItem().toString();
                String stringUrl = "http://203.64.69.155/php/android/index.php?" + data;
                tv.setText(stringUrl);
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new DownloadWebpageTask().execute(stringUrl);
                } else {
                    tv.setText("No network connection available.");
                }
            }
        });
    }
    public void orignal_area(){
        // Gets the URL from the UI's text field.
        //http://yokansoul.ddns.net:25567/android/index.php?area=%E5%8C%97%E6%8A%95%E5%8D%80&type=JOJO%E8%A3%B8%E5%A5%94%E4%BA%8B%E4%BB%B6%E7%B0%BF
        String[] stringUrl = new String[2];
        stringUrl[0] = "http://203.64.69.155/php/android/area.php";
        stringUrl[1] = "http://203.64.69.155/php/android/type.php";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask_area().execute(stringUrl[0]);
            new DownloadWebpageTask_type().execute(stringUrl[1]);
        } else {
            tv.setText("No network connection available.");
        }
    }
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                if(Option == "area"){
                    //取值
                    //[{"Numbering":"2","Type":"JOJO\u88f8\u5954\u4e8b\u4ef6\u7c3f","Area":"\u5317\u6295\u5340","Time":"2015-01-09",
                    // "location":"\u53f0\u5317\u5e02\u5317\u6295\u5340\u5927\u540c\u91cc\u5149\u660e\u8def15\u865f (1-30\u865f)","GPS":"25.132558,121.499123"}]
                    JSONArray josn_data = new JSONArray(result);
                    int data_list = josn_data.length();
                    area = new String[data_list];

                    for (int i = 0; i < data_list; i++) {
                        area[i] = josn_data.getJSONObject(i).getString("Area");
                    }

                    ArrayAdapter<String> list_area = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,area);
                    list_area.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                    sp_area.setAdapter(list_area);
                    Option = "null";
                }
                else if (Option == "type")
                {
                    //取值
                    //[{"Numbering":"2","Type":"JOJO\u88f8\u5954\u4e8b\u4ef6\u7c3f","Area":"\u5317\u6295\u5340","Time":"2015-01-09",
                    // "location":"\u53f0\u5317\u5e02\u5317\u6295\u5340\u5927\u540c\u91cc\u5149\u660e\u8def15\u865f (1-30\u865f)","GPS":"25.132558,121.499123"}]
                    JSONArray josn_data = new JSONArray(result);
                    int data_list = josn_data.length();
                    type = new String[data_list];

                    for (int i = 0; i < data_list; i++) {
                        type[i] = josn_data.getJSONObject(i).getString("Type");
                    }

                    ArrayAdapter<String> list_type = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,type);
                    list_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    sp_type.setAdapter(list_type);
                    Option = "null";
                }
                else {
                    //取值
                    //[{"Numbering":"2","Type":"JOJO\u88f8\u5954\u4e8b\u4ef6\u7c3f","Area":"\u5317\u6295\u5340","Time":"2015-01-09",
                    // "location":"\u53f0\u5317\u5e02\u5317\u6295\u5340\u5927\u540c\u91cc\u5149\u660e\u8def15\u865f (1-30\u865f)","GPS":"25.132558,121.499123"}]
                    JSONArray josn_data = new JSONArray(result);
                    int data_list = josn_data.length();
                    location = new String[data_list];
                    gps = new String[data_list];

                    for (int i = 0; i < data_list; i++) {
                        location[i] = josn_data.getJSONObject(i).getString("location");
                        gps[i] = josn_data.getJSONObject(i).getString("GPS");
                    }
                    //tv.setText(location[1] + "");

                    adapterLocation = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, location);
                    adapterLocation.notifyDataSetChanged();
                    lv.setAdapter(adapterLocation);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }}
    }
    private class DownloadWebpageTask_area extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                    //取值
                    //[{"Numbering":"2","Type":"JOJO\u88f8\u5954\u4e8b\u4ef6\u7c3f","Area":"\u5317\u6295\u5340","Time":"2015-01-09",
                    // "location":"\u53f0\u5317\u5e02\u5317\u6295\u5340\u5927\u540c\u91cc\u5149\u660e\u8def15\u865f (1-30\u865f)","GPS":"25.132558,121.499123"}]
                    JSONArray josn_data = new JSONArray(result);
                    int data_list = josn_data.length();
                    area = new String[data_list];

                    for (int i = 0; i < data_list; i++) {
                        area[i] = josn_data.getJSONObject(i).getString("Area");
                    }

                    ArrayAdapter<String> list_area = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,area);
                    list_area.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                    sp_area.setAdapter(list_area);
            } catch (JSONException e) {
                e.printStackTrace();
            }}
    }
    private class DownloadWebpageTask_type extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                //取值
                //[{"Numbering":"2","Type":"JOJO\u88f8\u5954\u4e8b\u4ef6\u7c3f","Area":"\u5317\u6295\u5340","Time":"2015-01-09",
                // "location":"\u53f0\u5317\u5e02\u5317\u6295\u5340\u5927\u540c\u91cc\u5149\u660e\u8def15\u865f (1-30\u865f)","GPS":"25.132558,121.499123"}]
                JSONArray josn_data = new JSONArray(result);
                int data_list = josn_data.length();
                type = new String[data_list];

                for (int i = 0; i < data_list; i++) {
                    type[i] = josn_data.getJSONObject(i).getString("Type");
                }

                ArrayAdapter<String> list_type = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,type);
                list_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                sp_type.setAdapter(list_type);
            } catch (JSONException e) {
                e.printStackTrace();
            }}
    }


    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        //Reader reader = null;
        //reader = new InputStreamReader(stream, "UTF-8");
        //char[] buffer = new char[len];
        //reader.read(buffer);
        BufferedReader bufferedReader  = new BufferedReader( new InputStreamReader(stream) );
        String tempStr;
        StringBuffer stringBuffer = new StringBuffer();

        while( ( tempStr = bufferedReader.readLine() ) != null ) {
            stringBuffer.append( tempStr );
        }
        bufferedReader.close();
        // 網頁內容字串
        String responseString = stringBuffer.toString();
        return new String(responseString);
    }
}

