# QUINTAVERSO — Remaster visual y auditoría de finales

## Alcance de la actualización
La historia, los eventos, la progresión de calendario, las contraseñas, las estadísticas y el formato de los guardados originales no se modificaron.

### Interfaz
- Estilo visual oscuro moderno (morado, lavanda y rosa).
- Tarjetas de temporadas diferenciadas por acento.
- Tipografía del sistema `Segoe UI` para Windows (fallback del sistema en otros SO).
- Botones de elección diferenciados de Continuar.
- Caja de diálogo con contraste, bordes y espaciado renovados.
- Sombras suaves sobre los fondos para facilitar la lectura.
- El CG de un final se muestra sin deformación y puede abrirse desde la pantalla final mediante «Ver splash completo».

### Encuadres automáticos (ambas campañas)
El menú superior de cada escena incluye **Plano automático / Primer plano / Cuerpo completo**. 
- Automático: poses neutrales de cuerpo completo; expresiones fuertes muestran rostro y torso.
- Primer plano: recorte manual para mirar ojos y gestos.
- Cuerpo completo: imagen sin recorte para poses y vestuario.
- Los archivos PNG originales no se recortan ni se duplican.
- Los controles de encuadre no se guardan con la partida: al abrir otra escena vuelven al modo automático.

Se corrigió además el contagio de emociones: Seba no adopta automáticamente la expresión del personaje que habla. La expresión del hablante se obtiene mediante su propio diálogo y sprite.

### CG de Seba: correspondencia oficial
| Final | Ruta `imagenes/cg/seba/` | Descripción |
|---|---|---|
| Verde | `final_verde.png` | Seba y Kelly felices |
| Azul | `final_azul.png` | Seba y Kelly reconstruyen confianza |
| Gris | `final_gris.png` | Kelly rompe con Seba; Amira aparece |
| Violeta | `final_violeta.png` | Seba y Amira juntos |
| Rojo | `final_rojo.png` | Seba y Amira pillados; Kelly furiosa; Rodrigo riéndose |
| Celeste | `final_celeste.png` | Seba y Javi comiendo ramen |
| Naranja A | `final_naranja_sebaxfena.png` | Seba y Feña; Richie triste |
| Naranja B | `final_naranja_richiexfena.png` | Richie y Feña; Seba feliz con ellos en un picnic |
| Turquesa | `final_turquesa.png` | El combo de Richie |
| Negro | `final_negro.png` | El harem colapsó |
| Blanco | `final_blanco.png` | Seba con sus amigos |
| Rosa secreto | `final_rosa_secreto.png` | Cambio de estética de Seba |
| Amarillo oculto | `final_amarillo.png` | Kelly nunca descubre la verdad |

**Reparaciones en recursos:** `final_rojo.png` estaba codificado como JPEG pese a su extensión PNG; fue normalizado a PNG. Se generó y asignó `final_amarillo.png`, que faltaba.

### Comprobar antes de jugar
1. Abrir el proyecto en IntelliJ con JDK 21 y recargar Maven.
2. Ejecutar `mvn test` o **Maven > Lifecycle > test**.
3. Ejecutar `python tools/validar_finales_seba.py` para verificar enlaces y PNG sin dependencias externas.
4. Ejecutar el juego. Comprobar menú, diálogos y opción de plano con resoluciones 1366x768 y 1600x900.
5. Acceder a las rutas de finales y comprobar los splash sin recortar (botón «Ver splash completo»).

### Alcance de la validación efectuada
- 13 CG de Seba asignados a sus 13 escenas finales.
- 351 escenas de ambas campañas analizadas: CG y fondos declarados presentes.
- Archivos PNG de recursos revisados: sin firmas incorrectas tras la reparación.
- Sintaxis Java: verificación de 31 archivos mediante analizador de Java 21, OK.
- **Pendiente de ejecutar en IntelliJ:** Maven, pruebas JUnit e inspección gráfica real (JavaFX/Maven no disponibles en el entorno de edición).
