package com.example.cosmi.vigilante;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RespuestaHabitante extends AppCompatActivity {

    private TextView textView ;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respuesta_habitante);

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras != null) {
            NotificationManager nm;
            String ns = Context.NOTIFICATION_SERVICE;
            nm = (NotificationManager) getSystemService(ns);
            int numNotifi = extras.getInt("numNotifi");
            nm.cancel(numNotifi);


            String res  = extras.getString("respuesta");
            String nameHabit = extras.getString("nameHabit");
            String text = "";
            switch (res){
                case "aceptado":
                    text = "Lo conozco";
                    break;
                case "rechazado":
                    text = "No lo conozco";
                    break;
                case "indispuesto":
                    text = "No me encuentro en casa";
                    break;
            }

            TextView respuesta = (TextView)findViewById(R.id.respuesta);
            TextView habitante = (TextView)findViewById(R.id.nameHabit);
            respuesta.setText(text);
            habitante.setText("Respuesta de: "+nameHabit);

        }

        // On close icon click finish activity
        findViewById(R.id.close_activity).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        finish();

                    }
                });
        findViewById(R.id.ok).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        finish();

                    }
                });

    }


}
