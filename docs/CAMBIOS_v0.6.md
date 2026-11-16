# QUINTAVERSO v0.6 — Lista de cambios y comprobaciones

## Errores de la v0.5 identificados

1. En `HelloApplication`, `MotorCalendario` y `MotorNarrativo` se creaba una `new Scene(...)` para cada pantalla. Ahora `HelloApplication.cambiarContenido` conserva la misma escena con `setRoot`, para evitar perder el modo fullscreen.
2. En `MotorNarrativo.renderizar`, una decisión aplicaba efectos y hacía `cambiarEscena` o `terminar` de inmediato. Ahora entra en un estado de reacción que se puede leer y guardar.
3. El `TitledPane` «Dirección visual y prompt» era una herramienta para preparar arte; se eliminó de la interfaz, no de las historias JSON.
4. `GestorGuardado` usaba solo `caceres-v05.json`; ahora tiene autoguardado `caceres-v06-auto.json`, tres ranuras manuales `caceres-v06-slot-N.json`, e importación de la v0.5.
5. La posición de lectura no se persistía; ahora `EstadoPartida` guarda contexto, escena, línea, actividad en curso, respuesta a la decisión y destino.

## Pruebas realizadas en este paquete

- El código Java independiente de JavaFX (`CalendarioJuego`, `Decision`, `EstadoPartida`, `ReaccionesNarrativas`, etc.) compiló con JDK 21.
- Simulación Java de una elección, modificación de estadísticas, punto de lectura y limpieza del estado: correcta.
- Validación de 205 IDs de escena, 334 decisiones, 68 respuestas personalizadas: correcta.
- Ningún enlace `siguiente` roto; ninguna imagen `fondo` o `cg` explícita inexistente.
- **Pendiente:** compilar con Maven y probar JavaFX visualmente en Windows, pues las dependencias JavaFX/Maven no están instaladas en el entorno de preparación.

## Archivos principales modificados

- `HelloApplication.java`, `MotorNarrativo.java`, `motor/MotorCalendario.java`
- `motor/EstadoPartida.java`, `motor/Decision.java`, `guardado/GestorGuardado.java`
- `src/main/resources/quintaverso.css` y JSON de escenas
- Nuevos `DialogoPartidas.java`, `motor/ReaccionesNarrativas.java`
- Tres tests nuevos y `empaquetar-windows.ps1`

## Guía de regresión

1. En Windows abre esta carpeta en IntelliJ, proyecto Maven Java 21.
2. Ejecuta **Lifecycle → test** y **Plugins → javafx → javafx:run**.
3. Usa F11; cambia varias veces calendario/diálogo/menú; mantén pantalla completa.
4. Guarda en ranura 1 al leer una línea; cambia de escena, carga ranura 1.
5. Guarda después de elegir una decisión y durante la reacción; carga esa ranura y observa que la afinidad no suba dos veces.
6. Juega hasta cerrar un turno; verifica que solo se haya contado una acción.
7. Verifica que el autoguardado no haya modificado `caceres-v05.json`.
8. Ejecuta `empaquetar-windows.ps1` **solo cuando Maven test y la prueba visual salgan bien**.
