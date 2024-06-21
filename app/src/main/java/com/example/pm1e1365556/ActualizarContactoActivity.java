package com.example.pm1e1365556;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ActualizarContactoActivity extends AppCompatActivity {

    private EditText editNombre, editTelefono, editNota;
    private Button btnActualizar;
    private ImageButton btnAtras;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_contacto);

        btnAtras = findViewById(R.id.btnAtras);
        editNombre = findViewById(R.id.editNombre);
        editTelefono = findViewById(R.id.editTelefono);
        editNota = findViewById(R.id.editNota);
        btnActualizar = findViewById(R.id.btnActualizar);

        // Aquí debes obtener el contactoId del intent y cargar los datos del contacto para actualizar
        int contactoId = getIntent().getIntExtra("contactoId", -1);

        // Implementar la lógica para cargar y actualizar los datos del contacto según su ID
        // Por simplicidad, aquí solo se muestra la interfaz de usuario

        btnAtras.setOnClickListener(v -> regresarInicio());
    }

    private void regresarInicio()
    {
        Intent intent = new Intent(this, ActivityListado.class);
        startActivity(intent);
        finish();
    }
}
