# QUINTAVERSO — Biblia visual para ilustrar con otra IA

Los JSON ya incluyen `direccionVisual` y `promptImagen` para **cada una de las 205 escenas**. El archivo `PROMPTS_VISUALES.csv` los reúne para trabajar más cómodo.

## Identidad y consistencia

Todos los personajes del juego son **adultos (20 años o más)**. La historia es ficción. Para evitar cambios de cara, ropa, color de ojos o altura entre capítulos, crea un **modelo de referencia** por personaje: vista frontal, tres cuartos, perfil y 6 expresiones, fondo plano sin objetos.

No atribuyas rasgos físicos reales que no hayas definido tú. Los prompts de escena usan descripciones generales y **no reconstruyen la apariencia real** de tus amigos.

### Ficha de Lumi

- Rasgos narrativos: joven adulta independiente, creativa, de cabello azul y lentes. A veces intensa y vulnerable cuando se enamora, pero **no debe representarse como indefensa**.
- Expresiones sugeridas: concentrada en un proyecto, riéndose con ganas, frustrada con Max, conteniendo lágrimas, pensativa frente al mar, tímidamente enamorada.
- Vestuario por situación: ropa cómoda para salir sola; chaqueta para pasear por la costanera; ropa de taller creativo.
- Luz: azul y cian para sus espacios independientes; ámbar cuando comparte un momento íntimo o cálido.
- Prompt base: `Anime visual novel character sheet, fictional adult woman age 22+, blue hair, glasses, independent creative personality, natural realistic anatomy, front 3/4 profile, six distinct expressions, consistent facial features, full body, outfit variations, plain background, no text, no nudity.`

### Ficha de Cony

- Rasgos narrativos: adulta de estatura baja/petite, segura, bromista y expresiva. Gusta de salir, coquetear y hablar claro; que el sprite transmita confianza, no un estereotipo de "chica fácil".
- Expresiones sugeridas: sonrisa pícara, carcajada, mirada desafiante, sinceridad inesperada, molestia cuando ignoran sus límites, ternura sorprendida.
- Vestuario por situación: look de fiesta, ropa casual de plaza/café, chaqueta liviana para el frío nocturno.
- Luz: rosado y ámbar para la comedia romántica; tonos neutros cuando expresa límites.
- Prompt base: `Anime visual novel character sheet, fictional short adult woman age 22+, confident playful expression, charming smile, stylish casual outfit, consistent character design, six expressions including serious boundary-setting face, full body, front 3/4 profile, no text, no nudity.`

### Ficha de Cáceres

- Rasgos narrativos: adulto de Villa Alemana que alterna nerviosismo, humor y decisiones importantes. El grupo lo molesta por sus entradas de pelo; **solo agrega rasgos físicos reales si cuentas con una referencia autorizada**.
- Expresiones sugeridas: atrapado mirando dos chats, risa incómoda, sonrisa genuina, arrepentimiento, declaración romántica y serenidad.
- Prompt base: `Anime visual novel protagonist character sheet, fictional adult Chilean man age 22+, expressive eyebrows, casual streetwear, vulnerable but humorous personality, consistent face, 6 emotional expressions, front side and 3/4 views, plain background, no text.`

### Kevin y Seba

- Adultos bromistas, cercanos y buenos aconsejando. Sus sprites deben poder cambiar **de risa brutal a tono sincero** en el mismo capítulo.
- No hay disfraz permanente de samurái para Seba.
- Prompts base: `Anime visual novel sprite sheet of a fictional adult Chilean male friend at a gaming desk, casual clothes, friendly teasing demeanor, laughing hard expression and sincere supportive expression, consistent character face, plain background, no text.`

### Jairo

- Adulto exageradamente seguro de sus consejos de citas, pero sin experiencia real; humor por sus propuestas absurdas (Excel, tutoriales, horarios). No necesita tratarse con desprecio.
- Prompts base: `Anime visual novel character sprite sheet, fictional adult male gamer, comedic overconfident expression, awkward thoughtful expression, playful body language, casual everyday outfit, consistent face, plain background, no text.`

### Max

- Adulto de la trama; sus errores se representan por incumplimientos concretos y conversaciones difíciles, no como un villano caricaturesco.
- Mantén una silueta discreta; no se necesita un aspecto físico específico si aún no está definido por el diseñador.

## Fondos recurrentes

| ID sugerida | Prompt base |
|---|---|
| `fondo_discord_noche` | `Anime visual novel background, modest Chilean adult gamer bedroom at night, dual monitors, violet light, empty room, 16:9, no text, no watermark` |
| `fondo_plaza_villa_alemana` | `Anime visual novel background, quiet small city plaza in Villa Alemana, Chile, public benches, warm street lights, dusk, 16:9, no people, no text` |
| `fondo_cafeteria_vina` | `Anime visual novel background, cozy contemporary cafe in Viña del Mar, Chile, golden hour, detailed windows and tables, 16:9, no people, no text` |
| `fondo_costanera` | `Anime visual novel background, promenade by the Pacific in Viña del Mar, Chile, sea breeze, pastel sunset, 16:9, no people, no text` |
| `fondo_terminal_valpo` | `Anime visual novel background, Valparaíso bus terminal, everyday Chilean urban details, daytime, 16:9, no people, no text` |
| `fondo_depto_cony` | `Anime visual novel background, welcoming modest living room in Chile at night, warm table lamps, quiet atmosphere, 16:9, no people, no text` |
| `fondo_cocina_manana` | `Anime visual novel background, small cozy apartment kitchen, two coffee cups on table, early sunlight, 16:9, no people, no text` |
| `fondo_mirador_noche` | `Anime visual novel background, hillside viewpoint overlooking city lights in Valparaíso/Viña del Mar, starry evening, cinematic, 16:9, no people, no text` |

## Pipeline sugerido

1. Genera las fichas maestras de personajes (turnarounds + expresiones). Guarda un ID / seed / LoRA / referencia consistente por personaje.
2. Genera **fondos sin personajes** usando la tabla de arriba.
3. Genera sprites independientes con **fondo transparente** y por emoción para superponer en JavaFX.
4. Para ilustraciones especiales, copia desde `PROMPTS_VISUALES.csv` o desde el panel 🎨 del juego; ajusta el prompt con el diseño maestro de los personajes.
5. Para la escena de Cony de noche, ilustra **miradas, conversación y el cierre de la puerta**; el encuentro íntimo ocurre fuera de cámara. Así la imagen mantiene continuidad sin escenas sexuales gráficas.
6. Nombra los recursos por ID estable (`lumi_sonrie.png`, `cony_seria.png`, `fondo_cafeteria_vina.png`) y luego conéctalos al juego sin cambiar la lógica.

## Convención sugerida de imágenes

`src/main/resources/imagenes/fondos/` para fondos (1920×1080 o 1600×900, 16:9) y `src/main/resources/imagenes/sprites/` para personajes PNG transparentes. **Esas carpetas están reservadas para arte futuro; v0.5 funciona sin imágenes**.
