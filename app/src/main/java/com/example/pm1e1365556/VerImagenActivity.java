package com.example.pm1e1365556;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class VerImagenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_imagen);

        // Obtener la URI de la imagen del intent
        String imagenUri = getIntent().getStringExtra("imagenUri");

        // Configurar la imagen en el ImageView
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageURI(Uri.parse(imagenUri));
    }
}

