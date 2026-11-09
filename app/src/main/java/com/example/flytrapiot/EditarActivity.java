package com.example.flytrapiot;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditarActivity extends AppCompatActivity {
    EditText input_codigo_sector, input_tipo_trampa, input_nivel_captura, input_observaciones;
    Button boton_guardar;

    public int id_actual ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        input_codigo_sector = findViewById(R.id.input_codigo_sector2);
        input_tipo_trampa = findViewById(R.id.input_tipo_trampa2);
        input_nivel_captura = findViewById(R.id.input_nivel_captura2);
        input_observaciones = findViewById(R.id.input_observaciones2);
        boton_guardar = findViewById(R.id.boton_guardar2);

        //recibimos el id enviado desde DetalleActivity
        Intent intent_actual = getIntent();
        id_actual = intent_actual.getIntExtra("id_actual", -1);

        if (id_actual == -1)
        {
            Toast.makeText(this, "No se recibio el ID de la trampa", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        cargarTrampa(id_actual);

        boton_guardar.setOnClickListener(v -> {
            actualizarTrampa(id_actual);
        });
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
            //existe valor: lo mostramos en el formulario
            input_codigo_sector.setText(consulta.getString(1));
            input_tipo_trampa.setText(consulta.getString(2));
            input_nivel_captura.setText(String.valueOf(consulta.getInt(3)));

            String observaciones = consulta.getString(4);
            input_observaciones.setText(observaciones == null ? "" : observaciones);
        }

        consulta.close();
        db.close();

        if (!existe)
        {
            //no existe valor
            Toast.makeText(this, "La trampa no existe", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void actualizarTrampa(int id)
    {
        String valor_sector = input_codigo_sector.getText().toString().trim();
        String valor_tipo = input_tipo_trampa.getText().toString().trim();
        String valor_nivel_texto = input_nivel_captura.getText().toString().trim();
        String valor_observaciones = input_observaciones.getText().toString().trim();

        //validar campos obligatorios
        if (valor_sector.isEmpty() || valor_tipo.isEmpty() || valor_nivel_texto.isEmpty())
        {
            Toast.makeText(this, "Codigo de sector, tipo de trampa y nivel de captura son obligatorios", Toast.LENGTH_LONG).show();
            return;
        }

        //validar nivel de captura entero y no negativo
        int valor_nivel;
        try
        {
            valor_nivel = Integer.parseInt(valor_nivel_texto);
        }
        catch (NumberFormatException e)
        {
            Toast.makeText(this, "El nivel de captura debe ser un numero entero", Toast.LENGTH_LONG).show();
            return;
        }

        if (valor_nivel < 0)
        {
            Toast.makeText(this, "El nivel de captura no puede ser negativo", Toast.LENGTH_LONG).show();
            return;
        }

        DBHelper admin = new DBHelper(this);
        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("codigo_sector", valor_sector);
        valores.put("tipo_trampa", valor_tipo);
        valores.put("nivel_captura", valor_nivel);
        valores.put("observaciones", valor_observaciones);

        //UPDATE solo del registro con el id recibido
        int filas_actualizadas = db.update("trampas", valores, "id=?", new String[]{String.valueOf(id)});

        db.close();

        if (filas_actualizadas > 0)
        {
            Toast.makeText(this, "Trampa actualizada", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "No se pudo actualizar la trampa", Toast.LENGTH_LONG).show();
        }

        //volvemos a DetalleActivity, que recarga los datos en onResume()
        finish();
    }
}
