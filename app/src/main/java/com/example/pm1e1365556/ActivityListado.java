package com.example.pm1e1365556;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import Configuracion.Contacto;
import Configuracion.SQLiteConexion;

public class ActivityListado extends AppCompatActivity {
    private ListView listaContactos;
    private ImageButton btnAtras;
    private Button btnCompartir, btnVerImagen, btnEliminar, btnActualizar;
    private EditText inputBusqueda;
    private SQLiteConexion dbHelper;
    private List<Contacto> contactos;
    private ContactoAdapter adapter;
    private int contactoSeleccionadoIndex = -1; // Índice del contacto seleccionado

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

        cargarContactos();

        listaContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contactoSeleccionadoIndex = position; // Guardar el índice del contacto seleccionado
                mostrarDialogo(contactos.get(position));
            }
        });

        btnCompartir.setOnClickListener(v -> compartirContacto());
        btnVerImagen.setOnClickListener(v -> verImagen());
        btnEliminar.setOnClickListener(v -> eliminarContacto());
        btnActualizar.setOnClickListener(v -> actualizarContacto());
        btnAtras.setOnClickListener(v -> regresarInicio());
    }

    private void mostrarDialogo(Contacto contacto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acciones para " + contacto.getNombre());
        builder.setItems(new CharSequence[]{"Llamar", "Eliminar", "Cancelar"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        llamarContacto(contacto);
                        break;
                    case 1:
                        eliminarContacto();
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void llamarContacto(Contacto contacto) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + contacto.getTelefono()));
        startActivity(intent);
    }

    private void cargarContactos() {
        contactos = dbHelper.obtenerContactos();
        adapter = new ContactoAdapter(this, contactos);
        listaContactos.setAdapter(adapter);
    }

    private void compartirContacto() {
        if (contactoSeleccionadoIndex != -1) {
            Contacto contacto = contactos.get(contactoSeleccionadoIndex);
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
            Contacto contacto = contactos.get(contactoSeleccionadoIndex);
            Intent intent = new Intent(this, VerImagenActivity.class);
            intent.putExtra("imagenUri", contacto.getImagenUri());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Seleccione un contacto para ver la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarContacto() {
        if (contactoSeleccionadoIndex != -1) {
            Contacto contacto = contactos.get(contactoSeleccionadoIndex);
            dbHelper.eliminarContacto(contacto.getId()); // Asumiendo que se maneja el ID en la base de datos
            cargarContactos();
            Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Seleccione un contacto para eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarContacto() {
        if (contactoSeleccionadoIndex != -1) {
            Contacto contacto = contactos.get(contactoSeleccionadoIndex);
            Intent intent = new Intent(this, ActualizarContactoActivity.class);
            intent.putExtra("contactoId", contacto.getId()); // Asumiendo que se maneja el ID en la base de datos
            startActivity(intent);
        } else {
            Toast.makeText(this, "Seleccione un contacto para actualizar", Toast.LENGTH_SHORT).show();
        }
    }
    private void regresarInicio()
    {
        Intent intent = new Intent(this, ActivityContacto.class);
        startActivity(intent);
        finish();
    }
}
