package com.example.akilliev;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Liste extends AppCompatActivity {
    LinearLayout linearLayout;
    String token;
    ListeleTh th;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);
        linearLayout = findViewById(R.id.linearl2);
        Intent intent3 = getIntent();
        token = intent3.getStringExtra("token");
        th=new ListeleTh();
        th.start();

    }

    public TextView olustur(){
        return  new TextView(this);
    }


    class ListeleTh  extends Thread{

        @Override
        public void run() {

            String stateUrl = "http://10.0.2.2:8080/ev/list/states";
            URL url = null;
            try {
                url = new URL(stateUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            int responseCode = 0;
            try {
                assert url != null;
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("User-Agent", "MyApp/1.0");
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setDoOutput(true);
                connection.setDoInput(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                        responseCode = connection.getResponseCode();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (responseCode == 200) {
                InputStreamReader reader = null;
                try {
                    reader = new InputStreamReader(connection.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader reader1 = new BufferedReader(reader);
                StringBuilder response = new StringBuilder();
                String line;
                try {
                    while ((line = reader1.readLine()) != null) {
                        response.append(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    JSONArray array=new JSONArray(response.toString());
                    for (int i=0;i<array.length();i++){
                        JSONObject object=array.getJSONObject(i);
                        TextView txt=olustur();
                        txt.setTextSize(18);
                        txt.setText(object.getString("isiklar")+","+object.getString("klima")+","+object.getString("televizyon"));
                        linearLayout.addView(txt);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }
    }

}