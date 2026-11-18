# QUINTAVERSO v0.9 — Temporada 03: SEBA

**Subtítulo:** El cagazo legendario  
**Protagonista:** Sebastián («Seba»)  
**Duración:** 30 días, 3 turnos diarios  
**Desenlaces:** **13** (incluidos los dos naranjas, el turquesa y el amarillo oculto).

## Nuevo: Amira y las noches de Discord

- **Si Seba besa a Amira y NO se lo cuenta a Kelly**, Amira se niega a seguir mintiendo, lo expone delante del grupo, Kelly termina la relación y Amira establece una distancia real. En esta continuidad NO regresa después para declararle amor: su arco termina con una conversación de cierre. No se puede desbloquear el final rojo/violeta a través de este conflicto.
- **Si Seba le cuenta la verdad a Kelly**, la relación termina, se abre un proceso de responsabilidad y Amira puede aceptar una conversación el día 20. No hay confesión antes del día 23 ni romance automático: se requiere reconocer el daño y respetar los límites.
- **24 noches nuevas de Discord**, disponibles en el turno NOCHE entre los días 1 y 24, además de los episodios antiguos de Los Cabros. Si duermes antes de jugarlas, esas noches concretas se pierden.
- **26 momentos opcionales** repartidos en el calendario para evitar que los días se sientan vacíos. Se abren durante hasta tres días desde el primer desbloqueo.
- El motor utiliza **ocho poses originales de Seba**, más diálogos condicionales y su retrato secundario cuando habla otra persona.

## Cómo iniciar

1. Abre esta carpeta como **proyecto Maven** en IntelliJ IDEA.
2. Configura **Project SDK = Java 21**.
3. Espera que Maven descargue las dependencias. En el panel Maven ejecuta `Lifecycle > test` y luego `Plugins > javafx > javafx:run`. Alternativamente, desde la terminal con Maven instalado:

   ```bash
   mvn clean test
   mvn javafx:run
   ```

4. Desde el menú selecciona **TEMPORADA 03 — SEBA**.
5. Introduce la clave **`SEBA2026`**.
6. Juega el prólogo, luego realiza actividades, duerme o pasa turno desde el calendario.

**Importante:** los capítulos principales de los días 6, 10, 12, 18, 26 y 29 son obligatorios. No se pueden omitir durmiendo. Las actividades opcionales aparecen cuando corresponde y los episodios personales se desbloquean en orden.

## Cómo funcionan las rutas

- **Kelly:** empieza como pareja de Seba. La fiesta permite irse a casa, hablarle antes, dormir junto a Amira sin contacto romántico o cruzar la línea. Si Seba golpea a Rodrigo, el día 12 tiene su propio capítulo de consecuencias y Kelly termina la relación.
- **Amira:** amistad de años, atracción posible y límites. El romance se formaliza solo si la relación con Kelly terminó. Si hubo infidelidad, la historia reconoce las consecuencias y conduce a un final diferente.
- **Javi:** aparece en el día 18 como alguien a quien Seba acaba de conocer. Sus episodios románticos se habilitan cuando ya terminó con Kelly. También puede ser una amiga y aparecer en el epílogo rosa.
- **Feña:** su ruta se desarrolla después de terminar con Kelly. Tiene dos variantes naranjas: Seba + Feña con Richie triste, o Feña + Richie con Seba feliz en un picnic como amigo. Si Seba provoca una pelea con Richie, hay un desenlace turquesa.
- **Estilo:** ruta de autodescubrimiento a través de filtros, ropa y un concurso de cosplay. Puede desbloquear un final secreto sin convertirlo en un castigo ni una broma a costa de Seba.
- **Amigos y diario:** ofrecen escenas adicionales con Kevin, Cáceres, Jairo y Rodrigo, o reflexiones. Las decisiones modifican estadísticas.

## Finales (contiene spoilers)

| ID | Final | Qué lo activa |
|---|---|---|
| `final_seba_verde` | El Seba fiel | Se fue de la fiesta sin meterse en líos y eligió construir con Kelly |
| `final_seba_azul` | Una segunda oportunidad | Durmió cerca de Amira sin engaño, habló con Kelly y acordaron recuperar la confianza |
| `final_seba_gris` | La noche que lo arruinó todo | La relación terminó y no se repararon sus mentiras |
| `final_seba_violeta` | Siempre fuiste tú | Terminó con Kelly, desarrolló el vínculo con Amira y eligió conocerla románticamente sin infidelidad |
| `final_seba_rojo` | El cagazo legendario | Hubo infidelidad y luego Seba y Amira comenzaron una relación |
| `final_seba_celeste` | La indicada | Terminó con Kelly, conoció a Javi y se comprometió a una nueva relación |
| `final_seba_naranja` | Seba y Feña | Terminó con Kelly, habló con Richie, completó la ruta de Feña y ambos eligieron estar juntos; Richie se va triste |
| `final_seba_naranja_picnic` | Amigos bajo el sol | Feña y Richie son pareja; Seba disfruta su amistad con ambos en un picnic |
| `final_seba_turquesa` | El combo de Richie | Seba provoca y empuja a Richie; Richie le pega y su amistad queda dañada |
| `final_seba_harem` | El harem colapsó | Eligió sostener dos historias secretas en el capítulo del día 26 |
| `final_seba_blanco` | El soltero profesional | Eligió estar solo y reorganizar su vida |
| `final_seba_femboy` | Femboy supremo | Desbloqueó la ruta del estilo y decidió expresarse así en el día 26 |
| `final_seba_amarillo` | Kelly nunca lo pilló | Durmió junto a Amira, Rodrigo no avisó, Seba eligió callar y seguir con Kelly |

### Guía rápida: final rosa

1. A partir del día 4, juega la categoría **🌸 Estilo**.
2. Elige las opciones que impliquen **probar el estilo por decisión propia** en los episodios 1, 2, 3 y 4; los episodios siguen un orden.
3. En el episodio 2 acepta participar en cosplay. En el 4 muéstrale una foto a un amigo de confianza. Debes acumular **45 o más puntos en Estilo**.
4. En el día 26, al elegir futuro, aparecerá **«Mostrar mi nuevo estilo sin pedir permiso 🌸»**. Esa elección desbloquea el final secreto.
5. Si al terminar con Kelly y conocer a Javi quieres explorar un desenlace alternativo, a partir del día 27 aparecerá una actividad adicional con ella.

### Guía rápida: final amarillo oculto

1. En la fiesta del día 10, elige **dormir al lado de Amira** (sin contacto romántico).
2. Cuando Rodrigo encuentre a ambos, pídele **borrar la foto**.
3. En el capítulo de consecuencias del día 12, elige **no contárselo a Kelly**.
4. En el día 26, elige **seguir con Kelly** aunque siga sin saberlo. Es un final narrativamente ambiguo, no un premio por mentir.

## Organización del código

| Archivo | Función |
|---|---|
| `motor/HitosSeba.java` | Despacha los capítulos importantes en el orden correcto |
| `motor/GestorEventosSeba.java` | Determina las actividades y restricciones de la campaña |
| `motor/FinalesSeba.java` | Resuelve los trece desenlaces por decisiones reales |
| `motor/GestorHistoria.java` | Carga `/historias/seba/*.json` sin mezclar temporadas |
| `motor/MotorCalendario.java` | Pantalla, tiempo, autoguardado, hitos y finales por temporada |
| `src/main/resources/historias/seba/` | 146 escenas, diálogos, decisiones y reacciones |
| `src/test/java/.../TemporadaSebaTest.java` | Pruebas de reglas, desbloqueos y finales |

## Partidas y recursos visuales

- Las partidas nuevas de Seba se guardan en `~/.quintaverso/partidas/seba-v06-auto.json`, con ranuras manuales `seba-v06-slot-{1,2,3}.json`. El sufijo `v06` permanece por compatibilidad con la estructura de guardado preexistente.
- **No se sobrescriben las partidas de Cáceres.** Tampoco se altera la carpeta de historias de Cáceres.
- Los **sprites originales que incluiste en el RAR** ahora están conectados para Kelly, Amira, Javi, Feña, Richie y Rodrigo. La carpeta `personajes/fena/` es un alias de `personajes/feña/` para que Java pueda encontrar los PNG incluso si el sistema usa una configuración regional sin UTF-8. Los CG quedan en `src/main/resources/imagenes/cg/caceres/` y `src/main/resources/imagenes/cg/seba/`; no en una carpeta `cfg`.
- El CG se activa con `"cg": "seba/nombre_archivo.png"` en la escena JSON. En los finales el juego vuelve a mostrarlo como fondo de la pantalla final.
- El final rojo ahora utiliza el CG `seba/final_rojo.png` ya existente en tu RAR. El final amarillo todavía no tiene un CG propio asociado.
- El juego es **ficción** con personajes adultos inspirados libremente en historias. Los acontecimientos deben tratarse como guion, no como hechos sobre personas reales.

## Comprobación técnica

Se validaron JSON, enlaces entre IDs, existencia de los fondos y compilación con Java 21 de las clases de reglas independientes de JavaFX. Se añadieron pruebas de continuidad de Amira, sus rutas incompatibles y el calendario Discord. No se ejecutó la interfaz JavaFX completa en este entorno.

**Limitación:** en el entorno donde se preparó esta entrega no hay Maven ni dependencias JavaFX disponibles, por lo que **no pudo ejecutarse la interfaz gráfica ni el ciclo completo de pruebas Maven**. Ejecuta `mvn clean test` desde IntelliJ o tu equipo y comprueba la interfaz antes de distribuir el juego.

## Guía de las escenas nuevas (v0.8)

### La pelea con Rodrigo

1. Día **10**, elige «Acostarse al lado de Amira solo para dormir».
2. En «La foto de Rodrigo», elige «Perder los estribos y pegarle un combo a Rodrigo por hocicón».
3. Se reproduce automáticamente `seba_fiesta_combo_rodri`, usando `imagenes/cg/seba/comborodri.png`.
4. Día **12** se abre `seba_post_combo`: Kelly enfrenta lo ocurrido y termina la relación. Las decisiones afectan cómo Seba asume su responsabilidad.

### Naranja: Seba con Feña y Richie triste

1. Termina la relación con Kelly sin mantener doble vida.
2. Completa los episodios de **Feña**, eligiendo cuidar la confianza y hablar con Richie en el episodio 3.
3. En el día **26**, selecciona «Hablar con Feña y Richie sin competir por nadie».
4. En la escena siguiente elige «Decirle a Feña que quiero intentarlo con ella, sin humillar a Richie».
5. CG: `seba/final_naranja_sebaxfena.png`.

### Naranja alternativo: Feña y Richie juntos; Seba feliz en el picnic

1. Sigue la ruta de Feña y termina la relación con Kelly.
2. En el día **26** elige la conversación respetuosa con Feña y Richie.
3. Elige «Aceptar que Feña eligió a Richie y seguir siendo amigo de ambos».
4. CG: `seba/final_naranja_richiexfena.png`.

### Turquesa: el combo de Richie

1. Termina la relación con Kelly y mejora la afinidad con Feña mediante los episodios opcionales.
2. En el día **26**, elige «Provocar a Richie por Feña y enfrentarlo cara a cara».
3. En el enfrentamiento, puedes **calmar la situación** (picnic) o **provocarlo y empujarlo** (final turquesa).
4. Si se desata la pelea, se muestra la CG `seba/final_turquesa.png`, y la misma ilustración vuelve a aparecer al terminar el calendario.

**Nota sobre guardados existentes:** las partidas antiguas siguen cargando. Si ya alcanzaste el día 26 en tu partida guardada, empieza una nueva partida para elegir las rutas nuevas; los eventos ya completados no se repiten automáticamente.
