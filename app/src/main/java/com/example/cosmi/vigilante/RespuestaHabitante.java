package com.example.cosmi.vigilante;

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

    }

    // Mostrar respuesta
    protected void mostrarRespuesta() {
        progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        textView = (TextView)findViewById(R.id.respuesta);
        if (textView.getVisibility() == View.GONE) {
            textView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
