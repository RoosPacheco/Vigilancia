package com.example.cosmi.vigilante;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;

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
import java.util.ArrayList;

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

                /**** Foto en alta resoluion****/

                /*

                File dir=getExternalFilesDir(Environment.DIRECTORY_DCIM);
                file = new File(dir,"pic.jpg");

                Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri = FileProvider.getUriForFile(getApplicationContext(),"com.example.cosmi.vigilante",file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                //Log.d("1",file.getAbsolutePath());
                if (ContextCompat.checkSelfPermission(Visitante.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),"FALTAN LOS PERMISOS", Toast.LENGTH_LONG).show();
                }
                else
                {
                    startActivityForResult(intent, 123);
                }

                */

                /***FOto baja resolucion*/
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 123);

            }
        });

        Button avisar = (Button) findViewById(R.id.avisar);
        avisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(btm != null) {
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

                    if(!"".equals(nameVisit)  && !"".equals(lastnameVisit) && !"".equals(nameHabit) && !"".equals(lastnameHabit) && !"".equals(mobiHabit) && !"".equals(calleHabi) && !"".equals(numHabi)) {

                        new DowloandInfo().execute(encode, nameVisit, lastnameVisit, nameHabit, lastnameHabit, mobiHabit, calleHabi, numHabi);
                        view.setEnabled(false);
                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
                        if (progressBar.getVisibility() == View.GONE) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Complete los campos", Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(),"Tome la foto", Toast.LENGTH_LONG).show();
                }

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

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            //Obtener el Bitmap de a captura alta resolucion
            //btm = BitmapFactory.decodeFile(file.getAbsolutePath());


            //Obtenemos el Bitmap de la imagen de baja resolución
            Bundle bundle = data.getExtras();
            btm = (Bitmap) bundle.get("data");


            ImageView imageView = findViewById(R.id.imageVisit);
            imageView.setImageBitmap(btm);


        }

    }

    public class Wrapper
    {
        String res;
        String nameVisit;
        String lastnameVisit;
        String urlImage;
        String IDvisita;
    }

    class DowloandInfo extends AsyncTask<String, Void, Wrapper> {

        String nameVisit     ;
        String lastnameVisit ;

        @Override
        protected Wrapper doInBackground(String... parameter) {// método que no tiene acceso a la parte visual
            HttpURLConnection connection;

            String imagen           = parameter[0];

            nameVisit               = parameter[1];
            lastnameVisit           = parameter[2];
            String nameHabit        = parameter[3];
            String lastnameHabit    = parameter[4];
            String mobiHabit        = parameter[5];
            String calleHabi        = parameter[6];
            String numHabit         = parameter[7];

            String direccion = direction+"Visitantes/getInfoVisitante.php";

            String res = null;
            Wrapper w = new Wrapper();

            Log.d("IMAGEN", imagen);

            try {

                Log.d("imagenstring","image="+URLEncoder.encode(imagen, "UTF-8"));

                connection = (HttpURLConnection) new URL(direccion).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);

                /*********/
                String params = "nameVisit=" + nameVisit + "&" + "lastnameVisit=" + lastnameVisit + "&" + "nameHabit=" + nameHabit + "&" + "lastnameHabit=" + lastnameHabit + "&" + "mobiHabit=" + mobiHabit + "&" + "calleHabi=" + calleHabi + "&" + "numHabit=" + numHabit + "&" + "image=" + URLEncoder.encode(imagen, "UTF-8");
                //String params = "image=" + URLEncoder.encode(imagen, "UTF-8")+ "&" +"nameVisit=" + nameVisit + "&" + "lastnameVisit=" + lastnameVisit + "&" + "nameHabit=" + nameHabit + "&" + "lastnameHabit=" + lastnameHabit + "&" + "mobiHabit=" + mobiHabit + "&" + "calleHabi=" + calleHabi + "&" + "numHabit=" + numHabit;
                Log.d("paramts", params);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                writer.write("nameVisit=" + nameVisit + "&" + "lastnameVisit=" + lastnameVisit + "&" + "nameHabit=" + nameHabit + "&" + "lastnameHabit=" + lastnameHabit + "&" + "mobiHabit=" + mobiHabit + "&" + "calleHabi=" + calleHabi + "&" + "numHabit=" + numHabit + "&" + "image=" + URLEncoder.encode(imagen, "UTF-8"));
                //writer.write("image=" + URLEncoder.encode(imagen, "UTF-8"));
                writer.flush();
                writer.close();
                outputStream.close();

                connection.connect();


                InputStream is = (InputStream) connection.getContent();//error java.net.SocketException: sendto failed: EPIPE (Broken pipe)
                byte [] b = new byte[100000];//buffer
                Integer numBytes = is.read(b);// numero de bites que lleyó
                //convertimos ese num de bites a una cadena
                res = new String(b, 0,  numBytes, "utf-8");

                Log.d("RES", res);

                JSONArray arr = new JSONArray(res);
                String token = "";
                String idVisita = "";
                String url = direction+"Visitantes/";

                token = arr.getString(0);
                url = url +arr.getString(1);
                idVisita = arr.getString(2);

                //token = "NO EXISTE";
               // token = "fopMfZ4rK6s:APA91bG56WSonYtFvLVqftuhVeAiM1JrJlaDF5DqJ9d_BkCxrTvu348aPAFDi4w07_4ArnVFyuWvLr_JpjU0vAzlGjMhuFmJdnsNF67VkaJuYCjPzt_wIMeDHgad2uin0QleX6SmWVn3";
                //ross
                //token = "e_wg0kAzWHY:APA91bHwoNHtc0GKvs402m9CIT8dhAz8O2CegJ1m3XwQVwjRR2f_sa-p6u3KAIWvmgFIAFhawq5hjSAuND-IbpoZxdNEY46IImien6TaJOjqeL5hXWwPHkvE5rmJ1tDj_EDckoaBdKBy";
                Log.d("res", token);

                w.res = token;
                w.nameVisit = nameVisit;
                w.lastnameVisit = lastnameVisit;
                w.urlImage = url;
                w.IDvisita = idVisita;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return w;
        }

        @Override
        protected void onPostExecute(Wrapper list) {
            //super.onPostExecute(res);



            String res = list.res;
            if ("NO EXISTE".equals(res)){
               // new RespuestaHabitante().mostrarRespuesta();

                Intent intent = new Intent(getApplicationContext(), RespuestaHabitante.class);
                startActivity(intent);

            }
            else if (!"NO EXISTE".equals(res)){
                Log.e("onPostExecute", res);
                //mandamos la notificación
                new notificar().execute(res, list.nameVisit, list.lastnameVisit, list.urlImage, list.IDvisita);

            }


        }
    }

    class notificar extends AsyncTask <String, Void, Void>{
        @Override
        protected Void doInBackground(String... resultado) {// método que no tiene acceso a la parte visual
            HttpURLConnection connection;
            String TOKEN             = resultado[0];
            String nombreV           = resultado[1];
            String appellV           = resultado[2];
            String nameImagen        = resultado[3];
            String idVisita          = resultado[4];

            Log.d("FCMToken", "token "+ FirebaseInstanceId.getInstance().getToken());
            String FCMToken = FirebaseInstanceId.getInstance().getToken();

            String direccion = direction+"notificar.php";
            try {

                connection = (HttpURLConnection) new URL(direccion).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                writer.write("token=" + TOKEN +"&"+"nameVisit="+nombreV+"&"+"lastNamVi="+appellV+"&"+"tokenVig="+FCMToken+"&"+"nameImagen="+nameImagen+"&"+"idVisita="+idVisita);
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
