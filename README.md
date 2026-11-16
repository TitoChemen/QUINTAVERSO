# QUINTAVERSO v0.5 — Edición gráfica integrada

**Esta versión muestra los 10 fondos, sprites/retratos y 8 CG en la temporada Cáceres.**
Abre `docs/INTEGRACION_GRAFICA.md` para conocer las modificaciones y ejecutarla
con Java 21 + Maven desde IntelliJ IDEA. Las 205 escenas tienen un fondo asignado.

> Este es un proyecto distinto del RAR original; se conservaron los diálogos,
> las decisiones y los enlaces narrativos. Los retratos de Kevin siguen pendientes.

---

# QUINTAVERSO v0.5 — Cáceres: Doble Tentación

**Motor de novela visual en Java 21, JavaFX 21 y Maven.** Novela romántica humorística para público adulto, ambientada ficticiamente en la Quinta Región de Chile. Todos los personajes del guion son adultos. La ficción no describe hechos reales de las personas que inspiraron los personajes.

## Cambios principales frente a v0.4

- 30 días × 3 turnos = **90 turnos** en vez de 14 días.
- **205 escenas, 817 líneas de diálogo y 334 alternativas de decisión** (incluyendo los 8 epílogos).
- **21 acontecimientos únicos**, con requisitos por día, estadísticas y decisiones previas.
- 24 conversaciones diferentes de Lumi, 24 de Cony y 18 noches diferentes de Discord; 12 reflexiones y 12 paseos. Cada actividad consume su siguiente conversación **sin repetirse hasta agotar las nuevas**.
- Tras agotar el catálogo, se ofrecen cuatro microescenas de rutina sin puntos extras. No hay bloqueo: **Pasar turno** y **Dormir hasta mañana** siempre funcionan desde el calendario.
- 8 estadísticas: vínculo Lumi, vínculo Cony, confianza Lumi, confianza Cony, honestidad, sospecha, amistad y claridad.
- 8 finales diferenciados: Lumi, Cony pareja, Cony casual, acuerdos no exclusivos conversados, descubierto, crecimiento, Lumi decide seguir con Max y final calvo.
- Las rutas requieren **hechos de la historia** y conversaciones de límites. **La afinidad por sí sola nunca autoriza un romance ni un encuentro íntimo.**
- Cada escena contiene notas de dirección visual y un prompt ilustrativo para otra IA. En el lector de escenas, abre el panel **🎨 Dirección visual y prompt** y pulsa **Copiar prompt visual**.
- Guardado automático en `caceres-v05.json`; **no sobrescribe el guardado de la v0.4**.
- `module-info.java` está correctamente ubicado en `src/main/java/module-info.java`, no dentro del paquete.

## Instalar sin estropear la v0.4

1. **Haz una copia de tu QUINTAVERSO v0.4** o guarda un commit Git.
2. Descomprime el ZIP **en una carpeta nueva**, por ejemplo `QUINTAVERSO-v0.5/`.
3. Desde IntelliJ IDEA abre **esa carpeta como proyecto**, espera a la importación de Maven y verifica Project SDK = **Java 21**.
4. Abre el panel **Maven → QUINTAVERSO → Lifecycle → test**; si pasa, ejecuta **Maven → QUINTAVERSO → Plugins → javafx → javafx:run**.
5. Si los módulos aparecen rojos pero Maven compila, usa **Reload All Maven Projects**. La configuración de Maven del IDE puede ser correcta aunque PowerShell no reconozca el comando `mvn`.

**Este ZIP NO incluye Maven Wrapper (`mvnw.cmd` ni `mvnw`).** No ejecutes esos archivos si no los has agregado tú. Usa Maven integrado desde el panel IntelliJ, o instala Maven en el sistema.

### Ubuntu / Kubuntu (si tienes Java 21 y Maven instalados)

```bash
java -version
mvn -version
mvn clean test
mvn javafx:run
```

Si careces de Java 21 o Maven, en una distribución apt compatible (por ejemplo Ubuntu con esos paquetes disponibles):

```bash
sudo apt update
sudo apt install openjdk-21-jdk maven
```

### Windows PowerShell

Si **Maven está instalado globalmente y está en PATH**:

```powershell
mvn clean test
mvn javafx:run
```

Si el comando `mvn` dice "no se reconoce", **no significa que el POM falle**: compila desde el panel Maven integrado de IntelliJ, igual que en v0.4.

## Contraseñas de las temporadas

- Cáceres: `QUINTA2026` o `CACERES2026`.
- Jairo: `JAIRO2026` (temporada no estrenada).
- Seba: `SEBA2026` (temporada no estrenada).

Son contraseñas de juego visibles en el código; **no sirven como protección antipiratería**.

## Cómo jugar

1. Elige Cáceres, ingresa la clave y juega el prólogo.
2. En el calendario selecciona una actividad. Lee cada línea; al final eliges una respuesta. Los actos del personaje producen efectos y se anotan en el historial.
3. **Los eventos con iconos especiales se juegan una sola vez**; no ignorarlos si quieres seguir tramas importantes. Puedes pasar tiempo con amigos, caminar, reflexionar, hablar con Lumi o Cony.
4. Usa **Pasar turno** para cambiar de mañana/tarde/noche, o **Dormir hasta mañana** para ir directamente al próximo día. Ambas opciones funcionan incluso si ningún evento especial se ha desbloqueado.
5. Cuando termina el día 30 se reproduce el desenlace determinado por las **decisiones registradas** y el estado.
6. Regresa al menú para guardar automáticamente o reinicia al terminar para recorrer rutas distintas.

## Guardado de la partida

- Windows: `C:\Users\TU_USUARIO\.quintaverso\partidas\caceres-v05.json`
- Ubuntu: `~/.quintaverso/partidas/caceres-v05.json`

La v0.4 usaba `caceres.json`: seguirá intacto. No renombres el archivo antiguo para forzar migración: hay eventos y estadísticas diferentes.

## Archivos importantes

```text
QUINTAVERSO-v0.5/
├── pom.xml
├── README.md
├── docs/
│   ├── DISENO_HISTORIA.md
│   ├── GUIA_JSON.md
│   └── PROMPTS_VISUALES.csv      <- 205 prompts y dirección de cámara
├── tools/
│   ├── generar_guion.py        <- fuente editable del guion completo
│   └── validar_guion.py        <- valida escenas, enlaces, recursos
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── module-info.java
    │   │   └── com/example/quintaverso/
    │   │       ├── HelloApplication.java
    │   │       ├── MotorNarrativo.java
    │   │       ├── Launcher.java
    │   │       ├── HelloController.java
    │   │       ├── motor/
    │   │       │   ├── CalendarioJuego.java
    │   │       │   ├── Condicion.java
    │   │       │   ├── Decision.java
    │   │       │   ├── Dialogo.java
    │   │       │   ├── Escena.java
    │   │       │   ├── EstadoPartida.java
    │   │       │   ├── GestorEventos.java
    │   │       │   ├── GestorHistoria.java
    │   │       │   └── MotorCalendario.java
    │   │       ├── seguridad/GestorAccesos.java
    │   │       └── guardado/GestorGuardado.java
    │   └── resources/
    │       ├── quintaverso.css
    │       └── historias/caceres/
    │           ├── prologo.json
    │           ├── lumi.json
    │           ├── cony.json
    │           ├── amigos.json
    │           ├── otros.json
    │           ├── eventos.json
    │           └── finales.json
    └── test/java/com/example/quintaverso/motor/
        ├── CalendarioJuegoTest.java
        ├── EstadoPartidaTest.java
        └── GestorHistoriaTest.java
```

### Si quieres reescribir el guion

Los JSON son la fuente que ejecuta el juego. Edita directamente cualquiera de ellos, o modifica `tools/generar_guion.py` y vuelve a generar con `python3 tools/generar_guion.py`. **Ojo:** regenerar sobrescribe los JSON generados y `PROMPTS_VISUALES.csv`. No edites simultáneamente el generador y los JSON sin controlar los cambios.

Las escenas se pueden ampliar sin tocar Java siempre que se respeten las IDs, la estructura de decisiones y las reglas de desbloqueo. Los eventos nuevos **sí** requieren registrarse en `motor/GestorEventos.java`. Ver `docs/GUIA_JSON.md`.

## Verificaciones

- `python3 tools/validar_guion.py` comprueba los archivos JSON, los destinos de escenas, la existencia de 90 conversaciones principales, los metadatos visuales y todos los eventos referenciados por Java.
- `mvn clean test` ejecuta las pruebas automatizadas JUnit cuando dispones de Maven y las dependencias.
- `mvn javafx:run` prueba la interfaz real.

**Nota de verificación de esta entrega:** el núcleo Java sin dependencias externas fue compilado con `javac --release 21`, y el calendario completó 90 turnos en una prueba de ejecución; los JSON y enlaces fueron validados. No pudo ejecutarse Maven/JavaFX aquí porque no había instalación de Maven ni acceso a los repositorios de dependencias. La compilación completa y la UI deben confirmarse en IntelliJ.

## Sobre el contenido de adultos y las ilustraciones

Hay diálogos directos sobre deseo, acuerdos, intimidad consensuada y un encuentro que ocurre **fuera de cámara** con una transición a negro. No hay narración sexual gráfica. Los prompts ilustrativos están orientados a fondos, expresiones, besos y situaciones románticas **sin desnudez**. El guion es ficticio; antes de usar la apariencia o la vida íntima de personas reales en contenido público, obtén su permiso.
