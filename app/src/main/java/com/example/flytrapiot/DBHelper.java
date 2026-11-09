package com.example.flytrapiot;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
    private static final String NOMBRE_BASE_DATOS = "flytrap_db";
    private static final int VERSION_BASE_DATOS = 1;

    DBHelper(Context contexto)
    {
        super(contexto, NOMBRE_BASE_DATOS, null, VERSION_BASE_DATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //TIENE TODO EL CODIGO PARA CREAR LAS TABLAS DE MI BASE DE DATOS
        //Esquema obligatorio de la evaluacion (no modificar nombres ni tipos)
        String crearTabla = "CREATE TABLE trampas ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "codigo_sector TEXT NOT NULL, "
                + "tipo_trampa TEXT NOT NULL, "
                + "nivel_captura INTEGER NOT NULL, "
                + "observaciones TEXT )";
        db.execSQL(crearTabla);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int version_vieja, int version_nueva)
    {
        db.execSQL("DROP TABLE IF EXISTS trampas"); //eliminamos la tabla
        onCreate(db); //creamos la nueva version de la tabla
    }
}
