package Configuracion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SQLiteConexion extends SQLiteOpenHelper {

    public SQLiteConexion(@Nullable Context context) {
        super(context, Trans.DBname, null, Trans.Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creación de la tabla de contactos
        db.execSQL(Trans.CreateTableContactos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar tabla existente y crear una nueva
        db.execSQL(Trans.DropTableContactos);
        onCreate(db);
    }

    // Métodos CRUD para la tabla de contactos
    public void insertarContacto(Contacto contacto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Trans.pais, contacto.getPais());
        values.put(Trans.nombre, contacto.getNombre());
        values.put(Trans.telefono, contacto.getTelefono());
        values.put(Trans.nota, contacto.getNota());
        values.put(Trans.imagenUri, contacto.getImagenUri());

        db.insert(Trans.TableContactos, null, values);
        db.close();
    }

    public ArrayList<Contacto> obtenerContactos() {
        ArrayList<Contacto> contactos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Trans.TableContactos, null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    Contacto contacto = new Contacto(
                            cursor.getInt(cursor.getColumnIndexOrThrow(Trans.id)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Trans.pais)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Trans.nombre)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Trans.telefono)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Trans.nota)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Trans.imagenUri))
                    );
                    contactos.add(contacto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return contactos;
    }


    public void eliminarContacto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Trans.TableContactos, Trans.id + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void actualizarContacto(Contacto contacto, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Trans.pais, contacto.getPais());
        values.put(Trans.nombre, contacto.getNombre());
        values.put(Trans.telefono, contacto.getTelefono());
        values.put(Trans.nota, contacto.getNota());
        values.put(Trans.imagenUri, contacto.getImagenUri());

        db.update(Trans.TableContactos, values, Trans.id + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
