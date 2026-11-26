# QUINTAVERSO 1.0 · Jairo: Entre la confianza y la tentación

- Temporada 02 habilitada con la clave existente `JAIRO2026`.
- 30 días de capítulos principales obligatorios, divididos en cuatro etapas. Los tres turnos del calendario siguen disponibles para actividades opcionales, descanso y Discord.
- 109 escenas (incluidos prólogo, variantes, 30 noches de Discord, actividades y finales), 394 intervenciones principales y 187 elecciones con reacciones.
- Las decisiones registran eventos persistentes: secretos, infidelidad, confesión, ruptura, acuerdos, citas, respeto del espacio y oportunidades perdidas. Las estadísticas no reemplazan estos requisitos.
- Doce finales con los CG originales de la carpeta `imagenes/cg/jairo`, y galería independiente de las ranuras de guardado.
- Sprites de Jairo, Mary, Ale, Andrea, Anto y Camila conectados al motor. Se mantiene el arte de las otras temporadas y los guardados anteriores. Se reutilizan los fondos disponibles; no se generaron fondos nuevos.
- Maven y el empaquetado Windows pasan a versión 1.0.0.

## Ejecutar en Windows

Abre la carpeta QUINTAVERSO en IntelliJ IDEA, selecciona JDK 21 y recarga Maven. Ejecuta `mvn clean test` y `mvn javafx:run`, o usa los objetivos equivalentes de Maven en IntelliJ. Para generar la aplicación Windows: `powershell -ExecutionPolicy Bypass -File .\empaquetar-windows.ps1`.

## Validación

Se compiló el núcleo Java sin dependencias externas y se ejecutaron 13 recorridos completos (12 finales distintos; naranja por dos vías), comprobación de recursos, galería y sprites de Jairo con `python tools/validar_jairo.py`. También se añadió una prueba JUnit para el proyecto Maven. No se ejecutó el build Maven completo ni se abrió JavaFX en este entorno porque Maven y sus dependencias no están disponibles y la descarga de repositorios está bloqueada. Se entrega código fuente, no un EXE precompilado.

## Guía de rutas (spoilers)

- Verde: fidelidad, relación vigente, sin confesión de una traición; no completar todos los requisitos del dorado.
- Dorado: presencia, escucha, independencia, privacidad, límites mutuos, crecimiento de Mary, equilibrio, autonomía, futuro, compromiso, amistad y umbrales de confianza, honestidad, madurez, amistad e inseguridad.
- Azul: confesar el error el día 19, respetar el espacio los días siguientes, reconciliarse el día 24 y no sabotear la reparación.
- Gris: cierre sin engaño, sin iniciar nueva relación y sin elegir expresamente el final soltero.
- Rojo: besar a Andrea, ocultarlo y afrontar la revelación; la agresión de Mary contra Andrea es una consecuencia negativa, no una recompensa.
- Violeta / rosa: cierre limpio con Mary, interés suficiente en Ale / Andrea, elegirla el día 23, invitarla y confirmar la conexión.
- Naranja: beso con Cami estando soltero, o confesión de la infidelidad seguida de separación, honestidad con Cami y reconexión confirmada.
- Celeste: cierre limpio, conocer a Anto, elegir su ruta y concretar la invitación con suficiente valentía.
- Turquesa: elegir la ruta de Anto, no concretar la invitación y descubrir que ella siguió adelante.
- Blanco: elegir estar solo, recuperar el tiempo propio y las amistades. También funciona como desenlace de recuperación si una relación falla y no queda una ruta romántica viable.
- Negro: coquetear en secreto con las cuatro chicas mientras se mantiene la relación con Mary y ocultarlo hasta la revelación del día 21.
