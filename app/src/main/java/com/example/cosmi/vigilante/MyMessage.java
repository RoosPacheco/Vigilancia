package com.example.cosmi.vigilante;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MyMessage extends FirebaseMessagingService {

    //String direction = "http://192.168.43.193/";
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("tokenPrimera", "Primera vez Refreshed token: " + s);

    }


    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();

        Log.d("mensaje", "Message recibido");
        Log.d("title", data.get("title"));
        Log.d("body", data.get("body"));

        int numero = (int) (Math.random() * 100) + 1;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel("homeApps", "Green", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Bitmap bmp = textAsBitmap(data.get("body"), 180, Color.WHITE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentTitle(data.get("title"));
        builder.setContentText(data.get("body"));
        builder.setSmallIcon(R.drawable.ic_warning_black_24dp);
        ///builder.setLargeIcon(bmp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("homeApps");
        }


        Intent intent = new Intent(getApplicationContext(),  MainActivity.class);
        if(data.get("title").equals("Visita")){
            intent= new Intent(getApplicationContext(),  AvisoHabitante.class);
            intent.putExtra("nombreVisita", data.get("nombre"));
            intent.putExtra("appellVisita", data.get("apellido"));
            intent.putExtra("tokenVig", data.get("tokenVig"));
            intent.putExtra("imagen", data.get("nameImage"));
            intent.putExtra("idVisita", data.get("idVisita"));

        }
        else if(data.get("title").equals("Respuesta")){
            intent= new Intent(getApplicationContext(),  RespuestaHabitante.class);
            intent.putExtra("respuesta", data.get("body"));
        }

        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(new Notification.Action(R.drawable.ic_warning_black_24dp,"Abrir",pendingIntent));

        notificationManager.notify(numero,  builder.build());
    }










}
