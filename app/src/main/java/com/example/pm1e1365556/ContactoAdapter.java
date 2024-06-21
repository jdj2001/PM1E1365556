package com.example.pm1e1365556;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_contacto, parent, false);
            holder = new ViewHolder();
            holder.tvNombre = convertView.findViewById(R.id.tvNombre);
            holder.tvTelefono = convertView.findViewById(R.id.tvTelefono);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contacto contacto = contactos.get(position);
        holder.tvNombre.setText(contacto.getNombre());
        holder.tvTelefono.setText(contacto.getTelefono());

        return convertView;
    }

    static class ViewHolder {
        TextView tvNombre;
        TextView tvTelefono;
    }
}
