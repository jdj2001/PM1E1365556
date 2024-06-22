package com.example.pm1e1365556;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import Configuracion.Contacto;
import Configuracion.SQLiteConexion;

import android.Manifest;

import org.jetbrains.annotations.Nullable;

public class ActivityListado extends AppCompatActivity {
    private static final int REQUEST_CODE_UPDATE = 1;
    private ListView listaContactos;
    private ImageButton btnAtras;
    private Button btnCompartir, btnVerImagen, btnEliminar, btnActualizar;
    private EditText inputBusqueda;
    private SQLiteConexion dbHelper;
    private List<Contacto> contactos;
    private List<Contacto> contactosFiltrados;
    private ContactoAdapter adapter;
    private int contactoSeleccionadoIndex = -1;
    private static final int REQUEST_CALL_PHONE = 1;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        listaContactos = findViewById(R.id.listaContactos);
        btnCompartir = findViewById(R.id.btnCompartir);
        btnVerImagen = findViewById(R.id.btnVerImagen);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnAtras = findViewById(R.id.btnAtras);
        inputBusqueda = findViewById(R.id.inputBusqueda);

        dbHelper = new SQLiteConexion(this);

        // Inicializar listas
        contactos = dbHelper.obtenerContactos();
        contactosFiltrados = new ArrayList<>(contactos);

        // Configurar adaptador con contactos no filtrados
        adapter = new ContactoAdapter(this, contactosFiltrados);
        listaContactos.setAdapter(adapter);

        // Escuchar cambios en el EditText de búsqueda
        inputBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita implementación aquí
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarContactos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Cargar los contactos inicialmente
        cargarContactos();

        // Configurar el clic en un contacto para mostrar opciones
        listaContactos.setOnItemClickListener((parent, view, position, id) -> {
            contactoSeleccionadoIndex = position;
            mostrarDialogo(contactosFiltrados.get(position));
        });

        // Configurar botones
        btnCompartir.setOnClickListener(v -> compartirContacto());
        btnVerImagen.setOnClickListener(v -> verImagen());
        btnEliminar.setOnClickListener(v -> eliminarContacto());
        btnActualizar.setOnClickListener(v -> actualizarContacto());
        btnAtras.setOnClickListener(v -> regresarInicio());
    }

    private void mostrarDialogo(Contacto contacto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acciones para " + contacto.getNombre());
        builder.setItems(new CharSequence[]{"Llamar", "Eliminar", "Cancelar"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    solicitarLlamada(contacto.getTelefono());
                    break;
                case 1:
                    eliminarContacto();
                    break;
                case 2:
                    dialog.dismiss();
                    break;
            }
        });
        builder.create().show();
    }

    private void filtrarContactos(String textoBusqueda) {
        contactosFiltrados.clear();
        if (TextUtils.isEmpty(textoBusqueda)) {
            contactosFiltrados.addAll(contactos);
        } else {
            for (Contacto contacto : contactos) {
                if (contacto.getNombre().toLowerCase().contains(textoBusqueda.toLowerCase())) {
                    contactosFiltrados.add(contacto);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void solicitarLlamada(String numeroTelefono) {
        if (ContextCompat.checkSelfPermission(ActivityListado.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityListado.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
        } else {
            realizarLlamada(numeroTelefono);
        }
    }

    private void realizarLlamada(String numeroTelefono) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + numeroTelefono));
        try {
            startActivity(intent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (contactoSeleccionadoIndex != -1) {
                    Contacto contacto = contactosFiltrados.get(contactoSeleccionadoIndex);
                    realizarLlamada(contacto.getTelefono());
                }
            } else {
                Toast.makeText(this, "Permiso de llamada denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cargarContactos() {
        contactos = dbHelper.obtenerContactos();
        contactosFiltrados.clear();
        contactosFiltrados.addAll(contactos);
        adapter.notifyDataSetChanged();
    }

    private void compartirContacto() {
        if (contactoSeleccionadoIndex != -1) {
            Contacto contacto = contactosFiltrados.get(contactoSeleccionadoIndex);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String textoCompartir = "Nombre: " + contacto.getNombre() +
                    "\nTeléfono: " + contacto.getTelefono() +
                    "\nPaís: " + contacto.getPais() +
                    "\nNota: " + contacto.getNota();
            intent.putExtra(Intent.EXTRA_TEXT, textoCompartir);
            startActivity(Intent.createChooser(intent, "Compartir contacto"));
        } else {
            Toast.makeText(this, "Seleccione un contacto para compartir", Toast.LENGTH_SHORT).show();
        }
    }

    private void verImagen() {
        if (contactoSeleccionadoIndex != -1) {
            Contacto contacto = contactosFiltrados.get(contactoSeleccionadoIndex);
            Intent intent = new Intent(this, VerImagenActivity.class);
            intent.putExtra("imagenUri", contacto.getImagenUri());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Seleccione un contacto para ver la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarContacto() {
        if (contactoSeleccionadoIndex != -1) {
            Contacto contacto = contactosFiltrados.get(contactoSeleccionadoIndex);
            dbHelper.eliminarContacto(contacto.getId());
            cargarContactos();
            Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Seleccione un contacto para eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarContacto() {
        if (contactoSeleccionadoIndex != -1) {
            Contacto contacto = contactosFiltrados.get(contactoSeleccionadoIndex);
            Intent intent = new Intent(this, ActualizarContactoActivity.class);
            intent.putExtra("contactoId", contacto.getId());
            startActivityForResult(intent, REQUEST_CODE_UPDATE);
        } else {
            Toast.makeText(this, "Seleccione un contacto para actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    private void regresarInicio() {
        Intent intent = new Intent(this, ActivityContacto.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE && resultCode == RESULT_OK) {
            cargarContactos(); // Recargar la lista de contactos después de actualizar
        }
    }
}