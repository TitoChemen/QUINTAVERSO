# QUINTAVERSO — integración gráfica de la temporada Cáceres (v0.5)

**Esta edición parte del RAR del usuario.** Las escenas, estadísticas, eventos, guardados, diálogos y decisiones de la v0.5 se conservan. Añade imagen por escena, retrato por hablante y CG al reproducir/terminar cualquiera de los ocho epílogos.

## Cómo arrancar desde IntelliJ IDEA

1. Descomprimir el ZIP en una carpeta propia; **no sobrescribir la v0.5 original**.
2. Abrir la carpeta `QUINTAVERSO-v0.5-GRAFICO` como proyecto Maven. Usar **JDK 21**.
3. Esperar a que IntelliJ descargue/importe las dependencias del `pom.xml`.
4. Ejecutar **Maven → Lifecycle → test**, y después **Plugins → javafx → javafx:run**.
5. Iniciar temporada **Cáceres**. Al abrir escenas se ven los fondos y personajes. Al terminar la campaña (día 30) aparece el CG correspondiente. El prólogo también usa un fondo.

El ZIP contiene recursos en `src/main/resources/imagenes`: **10 fondos, 8 CG con ruta asignada, retratos y sprites de 5 personajes**, además de otros archivos suministrados. No requiere ComfyUI, Python, Internet ni API para mostrarlos durante el juego.

## Archivos que se modificaron

- `MotorNarrativo.java`: escenario visual redimensionable, texto y opciones visibles en panel inferior con desplazamiento, retrato de cada hablante, CG de los ocho finales, panel de prompts plegado.
- `MotorCalendario.java`: pantalla final con imagen de su CG detrás de los botones y estadísticas.
- `Escena.java`: dos propiedades opcionales nuevas `fondo` y `cg` compatibles con Jackson.
- `RecursosVisuales.java`: rutas y selección de expresión; reconoce `Cony (audio)` y `Kevin (mensaje)`. En escenas de chat mantiene a Lumi/Cony en pantalla durante la narración.
- `quintaverso.css`: temas transparentes para lectura frente a los fondos.
- Los siete `historias/caceres/*.json`: cada una de las **205 escenas** tiene un campo `fondo` y los **8 finales** además `cg`. Ver `docs/MAPA_VISUAL_ESCENAS.csv`.
- `tools/generar_guion.py`: ahora conserva los campos `fondo` y `cg` de escenas con IDs existentes al regenerar, evitando perder el trabajo visual.
- `tools/validar_recursos_visuales.py`: prueba de rutas y cabeceras PNG.

## Cómo cambiar una ilustración sin tocar Java

Ejemplo en `src/main/resources/historias/caceres/`:

```json
{
  "id": "final_lumi",
  "titulo": "FINAL AZUL — Elegirse sin perderse",
  "lugar": "Costanera · Un mes después",
  "fondo": "costanera_atardecer.png",
  "cg": "final_azul.png"
}
```

Los valores `fondo`/`cg` son **nombres de archivos** (no rutas absolutas) de `imagenes/fondos/` y `imagenes/cg/`. En la escena real, conserva también los campos `dialogos`, `decisiones`, etc.; arriba se muestra un ejemplo simplificado de nombres.

Puedes sustituir el contenido de un PNG conservando el nombre para mejorar la calidad sin cambiar el guion.

## Las expresiones

Al leer cada línea se identifica quien habla (incluidos mensajes y audios), y una heurística sencilla elige entre neutral, risa, serio, nervioso, sorpresa y coqueteo. Esta selección **no cambia el significado ni las decisiones de la historia**. Las 8 poses disponibles de Lumi/Cony se reutilizan según el tono de la línea. Los retratos de Seba, Jairo y Cáceres cambian según el texto.

El motor **no dibuja personajes sobre los CG de final**, porque ya están incluidos en las ilustraciones panorámicas.

## Puntos pendientes de arte (no son fallas de compilación)

- **Kevin:** la carpeta `imagenes/personajes/kevin/` llegó vacía. En diálogos suyos se muestra un avatar estilizado con `K`. No se ha inventado un retrato ni se ha asignado la cara de otro personaje. Cuando tengas un retrato, agrégalo y modifica el caso `kevin` de `RecursosVisuales.retrato(...)`.
- Algunas escenas del departamento/cocina de Cony, fiesta, feria y taller **no tienen fondos específicos entre los 10 proporcionados**. Se asignó un interior de reemplazo; el CSV permite localizar cada escena y reemplazarlo cuando generes esos escenarios.
- Los retratos de Cáceres en PNG pequeños son RGB con fondo propio. Se pueden ver con ese fondo en escenas de diálogo; su pose `caceres_cuerpo_completo_neutral.png` sí tiene transparencia.
- Los CG e imágenes usadas son las del RAR recibido; no se han reemplazado ni reimaginado.
- Esta integración no genera automáticamente escenas animadas ni sincroniza bocas; muestra ilustraciones estáticas.

## Verificación

```bash
python tools/validar_guion.py
python tools/validar_recursos_visuales.py
```

La prueba de interfaz JavaFX y las pruebas Maven deben correrse en IntelliJ: el entorno de empaquetado no tiene dependencias JavaFX/Maven descargadas. La lógica Java pura y los JSON sí se validaron.

### Nombres de archivos portables

Los 8 CG y los 3 retratos de Jairo se renombraron a nombres ASCII (`final_azul.png`, `jairo_neutral.png`, etc.) para que las rutas Java `getResource` funcionen incluso en sistemas con localización no UTF-8. **El contenido de las imágenes es el mismo que venía en el RAR**.
