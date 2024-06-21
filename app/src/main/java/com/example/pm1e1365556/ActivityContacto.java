package com.example.pm1e1365556;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

import Configuracion.Contacto;
import Configuracion.SQLiteConexion;

public class ActivityContacto extends AppCompatActivity {
    private ImageView imagenContacto;
    private EditText inputNombre, inputTelefono, inputNota;
    private Spinner spinnerPais;
    private Button btnSalvar, btnVerContactos, btnSeleccionarImagen;
    private Uri imagenUri;

    private SQLiteConexion dbHelper;

    private static final int REQUEST_CAMERA_CAPTURE = 1;
    private static final int REQUEST_GALLERY_PICK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        imagenContacto = findViewById(R.id.imagenContacto);
        inputNombre = findViewById(R.id.inputNombre);
        inputTelefono = findViewById(R.id.inputTelefono);
        inputNota = findViewById(R.id.inputNota);
        spinnerPais = findViewById(R.id.spinnerPais);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnVerContactos = findViewById(R.id.btnVerContactos);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);

        dbHelper = new SQLiteConexion(this);

        // Llenar el Spinner con las opciones de los países
        List<String> paisesList = Arrays.asList("Honduras (504)", "Guatemala (502)", "El Salvador (503)", "Nicaragua (505)", "Costa Rica (506)", "Panamá (507)");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paisesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPais.setAdapter(adapter);

        // Configurar evento para seleccionar imagen desde la galería o la cámara
        btnSeleccionarImagen.setOnClickListener(v -> seleccionarImagen());

        btnSalvar.setOnClickListener(v -> salvarContacto());

        btnVerContactos.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(ActivityContacto.this, ActivityListado.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ActivityContacto.this, "Error al abrir la lista de contactos", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void seleccionarImagen() {
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, getPickImageIntent());
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Seleccionar fuente de imagen");

        startActivityForResult(chooserIntent, REQUEST_GALLERY_PICK);
    }

    // Método para crear un intent para seleccionar imágenes desde la galería o la cámara
    private Intent getPickImageIntent() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Asegurarse de que haya una aplicación que pueda manejar la acción de captura de imágenes
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent[] intents = {cameraIntent};
        pickIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        return pickIntent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY_PICK) {
                if (data == null || data.getData() == null) {
                    // Si no se seleccionó ninguna imagen
                    Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show();
                    return;
                }
                imagenUri = data.getData();
                imagenContacto.setImageURI(imagenUri);
            } else if (requestCode == REQUEST_CAMERA_CAPTURE) {
                if (data != null && data.getData() != null) {
                    // Si se capturó una nueva imagen desde la cámara
                    imagenUri = data.getData();
                    imagenContacto.setImageURI(imagenUri);
                } else {
                    // Si no se capturó una nueva imagen (por ejemplo, cancelación)
                    Toast.makeText(this, "Captura de imagen cancelada", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void salvarContacto() {
        String pais = spinnerPais.getSelectedItem().toString();
        String nombre = inputNombre.getText().toString().trim();
        String telefono = inputTelefono.getText().toString().trim();
        String nota = inputNota.getText().toString().trim();
        String imagen = imagenUri != null ? imagenUri.toString() : "";

        if (nombre.isEmpty() || telefono.isEmpty() || pais.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar teléfono con expresión regular para 8 dígitos
        if (!telefono.matches("\\d{8}")) {
            Toast.makeText(this, "Número de teléfono no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Contacto contacto = new Contacto(0, pais, nombre, telefono, nota, imagen);
        // Guardar contacto en la base de datos
        dbHelper.insertarContacto(contacto);

        Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show();
        limpiarCampos();
    }

    private void limpiarCampos() {
        inputNombre.setText("");
        inputTelefono.setText("");
        inputNota.setText("");
        imagenContacto.setImageResource(R.drawable.default_image);
        imagenUri = null;
    }
}
