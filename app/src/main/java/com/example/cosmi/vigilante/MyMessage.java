package com.example.cosmi.vigilante;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
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

            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            notificationChannel.setSound(soundUri,att );


            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Bitmap bmp = textAsBitmap(data.get("body"), 180, Color.WHITE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSound(alarmSound);
        builder.setContentTitle(data.get("title"));
        builder.setContentText(data.get("body"));
        builder.setSmallIcon(R.drawable.ic_warning_black_24dp);


        builder.setAutoCancel(true);


        ///builder.setLargeIcon(bmp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("homeApps");
        }


        Intent intent = new Intent(getApplicationContext(),  MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),1,intent,PendingIntent.FLAG_UPDATE_CURRENT);;

        Intent notificationService = new Intent(getApplicationContext(),  serviceNotification.class);
        Intent notificationService2 = new Intent(getApplicationContext(),  serviceNotification2.class);

        if(data.get("title").equals("Visita")){

            intent= new Intent(getApplicationContext(),  AvisoHabitante.class);

            intent.putExtra("nombreVisita", data.get("nombre"));
            intent.putExtra("appellVisita", data.get("apellido"));
            intent.putExtra("tokenVig", data.get("tokenVig"));
            intent.putExtra("imagen", data.get("nameImage"));
            intent.putExtra("idVisita", data.get("idVisita"));

            intent.putExtra("numNotifi", numero);
            intent.putExtra("nameHabit", data.get("nameHabit"));

            Bitmap imageVisit = getBitmapFromURL(data.get("nameImage"));
            builder.setLargeIcon(imageVisit);
            builder.setStyle(new Notification.BigPictureStyle()
                    .bigPicture(imageVisit));



            notificationService.putExtra("tokenVig", data.get("tokenVig"));
            notificationService.putExtra("nameHabit", data.get("nameHabit"));
            notificationService.putExtra("idVisita", data.get("idVisita"));
            notificationService.putExtra("numNotifi", numero);

            notificationService2.putExtra("tokenVig", data.get("tokenVig"));
            notificationService2.putExtra("nameHabit", data.get("nameHabit"));
            notificationService2.putExtra("idVisita", data.get("idVisita"));
            notificationService2.putExtra("numNotifi", numero);

            pendingIntent = PendingIntent.getActivity(getApplicationContext(),1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntentN2 = PendingIntent.getService(getApplicationContext(), 0, notificationService, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntentN3 = PendingIntent.getService(getApplicationContext(), 0, notificationService2,PendingIntent.FLAG_UPDATE_CURRENT);

            builder.addAction(R.drawable.ic_like, "ACEPTADO", pendingIntentN2);
            builder.addAction(R.drawable.ic_like, "DENEGADO", pendingIntentN3);
        }
        else if(data.get("title").equals("Respuesta")){
            intent= new Intent(getApplicationContext(),  RespuestaHabitante.class);

            intent.putExtra("respuesta", data.get("body"));
            intent.putExtra("nameHabit", data.get("nameHabit"));
            intent.putExtra("numNotifi", numero);
            pendingIntent = PendingIntent.getActivity(getApplicationContext(),numero,intent,PendingIntent.FLAG_UPDATE_CURRENT);

            builder.addAction(new Notification.Action(R.drawable.ic_warning_black_24dp,"Abrir",pendingIntent));
        }
        builder.setContentIntent(pendingIntent);
        notificationManager.notify(numero,  builder.build());
    }



    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }







}
