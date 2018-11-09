package com.example.cosmi.vigilante;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Visitante extends AppCompatActivity {

    protected String direction = "http://proyectomovilesvigilancia.hostingerapp.com/app/";
    File file;
    private Bitmap btm;

    private Boolean ban = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitante);

        ImageView picture = (ImageView) findViewById(R.id.imageVisit);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //crear un archivo
                try {
                    File dir = getExternalFilesDir(Environment.DIRECTORY_DCIM);
                    Log.d("1",dir.getAbsolutePath());
                    file = File.createTempFile("image", ".jpg", dir);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    Log.d("dire_picture",dir.getAbsolutePath());
                    startActivityForResult(intent, 123);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button avisar = (Button) findViewById(R.id.avisar);
        avisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Proceso para convertir el Bitmap a una cadena string
                ByteArrayOutputStream outs = new ByteArrayOutputStream();
                btm.compress(Bitmap.CompressFormat.JPEG, 100, outs);
                String encode = Base64.encodeToString(outs.toByteArray(), Base64.DEFAULT);

                EditText nameV = (EditText) findViewById(R.id.fullNameVisit);
                EditText lastnameV = (EditText) findViewById(R.id.lastNameVisit);
                EditText nameH = (EditText) findViewById(R.id.fullNameHabit);
                EditText lastnameH = (EditText) findViewById(R.id.lastNameHabit);
                EditText moviH = (EditText) findViewById(R.id.mobileNumber);
                EditText calle = (EditText) findViewById(R.id.calle);
                EditText num   = (EditText) findViewById(R.id.numero);

                String nameVisit = (String.valueOf(nameV.getText())).toUpperCase();
                String lastnameVisit = (String.valueOf(lastnameV.getText())).toUpperCase();
                String nameHabit = (String.valueOf(nameH.getText())).toUpperCase();
                String lastnameHabit = (String.valueOf(lastnameH.getText())).toUpperCase();
                String mobiHabit = String.valueOf(moviH.getText());
                String calleHabi = (String.valueOf(calle.getText())).toUpperCase();
                String numHabi   = String.valueOf(num.getText());


                new DowloandInfo().execute(encode,nameVisit,lastnameVisit, nameHabit,lastnameHabit, mobiHabit, calleHabi, numHabi );



            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            btm = BitmapFactory.decodeFile(file.getAbsolutePath());

            ImageView imageView = findViewById(R.id.imageVisit);
            imageView.setImageBitmap(btm);

        }

    }

    class DowloandInfo extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... parameter) {// método que no tiene acceso a la parte visual
            HttpURLConnection connection;

            String imagen           = parameter[0];

            String nameVisit        = parameter[1];
            String lastnameVisit    = parameter[2];
            String nameHabit        = parameter[3];
            String lastnameHabit    = parameter[4];
            String mobiHabit        = parameter[5];
            String calleHabi        = parameter[6];
            String numHabit         = parameter[7];

            String direccion = direction+"getInfoVisitante.php";

            Log.d("paramenters", nameVisit+"-"+nameHabit+"-"+mobiHabit+"-"+calleHabi+"-"+numHabit);

            String res = null;

            try {
                Log.d("umagen string", URLEncoder.encode("image=" + imagen, "UTF-8"));



                connection = (HttpURLConnection) new URL(direccion).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);

                /*********/

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("image",  URLEncoder.encode(imagen, "UTF-8"))
                        .appendQueryParameter("nameVisit", nameVisit)
                        .appendQueryParameter("lastnameVisit", lastnameVisit)
                        .appendQueryParameter("nameHabit", nameHabit)
                        .appendQueryParameter("lastnameHabit", lastnameHabit)
                        .appendQueryParameter("mobiHabit", mobiHabit)
                        .appendQueryParameter("calleHabi", calleHabi)
                        .appendQueryParameter("numHabit", numHabit);
                String query = builder.build().getEncodedQuery();



                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                writer.write(query);
                writer.flush();
                writer.close();
                outputStream.close();

                connection.connect();
                InputStream is = (InputStream) connection.getContent();
                byte [] b = new byte[100000];//buffer
                Integer numBytes = is.read(b);// numero de bites que lleyó
                //convertimos ese num de bites a una cadena
                res = new String(b, 0,  numBytes, "utf-8");


                Log.d("res", res);

                if(res.equals("Connectedsuccessfully")){
                    ban = true;
                }





            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            if (!"NO EXISTE".equals(res)){
                //mandamos la notificación
                new notificar().execute(res);

            }

        }
    }

    class notificar extends AsyncTask <String, Void, Void>{
        @Override
        protected Void doInBackground(String... token) {// método que no tiene acceso a la parte visual
            HttpURLConnection connection;
            String TOKEN           = token[0];

            String direccion = direction+"notificar.php";
            try {

                connection = (HttpURLConnection) new URL(direccion).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

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

}
