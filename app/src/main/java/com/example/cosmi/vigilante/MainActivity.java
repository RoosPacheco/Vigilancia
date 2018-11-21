package com.example.cosmi.vigilante;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    protected String direction = "http://proyectomovilesvigilancia.hostingerapp.com/app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras == null) {
            new queryToken().execute();
        }


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("launchtoken", newToken);

            }
        });


        // On close icon click finish activity
        findViewById(R.id.close_activity).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        finish();

                    }
                });


    }

    class queryToken extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... a) {// método que no tiene acceso a la parte visual
            HttpURLConnection connection;


            Log.d("FCMToken", "token "+ FirebaseInstanceId.getInstance().getToken());
            String FCMToken = FirebaseInstanceId.getInstance().getToken();

            String direccion = direction+"queryToken.php";

            String res = null;
            try {

                connection = (HttpURLConnection) new URL(direccion).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                writer.write("token=" + FCMToken );
                writer.flush();
                writer.close();
                outputStream.close();

                connection.connect();
                InputStream is = (InputStream) connection.getContent();

                byte [] b = new byte[100000];//buffer
                Integer numBytes = is.read(b);// numero de bites que leyó
                //convertimos ese num de bites a una cadena
                res = new String(b, 0,  numBytes, "utf-8");
                Log.d("queryToken", res);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String resul) {
            //super.onPostExecute(res);

            switch (resul){
                case "HABITANTE":
                    Log.d("HABITANTE", "habitante");
                    break;
                case "VIGILANTE":
                    Intent intent = new Intent(getApplicationContext(), Visitante.class);
                    startActivity(intent);
                    break;
                case "NOREGISTERED":
                    Intent intent2 = new Intent(getApplicationContext(), ActivityloginRegister.class);
                    startActivity(intent2);
                    break;
            }


        }

    }

}
