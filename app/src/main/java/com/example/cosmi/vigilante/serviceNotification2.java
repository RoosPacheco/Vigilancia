package com.example.cosmi.vigilante;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class serviceNotification2 extends Service {

    protected String direction = "http://proyectomovilesvigilancia.hostingerapp.com/app/";
    String tokenVig;
    String idVisita;
    String nameHabit;
    public serviceNotification2() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("servicio", "servicio1");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d("onStartCommand", "servicio1");

        Bundle extras = intent.getExtras();
        if (extras != null) {
            NotificationManager nm;
            String ns = Context.NOTIFICATION_SERVICE;
            nm = (NotificationManager) getSystemService(ns);
            int numNotifi = extras.getInt("numNotifi");
            nm.cancel(numNotifi);

            tokenVig  = extras.getString("tokenVig");
            idVisita  = extras.getString("idVisita");
            nameHabit = extras.getString("nameHabit");


            Log.d("tokenVig", tokenVig);
            new notificar().execute(tokenVig, "rechazado", nameHabit);
            new actualizarVisita().execute("rechazado", idVisita);

        }

        return super.onStartCommand(intent, flags, startId);

    }

    class notificar extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... resultado) {// método que no tiene acceso a la parte visual
            HttpURLConnection connection;
            String TOKEN        = resultado[0];
            String respuesta    = resultado[1];
            String nameHabit    = resultado[2];

            String res = null;

            String direccion = direction+"notificarVigilante.php";
            try {

                connection = (HttpURLConnection) new URL(direccion).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                Log.d("token",TOKEN);
                writer.write("token="+TOKEN +"&"+"respuesta=" + respuesta+ "&"+"nameHabit=" + nameHabit);
                writer.flush();
                writer.close();
                outputStream.close();

                connection.connect();
                InputStream is = (InputStream) connection.getContent();

                byte [] b = new byte[100000];//buffer
                Integer numBytes = is.read(b);// numero de bites que leyó
                //convertimos ese num de bites a una cadena
                res = new String(b, 0,  numBytes, "utf-8");
                Log.d("respuestaNotifi", res);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }
        protected void onPostExecute(String result) {
            if (result != null){

            }
        }


    }

    class actualizarVisita extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... resultado) {// método que no tiene acceso a la parte visual
            HttpURLConnection connection;
            String respuesta    = resultado[0];
            String idVisita     = resultado[1];

            String res = null;

            String direccion = direction+"Visitantes/updateVisita.php";
            try {

                connection = (HttpURLConnection) new URL(direccion).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                Log.d("idVisita",idVisita);
                writer.write("respuesta="+respuesta +"&"+"idVisita=" + idVisita);
                writer.flush();
                writer.close();
                outputStream.close();

                connection.connect();
                InputStream is = (InputStream) connection.getContent();

                byte [] b = new byte[100000];//buffer
                Integer numBytes = is.read(b);// numero de bites que leyó
                //convertimos ese num de bites a una cadena
                res = new String(b, 0,  numBytes, "utf-8");
                Log.d("actualizarVisita", res);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }
        protected void onPostExecute(String result) {
            if (result != null){
                Log.d("onPostExecute", result);
            }
        }


    }
}
