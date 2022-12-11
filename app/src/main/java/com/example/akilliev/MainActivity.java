package com.example.akilliev;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText nameText;
    EditText passText;
    Button girisBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameText = findViewById(R.id.nameText);
        passText = findViewById(R.id.passText);
        girisBut = findViewById(R.id.girisBut);
    }

    public void veriAl(View view) throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            // İnternet bağlı
            veri(nameText.getText().toString(), passText.getText().toString());
        } else {
            // İnternet bağlı değil
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("İnternet Bağlantısı");
            builder.setMessage("İnternet bağlantısı kapalı lütfen açtıktan sonra tekrar deneyiniz !!");
            builder.setPositiveButton("Tamam", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void veri(String kullaniciAdi, String sifre) {


        new Thread(() -> {
            Looper.prepare();

            String girisUrl = "http://10.0.2.2:8080/api/auth/login";
            URL url = null;
            try {
                url = new URL(girisUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                connection.setRequestMethod("POST");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            JSONObject json = new JSONObject();
            try {
                json.put("kullaniciAdi", nameText.getText().toString());
                json.put("sifre", passText.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String jsonData=json.toString();
            OutputStreamWriter outputStreamWriter = null;
            try {
                outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
                outputStreamWriter.write(jsonData);
                outputStreamWriter.flush();
                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int responseCode = 0;
            try {
                responseCode = connection.getResponseCode();
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
                    JSONObject jsonObject=new JSONObject(response.toString());
                    String key= String.valueOf(jsonObject.get("accesToken"));
                    Intent intent = new Intent(this, AnaEkran.class);
                    intent.putExtra("token", key);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Hata");
                builder.setMessage("Bir hata oluştu!");
                builder.setPositiveButton("Tamam", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            Handler handler = new Handler();
            Looper.loop();

        }).start();


    }

}