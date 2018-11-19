package com.example.cosmi.vigilante;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class AvisoHabitante extends AppCompatActivity {

    protected String direction = "http://proyectomovilesvigilancia.hostingerapp.com/app/";

    String tokenVig;
    String idVisita;
    String nameHabit;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aviso_habitante);

        Intent i = getIntent();
        Bundle extras = i.getExtras();

        if (extras != null) {
            NotificationManager nm;
            String ns = Context.NOTIFICATION_SERVICE;
            nm = (NotificationManager) getSystemService(ns);
            int numNotifi = extras.getInt("numNotifi");
            nm.cancel(numNotifi);

            String nombre  = extras.getString("nombreVisita");
            String apellido  = extras.getString("appellVisita");
            tokenVig  = extras.getString("tokenVig");
            String urlImage  = extras.getString("imagen");
            idVisita  = extras.getString("idVisita");
            nameHabit = extras.getString("nameHabit");

            TextView nombreT = (TextView)findViewById(R.id.fullNameVisit);
            TextView apelliT = (TextView)findViewById(R.id.lastNameVisit);

            nombreT.setText(nombre);
            apelliT.setText(apellido);

            Log.d("tokenVig", tokenVig);
            Log.d("URL", urlImage);
            new DownloadImageTask().execute(urlImage);

        }

        Button aceptar      = (Button) findViewById(R.id.aceptado);
        Button rechazar     = (Button) findViewById(R.id.rechazado);
        Button indispuesto  = (Button) findViewById(R.id.indispuesto);

        //String token = "e_wg0kAzWHY:APA91bHwoNHtc0GKvs402m9CIT8dhAz8O2CegJ1m3XwQVwjRR2f_sa-p6u3KAIWvmgFIAFhawq5hjSAuND-IbpoZxdNEY46IImien6TaJOjqeL5hXWwPHkvE5rmJ1tDj_EDckoaBdKBy";

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new notificar().execute(tokenVig,"aceptado", nameHabit);
                new actualizarVisita().execute("aceptado", idVisita);
                view.setEnabled(false);
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
                if (progressBar.getVisibility() == View.GONE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        rechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new notificar().execute(tokenVig, "rechazado", nameHabit);
                new actualizarVisita().execute("rechazado", idVisita);
                view.setEnabled(false);
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
                if (progressBar.getVisibility() == View.GONE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        indispuesto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new notificar().execute(tokenVig, "indispuesto", nameHabit);
                new actualizarVisita().execute("indispuesto", idVisita);
                view.setEnabled(false);
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
                if (progressBar.getVisibility() == View.GONE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        builder  = new AlertDialog.Builder(this);


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
                alert();
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
                alert();
            }
        }


    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            ImageView bmImage = findViewById(R.id.imageVisit);
            bmImage.setImageBitmap(result);
        }
    }


    private void alert(){
        builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);
        //Setting message manually and performing action on button click
        builder.setMessage("La respuesta fué enviadá, Gracias")
                .setCancelable(false)
                .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Mensaje");
        alert.show();
    }
}


