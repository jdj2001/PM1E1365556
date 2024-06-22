package com.example.pm1e1365556;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import Configuracion.Contacto;

public class ContactoAdapter extends ArrayAdapter<Contacto> {

    private Context context;
    private List<Contacto> contactos;

    public ContactoAdapter(Context context, List<Contacto> contactos) {
        super(context, R.layout.item_contacto, contactos);
        this.context = context;
        this.contactos = contactos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Contacto contacto = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_contacto, parent, false);
        }

        TextView textViewNombre = convertView.findViewById(R.id.textViewNombre);
        ImageView imageViewContacto = convertView.findViewById(R.id.imageViewContacto);

        textViewNombre.setText(contacto.getNombre());

        if (contacto.getImagenUri() != null && !contacto.getImagenUri().isEmpty()) {
            imageViewContacto.setImageURI(Uri.parse(contacto.getImagenUri()));
        } else {
            imageViewContacto.setImageResource(R.drawable.default_image);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvNombre;
        TextView tvTelefono;
    }
}