# QUINTAVERSO v0.8 — Ruta de Seba: naranja ×2, turquesa y combo a Rodrigo

Fecha de preparación: 17 de noviembre de 2026.

## Cambios de juego

- La temporada de Seba ahora incluye **88 escenas** y **13 desenlaces diferentes**.
- Día 10, fiesta: al dormir junto a Amira puedes elegir **pegarle a Rodrigo**. Esta decisión reproduce la CG `imagenes/cg/seba/comborodri.png` y el día 12 abre la escena especial `seba_post_combo`, donde Kelly confronta lo ocurrido.
- Día 26, ruta de Feña: la opción respetuosa con Feña y Richie abre dos posibles resultados narrativos:
  - `final_seba_naranja`: Seba y Feña están juntos, Richie se marcha triste (`final_naranja_sebaxfena.png`).
  - `final_seba_naranja_picnic`: Feña y Richie son pareja, Seba sigue feliz como amigo de ambos en un picnic (`final_naranja_richiexfena.png`).
- Día 26, confrontación: Sebastián puede bajar el tono y recuperar su amistad con Richie (picnic) o provocarlo y empujarlo. En esta última ruta Richie le pega y se desbloquea el final `final_seba_turquesa` con `final_turquesa.png`.
- El encuentro violento tiene consecuencias en los diálogos, en la amistad y en las relaciones. No se presenta como solución romántica.

## Código y recursos

- `src/main/java/com/example/quintaverso/motor/FinalesSeba.java`: dos nuevos desenlaces calculados por eventos persistentes.
- `src/main/java/com/example/quintaverso/motor/HitosSeba.java`: consecuencias de la pelea con Rodrigo antes de las variantes de la foto.
- `src/main/java/com/example/quintaverso/RecursosVisuales.java`: soporta `cg/caceres/` y `cg/seba/`, y utiliza retratos de Kelly, Amira, Javi, Feña, Richie y Rodrigo.
- `src/main/resources/historias/seba/{hitos,fena,finales}.json`: diálogos, elecciones, ramas, CG y estados nuevos.
- `src/main/resources/imagenes/personajes/fena/`: alias compatible con sistemas sin UTF-8 para las imágenes originales de `feña/`. Se conserva la carpeta original.
- `src/test/java/com/example/quintaverso/motor/TemporadaSebaTest.java`: pruebas para 13 finales, 2 naranjas, turquesa, imágenes y el capítulo especial de Rodrigo.

## Validación en entorno de preparación

- JSON: parseo y enlaces a escenas válidos en las temporadas de Cáceres (205 escenas) y Seba (88 escenas).
- CG: 21 escenas con CG declarados, todos los archivos presentes y PNG íntegros.
- Compilación con `javac` Java 21: clases del motor independientes de JavaFX y `RecursosVisuales`.
- Harness Java: resultados para los 13 finales, evento con Rodrigo, sprites originales, CG de Seba y compatibilidad CG de Cáceres.
- **Pendiente por verificar en IntelliJ/Windows**: `mvn clean test` y la interfaz JavaFX en vivo. El entorno de preparación no contiene Maven ni las dependencias JavaFX.

No se alteraron los JSON de la temporada de Cáceres ni su progreso guardado.
