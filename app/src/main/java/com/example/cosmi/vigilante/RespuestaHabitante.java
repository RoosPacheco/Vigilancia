package com.example.cosmi.vigilante;

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

            String res  = extras.getString("respuesta");
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
            respuesta.setText(text);

        }

    }


}
