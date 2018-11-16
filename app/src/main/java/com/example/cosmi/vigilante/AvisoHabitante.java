package com.example.cosmi.vigilante;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aviso_habitante);

        Intent i = getIntent();
        Bundle extras = i.getExtras();

        if (extras != null) {

            String nombre  = extras.getString("nombreVisita");
            String apellido  = extras.getString("appellVisita");
            tokenVig  = extras.getString("tokenVig");
            String urlImage  = extras.getString("imagen");

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

                new notificar().execute(tokenVig,"aceptado");
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
                new notificar().execute(tokenVig,"rechazado");
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
                new notificar().execute(tokenVig, "indispuesto");
                view.setEnabled(false);
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
                if (progressBar.getVisibility() == View.GONE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    class notificar extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... resultado) {// método que no tiene acceso a la parte visual
            HttpURLConnection connection;
            String TOKEN        = resultado[0];
            String respuesta    = resultado[1];

            String direccion = direction+"notificarVigilante.php";
            try {

                connection = (HttpURLConnection) new URL(direccion).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                Log.d("token",TOKEN);
                writer.write("token="+TOKEN +"&"+"respuesta=" + respuesta);
                writer.flush();
                writer.close();
                outputStream.close();

                connection.connect();
                InputStream is = (InputStream) connection.getContent();

                byte [] b = new byte[100000];//buffer
                Integer numBytes = is.read(b);// numero de bites que leyó
                //convertimos ese num de bites a una cadena
                String res = new String(b, 0,  numBytes, "utf-8");
                Log.d("respuestaNotifi", res);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
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

}


