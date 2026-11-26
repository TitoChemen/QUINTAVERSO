# QUINTAVERSO v1.1 — Jairo: edición ampliada y continuidad

## Cambios principales

- Conserva los 30 días, las doce rutas finales y todos los recursos originales.
- 156 escenas, 681 intervenciones principales, 565 intervenciones de reacción y 285 decisiones (incluye variantes mutuamente excluyentes y escenas opcionales, no todas se leen en una misma partida).
- Los 30 capítulos tienen un interludio obligatorio enlazado. Se agregó una confesión detallada que muestra únicamente las acciones cometidas por el jugador, y 16 escenas opcionales de personajes, amistad y vida propia.
- Los diálogos recuerdan decisiones concretas: la conversación con Ale, el audio de Mary, el trabajo con Andrea, el carrete, los besos, la petición de espacio, la reconciliación y las citas acordadas.
- Los diálogos posteriores a una elección representan acciones o conversaciones reales, en lugar de leer en voz alta el texto del botón. Las 30 noches opcionales de Discord tienen respuestas distintas.
- Se distingue entre pareja vigente, pausa, ruptura y bloqueo de contactos. Las escenas románticas opcionales se restringen según la situación y el día; una amistad no implica romance.
- El día 15 (carrete), las noches Discord y el mensaje nocturno tienen su capítulo en turno noche. Los demás capítulos comienzan en la tarde y sus interludios dejan el calendario en noche. El botón de dormir lleva al capítulo pendiente antes de permitir pasar de día. Las actividades de tiempo libre consumen turnos.
- Anto requiere afinidad y valentía, y una cita confirmada. El jugador puede desistir expresamente de su ruta o de otra cita ya acordada y optar por seguir solo. Las decisiones de los interludios también pueden cambiar el final: por ejemplo, no enviar el audio sincero del día 3 puede impedir el dorado incluso si se eligen las mejores decisiones principales.
- Se preservaron las ilustraciones de los finales, las temporadas Cáceres/Seba y los identificadores de los guardados y la galería. No se añadieron fondos nuevos; se reutiliza el conjunto visual disponible.

## Validación

Ejecutar desde la raíz: `python tools/validar_jairo.py`. Este verificador genera instancias de las clases Java del motor con los datos JSON reales, recorre enlaces y aplica efectos y condiciones. Se probaron 16 recorridos dirigidos (los doce finales, el naranja por dos rutas y tres casos adicionales de decisiones que cambian el desenlace) y 5.000 partidas pseudoaleatorias con actividades opcionales. Se comprobaron ausencia de nodos sin salida, ciclos inesperados, estados incompatibles pareja/pausa, finales incompatibles con pareja vigente, doce CG, ocho sprites de Jairo y persistencia de galería. No es una prueba exhaustiva de todas las combinaciones posibles ni una ejecución de JavaFX.

También pasaron `python tools/validar_guion.py`, `python tools/validar_finales_seba.py` y `python tools/validar_recursos_visuales.py`. Este último se corrigió para buscar los CG históricos de Cáceres en su carpeta real.

## Instalación y guardados

Se entrega el proyecto fuente completo, no un EXE. Descomprimir en una carpeta nueva, abrir con IntelliJ y JDK 21, recargar Maven y ejecutar `mvn clean test` y `mvn javafx:run`. Para empaquetar: `powershell -ExecutionPolicy Bypass -File .\empaquetar-windows.ps1`.

Se mantuvieron los IDs originales, pero se recomienda comenzar una nueva partida de Jairo: los guardados antiguos conservan índices de diálogo y reacciones pendientes de la edición anterior, por lo que pueden saltar contenido nuevo o reanudar en otra línea. Los finales ya desbloqueados se conservan en su galería independiente. No se modificó el formato de guardado.

El núcleo Java se compiló en JDK 21; Maven y las dependencias JavaFX/Jackson/JUnit no están instalados en el entorno de edición. Por ello la compilación Maven y la prueba interactiva de la interfaz deben ejecutarse localmente.

## Rutas (spoilers)

Ver `CAMBIOS_v1.0.md` para los doce finales originales. En v1.1 el dorado añade los acuerdos demostrados en los interludios (audio sincero, pacto de retomar conversaciones, cumplir acuerdos), y el celeste exige confirmar la cita. Las nuevas elecciones explícitas de desistir permiten llegar al blanco sin que la timidez fuerce el turquesa. El rojo y el negro prevalecen sobre las demás rutas si se activó su revelación. Los finales no dependen solo de los puntos.
