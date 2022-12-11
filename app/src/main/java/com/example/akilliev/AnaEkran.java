package com.example.akilliev;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AnaEkran extends AppCompatActivity {
    Getir getir;
    Button listeleBut;

    String token = "";
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch klimaSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch isikSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch televizyonSwitch;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_ekran);
        listeleBut=findViewById(R.id.listeleButon);
        klimaSwitch = findViewById(R.id.klimaSwitch);
        isikSwitch = findViewById(R.id.lambaSwitch);
        televizyonSwitch = findViewById(R.id.televizyonSwitch);
        Intent intent2 = getIntent();
        token = intent2.getStringExtra("token");
        getir = new Getir();
        getir.start();

    }
    public void listele(View view){
        Intent intent=new Intent(this,Liste.class);
        intent.putExtra("token",token);
        startActivity(intent);

    }


    class Getir extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            String klimaState = "";
            String isikState = "";
            String televizyonState = "";
            String stateUrl = "http://10.0.2.2:8080/ev/get/state";
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
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "MyApp/1.0");
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                responseCode = connection.getResponseCode();
            } catch (Exception e) {
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
                    JSONArray array = new JSONArray(response.toString());
                    JSONObject object = array.getJSONObject(0);
                    televizyonState = String.valueOf(object.get("televizyon"));
                    klimaState = String.valueOf(object.get("klima"));
                    isikState = String.valueOf(object.get("isiklar"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (isikState.equals("1")) {
                    isikSwitch.setChecked(true);
                } else if (isikState.equals("0")) {
                    isikSwitch.setChecked(false);
                }
                if (klimaState.equals("1")) {
                    klimaSwitch.setChecked(true);
                } else if (klimaState.equals("0")) {
                    klimaSwitch.setChecked(false);
                }
                if (televizyonState.equals("1")) {
                    televizyonSwitch.setChecked(true);
                } else if (televizyonState.equals("0")) {
                    televizyonSwitch.setChecked(false);
                }
            }
        }
    }
}
