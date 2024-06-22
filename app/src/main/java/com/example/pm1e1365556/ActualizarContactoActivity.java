package com.example.pm1e1365556;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;

import Configuracion.Contacto;
import Configuracion.SQLiteConexion;

public class ActualizarContactoActivity extends AppCompatActivity {

    private EditText editNombre, editTelefono, editNota;
    private Button btnActualizar, btnSeleccionarImagen;
    private ImageView imgContacto;
    private SQLiteConexion dbHelper;
    private int contactoId;
    private Uri imagenUri;

    private static final int REQUEST_GALLERY_PICK = 1;
    private static final int REQUEST_CAMERA_CAPTURE = 2;
    private static final int PERMISSIONS_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_contacto);

        editNombre = findViewById(R.id.editNombre);
        editTelefono = findViewById(R.id.editTelefono);
        editNota = findViewById(R.id.editNota);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        imgContacto = findViewById(R.id.imgContacto);

        dbHelper = new SQLiteConexion(this);

        contactoId = getIntent().getIntExtra("contactoId", -1);
        cargarDatosContacto();

        btnSeleccionarImagen.setOnClickListener(v -> verificarPermisos());

        btnActualizar.setOnClickListener(v -> actualizarContacto());
    }

    private void cargarDatosContacto() {
        Contacto contacto = dbHelper.obtenerContactoPorId(contactoId);
        if (contacto != null) {
            editNombre.setText(contacto.getNombre());
            editTelefono.setText(contacto.getTelefono());
            editNota.setText(contacto.getNota());

            if (contacto.getImagenUri() != null && !contacto.getImagenUri().isEmpty()) {
                imagenUri = Uri.parse(contacto.getImagenUri());
                imgContacto.setImageURI(imagenUri);
            }
        } else {
            Toast.makeText(this, "No se encontró el contacto para actualizar", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void verificarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
            } else {
                seleccionarImagen();
            }
        } else {
            seleccionarImagen();
        }
    }

    private void seleccionarImagen() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooserIntent = Intent.createChooser(galleryIntent, "Selecciona una imagen");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

        startActivityForResult(chooserIntent, REQUEST_GALLERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY_PICK && data != null) {
                if (data.getData() != null) {
                    imagenUri = data.getData();
                    imgContacto.setImageURI(imagenUri);
                } else {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imgContacto.setImageBitmap(imageBitmap);
                    imagenUri = getImageUri(imageBitmap);
                }
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Imagen Capturada", null);
        return Uri.parse(path);
    }

    private void actualizarContacto() {
        String nombre = editNombre.getText().toString().trim();
        String telefono = editTelefono.getText().toString().trim();
        String nota = editNota.getText().toString().trim();
        String imagen = imagenUri != null ? imagenUri.toString() : "";

        if (nombre.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Nombre y teléfono son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!telefono.matches("\\d{8}")) {
            Toast.makeText(this, "Número de teléfono no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Contacto contactoActualizado = new Contacto(contactoId, "", nombre, telefono, nota, imagen);
        dbHelper.actualizarContacto(contactoActualizado, contactoId);

        Toast.makeText(this, "Contacto actualizado correctamente", Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                seleccionarImagen();
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show();
            }
        }
    }
}