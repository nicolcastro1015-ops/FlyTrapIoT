package com.example.flytrapiot;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetalleActivity extends AppCompatActivity {
    public TextView txt_id, txt_codigo_sector, txt_tipo_trampa, txt_nivel_captura, txt_observaciones;
    public Button boton_editar, boton_eliminar;

    public int id_actual ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txt_id = findViewById(R.id.txt_id);
        txt_codigo_sector = findViewById(R.id.txt_codigo_sector);
        txt_tipo_trampa = findViewById(R.id.txt_tipo_trampa);
        txt_nivel_captura = findViewById(R.id.txt_nivel_captura);
        txt_observaciones = findViewById(R.id.txt_observaciones);

        boton_editar = findViewById(R.id.boton_editar);
        boton_eliminar = findViewById(R.id.boton_eliminar);

        //recibimos el id enviado desde MainActivity
        Intent intent_actual = getIntent();
        id_actual = intent_actual.getIntExtra("id_actual", -1);

        if (id_actual == -1)
        {
            Toast.makeText(this, "No se recibio el ID de la trampa", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //eliminacion directa, SIN dialogo de confirmacion (requisito de la evaluacion)
        boton_eliminar.setOnClickListener(v -> {
            eliminarTrampa(id_actual);
        });

        boton_editar.setOnClickListener(v -> {
            Intent editar_intent = new Intent(DetalleActivity.this, EditarActivity.class);
            editar_intent.putExtra("id_actual", id_actual);
            startActivity(editar_intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //se vuelve a consultar al regresar desde EditarActivity para mostrar los datos actualizados
        cargarTrampa(id_actual);
    }

    public void cargarTrampa(int id)
    {
        DBHelper admin = new DBHelper(this);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor consulta = db.rawQuery(
                "SELECT id,codigo_sector,tipo_trampa,nivel_captura,observaciones FROM trampas WHERE id=?",
                new String[]{String.valueOf(id)});

        boolean existe = consulta.moveToFirst();

        if(existe)
        {
            //existe valor
            String observaciones = consulta.getString(4);
            if (observaciones == null || observaciones.isEmpty())
            {
                observaciones = "Sin observaciones";
            }

            txt_id.setText("# " + consulta.getInt(0));
            txt_codigo_sector.setText("Codigo sector : " + consulta.getString(1));
            txt_tipo_trampa.setText("Tipo trampa : " + consulta.getString(2));
            txt_nivel_captura.setText("Nivel captura : " + consulta.getInt(3));
            txt_observaciones.setText("Observaciones : " + observaciones);
        }

        consulta.close();
        db.close();

        if (!existe)
        {
            //no existe valor (por ejemplo, ya fue eliminado)
            Toast.makeText(this, "La trampa no existe", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void eliminarTrampa(int id)
    {
        DBHelper admin = new DBHelper(this);
        SQLiteDatabase db = admin.getWritableDatabase();

        //se elimina SOLO el registro cuyo id coincide con el seleccionado
        int filas_eliminadas = db.delete("trampas", "id=?", new String[]{String.valueOf(id)});

        db.close();

        if (filas_eliminadas > 0)
        {
            Toast.makeText(this, "Trampa eliminada", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "No se pudo eliminar la trampa", Toast.LENGTH_LONG).show();
        }

        //volvemos a MainActivity, que recarga la lista en onResume()
        finish();
    }
}
