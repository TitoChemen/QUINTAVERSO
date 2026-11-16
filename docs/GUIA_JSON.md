# Guía para editar diálogos, decisiones y prompts

El juego lee escenas de `src/main/resources/historias/caceres/*.json` usando Jackson. Cada archivo es una **lista** JSON de escenas.

## Ejemplo mínimo de escena

```json
{
  "id": "ejemplo_cita",
  "titulo": "Una invitación distinta",
  "lugar": "Café de Viña del Mar",
  "direccionVisual": "Plano medio de ambos adultos en una mesa, tonos cálidos, música suave.",
  "promptImagen": "Anime visual novel scene, fully clothed adult couple seated at a cafe in Viña del Mar, warm cinematic light, 16:9, no text",
  "dialogos": [
    {"personaje": "Narrador", "texto": "La lluvia golpea la ventana."},
    {"personaje": "Cony", "texto": "¿Vamos a salir o te vas a quedar pensando toda la tarde?"}
  ],
  "decisiones": [
    {
      "texto": "Aceptar sin hacer promesas",
      "siguiente": "ejemplo_respuesta",
      "efectos": {"cony": 4, "confianza_cony": 6},
      "eventos": ["acepto_invitacion"]
    },
    {
      "texto": "Decir que hoy no puede",
      "efectos": {"honestidad": 3}
    }
  ]
}
```

**Importante:** una escena enlazada como `ejemplo_respuesta` debe existir en el conjunto de archivos JSON. Si `siguiente` no existe y no quedan diálogos, el motor vuelve al calendario (cada actividad consume un turno). No incluyas comentarios `//` en JSON.

## Propiedades

- `id`: identificador único y estable.
- `titulo`, `lugar`: cabecera de la escena.
- `dialogos`: lista de `{ "personaje": "...", "texto": "..." }`.
- `decisiones`: lista de opciones, cada una con `texto` y opcionalmente `siguiente`, `efectos`, `eventos`, `desactivarEventos`, `condiciones`.
- `siguiente`: enlace automático si no hay decisión, como en el prólogo.
- `direccionVisual`: instrucciones de planos, poses, gestos, ambiente (se muestran en el juego al abrir panel de dirección).
- `promptImagen`: texto listo para copiar y usar con un modelo de ilustración anime. Se deja sin desnudez ni acciones sexuales gráficas.

## Estadísticas válidas

`lumi`, `cony`, `confianza_lumi`, `confianza_cony`, `honestidad`, `sospecha`, `amistad`, `claridad`. Todas van de 0 a 100.

## Condiciones de decisiones

Cada condición en una lista se aplica con lógica **AND** (todas deben cumplirse):

```json
"condiciones": [
  {"evento": "max_ruptura"},
  {"estadistica": "confianza_lumi", "minimo": 45},
  {"diaDesde": 20, "diaHasta": 30}
]
```

Usa `{"evento":"cony_pareja", "eventoActivo":false}` para exigir que una bandera esté desactivada.

`desactivarEventos` elimina banderas que podrían producir contradicciones, por ejemplo cuando se decide dejar un vínculo casual para pasar a una relación exclusiva.

## Actividades del calendario

El catálogo de actividades y fechas está en `motor/GestorEventos.java`. No basta con agregar un JSON; un evento nuevo debe ser anunciado en `disponibles(estado)` para que aparezca como botón. Los chats `lumi_1...lumi_24` y `cony_1...cony_24` se eligen por un contador guardado en `EstadoPartida.repeticiones`.

`tools/generar_guion.py` es la fuente para regenerar los archivos originales y el CSV de prompts. **Si editas un JSON manualmente, respaldalo antes de volver a ejecutar el generador porque sobrescribe los JSON.**

## Guardado / compatibilidad

La partida v0.5 se guarda como `caceres-v05.json` en la carpeta `.quintaverso/partidas` de tu usuario. La v0.4 usaba otro archivo y otra estructura. **No se migra automáticamente**: se conserva y empieza una campaña nueva en v0.5.
