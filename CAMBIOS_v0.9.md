# QUINTAVERSO 0.9.0 — SEBA: CONTINUIDAD, DISCORD Y PERSONAJES

Base: el último archivo QUINTAVERSO(2).rar recibido el 17/11/2026.
No incluye instalador Windows; sí fuentes Maven de Java 21 y recursos para abrir en IntelliJ.

## Qué cambió de verdad

- Temporada Seba: 146 escenas y 1254 líneas de diálogo. Antes: 88 escenas, menos de 500 líneas.
- 24 noches nuevas de Discord (`historias/seba/discord_noches.json`), disponibles exclusivamente durante el turno NOCHE del día correspondiente. No se repiten si el jugador las omite.
- 26 momentos opcionales contextualizados (`historias/seba/momentos.json`) con ventanas de desbloqueo de hasta tres días.
- 43 episodios preexistentes ampliados con 4 líneas específicas adicionales cada uno; sus decisiones siguen siendo las originales.
- 8 escenas de continuidad (`historias/seba/continuidad.json`): confidencia a Kelly, ruptura por secreto, exposición al grupo, la reacción de Kelly, distancia, cierre, reencuentro honesto y confesión tardía.
- Diálogo JSON admite `expresion` y `condiciones`: frases que solo aparecen si el estado narrativo cumple los requisitos. No solo las decisiones pueden tener condiciones.
- Se aprovechan los 8 sprites de Seba numerados 01–08 en `imagenes/personajes/seba`; el protagonista aparece también como segunda figura cuando hablan sus amigos.
- Calendario Seba con actividades agrupadas por temática en paneles desplegables, contador de noches jugadas y resumen del estado de la historia. Cáceres conserva su vista habitual.
- El CG `imagenes/cg/seba/final_rojo.png` ahora aparece en el final rojo; se conservan los dos finales naranja, el turquesa y el combo a Rodrigo.
- Partidas: guardado versión interna 4, lectura compatible con v2/v3/v4. Migración cautelosa de la ruta v0.8 en la que Seba registró infidelidad y mentira, sin confesión. Las partidas terminadas no cambian.

## El error de continuidad de Amira: arreglado por dos rutas excluyentes

### Ruta A: ocultar el beso después de la fiesta

1. Seba rechaza decirle la verdad a Kelly.
2. Amira lo enfrenta y se baja de la mentira. Lo cuenta al grupo, asumiendo su parte.
3. Kelly se entera por terceros, confronta a Seba y termina la relación.
4. Días 16 y 20: ausencia, distancia y conversación de cierre. Amira NO vuelve como interés romántico.
5. Sus actividades románticas quedan bloqueadas y el cálculo del final impide forzar un desenlace violeta/rojo aunque el jugador acumule afinidad.

### Ruta B: confesárselo a Kelly y asumir las consecuencias

1. Seba habla con Kelly directamente. La relación termina y se reconoce el dolor causado.
2. El día 12 elige aceptar responsabilidad; no basta con haber confesado.
3. Día 20, Amira accede a hablar sin prometer romance. El jugador puede pedir disculpas, respetar los límites o presionar.
4. Solo con un vínculo reabierto y responsabilidad asumida se abre la confesión del día 23.
5. Si Amira acepta acercarse y el jugador se compromete con sinceridad, el día 26 puede aparecer la elección de formar pareja. El final rojo conserva el antecedente de infidelidad, pero no borra sus consecuencias.

En la ruta sin infidelidad, el final violeta continúa siendo posible tras una ruptura legítima y la ruta personal con Amira.

## Cómo probar

- Nueva partida con `SEBA2026`, jugar el día 10, elegir el beso y luego ocultarlo. Revisar los acontecimientos de los días 12, 16 y 20. La opción de romance con Amira NO debe aparecer el día 26.
- Otra partida: en día 10 elige el beso, luego hablarle a Kelly. En día 12 acepta la responsabilidad, día 20 escucha a Amira y, si se habilita, día 23 decide si puede haber un futuro. Después de sus actividades, verifica si el día 26 puede elegirse su ruta.
- En la noche del día 1, 2, 3... 24 entra a Discord. Cada día tiene una conversación distinta; al jugarla consume el turno nocturno y avanza al siguiente día.
- Completa un capítulo con una línea de diálogo condicional para comprobar que solo aparezca la correspondiente a la ruta.
- Comprueba el final rojo con el CG real y que los dos naranjas y el turquesa sigan mostrando sus imágenes.

## Notas de compatibilidad

Las partidas se guardan como `~/.quintaverso/partidas/seba-v06-auto.json` y ranuras manuales; se conserva el sufijo v06 para no perder los archivos existentes. Una partida guardada a mitad de una escena cuya estructura cambió puede reanudar desde una línea narrativa distinta. Para apreciar la historia completa se recomienda iniciar una partida nueva y mantener una copia de las ranuras previas.

## Validación realizada en el entorno de entrega

- Se verificaron los 146 IDs de escena de Seba, los 205 de Cáceres, las referencias JSON de decisiones y continuaciones y las rutas de todos los CG y fondos.
- Se compiló el núcleo Java 21 (estado, condiciones, decisiones, hitos, finales, eventos y reacciones), y pasaron pruebas de rama honesta, rama delatada, desbloqueos de Discord y finales especiales.
- No se ejecutó la interfaz JavaFX completa ni Maven aquí porque no están instaladas sus dependencias. Probar `mvn clean test` y `mvn javafx:run` desde IntelliJ antes de empaquetar un ejecutable.
