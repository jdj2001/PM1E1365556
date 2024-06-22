package com.example.pm1e1365556;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Mapa para almacenar la longitud del número de teléfono por país
    private Map<String, Integer> longitudTelefonoPorPais;

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

        // Inicializar el mapa con la longitud del número de teléfono por país
        longitudTelefonoPorPais = new HashMap<>();
        longitudTelefonoPorPais.put("Honduras (504)", 8);
        longitudTelefonoPorPais.put("Guatemala (502)", 8);
        longitudTelefonoPorPais.put("El Salvador (503)", 8);
        longitudTelefonoPorPais.put("Nicaragua (505)", 8);
        longitudTelefonoPorPais.put("Costa Rica (506)", 8);
        longitudTelefonoPorPais.put("Panamá (507)", 8);

        // Configurar limitaciones de longitud para los EditText
        inputNombre.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        inputNota.setFilters(new InputFilter[]{new InputFilter.LengthFilter(60)});

        // Configurar listener para el Spinner de países
        spinnerPais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String paisSeleccionado = parent.getItemAtPosition(position).toString();
                int longitudMaxima = longitudTelefonoPorPais.get(paisSeleccionado);
                inputTelefono.setFilters(new InputFilter[]{new InputFilter.LengthFilter(longitudMaxima)});
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Por defecto, establecer una longitud máxima
                inputTelefono.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
            }
        });

        // Configurar evento para seleccionar imagen
        btnSeleccionarImagen.setOnClickListener(v -> seleccionarImagen());

        // Configurar evento para salvar contacto
        btnSalvar.setOnClickListener(v -> salvarContacto());

        // Configurar evento para ver la lista de contactos
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar recursos relacionados con la imagen
        if (imagenUri != null) {
            // Eliminar la imagen de la galería si se capturó una nueva
            if (imagenUri.getScheme().equals("content")) {
                getContentResolver().delete(imagenUri, null, null);
            }
            imagenUri = null;
        }
    }

    private void seleccionarImagen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_CAPTURE);
            } else {
                showImagePickerDialog();
            }
        } else {
            showImagePickerDialog();
        }
    }
    /*private void seleccionarImagen() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_CAPTURE);
                } else {
                    showImagePickerDialog();
                }
            } else {
                showImagePickerDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al seleccionar imagen", Toast.LENGTH_SHORT).show();
        }
    }*/

    /*private void salvarContacto() {
        try {
            String pais = spinnerPais.getSelectedItem().toString();
            String nombre = inputNombre.getText().toString().trim();
            String telefono = inputTelefono.getText().toString().trim();
            String nota = inputNota.getText().toString().trim();
            String imagen = imagenUri != null ? imagenUri.toString() : "";

            if (nombre.isEmpty() || telefono.isEmpty() || pais.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            int longitudMaxima = longitudTelefonoPorPais.get(pais);
            if (telefono.length() != longitudMaxima) {
                Toast.makeText(this, "Número de teléfono no válido para " + pais, Toast.LENGTH_SHORT).show();
                return;
            }

            Contacto contacto = new Contacto(0, pais, nombre, telefono, nota, imagen);
            // Guardar contacto en la base de datos
            dbHelper.insertarContacto(contacto);

            Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show();
            limpiarCampos();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar contacto", Toast.LENGTH_SHORT).show();
        }
    }*/

    private void showImagePickerDialog() {
        String[] options = {"Seleccionar desde Galería", "Tomar Foto"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Imagen");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Seleccionar desde Galería
                pickImageFromGallery();
            } else {
                // Tomar Foto
                dispatchTakePictureIntent();
            }
        });
        builder.show();
    }

    private void pickImageFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PICK);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imagenUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagenUri);
            startActivityForResult(takePictureIntent, REQUEST_CAMERA_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_CAPTURE) {
                if (imagenUri != null) {
                    imagenContacto.setImageURI(imagenUri);
                    // La imagen capturada se almacena en la galería
                    galleryAddPic();
                }
            } else if (requestCode == REQUEST_GALLERY_PICK) {
                if (data == null || data.getData() == null) {
                    Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show();
                    return;
                }
                imagenUri = data.getData();
                imagenContacto.setImageURI(imagenUri);
            }
        }
    }
    /*protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_CAPTURE) {
                if (imagenUri != null) {
                    try {
                        imagenContacto.setImageURI(imagenUri);
                        // La imagen capturada se almacena en la galería
                        galleryAddPic();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al mostrar imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == REQUEST_GALLERY_PICK) {
                if (data == null || data.getData() == null) {
                    Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    imagenUri = data.getData();
                    imagenContacto.setImageURI(imagenUri);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al mostrar imagen", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }*/

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imagenUri);
        sendBroadcast(mediaScanIntent);
    }
    /*private void galleryAddPic() {
        try {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(imagenUri);
            sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar imagen en la galería", Toast.LENGTH_SHORT).show();
        }
    }*/

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

        int longitudMaxima = longitudTelefonoPorPais.get(pais);
        if (telefono.length() != longitudMaxima) {
            Toast.makeText(this, "Número de teléfono no válido para " + pais, Toast.LENGTH_SHORT).show();
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
    /*private void limpiarCampos() {
        inputNombre.setText("");
        inputTelefono.setText("");
        inputNota.setText("");
        imagenContacto.setImageResource(R.drawable.default_image);

        // Liberar la imagen URI
        if (imagenUri != null) {
            // Eliminar la imagen de la galería si se capturó una nueva
            if (imagenUri.getScheme().equals("content")) {
                getContentResolver().delete(imagenUri, null, null);
            }
            imagenUri = null;
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImagePickerDialog();
            } else {
                Toast.makeText(getApplicationContext(), "Acceso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
