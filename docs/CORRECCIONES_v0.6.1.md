# QUINTAVERSO v0.6.1 — Corrección de inicio y menú

## Qué se corrigió

1. **Error al empezar/continuar la temporada:** un `Alert(CONFIRMATION)` traía `OK` y `CANCEL` por defecto, y `MotorCalendario.iniciar()` añadía `CANCEL` por segunda vez. El `ButtonBar` fallaba con `Children: duplicate children added`. Ahora se limpian los tipos de botón antes de agregar los específicos.
2. **Botón repetido de cargar partida:** se retiró el botón inferior `Continuar o cargar partida de Cáceres` de la pantalla de temporadas.
3. **Inicio más claro:** el botón de la tarjeta de Cáceres dice `Jugar / Continuar`. El diálogo de acceso y el selector de partida ya existentes siguen siendo los puntos de entrada.

## Prueba recomendada en Windows

1. Abre el proyecto en IntelliJ como proyecto Maven, ejecuta `clean test` y `javafx:run`.
2. En Cáceres, inicia una partida nueva. Vuelve al menú y entra otra vez para comprobar la ventana **Continuar autoguardado / Cargar ranura manual / Importar partida v0.5 / Nueva partida** (según los guardados que existan).
3. Comprueba que la alerta no falla, y prueba **Guardar partida** y **Cargar partida** durante una conversación.
4. Activa F11 y cambia de escena. Revisa que el modo pantalla completa se conserve.

**Conservación de datos:** no es necesario borrar las partidas de `~/.quintaverso/partidas`. Esta actualización solo modifica el selector de acceso y el menú.

**Verificación:** corrección inspeccionada en el código; la interfaz JavaFX debe probarse en Windows.
