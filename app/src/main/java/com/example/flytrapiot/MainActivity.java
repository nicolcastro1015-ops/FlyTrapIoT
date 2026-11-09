package com.example.flytrapiot;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Datos del XML
    EditText input_codigo_sector, input_tipo_trampa, input_nivel_captura, input_observaciones;
    Button boton_registrar;
    ListView lista_trampas;

    //Datos necesarios para ListView
    List<String> datos_trampas = new ArrayList<>();
    List<Integer> lista_id = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //incializar variables
        input_codigo_sector = findViewById(R.id.input_codigo_sector);
        input_tipo_trampa = findViewById(R.id.input_tipo_trampa);
        input_nivel_captura = findViewById(R.id.input_nivel_captura);
        input_observaciones = findViewById(R.id.input_observaciones);
        boton_registrar = findViewById(R.id.boton_registrar);
        lista_trampas = findViewById(R.id.lista_trampas);

        boton_registrar.setOnClickListener(v -> {
            crearTrampa();
        });

        lista_trampas.setOnItemClickListener( new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int posicion, long id){
                //si la lista esta vacia solo existe el mensaje "No existen trampas", no hay id que enviar
                if (posicion >= lista_id.size())
                {
                    return;
                }

                //buscamos el ID REAL de la base de datos (no la posicion) y lo enviamos a DetalleActivity
                Integer id_actual = lista_id.get(posicion);

                Intent intent_detalle = new Intent(MainActivity.this, DetalleActivity.class);
                intent_detalle.putExtra("id_actual", id_actual);
                startActivity(intent_detalle);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //onResume se ejecuta al abrir la app y cada vez que volvemos desde Detalle o Editar,
        //asi la lista siempre muestra los datos actualizados despues de editar o eliminar
        cargarTrampas();
    }

    public void cargarTrampas()
    {
        DBHelper admin = new DBHelper(this);
        SQLiteDatabase db = admin.getReadableDatabase();

        datos_trampas = new ArrayList<>();
        lista_id = new ArrayList<Integer>();

        //CONSULTAR DATOS A LA BASE DE DATOS
        Cursor fila = db.rawQuery("SELECT id,codigo_sector,tipo_trampa,nivel_captura FROM trampas", null);

        //validar que tenga por lo menos un dato
        if (fila.moveToFirst())
        {
            //tiene como minimo un dato
            do {
                //obtener los datos de la fila
                int id_actual = fila.getInt(0);
                String sector_actual = fila.getString(1);
                String tipo_actual = fila.getString(2);
                int nivel_actual = fila.getInt(3);

                String trampa_actual = "# " + id_actual + " | Sector: " + sector_actual
                        + " | Tipo: " + tipo_actual + " | Captura: " + nivel_actual;
                datos_trampas.add(trampa_actual);
                lista_id.add(id_actual);

            } while (fila.moveToNext());
        }
        else
        {
            //si no retorna nada de la base de datos
            datos_trampas.add("No existen trampas en la base de datos");
        }

        //cerramos primero el cursor y luego la base de datos
        fila.close();
        db.close();

        //adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos_trampas);
        lista_trampas.setAdapter(adapter);
    }

    public void crearTrampa()
    {
        String sector_actual = String.valueOf(input_codigo_sector.getText()).trim();
        String tipo_actual = String.valueOf(input_tipo_trampa.getText()).trim();
        String nivel_texto = String.valueOf(input_nivel_captura.getText()).trim();
        String observaciones_actual = String.valueOf(input_observaciones.getText()).trim();

        //1. validar campos obligatorios (observaciones es opcional)
        if (sector_actual.isEmpty() || tipo_actual.isEmpty() || nivel_texto.isEmpty())
        {
            Toast.makeText(this, "Ingrese codigo de sector, tipo de trampa y nivel de captura", Toast.LENGTH_LONG).show();
            return;
        }

        //2. validar que el nivel de captura sea un numero entero no negativo
        int nivel_actual;
        try
        {
            nivel_actual = Integer.parseInt(nivel_texto);
        }
        catch (NumberFormatException e)
        {
            Toast.makeText(this, "El nivel de captura debe ser un numero entero", Toast.LENGTH_LONG).show();
            return;
        }

        if (nivel_actual < 0)
        {
            Toast.makeText(this, "El nivel de captura no puede ser negativo", Toast.LENGTH_LONG).show();
            return;
        }

        //3. si todo es valido agregamos
        DBHelper admin = new DBHelper(this);
        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues nuevo_dato = new ContentValues();
        nuevo_dato.put("codigo_sector", sector_actual);
        nuevo_dato.put("tipo_trampa", tipo_actual);
        nuevo_dato.put("nivel_captura", nivel_actual); //se guarda como INTEGER
        nuevo_dato.put("observaciones", observaciones_actual); //puede quedar vacio

        //4. insertar (db.insert retorna -1 si hubo un error)
        long resultado = db.insert("trampas", null, nuevo_dato);

        db.close();

        if (resultado != -1)
        {
            //reiniciar los inputs
            input_codigo_sector.setText("");
            input_tipo_trampa.setText("");
            input_nivel_captura.setText("");
            input_observaciones.setText("");

            Toast.makeText(this, "Trampa registrada", Toast.LENGTH_SHORT).show();

            //5. actualizar inmediatamente la lista
            cargarTrampas();
        }
        else
        {
            Toast.makeText(this, "Error al registrar la trampa", Toast.LENGTH_LONG).show();
        }
    }
}
