package Configuracion;

public class Trans {
    // Versión de la base de datos
    public static final int Version = 1;
    // Nombre de la base de datos
    public static final String DBname = "ContactosDB";
    // Nombre de la tabla
    public static final String TableContactos = "contactos";
    // Columnas de la tabla
    public static final String id = "id";
    public static final String pais = "pais";
    public static final String nombre = "nombre";
    public static final String telefono = "telefono";
    public static final String nota = "nota";
    public static final String imagenUri = "imagenUri";

    // DDL para crear la tabla
    public static final String CreateTableContactos = "CREATE TABLE " + TableContactos + " ( " +
            id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            pais + " TEXT, " +
            nombre + " TEXT, " +
            telefono + " TEXT, " +
            nota + " TEXT, " +
            imagenUri + " TEXT )";

    public static final String DropTableContactos = "DROP TABLE IF EXISTS " + TableContactos;
}