# FlyTrap IoT

Prototipo Android (Java + SQLite) para registrar y administrar trampas de monitoreo
de la mosca de la fruta (*Ceratitis capitata*). CRUD completo con persistencia local.

Base de datos: `flytrap_db` — Tabla: `trampas`

| Pantalla | Función |
|---|---|
| MainActivity | Formulario de registro (INSERT) + ListView de trampas (SELECT) |
| DetalleActivity | Muestra una trampa por ID, botones Editar y Eliminar (DELETE directo) |
| EditarActivity | Formulario con datos actuales y guardado (UPDATE) |

## Guía de pruebas

1. Registrar 3 trampas (ej. SEC-01/McPhail/5, SEC-02/Jackson/0/"Revisar cebo", SEC-03/McPhail/12).
2. Probar validaciones: dejar el sector vacío → aviso; observaciones vacías → se registra igual.
3. Verificar que las 3 aparecen en la lista con su `#id`.
4. Tocar la trampa #2 → el detalle muestra los datos de la #2 (no de otra).
5. Editar: cambiar nivel a 7 → Guardar → el detalle muestra 7 → volver → la lista muestra 7.
6. Eliminar la #2 → vuelve directo a la lista sin diálogo y la #2 ya no aparece; #1 y #3 siguen.
7. Eliminar todas → aparece "No existen trampas en la base de datos"; tocar ese mensaje no hace nada.
8. Cerrar la app por completo (quitarla de recientes) y volver a abrirla → los datos siguen ahí.

## Commits sugeridos

```
git commit -m "Proyecto base FlyTrapIoT"
git commit -m "DBHelper: creacion de flytrap_db y tabla trampas"
git commit -m "CREATE: formulario e insercion con ContentValues"
git commit -m "READ: listado de trampas en ListView"
git commit -m "Envio del id por Intent y DetalleActivity"
git commit -m "UPDATE: EditarActivity con db.update"
git commit -m "DELETE: eliminacion directa por id"
```
