"""Genera el guion editable de QUINTAVERSO v0.5. Python 3, sin librerías externas.
Historias ficticias protagonizadas exclusivamente por personajes adultos.
"""
import json, pathlib, csv
ROOT = pathlib.Path(__file__).resolve().parents[1]
DEST = ROOT / 'src/main/resources/historias/caceres'
DEST.mkdir(parents=True, exist_ok=True)
# Si regeneras el guion, conserva los archivos gráficos asignados manualmente
# a las mismas IDs. Sin esto el generador borraría fondo/cg de cada escena.
VISUALES_PREVIOS = {}
for archivo_previo in DEST.glob('*.json'):
    try:
        for previo in json.loads(archivo_previo.read_text(encoding='utf-8')):
            if isinstance(previo, dict) and previo.get('id'):
                VISUALES_PREVIOS[previo['id']] = {k: previo[k] for k in ('fondo', 'cg') if previo.get(k)}
    except (ValueError, OSError):
        pass
scenes = {k: [] for k in ['prologo','lumi','cony','amigos','otros','eventos','finales']}

def D(personaje, texto): return {'personaje':personaje,'texto':texto}
def C(txt, next=None, effects=None, flags=None, off=None, cond=None):
    data = {'texto':txt}
    if next is not None: data['siguiente']=next
    if effects: data['efectos']=effects
    if flags: data['eventos']=flags
    if off: data['desactivarEventos']=off
    if cond: data['condiciones']=cond
    return data

def visual(lugar, asunto, emotion='cinematic, sincere and expressive'):
    return (f'Anime visual novel background, fictional adult characters aged 20+, {lugar}. '
            f'{asunto}. Composition: 16:9, cinematic framing, natural gestures, detailed anime illustration, '
            f'{emotion}, no text, no watermark, no nudity.')

def S(grupo, ident, titulo, lugar, dialogos, *, choices=None, nxt=None, direccion=None, prompt=None):
    if ident in {s['id'] for g in scenes.values() for s in g}: raise ValueError('repetida '+ident)
    data = {'id':ident,'titulo':titulo,'lugar':lugar,'dialogos':[D(*x) for x in dialogos],
            'direccionVisual':direccion or f'Plano medio en {lugar}. Mostrar gestos, pausas y lenguaje corporal; la emoción debe coincidir con los diálogos.',
            'promptImagen':prompt or visual(lugar, titulo)}
    if choices is not None: data['decisiones']=choices
    if nxt: data['siguiente']=nxt
    scenes[grupo].append(data)
    return data

# PRÓLOGO
S('prologo','prologo_inicio','El último pelo de la esperanza','Villa Alemana · Discord, 23:47',[
 ('Narrador','La pantalla ilumina la pieza de Cáceres. Hay dos conversaciones abiertas, tres amigos conectados a Discord y cero ideas claras sobre lo que siente.'),
 ('Kevin','Oe, Cáceres, ¿cómo tenís dos chats abiertos y menos capacidad de decidir que un semáforo desenchufado?'),
 ('Seba','Hermano, no confundái que te hablen bonito con que ya tenís dos pololas. Pensá primero.'),
 ('Jairo','Yo les mandaría un Excel con los horarios, pa no equivocarme.'),
 ('Kevin','JAJAJA CÁLLATE, JAIRO. Tú no sabís ni cuándo se manda un buenas noches.'),
 ('Cáceres','Ya, ¿van a darme un consejo o solo van a webearme toda la noche?'),
 ('Seba','Las dos, po. Pero en serio: ni la Lumi ni la Cony son trofeos. Habla claro.')
], nxt='prologo_promesa', direccion='Discord visto como interfaz ficticia sin logos oficiales. Protagonista solo a su escritorio, luz violeta del monitor.', prompt=visual('bedroom in Villa Alemana late at night', 'adult young man at a desk, worried but amused, violet monitor light, phone displaying two unread notifications'))
S('prologo','prologo_promesa','El consejo que nadie pidió','Discord',[
 ('Kevin','No podís arreglar la relación de la Lumi por ella. Y tampoco podís esperar que la Cony quiera lo mismo que tú.'),
 ('Cáceres','Eso es lo que me complica. Con una me dan ganas de cuidarla, con la otra... puta, me prende la forma que tiene de hablar.'),
 ('Jairo','Yo digo que te hagai el difícil. Lo vi en un video.'),
 ('Seba','Por eso mismo no tenís polola, Jairo.')
],choices=[
 C('«Voy a preguntar qué quiere cada una, sin vender humo.»','prologo_dos_chats',{'honestidad':8,'claridad':8,'amistad':3},['prometi_ser_claro']),
 C('«Improviso. Si la cago, me disculpo.»','prologo_dos_chats',{'sospecha':8,'claridad':-5},['prometi_improvisar']),
 C('«Por ahora quiero conocerlas, nada más.»','prologo_dos_chats',{'claridad':6,'amistad':3},['prometi_ir_lento'])
])
S('prologo','prologo_dos_chats','Dos notificaciones, dos realidades','Habitación de Cáceres',[
 ('Narrador','El teléfono vibra. Lumi manda una foto de su cuaderno lleno de ideas para un proyecto. Cony envía un audio entre risas.'),
 ('Lumi (mensaje)','Hoy salí sola a hacer mil cosas, me encantó. ¿Después te puedo contar una tontera?'),
 ('Cony (audio)','Oye, wn, me acordé de ti. ¿Cuándo salimos a hacer algo entretenido? Nada de entrevista de pega, eso sí.'),
 ('Cáceres','Ya empezó el QUINTAVERSO, ctm.')
],choices=[
 C('Responder a Lumi y preguntarle por su proyecto.','prologo_cierre',{'lumi':6,'confianza_lumi':5},['primera_lumi']),
 C('Responder a Cony con una talla y proponer un café.','prologo_cierre',{'cony':6,'confianza_cony':5},['primera_cony']),
 C('Responder a ambas con calma, sin prometer nada.','prologo_cierre',{'honestidad':5,'claridad':4},['primera_ambas'])
])
S('prologo','prologo_cierre','Comienza el calendario','Lunes · Villa Alemana',[
 ('Narrador','Al otro día, el calendario está completamente libre. Ni Lumi ni Cony saben qué pasará. Cáceres tampoco.'),
 ('Narrador','Durante 30 días elegirás cómo ocupar mañana, tarde y noche. Una buena conversación no garantiza un romance; una mala decisión puede cerrar puertas.'),
 ('Kevin (mensaje)','Y si te mandai una cagá, mínimo no nos mintai al contarla. KASDKASDK.'),
 ('Narrador','Los personajes son adultos; los vínculos, deseos y límites se deben conversar. El juego no supone consentimiento por subir una barra de afinidad.')
])

# CONVERSACIONES COTIDIANAS — 24 completamente distintas por persona. Cada una se consume en orden.
lumi_topics = [
 ('Autonomía de un lunes','Mi agenda está llena y me encanta. Me carga cuando alguien cree que estar de pareja significa hacer todo juntos.','Eso no cambia que a veces quiera pasarme una tarde entera abrazada a alguien.','No tienes que elegir entre independencia y cariño.'),
 ('La playlist imposible','Hice una playlist para caminar sola por Viña. Pasé de música triste a perreo en tres canciones.','El algoritmo cree que soy cuatro personas distintas.','Pásame esa playlist, quiero conocer esas cuatro versiones.'),
 ('Desayuno sin apuro','Hoy me preparé desayuno antes de revisar el celular. Parece una estupidez, pero me hizo bien.','Cuando me enamoro me olvido de esos rituales y después me enojo conmigo misma.','Que alguien te guste no tiene por qué borrarte la mañana.'),
 ('El mural','Pasé por un mural precioso y me quedé mirándolo como diez minutos.','A veces quisiera fotografiar esas cosas y armar una colección.','Hagamos una ruta fotográfica cuando quieras.'),
 ('La tía del almacén','La señora del almacén me preguntó por Max antes de preguntarme por mí.','Me dio rabia. Como si yo no tuviera nombre propio.','No eres un apéndice de nadie, po.'),
 ('Lluvia sobre la micro','Me mojé hasta los calcetines. Igual me reí cuando se me dio vuelta el paraguas.','Una cabra me ofreció compartir el suyo. La gente sorprende.','Las mejores anécdotas salen cuando todo sale mal.'),
 ('El proyecto que empieza','Estoy armando una idea que me entusiasma y no necesito permiso para intentarla.','Aunque me encantaría que alguien se interesara de verdad en escucharla.','Te escucho, cuenta desde el principio.'),
 ('La foto que no subió','Me saqué una foto que me gustó y la borré por pensar demasiado en qué dirían.','Después dije: ¿desde cuándo me importa tanto?','Si te gustaba a ti, ya era suficiente motivo.'),
 ('La comida favorita','Un día te voy a mostrar un local que tiene la mejor comida del planeta.','Y no, no te voy a decir dónde; si te cuento, pierde la gracia.','Me gustan los misterios que terminan en comida.'),
 ('La tarde de silencio','Hoy no estaba triste ni enojada. Solo no quería conversar con nadie.','Antes sentía que tenía que justificar eso.','Puedes decirme que necesitas espacio y listo.'),
 ('El miedo a necesitar','A mí me cuesta reconocer que extraño a alguien. Me siento demasiado vulnerable.','Después cuando estoy enamorada me pongo intensa y quiero saber todo de él.','Extrañar es normal; perderte en la otra persona no te hace bien.'),
 ('Audífonos y costa','Caminé por la costa sin apurarme. Me dio una paz tremenda.','Quiero repetirlo sin mirar el celular a cada rato.','Te puedo acompañar o puedes ir sola, como prefieras.'),
 ('El recuerdo divertido','Una vez me confundí de micro y terminé lejísimos. Igual encontré un café lindo.','Tal vez tengo suerte para perderme.','Entonces yo me encargo de llevar un mapa.'),
 ('Una llamada pendiente','Me cuesta decir que algo me duele sin sentir que exagero.','No quiero volver a quedarme callada para evitar un conflicto.','Podés hablar sin pedir disculpas por sentir.'),
 ('La amiga sincera','Mi amiga me dijo que me estoy esforzando demasiado por hacer feliz a todos.','Y me molestó porque tiene razón.','Por algo te lo dijo alguien que te quiere.'),
 ('La feria del sábado','Compré flores para mi pieza. Me gustó elegirlas por mí misma.','No todo gesto bonito tiene que venir de una pareja.','Eso sí que suena a una buena tarde.'),
 ('La ambición','Quiero hacer cosas grandes, aunque me dé miedo decirlo fuerte.','A veces pienso que enamorarme me va a distraer de todo.','Si un vínculo te obliga a achicarte, no es el adecuado.'),
 ('Un vestido guardado','Encontré ropa que no usaba porque a Max no le gustaba.','La probé y pensé: qué ridículo haberla escondido.','Quédate con la que te haga sentir tú.'),
 ('El mensaje no enviado','Escribí un párrafo larguísimo y lo borré.','Creo que a veces confundo explicar con justificarme.','No siempre se necesita una tesis para poner un límite.'),
 ('Una tarde liviana','Hoy no pasó nada dramático. Y, qué alivio, estuvo buenísimo.','Podríamos hablar de cualquier tontera y no del amor.','Tengo un repertorio completo de tonteras.'),
 ('Mirar al futuro','Me pregunto dónde voy a estar el próximo año.','No quiero responder esa pregunta pensando primero en otra persona.','Podés construir un futuro sin dejar de querer a alguien.'),
 ('La canción vergonzosa','Tengo una canción terrible pegada y no la voy a reconocer en público.','No me juzgues si un día la canto en el auto.','No prometo no grabar el concierto.'),
 ('El límite más difícil','Decir no me cuesta mucho cuando tengo miedo de perder a alguien.','Estoy practicándolo con cosas chicas.','La persona correcta también respeta tus no.'),
 ('Un mensaje sin urgencia','Me gustó que me escribieras sin exigirme respuesta inmediata.','Se siente raro, pero bonito, que algo pueda ser simple.','No hay prueba de velocidad para hablar conmigo.')
]

for i, (tema, primera, segunda, respuesta) in enumerate(lumi_topics, 1):
    S('lumi',f'lumi_{i}',f'Chat con Lumi: {tema}','Chat · Lumi',[
        ('Narrador',f'El celular vibra. Esta vez la conversación empieza por algo propio de Lumi: {tema.lower()}.'),
        ('Lumi',primera),('Lumi',segunda),('Cáceres',respuesta),
        ('Narrador','Lumi demora un momento en responder. No porque tenga que hacerlo, sino porque está viviendo su día.')
    ],choices=[
        C('Seguir conversando y escuchar de verdad.',None,{'lumi':4,'confianza_lumi':4,'honestidad':2}),
        C('Tirar una talla para aliviar la tensión.',None,{'lumi':3,'amistad':1}),
        C('Cambiar el tema a «¿y Max?».',None,{'lumi':-2,'confianza_lumi':-4,'sospecha':1})
    ],direccion=f'Pantalla de mensajería junto a Lumi en una escena cotidiana. Tema: {tema}. Alternar detalles del día y reacciones auténticas.',
    prompt=visual('Viña del Mar, Chile, slice-of-life scene',f'adult independent blue-haired woman with glasses engaged in her own day; scene focused on {tema.lower()}, phone message overlay space', 'grounded, character-driven'))

cony_topics = [
 ('La voz de la fiesta','Oe, me acordé de cuando te quedaste callado en la fiesta y después no paraste de hablar.','Fue como desbloquearte un DLC de personalidad.','Y tú venías con el DLC de molestar instalado.'),
 ('Sin etiquetas','Me gusta salir, conocer gente, pasarlo bien. No estoy buscando que me pidan pololeo.','Te lo digo ahora pa que después no aparezcan los malentendidos.','Gracias por decírmelo así de claro.'),
 ('Plan improvisado','Tengo ganas de salir pero no de hacer un panorama de tres horas con itinerario.','Una caminata y algo rico me basta.','Plan aprobado por el comité de flojera.'),
 ('La prima copuchenta','Mi prima me preguntó si estábamos saliendo. Le dije que estábamos conversando.','Me miró como si hubiera cometido un crimen.','Las primas están contratadas por la CIA, parece.'),
 ('El audio de treinta segundos','Te iba a mandar un audio cortito y duró cuatro minutos.','Todo porque me puse a contar un cahuín sin importancia.','Manda la segunda temporada de ese audio.'),
 ('La ropa para salir','Estoy pensando si usar algo arreglado o andar cómoda.','Me da paja que alguien crea que me vestí para impresionarlo.','Ponte lo que te haga sentir bien, no lo que esperen.'),
 ('Las ganas de besar','Te digo algo y no te pongai nervioso: me dieron ganas de besarte la otra vez.','Pero que me den ganas no significa que haya firmado contrato de pareja.','Quedó clarísimo. A mí también me gustai.'),
 ('La noche divertida','Quiero un panorama donde nos riamos y no tengamos que fingir que somos elegantes.','Si terminamos comiendo papas fritas sentados en una plaza, mejor.','Alta cocina francesa versión quinta región.'),
 ('La pregunta de frente','¿A ti te molesta que alguien quiera algo casual?','A veces siento que me miran como si eso fuera una falta de respeto.','Mientras se converse y sea mutuo, no tiene nada de malo.'),
 ('El mensaje a destiempo','Ayer quise hablarte a las dos de la mañana y dije: mejor no despertar al pobre wn.','Tengo un horario de ideas peligrosísimo.','Mis notificaciones agradecen tu consideración.'),
 ('El día sin drama','Hoy fui a hacer trámites, comí algo rico y volví. Cero novelón.','No todos los días necesitan un capítulo intenso.','Menos mal, si estamos en una visual novel.'),
 ('¿Y si me encariño?','Me hago la canchera pero igual me gusta cuando me tratan con cariño.','Eso no significa que me vea pololeando mañana.','Una cosa no obliga a la otra.'),
 ('El meme equivocado','Te mandé un meme y después vi que era pésimo.','Ahora me da vergüenza hasta abrir el chat.','Ese meme es patrimonio cultural, no se borra.'),
 ('La cita sin tensión','¿Podemos juntarnos sin hablar de qué somos cada cinco minutos?','Me gusta saber a qué atenerme, pero también pasarlo bien.','Sí, se puede hablar claro y después disfrutar el día.'),
 ('La amiga escéptica','Una amiga me dijo que todos los hombres prometen cosas bonitas.','Yo le dije que prefiero mirar cómo se comportan.','Buena política, yo también haría eso.'),
 ('Caminar sin destino','Hay una plaza donde me gusta ir a mirar a los perros pasar.','Suena estúpido, pero me baja las revoluciones.','Quiero conocer esa plaza de perros famosos.'),
 ('No contestar de inmediato','A veces dejo el teléfono tirado y se me olvida contestar.','No es una estrategia de seducción, es que tengo vida.','Se agradece la aclaración, su señoría.'),
 ('Una conversación incómoda','Si algún día lo que queremos deja de coincidir, prefiero que me lo digai.','No quiero andar adivinando intenciones.','Te lo diría, y espero lo mismo de ti.'),
 ('Las primeras dudas','Me sorprendí pensando en ti sin que me hubieras escrito.','No voy a hacer un escándalo, pero me pareció curioso.','A mí también me pasa a veces.'),
 ('La independencia','Me gusta pagar mis cosas y decidir mis panoramas.','Si tengo una relación, no voy a desaparecer de mi vida.','Ni deberías tener que hacerlo.'),
 ('No todo es química','Podemos tener química y aun así querer cosas distintas.','Es raro decirlo, pero me da tranquilidad que entiendas eso.','Prefiero una verdad incómoda a una promesa falsa.'),
 ('El chiste privado','Ya nadie más entiende el chiste de la fiesta.','Es nuestra pequeña tontera, parece.','De las pocas tradiciones que respetaré.'),
 ('Una posibilidad nueva','Antes habría dicho que no me veo en una relación ni a palos.','Ahora diría que no sé. Pero todavía quiero decidirlo yo.','No hay que apurar esa respuesta.'),
 ('El mensaje sin máscara','Contigo he podido decir lo que quiero sin hacerme la desentendida.','Eso vale más de lo que parece.','A mí también me sirve saber dónde estamos parados.')
]
for i,(tema,primera,segunda,respuesta) in enumerate(cony_topics,1):
    S('cony',f'cony_{i}',f'Chat con Cony: {tema}','Chat · Cony',[
        ('Narrador',f'Entre audios y tallas aparece un tema distinto: {tema.lower()}.'),
        ('Cony',primera),('Cony',segunda),('Cáceres',respuesta),
        ('Narrador','No se promete una relación. Se entiende mejor lo que cada uno espera.')
    ],choices=[
        C('Responder sin asumir que quieren ser pareja.',None,{'cony':4,'confianza_cony':4,'honestidad':2}),
        C('Coquetear y seguir el juego.',None,{'cony':5,'confianza_cony':1}),
        C('Insistir en que tarde o temprano serán pololos.',None,{'cony':-3,'confianza_cony':-5,'sospecha':2})
    ],direccion=f'Captura de chat estilizada, Cony haciendo su vida por cuenta propia. Tema: {tema}. Dejar espacio para retratos.',
    prompt=visual('Villa Alemana, Chile, slice-of-life',f'fictional adult young woman with expressive mischievous smile, on her phone, independent and relaxed, daily vignette about {tema.lower()}','warm, humorous'))

# DISCORD: 18 escenas distintas de consejos y humor, no samuráis.
group_talk = [
 ('La hoja Excel','Jairo','Yo digo que hagai un Excel: columna de afecto, columna de disponibilidad y semáforo de riesgo.','Kevin','El wn convirtió el amor en control de inventario.','Seba','Más útil sería preguntarle a la persona qué quiere.'),
 ('El visto','Cáceres','La Lumi me dejó en visto hace una hora.','Seba','Capaz está ocupada, hermano. Nadie está obligado a contestarte al tiro.','Jairo','O puede ser una prueba psicológica de nivel siete.'),
 ('La cita','Kevin','Oe, pa una cita funciona escuchar y no vender un personaje que no erís.','Jairo','¿Y llevar una presentación de PowerPoint?','Seba','Especialmente NO llevar PowerPoint, por favor.'),
 ('El pelo','Seba','Cáceres, no te preocupís por las entradas de pelo. Tu mayor problema es que respondís tres días después.','Cáceres','Déjame tener una crisis a la vez.','Kevin','Hermano, administrai el romanticismo como ticket de soporte.'),
 ('Tesis de Jairo','Jairo','Para mí el amor es un RPG y hay que subir carisma.','Kevin','Eso explica por qué nunca salís de la pantalla de creación de personaje.','Seba','Habla con gente real, experto.'),
 ('¿Exclusividad?','Cáceres','Si no hay pololeo, ¿qué significa exclusividad?','Kevin','Lo que conversen. No hay contrato implícito.','Jairo','¿Y si hacemos un EULA de pareja?'),
 ('Max','Seba','Si la Lumi está con Max, tú no eres el reemplazo designado.','Kevin','Podís acompañarla sin convertirte en el protagonista de su ruptura.','Jairo','Voy anotando: no ser reemplazo, entendido.'),
 ('Cony','Kevin','Cony puede querer algo casual sin que eso sea una invitación a prometerle matrimonio.','Seba','Lo importante es que ambas partes quieran lo mismo y puedan cambiar de opinión.','Jairo','Entonces no compro anillo en el primer café.'),
 ('Ciberseguridad sentimental','Jairo','¿Y si cambiamos todas las contraseñas a un solo secreto compartido?','Kevin','Hermano, soy informático y hasta a mí me dio vergüenza escucharte.','Seba','La confianza no es pedirle claves a nadie.'),
 ('El consejo bueno','Cáceres','Siento que a veces la voy a cagar haga lo que haga.','Seba','Vas a equivocarte igual. La diferencia es si te haces cargo o te escondes.','Kevin','Y si no sabís qué querís, dilo así.'),
 ('El meme','Jairo','Tengo el meme perfecto para declararse. Es una rana con un corazón.','Kevin','Prohibido enviar ranas sin contexto.','Seba','A veces un mensaje honesto sirve más que doce stickers.'),
 ('Rumores','Kevin','Ojo con contar los chats privados de la gente acá.','Seba','Una talla no justifica dejar mal a alguien.','Jairo','¿Ni aunque sea material de stand-up?'),
 ('Pena','Cáceres','Me da miedo que la Lumi vuelva con Max y yo quede ahí esperando.','Seba','Entonces no te pongai en pausa. Ella decide su vida y tú la tuya.','Kevin','Acompañar no es reservarse un turno.'),
 ('Deseo y cariño','Jairo','No entiendo cómo se puede querer pasar la noche con alguien sin querer pololear.','Kevin','Porque el deseo y el compromiso no son la misma cosa, genio.','Seba','Y ambas cosas requieren sinceridad.'),
 ('Escuchar un no','Seba','Si te dicen que no, es no. Si te dicen todavía no sé, no es un sí escondido.','Jairo','¿Y si mando un formulario de confirmación?','Kevin','Jairo, desenchufa el router.'),
 ('La disculpa','Kevin','Pedir perdón sin cambiar nada es como hacer commit sin guardar archivos.','Seba','JAJAJA, esa te salió técnica. Igual tiene razón.','Jairo','¿Puedo hacer rollback de una cita mala?'),
 ('Elegir','Cáceres','Tengo que decidir qué clase de persona quiero ser, no solo con quién salir.','Seba','Eso, hermano. Ahí recién empezamos a hablar.','Jairo','¿Y una clase de persona con mejores entradas de pelo?'),
 ('La última talla','Kevin','Si terminai solo no significa que perdiste.','Seba','Pero si terminai solo por mentiroso, ahí sí que te vamos a webear.','Jairo','Yo propongo una copa para el último pelo.')
]
for i,(tema,a,primera,b,segunda,c,tercera) in enumerate(group_talk,1):
    S('amigos',f'discord_{i}',f'En Discord: {tema}','Discord · Canal de los cabros',[
        ('Narrador',f'El canal de voz está activo. Entre juegos y bromas surge un tema: {tema.lower()}.'),
        (a,primera),(b,segunda),(c,tercera),
        ('Cáceres','Ya, está bien. Igual me sirve hablarlo con ustedes, aunque sean unos wnes.')
    ], choices=[
        C('Tomarse en serio el consejo útil.',None,{'amistad':5,'claridad':4,'honestidad':1}),
        C('Responder con una talla y pasar un buen rato.',None,{'amistad':5,'claridad':1}),
        C('Hacerse el desentendido.',None,{'amistad':1,'claridad':-3})
    ],prompt=visual('discord voice chat at an adult gaming desk, Chile','computer monitors, headsets, four adult friends joking and advising one another, comedic composition'))

reflexiones = [
 ('No confundir atracción con obligación','Se puede tener ganas de besar a alguien sin prometerle una vida entera.'),
 ('La culpa no es brújula','Sentirte culpable puede señalar un error, pero no reemplaza decir la verdad.'),
 ('Lo que no sabes','Quizás todavía no sabes con quién quieres estar. Eso también se puede reconocer.'),
 ('No eres el salvador de nadie','Escuchar a Lumi no convierte su conflicto con Max en un problema que debas solucionar.'),
 ('Cony no es una apuesta','Que quiera algo casual no significa que debas tratarla como una conquista.'),
 ('Tus amigos te conocen','Kevin y Seba molestan, pero también te dicen cuándo estás esquivando lo evidente.'),
 ('La primera mentira','Muchas mentiras empiezan con ganas de evitar una conversación incómoda.'),
 ('La libertad','Elegir con quién estar incluye aceptar que otras personas también pueden decir que no.'),
 ('El deseo y el después','Una noche deseada puede ser bonita sin volverse una promesa que nadie hizo.'),
 ('Aprender a estar solo','La soledad no es un castigo ni un final malo.'),
 ('El miedo a perder','No puedes conservar a alguien a fuerza de controlar sus decisiones.'),
 ('El cierre','Un buen final no es sumar dos corazones. Es entender lo que realmente acordaron.')
]
for i,(tema,texto) in enumerate(reflexiones,1):
    S('otros',f'reflexion_{i}',f'Cuaderno: {tema}','Pieza de Cáceres · Cuaderno',[
        ('Narrador','La ciudad sigue funcionando al otro lado de la ventana. Cáceres deja el teléfono boca abajo.'),
        ('Cáceres',texto),
        ('Narrador','Anota una idea. No la comparte para recibir aprobación. Quiere recordarla.')
    ],choices=[C('Escribirlo para no olvidarlo.',None,{'claridad':7,'honestidad':3}),C('Dejarlo para después.',None,{'claridad':2})],
    prompt=visual('bedroom in Villa Alemana at dusk','adult student writing in notebook by the window, contemplative, evening light'))

paseos = [
 ('Plaza de Villa Alemana','Los niños juegan a lo lejos y el viento mueve las hojas. Cáceres aprende a caminar sin mirar cada notificación.'),
 ('Micro a Viña','Una pareja se ríe al fondo de la micro. Cáceres sonríe y se pone audífonos.'),
 ('Costanera','La brisa salada le despeina lo poco que le queda de dignidad; el mar no opina de su vida romántica.'),
 ('Café pequeño','Pide un café y escucha conversaciones que no tienen nada que ver con él.'),
 ('Plaza Victoria','Valparaíso va a su ritmo. Nadie se detiene a resolverle su corazón.'),
 ('Parque de noche','Los postes alumbran el camino. Cáceres recuerda una conversación incómoda y piensa cómo decir la verdad.'),
 ('Terminal','Mira las micros salir y entrar. Qué fácil sería cambiarse de destino solo con pagar otro pasaje.'),
 ('Feria','Una vendedora le ofrece flores. Cáceres compra una para su propia pieza.'),
 ('Mirador','Las luces de la Quinta empiezan a encenderse. No hay respuestas mágicas desde arriba.'),
 ('Paseo corto','Hace calor. Compra agua y vuelve a casa con la cabeza un poco más ordenada.'),
 ('Bajada al mar','Se sienta en un muro y mira cómo pasan las nubes. Se permite no tener un plan perfecto.'),
 ('Ruta de vuelta','Ya conoce cada curva del camino. Lo que aún no conoce son todas sus propias decisiones.')
]
for i,(lugar,texto) in enumerate(paseos,1):
    S('otros',f'paseo_{i}',f'Caminata: {lugar}',f'{lugar} · Quinta Región',[
        ('Narrador',texto),('Cáceres','Tenía que salir un rato. Estoy pensando demasiado en los chats.'),
        ('Narrador','Respira, mira alrededor y recuerda que su vida es más grande que una conversación.')
    ],choices=[C('Volver con la mente más clara.',None,{'claridad':5,'sospecha':-2}),C('Compartir una talla con los cabros.',None,{'amistad':3,'claridad':2})],
    prompt=visual(f'{lugar}, coastal central Chile',f'adult young man walking alone thoughtfully on {lugar}, realistic urban Chilean ambience'))

for grupo,total in [('lumi',4),('cony',4),('discord',4),('reflexion',4),('paseo',4)]:
    for i in range(1,5):
        quien = {'lumi':'Lumi','cony':'Cony','discord':'Kevin','reflexion':'Cáceres','paseo':'Narrador'}[grupo]
        frases = {
         'lumi':['Hoy estoy a full, después te cuento cómo me fue.','Me fue bien en algo que me importaba. Quería compartirlo.','Voy a pasar la tarde con mis cosas. Hablamos después.','Hoy quiero una conversación liviana, sin analizar la vida.'],
         'cony':['Ando en otra, pero te mando un saludo jajaja.','Hoy me reí acordándome de una talla tuya.','Voy a salir con unas amigas; hablamos después.','Estoy ocupada, pero me gusta conversar sin presiones.'],
         'discord':['Oe wn, ¿qué tal la vida? ¿Seguís con las mismas preguntas?','Hoy solo vinimos a jugar, sin terapia sentimental.','El Jairo tiene una teoría nueva y es una aberración.','No te olvidís de vivir fuera del celular.'],
         'reflexion':['Hoy basta con no complicar más las cosas.','La calma también es una buena decisión.','No necesito inventar una respuesta ahora.','Tengo derecho a cambiar de opinión, con respeto.'],
         'paseo':['La ciudad cambia mientras caminas sin apuro.','Hay un poco de viento y poca prisa.','Un paseo no resuelve todo, pero ayuda.','El ruido de la calle se mezcla con tus pensamientos.']
        }[grupo]
        S('otros',f'{grupo}_rutina_{i}','Un momento de rutina','Quinta Región · Día cotidiano',[
            (quien, frases[i-1]),('Narrador','No ocurre un gran evento. A veces el tiempo pasa y eso está bien.')
        ],choices=[C('Terminar y seguir con el día.')],
        prompt=visual('central Chile, ordinary day','adult character taking a short break in an everyday quiet moment'))

# EVENTOS PRINCIPALES; ramas se escriben explícitamente y no se repiten.
def evento(id,titulo,lugar,lines,choices,prompt_detail=None,direccion=None):
    return S('eventos',id,titulo,lugar,lines,choices=choices,
     direccion=direccion or f'Secuencia narrativa con planos generales de {lugar}, planos medios del diálogo y primeros planos en las decisiones.',
     prompt=visual(lugar,prompt_detail or titulo))

def rama(id,titulo,lugar,lines,flags=None):
    return S('eventos',id,titulo,lugar,lines)

evento('evento_fiesta','La conejita de la fiesta (recuerdo)','Fiesta de disfraces · Villa Alemana',[
 ('Narrador','Unas luces de colores cortan la oscuridad. Suena música demasiado fuerte. Kevin saluda a Cáceres y le hace señas para acercarse.'),
 ('Kevin','Oe, ven. Te presento a la Cony. Pero no te pongai raro, por favor.'),
 ('Cony','¿Así que tú eres el famoso Cáceres? Espero que sea famoso por algo bueno.'),
 ('Seba','Es famoso por quedarse diez minutos pensando una respuesta de dos palabras.'),
 ('Jairo','Yo tengo un tutorial pa eso.'),
 ('Kevin','Jairo, nadie te pidió un tutorial de nada.'),
 ('Narrador','Cony se ríe. Cáceres siente el impulso de quedarse a conocerla.')
],[
 C('Invitarla a salir al patio para poder escucharla.','fiesta_patio',{'cony':10,'confianza_cony':7},['fiesta_conversacion']),
 C('Quedarte en el grupo y hacerla reír.','fiesta_grupo',{'cony':7,'amistad':7},['fiesta_grupo']),
 C('Preguntarle directamente si quiere intercambiar números.','fiesta_numero',{'cony':5,'honestidad':5},['fiesta_numero'])
], 'adult Chilean costume party, four friends introducing an adult playful young woman, warm colorful lights, no identifiable celebrities')
rama('fiesta_patio','Lo que dijo en el patio','Patio de la fiesta',[
 ('Cony','Te vi más nervioso que la chucha allá adentro. Tranquilo, no te voy a interrogar.'),
 ('Cáceres','Es que el Kevin me presenta como si yo fuera un producto en oferta.'),
 ('Cony','JAJAJA. Me caíste bien. ¿Nos seguimos hablando después?'),
 ('Narrador','Intercambian sus números. No pactan nada más; tampoco necesitan hacerlo.')
])
rama('fiesta_grupo','La carcajada','Fiesta',[
 ('Jairo','Tengo un consejo para el amor: nunca mandar dos mensajes seguidos.'),
 ('Cony','¿Y quién te enseñó eso?'),('Kevin','Nadie, por eso no le funciona.'),
 ('Narrador','Cony se ríe tanto que casi deja caer su vaso. Cáceres piensa que le gustaría volver a verla.')
])
rama('fiesta_numero','Un número, no una promesa','Fiesta',[
 ('Cáceres','Me gustó conversar contigo. ¿Te parece si nos hablamos después?'),
 ('Cony','Sí, po. Pero no me mandís un testamento de buenas noches.'),
 ('Narrador','Se agregan. Que haya química no significa que ya exista una relación.')
])

evento('evento_lumi_identidad','La Lumi que no cabe en un chat','Cafetería de Viña',[
 ('Narrador','Lumi llega sola, con una bolsa llena de cosas para sus proyectos. Pide antes de mirar el teléfono. Su día empezó mucho antes que esta cita.'),
 ('Lumi','Yo hago todo sola cuando quiero. Me gusta moverme, inventar panoramas, tener mis tiempos.'),
 ('Cáceres','¿Y el Max?'),
 ('Lumi','Ahí está mi contradicción. Cuando me enamoro me pongo demasiado pendiente de la persona. Empiezo a mirar si respondió y me olvido de lo que quería hacer.'),
 ('Lumi','Quiero aprender a querer sin volverme adicta a la atención del otro.'),
 ('Narrador','No espera que Cáceres le solucione el dilema. Quiere ser escuchada.')
],[
 C('«Me interesa conocer tu mundo, no solo tus problemas.»','lumi_identidad_escucha',{'lumi':8,'confianza_lumi':10,'claridad':4},['lumi_mundo']),
 C('«Si Max falla tanto, yo te trataría mejor.»','lumi_identidad_comparacion',{'lumi':2,'confianza_lumi':-7,'sospecha':6}),
 C('«Suena agotador. ¿Qué harías solo por ti?»','lumi_identidad_pregunta',{'lumi':6,'confianza_lumi':8,'honestidad':4},['lumi_mundo'])
], 'independent adult blue-haired woman with glasses at cafe, notebooks and project materials, candid conversation')
rama('lumi_identidad_escucha','Sin pedir explicaciones','Cafetería',[
 ('Lumi','Eso me gustó. A veces siento que la gente solo escucha la parte sentimental de mi vida.'),
 ('Cáceres','Bueno, entonces cuéntame qué estás armando.'),
 ('Narrador','La conversación se extiende entre ideas, música y planes que no incluyen a Max.')
])
rama('lumi_identidad_comparacion','La comparación incómoda','Cafetería',[
 ('Lumi','No necesito que compitas con él. Necesito aprender a no olvidarme de mí.'),
 ('Cáceres','Sí, tienes razón. Me apuré.'),
 ('Narrador','Lumi bebe un sorbo de café y cambia de tema. Algo quedó en el aire.')
])
rama('lumi_identidad_pregunta','Lo que ella elige','Cafetería',[
 ('Lumi','Me iría a sacar fotos por toda la costa. Sola, si quiero. Acompañada, si me nace.'),
 ('Cáceres','Suena a un buen plan. ¿Me mostrarías algunas después?'),
 ('Narrador','Lumi sonríe: esta vez el tema es lo que ella desea.')
])

evento('evento_cony_libre','La Cony viene sin contrato','Plaza de Villa Alemana',[
 ('Narrador','Cony llega con una sonrisa traviesa. Se sienta frente a Cáceres, no a su lado; quiere mirarlo a la cara.'),
 ('Cony','Te lo digo altiro: me gustai, me atraís, me dan ganas de besarte. Pero no estoy buscando pololo por ahora.'),
 ('Cáceres','¿Y entonces qué andai buscando?'),
 ('Cony','Salir, pelarme si hay ganas, quizá acostarme con alguien si ambos queremos. Sin venderle cuentos a nadie.'),
 ('Cony','Y si algún día me cambia la cabeza, lo voy a decir. Pero no me esperís como si fuera una promesa.')
],[
 C('«Me sirve algo casual, pero hablemos de límites.»','cony_libre_acuerdo',{'cony':8,'confianza_cony':12,'honestidad':8},['cony_casual_acordado'],['cony_sin_prisa']),
 C('«Prefiero ir lento. Me gustai, pero no quiero fingir.»','cony_libre_lento',{'cony':6,'confianza_cony':8,'claridad':6},['cony_sin_prisa'],['cony_casual_acordado']),
 C('«Ya te voy a convencer de ser mi polola.»','cony_libre_presion',{'cony':-8,'confianza_cony':-15,'sospecha':8},['cony_presionada'])
], 'two adult young people having a frank conversation on a small town plaza bench, daylight, respectful body language')
rama('cony_libre_acuerdo','Sin letra chica','Plaza',[
 ('Cony','Me gusta eso. Si nos besamos o pasa algo más, no voy a asumir exclusividad.'),
 ('Cáceres','Entonces hablamos si cambia lo que sentimos. Nada de desaparecer ni inventar compromisos.'),
 ('Narrador','Se despiden con un abrazo. Hay ganas, pero también una conversación que sostiene esas ganas.')
])
rama('cony_libre_lento','Sin apurarla','Plaza',[
 ('Cony','Perfecto. Me gusta cuando alguien dice lo que quiere sin hacerse el interesante.'),
 ('Cáceres','Yo necesito cachar si lo casual me acomoda de verdad.'),
 ('Narrador','Cony asiente. Nadie tiene que apurar a nadie.')
])
rama('cony_libre_presion','La sonrisa se borra','Plaza',[
 ('Cony','Eso mismo es lo que no quiero. No soy un desafío que tengai que ganar.'),
 ('Cáceres','Ya. Estuvo feo cómo sonó.'),
 ('Narrador','La conversación se enfría. Las ganas no compensan el desprecio por un límite.')
])

evento('evento_max_planton','La tercera cancelación','Llamada de Lumi · Noche',[
 ('Lumi','Max volvió a cancelar. Le pedí que viniera a algo importante para mí. Lo había anotado hace semanas.'),
 ('Cáceres','¿Te avisó antes?'),('Lumi','Diez minutos después de la hora. Y terminó diciendo que yo exageraba.'),
 ('Lumi','Qué rabia. Porque puedo organizarlo todo sola, pero cuando amo a alguien me dan ganas de que esté presente.'),
 ('Narrador','Cáceres tiene un consejo en la punta de la lengua. Puede ser útil o sonar como si estuviera esperando su oportunidad.')
],[
 C('«Puedes decirle que esto te duele, sin pedir permiso.»','max_planton_apoyo',{'confianza_lumi':10,'honestidad':5,'lumi':4},['max_planton_escuchado']),
 C('«Déjalo y quédate conmigo.»','max_planton_presion',{'confianza_lumi':-8,'lumi':2,'sospecha':10},['max_planton_escuchado']),
 C('«¿Quieres desahogarte o prefieres pensar qué decirle?»','max_planton_escucha',{'confianza_lumi':12,'lumi':5,'claridad':4},['max_planton_escuchado'])
], 'blue-haired adult woman with glasses taking a late-night phone call by a window, holding back frustration')
rama('max_planton_apoyo','Lo que no se negocia','Llamada',[
 ('Lumi','A veces necesito que alguien me lo diga así. Sin tratarme de exagerada.'),
 ('Cáceres','No tengo que decidir por ti. Solo me importa que te escuchen.'),
 ('Narrador','Lumi termina la llamada y escribe en su cuaderno lo que quiere decir.')
])
rama('max_planton_presion','La oportunidad equivocada','Llamada',[
 ('Lumi','No soy un premio por el que estai compitiendo, Cáceres.'),
 ('Cáceres','Perdón. Fue una mala forma de decirlo.'),
 ('Narrador','Hay un silencio que no se arregla coqueteando.')
])
rama('max_planton_escucha','Primero escuchar','Llamada',[
 ('Lumi','Quiero desahogarme un rato. Después pensaré qué hago.'),
 ('Cáceres','Dale. Te escucho.'),('Narrador','A veces el buen consejo no empieza con una solución.')
])

evento('evento_consejo_1','Los asesores no contratados','Discord · Viernes',[
 ('Cáceres','La Cony quiere algo casual y la Lumi está complicada con Max. ¿Cómo hago para no quedar como un imbécil?'),
 ('Kevin','Preguntando y escuchando, po. Ni la Lumi necesita un salvador ni la Cony te debe pololeo.'),
 ('Seba','Si tú querís algo distinto, dilo antes de hacerte ilusiones.'),
 ('Jairo','Yo vi un hilo que dice que hay que responder cada 37 minutos exactos.'),
 ('Kevin','Jairo, tú no eres un reloj atómico con Tinder.'),
 ('Cáceres','JAJAJA, ya, pero los escucho.')
],[
 C('Hacerles caso y hablar directo.', 'consejo_1_directo',{'amistad':8,'claridad':8,'honestidad':5},['consejo_escuchado']),
 C('Pedirle a Jairo el hilo para reírse.', 'consejo_1_jairo',{'amistad':8,'claridad':-3}),
 C('Cambiar el tema y jugar.', 'consejo_1_juego',{'amistad':5})
], 'four adult male friends in separate gaming desks in a video-call style composition; humorous Chilean dialogue')
rama('consejo_1_directo','Un consejo útil','Discord',[
 ('Seba','Entonces no prometai exclusividad a nadie si ni tú sabís si la querís.'),
 ('Kevin','Eso. Trátalas bien sin intentar controlar la historia.'),('Narrador','La llamada sigue con risas, pero el consejo queda.')
])
rama('consejo_1_jairo','El método de los 37 minutos','Discord',[
 ('Jairo','En el hilo decía que genera misterio.'),('Kevin','Genera que te bloqueen, hermano.'),
 ('Narrador','Todos se ríen. Cáceres por fin deja de mirar el teléfono un rato.')
])
rama('consejo_1_juego','Ranked sentimental','Discord',[
 ('Seba','Ya, a jugar. Pero después no nos vengai con que todo te pilló de sorpresa.'),
 ('Narrador','Cambian a un juego. La noche no necesita una respuesta inmediata.')
])

evento('evento_cony_cita','Química en la Quinta','Paseo nocturno · Villa Alemana',[
 ('Narrador','Cony llega con una chaqueta clara. Caminan bajo las luces amarillas de la plaza, compartiendo papas fritas de una bolsa.'),
 ('Cony','Me caís muy bien, pero no te quiero vender una película de amor.'),
 ('Cáceres','¿Y si solo quiero estar aquí y pasarlo bien?'),
 ('Cony','Entonces ven, po. No te hagai el dramático.'),
 ('Narrador','Cony se acerca, dejando espacio para que Cáceres elija.')
],[
 C('Preguntar si puede besarla.','cony_cita_beso',{'cony':10,'confianza_cony':8,'honestidad':3},['cony_primer_beso']),
 C('Seguir caminando y conversando.','cony_cita_camino',{'cony':7,'confianza_cony':9}),
 C('Preguntar si esto significa que serán pareja.','cony_cita_etiqueta',{'cony':-4,'confianza_cony':-7})
], 'adult couple sharing fries on a lamplit Chilean plaza at night; romantic but not graphic')
rama('cony_cita_beso','Un beso sin letra chica','Plaza',[
 ('Cáceres','¿Puedo besarte?'),('Cony','Sí, tontito, te estaba dando todas las señales.'),
 ('Narrador','Se besan con ganas y después se ríen por lo nervioso que estaba Cáceres.'),
 ('Cony','Que nos besemos no me convierte en tu polola, ¿ya?'),('Cáceres','Entendido.')
])
rama('cony_cita_camino','La cita que no necesitó beso','Plaza',[
 ('Cony','Me gustó que no vinieras con un libreto.'),('Cáceres','No tengo libreto. Tengo hambre.'),
 ('Narrador','Compran algo más para comer y la noche termina con un abrazo largo.')
])
rama('cony_cita_etiqueta','La pregunta anticipada','Plaza',[
 ('Cony','No, po. Te lo dije. Me gusta conocerte, no quiero ponerle nombre todavía.'),
 ('Narrador','Cáceres se da cuenta de que estaba intentando asegurarse de una respuesta que ella no ofreció.')
])

evento('evento_lumi_taller','Lo que enciende a Lumi','Taller creativo · Viña del Mar',[
 ('Narrador','Lumi trabaja concentrada en un proyecto creativo. Tiene las mangas remangadas y los lentes un poco torcidos de tanto moverse.'),
 ('Lumi','Cuando me meto en esto me olvido del mundo. Antes me daba culpa no responderle a Max mientras hacía algo mío.'),
 ('Cáceres','Se te nota feliz. ¿Necesitas ayuda?'),
 ('Lumi','Sí, pero no me lo hagai tú. Dame esa herramienta y quédate a hacerme compañía.'),
 ('Narrador','No parece necesitar que la rescaten: necesita espacio para seguir siendo ella.')
],[
 C('Ayudar sin quitarle el control.','lumi_taller_bien',{'lumi':7,'confianza_lumi':12,'claridad':5},['lumi_taller_visto']),
 C('Decir que prefieres irte si no te presta atención.','lumi_taller_mal',{'lumi':-6,'confianza_lumi':-9},['lumi_taller_visto'])
], 'creative workshop in Chile, independent adult woman with blue hair glasses painting or crafting, adult male friend observing respectfully')
rama('lumi_taller_bien','Su proyecto, su momento','Taller',[
 ('Lumi','Gracias. ¿Cachai lo distinto que es que alguien me acompañe sin ponerse al centro?'),
 ('Cáceres','Y además me quedó pintura en la cara. Es excelente intercambio.'),
 ('Narrador','Ambos se ríen. Ella vuelve a concentrarse, contenta.')
])
rama('lumi_taller_mal','La atención no es deuda','Taller',[
 ('Lumi','Te invité para compartir algo importante para mí, no para dejar de hacerlo por ti.'),
 ('Cáceres','Sí... me fui en la volá.'),
 ('Narrador','Lumi se toma un momento antes de retomar su trabajo.')
])

evento('evento_cony_noche','La invitación','Departamento de Cony · Noche',[
 ('Narrador','Después de una salida entre risas, Cony invita a Cáceres a subir. La casa está tranquila; la conversación también.'),
 ('Cony','Me atraís, Cáceres. Tengo ganas de estar contigo esta noche. Te lo pregunto bien claro porque no quiero suposiciones.'),
 ('Cáceres','A mí también me gustai. ¿Y seguimos con lo que hablamos? Sin prometer pololeo.'),
 ('Cony','Exacto. Si cambiamos de opinión, paramos. Y mañana seguimos siendo dos personas que se respetan.'),
 ('Narrador','La decisión es de ambos; no hay presión, ni obligación de que suceda nada.')
],[
 C('«Sí, quiero quedarme contigo. Estoy cómodo con esto.»','cony_noche_si',{'cony':12,'confianza_cony':10,'honestidad':5},['cony_intimidad']),
 C('«Me gustai, pero hoy prefiero irme a casa.»','cony_noche_no',{'cony':4,'confianza_cony':9,'claridad':6},['cony_respeto_limite'])
], 'adult couple on a living room sofa at night, warm interior, looking at each other, intimacy implied not shown, fully clothed')
rama('cony_noche_si','Puerta cerrada, conversación abierta','Departamento de Cony · Noche',[
 ('Cony','¿Estás seguro?'),('Cáceres','Sí. ¿Tú también?'),('Cony','Sí.'),
 ('Narrador','Se besan. Cony le toma la mano y lo guía hacia el pasillo. La escena se funde a negro.'),
 ('Narrador','Esa noche tienen un encuentro íntimo consensuado. No se muestra. Ninguno promete una relación por haberlo vivido.'),
 ('Narrador','Al amanecer tendrán que hablar de cómo se sienten, no fingir que no pasó nada.')
])
rama('cony_noche_no','Decir que no también acerca','Departamento de Cony',[
 ('Cony','Gracias por decirlo. Me gusta saber que podís elegir sin hacerte el valiente.'),
 ('Cáceres','La próxima cita la hacemos con menos nervios.'),
 ('Narrador','Se abrazan y Cáceres vuelve a su casa. La confianza crece incluso sin pasar la noche juntos.')
])

evento('evento_max_mentira','La verdad a medias','Mirador · Viña del Mar',[
 ('Narrador','Lumi llega seria. Tiene su bolso cruzado y ojeras de una noche mal dormida.'),
 ('Lumi','Max me dijo que estaba enfermo el día que canceló. Después vi una historia: estaba de fiesta, tranquilo, con gente que ni conocía.'),
 ('Lumi','Lo que me dolió no fue la fiesta. Fue que me mintiera y me hiciera sentir ridícula por preguntar.'),
 ('Cáceres','Eso sí que es una cagá.'),
 ('Lumi','Y lo peor es que una parte de mí quiere perdonarlo altiro. Cuando amo, me cuesta soltar.')
],[
 C('«No tienes que decidir hoy. Lo importante es que te respeten.»','max_mentira_apoyo',{'confianza_lumi':12,'honestidad':6,'claridad':5},['max_mentira_escuchada']),
 C('«Te lo dije, conmigo no pasaría.»','max_mentira_ego',{'lumi':-4,'confianza_lumi':-12,'sospecha':10},['max_mentira_escuchada']),
 C('«¿Qué necesitarías para volver a confiar en él?»','max_mentira_pregunta',{'confianza_lumi':10,'lumi':3,'claridad':7},['max_mentira_escuchada'])
], 'independent adult blue-haired woman with glasses seated on coastal viewpoint, disappointed after discovering a lie, respectful friend alongside')
rama('max_mentira_apoyo','La noche de los límites','Mirador',[
 ('Lumi','Me cuesta creer que puedo quererlo y aun así decirle basta.'),
 ('Cáceres','Eso solo lo podís decidir tú.'),
 ('Narrador','Lumi deja el teléfono en el bolso y mira el mar. Por una vez no espera una notificación.')
])
rama('max_mentira_ego','No era una competencia','Mirador',[
 ('Lumi','No necesito que la caguemos con otra comparación. Necesito ordenar mi cabeza.'),
 ('Cáceres','Perdón. Fue egoísta.'),
 ('Narrador','Ella necesita distancia. Cáceres entiende que tratarla bien no le garantiza nada.')
])
rama('max_mentira_pregunta','La pregunta necesaria','Mirador',[
 ('Lumi','No sé. Tal vez que deje de minimizar todo lo que siento. O tal vez ya sea tarde.'),
 ('Narrador','La respuesta le pertenece a ella, incluso si no es la que Cáceres espera.')
])

evento('evento_cony_despues','Lo que pasó anoche','Cocina de Cony · Mañana',[
 ('Narrador','La mañana encuentra dos tazas sobre la mesa. Cony está preparando café; Cáceres mira su celular apagado.'),
 ('Cony','Oe. Anoche nos acostamos y yo lo pasé bien. Pero quiero saber si quedamos en la misma página.'),
 ('Cáceres','A mí también me gustó. No quiero inventar una historia distinta a la que hablamos.'),
 ('Cony','Bien. Que haya intimidad no significa que seamos pololos. Y si algo cambia, lo hablamos.'),
 ('Narrador','A veces lo más íntimo es poder conversar sin inventar promesas.')
],[
 C('«Sigamos con acuerdos claros; no asumo exclusividad.»','cony_despues_claro',{'confianza_cony':12,'honestidad':8,'claridad':6},['cony_limites_revisados']),
 C('«En verdad yo quiero exclusividad, necesito pensarlo.»','cony_despues_honesto',{'confianza_cony':7,'honestidad':10,'claridad':7},['cony_necesita_revisar']),
 C('«No hace falta hablar, total fue una noche.»','cony_despues_evasivo',{'confianza_cony':-15,'honestidad':-10,'sospecha':7})
], 'morning kitchen scene, adult couple in casual fully clothed outfits drinking coffee across a table, sincere talk')
rama('cony_despues_claro','Lo acordado vale','Cocina',[
 ('Cony','Me gusta poder hablarte así de frente.'),
 ('Cáceres','Y si alguna vez queremos algo distinto, no lo escondemos.'),
 ('Narrador','La despedida es cálida y tranquila. El vínculo continúa sin convertirse automáticamente en noviazgo.')
])
rama('cony_despues_honesto','Querer otra cosa es válido','Cocina',[
 ('Cony','Gracias por no fingir que te acomoda todo. No sé si puedo darte eso ahora.'),
 ('Narrador','No se pelean. Ponen la historia en pausa para pensar qué esperan.')
])
rama('cony_despues_evasivo','El silencio no resuelve','Cocina',[
 ('Cony','No me molesta que haya sido una noche. Me molesta que no podai hablar de eso.'),
 ('Cáceres','Tenís razón.'),('Narrador','Aparece una distancia que no estaba la noche anterior.')
])

evento('evento_lumi_decision','Lumi no es un premio','Parque de Viña del Mar',[
 ('Narrador','Lumi citó a Cáceres para caminar. No está llorando. Está cansada, pero sorprendentemente serena.'),
 ('Lumi','Hoy hablé con Max. Le dije que no quiero seguir justificando mentiras ni cancelaciones.'),
 ('Lumi','Me pidió otra oportunidad. Y una parte mía todavía lo ama, aunque me esté haciendo daño.'),
 ('Lumi','No te pido que elijas por mí. Necesito hablar en voz alta antes de decidir.')
],[
 C('«Lo que decidas, que sea por ti. Mereces un vínculo que respete tu vida.»','lumi_decision_ruptura',{'confianza_lumi':13,'claridad':10,'honestidad':6},['max_ruptura'],['lumi_sigue_max']),
 C('«Si quieres darle otra oportunidad, tiene que cambiar de verdad.»','lumi_decision_sigue',{'confianza_lumi':6,'claridad':2},['lumi_sigue_max'],['max_ruptura']),
 C('«Termina con él y pololea conmigo.»','lumi_decision_presion',{'confianza_lumi':-18,'sospecha':15,'lumi':-5},['lumi_sigue_max'],['max_ruptura'])
], 'blue-haired adult woman with glasses standing in green city park, emotional independence, candid conversation')
rama('lumi_decision_ruptura','El mensaje definitivo','Parque',[
 ('Lumi','Tenís razón. Y la verdad es que ya tomé la decisión antes de venir. Terminé con Max esta mañana.'),
 ('Cáceres','¿Cómo te sentís?'),('Lumi','Triste. Liberada. Con ganas de escribirle. Todo junto.'),
 ('Lumi','Y por favor no tomes esto como que ya estoy disponible para ti. Necesito recuperar mi propio ritmo.'),
 ('Narrador','Cáceres asiente. Acompañarla no significa apropiarse del capítulo siguiente.')
])
rama('lumi_decision_sigue','Otra oportunidad, sus condiciones','Parque',[
 ('Lumi','Quiero intentar una vez más, pero con límites concretos. No necesito que estés de acuerdo.'),
 ('Cáceres','Es tu decisión. Espero que te cuides.'),
 ('Narrador','Lumi sigue con Max por ahora. No es una invitación para que Cáceres se quede esperando eternamente.')
])
rama('lumi_decision_presion','El ultimátum ajeno','Parque',[
 ('Lumi','Cáceres, no. Que Max me haya fallado no te da derecho a decirme con quién estar.'),
 ('Cáceres','Tienes razón. Me fui al carajo.'),
 ('Narrador','Lumi elige tomar distancia y ordenar su vida sin esa presión.')
])

evento('evento_cruce','La Quinta es un pañuelo','Terminal de Valparaíso',[
 ('Narrador','Cáceres espera una micro cuando oye dos voces conocidas. Lumi mira un mapa y Cony está comprando agua.'),
 ('Cony','Oe, ¡Cáceres! ¿Qué hacís acá?'),('Lumi','Ah, hola. ¿Nos conocemos?'),
 ('Narrador','Aún no se ha dicho ninguna mentira. La siguiente frase puede cambiar eso.')
],[
 C('Presentarlas por sus nombres y explicar cómo se conocen.','cruce_claro',{'honestidad':9,'confianza_lumi':5,'confianza_cony':5,'sospecha':-10},['cruce_honesto']),
 C('Decirle a cada una que la otra es solo «una desconocida».','cruce_mentira',{'honestidad':-25,'sospecha':45,'confianza_lumi':-10,'confianza_cony':-10},['mentira_grave']),
 C('Presentarlas y reconocer que ambas te interesan, sin esconderlo.','cruce_frente',{'honestidad':12,'claridad':10,'sospecha':-5},['cruce_honesto','intereses_declarados'])
], 'three fully dressed adult acquaintances meeting unexpectedly at Valparaiso bus terminal, awkward but nonviolent atmosphere')
rama('cruce_claro','La presentación','Terminal',[
 ('Cáceres','Lumi, ella es la Cony. Nos conocimos en una fiesta. Cony, Lumi, una amiga con la que hablo harto.'),
 ('Cony','Mucho gusto.'),('Lumi','Igual.'),
 ('Narrador','La situación es extraña, pero no tiene por qué convertirse en tragedia. La verdad deja espacio para conversar después.')
])
rama('cruce_mentira','La captura que faltaba','Terminal',[
 ('Cony','¿Desconocida? Si me saludaste recién, poh.'),
 ('Lumi','Cáceres, eso no sonó nada bien.'),
 ('Narrador','La mentira es tan torpe que ninguna necesita una captura de pantalla para entenderla.')
])
rama('cruce_frente','Sin andar a escondidas','Terminal',[
 ('Cáceres','Me gusta conversar con ambas, pero no quiero hacerlas creer que hay un compromiso que nadie pactó.'),
 ('Cony','Por lo menos sabís decirlo de frente.'),
 ('Lumi','No es una conversación para tener acá, pero agradezco que no inventes cosas.'),
 ('Narrador','La incomodidad no desaparece; al menos nadie fue engañado.')
])

evento('evento_lumi_espacio','Después de Max','Costanera de Viña',[
 ('Narrador','Lumi camina con un bolso pequeño. Tiene el celular silenciado por primera vez en semanas.'),
 ('Lumi','Lo echo de menos, y eso me da rabia. Me acostumbré a saber qué hacía a cada rato.'),
 ('Lumi','Quiero salir de eso sin pasar directo a depender de otra persona.'),
 ('Cáceres','Puedo acompañarte sin que esto se vuelva una cuenta pendiente.')
],[
 C('Respetar su espacio y hablar de sus proyectos.','lumi_espacio_bien',{'lumi':7,'confianza_lumi':12,'honestidad':5},['lumi_espacio_respetado']),
 C('Confesar que te gusta, pero dejar claro que no esperas respuesta.','lumi_espacio_confesion',{'lumi':8,'confianza_lumi':7,'claridad':5},['lumi_espacio_respetado']),
 C('Presionarla para comenzar una relación inmediatamente.','lumi_espacio_mal',{'confianza_lumi':-15,'sospecha':8})
], 'adult blue-haired woman glasses walking alone with adult male friend along sea promenade in Viña del Mar at golden hour')
rama('lumi_espacio_bien','Tiempo propio','Costanera',[
 ('Lumi','Me hace bien que no estés mirando la historia como una carrera.'),
 ('Cáceres','Además tenís unos proyectos buenísimos. Prefiero escucharte hablando de eso.'),
 ('Narrador','Terminan mirando las olas. La complicidad crece sin urgencia.')
])
rama('lumi_espacio_confesion','Una verdad sin presión','Costanera',[
 ('Lumi','Gracias por decirlo así. A mí también me pasa algo contigo, pero aún no estoy lista para definirlo.'),
 ('Cáceres','Está bien. Nadie te puso reloj.'),('Narrador','Las palabras quedan flotando sin volverse un contrato.')
])
rama('lumi_espacio_mal','No es el momento','Costanera',[
 ('Lumi','Te dije que necesitaba respirar. Si me apurás, me pasa lo mismo de siempre.'),
 ('Cáceres','Perdón. No te escuché.'),('Narrador','La tarde termina antes de lo esperado.')
])

evento('evento_cony_cambio','Cony no esperaba extrañarte','Café',[
 ('Cony','Te voy a decir algo, pero no te agrandís.'),('Cáceres','Ya me agrandé, perdón.'),
 ('Cony','JAJAJA. Me pasa que te extraño cuando no hablamos. Y yo juraba que este mes iba a estar cero romántica.'),
 ('Cony','No significa que mañana quiera pololear. Solo estoy pensando que quizá no sé todo lo que voy a querer.')
],[
 C('«No tienes que decidir nada hoy; me gusta conocerte.»','cony_cambio_libre',{'cony':9,'confianza_cony':12,'claridad':5},['cony_pareja_posible']),
 C('«A mí también me pasa, pero quiero ser cuidadoso.»','cony_cambio_honesto',{'cony':9,'confianza_cony':10,'honestidad':7},['cony_pareja_posible']),
 C('«Sabía que te iba a terminar convenciendo.»','cony_cambio_ego',{'cony':-10,'confianza_cony':-12,'sospecha':5})
], 'young adult couple at cafe counter, woman looking surprised by her own feelings, warm tone')
rama('cony_cambio_libre','Sin convencer a nadie','Café',[
 ('Cony','Me gusta que no andís intentando ganarme una discusión.'),
 ('Cáceres','Prefiero que lo conversemos cuando de verdad sepamos.'),
 ('Narrador','Cony sonríe. No se ha convertido mágicamente en otra persona; simplemente se permitió dudar.')
])
rama('cony_cambio_honesto','La posibilidad','Café',[
 ('Cony','Por eso mismo se siente distinto. No hay apuro.'),
 ('Cáceres','Pero hay ganas de seguir viéndonos.'),
 ('Narrador','Ambos asienten. Los dos saben que todavía faltan conversaciones.')
])
rama('cony_cambio_ego','No era un desafío','Café',[
 ('Cony','¿Convencerme? No es una competencia, Cáceres.'),
 ('Narrador','El comentario corta la confianza que venían construyendo.')
])

evento('evento_consejo_2','La asamblea sentimental','Discord',[
 ('Kevin','A ver, reporte del desastre. ¿Qué quieren ellas? ¿Qué querís tú? No, Jairo, tú no.'),
 ('Jairo','Yo solo iba a recomendar un tutorial sobre cómo hacer contacto visual.'),
 ('Seba','Hermano, la Cony ha sido clarísima con lo casual. Si después cambia, bien, pero no te basís en eso.'),
 ('Kevin','Y a la Lumi no le podís cobrar interés por haberla escuchado cuando Max la cagó.'),
 ('Cáceres','Ya, esa sí que me llegó.'),
 ('Jairo','Yo le hubiese mandado un corazoncito de agradecimiento, pero ya me retaron.')
],[
 C('Aceptar que un buen trato no compra amor.','consejo_2_madurez',{'amistad':9,'claridad':10,'honestidad':6},['consejo_segundo']),
 C('Agradecerles con una talla y seguir jugando.','consejo_2_talla',{'amistad':10,'claridad':3},['consejo_segundo'])
], 'four adult young Chilean gaming friends at desks with headphones, comedic evening group call')
rama('consejo_2_madurez','El consejo que se queda','Discord',[
 ('Seba','Eso, wn. Ser buena persona no es una inversión con retorno garantizado.'),
 ('Kevin','Y cuando no sepai, habla. Es mejor que tener tres finales inventados en la cabeza.'),
 ('Narrador','Cáceres anota la idea. Jairo sigue buscando su tutorial en silencio.')
])
rama('consejo_2_talla','Una noche liviana','Discord',[
 ('Jairo','¿Entonces les mando una encuesta de satisfacción de la relación?'),
 ('Kevin','DESINSTÁLALE EL EXCEL A ESTE CULIAO.'),
 ('Narrador','Cáceres se ríe. Hasta un consejo serio puede terminar bien en el grupo.')
])

evento('evento_lumi_sentimientos','No quiero reemplazar a nadie','Muelle · Viña del Mar',[
 ('Narrador','La tarde está fría. Lumi llegó antes; está mirando barcos y tarareando una canción.'),
 ('Lumi','Me está pasando algo contigo. Me gusta cómo me tratás, cómo no me pedís que desaparezca de mi propia vida.'),
 ('Lumi','Pero también me asusta. Cuando me enamoro me vuelvo demasiado pendiente, y no quiero repetir eso.'),
 ('Cáceres','Yo tampoco quiero que esto sea un reemplazo de Max.'),
 ('Narrador','Lumi le sostiene la mirada. Esta vez quiere decidir con la cabeza y el corazón, no con miedo.')
],[
 C('«Me gustai. Quiero intentarlo despacio, sin perder nuestros espacios.»','lumi_sentimientos_si',{'lumi':15,'confianza_lumi':12,'honestidad':7},['lumi_romance']),
 C('«Te quiero, pero ahora no estoy listo para una relación.»','lumi_sentimientos_no',{'confianza_lumi':8,'honestidad':10,'claridad':9},['lumi_amistad_elegida'],['lumi_romance']),
 C('«Prométeme que estarás pendiente de mí todo el día.»','lumi_sentimientos_mal',{'confianza_lumi':-20,'lumi':-12,'sospecha':10})
], 'adult blue-haired glasses woman confessing feelings to adult man on pier at sunset, emotional, fully clothed')
rama('lumi_sentimientos_si','Un beso con los pies en la tierra','Muelle',[
 ('Lumi','Eso sí me gusta. Quiero poder extrañarte sin angustiarme y seguir haciendo mis cosas.'),
 ('Cáceres','Y yo quiero conocerte, no vigilarte.'),
 ('Narrador','Se acercan y se besan. Después se separan con una risa nerviosa, sabiendo que una promesa todavía se construye día a día.')
])
rama('lumi_sentimientos_no','Una respuesta honesta','Muelle',[
 ('Lumi','Me duele un poco, pero te agradezco que no me des esperanzas falsas.'),
 ('Narrador','Caminan de vuelta por la costa. A veces una amistad sincera es la decisión correcta.')
])
rama('lumi_sentimientos_mal','El miedo se parece demasiado','Muelle',[
 ('Lumi','Eso es lo que me da miedo. No quiero depender de un mensaje para estar tranquila.'),
 ('Narrador','La conversación se enfría. El cariño no justifica controlar a nadie.')
])

evento('evento_cony_pareja','La posibilidad de algo serio','Plaza · Villa Alemana',[
 ('Cony','¿Te acordai cuando te dije que no quería pololo ni cagando?'),
 ('Cáceres','Imposible olvidarlo. Me lo dijiste como tres veces.'),
 ('Cony','Sigo siendo yo. Pero me gusta estar contigo incluso cuando no estamos coqueteando.'),
 ('Cony','Quiero preguntarte algo: ¿y si probamos salir en serio? No porque te lo deba, sino porque me nace.')
],[
 C('«Sí, pero definamos qué significa para nosotros.»','cony_pareja_si',{'cony':15,'confianza_cony':15,'honestidad':8},['cony_pareja','cony_limites_revisados'],['cony_casual_acordado']),
 C('«Me importas, pero prefiero mantenerlo casual.»','cony_pareja_casual',{'cony':5,'confianza_cony':8,'honestidad':8},['cony_casual_acordado','cony_limites_revisados'],['cony_pareja']),
 C('«No siento lo mismo. Mejor no sigamos como si nada.»','cony_pareja_no',{'honestidad':10,'claridad':10},['cony_cierre_amable'],['cony_pareja','cony_casual_acordado'])
], 'adult young couple in a Chilean town plaza, candid heart-to-heart conversation, emotional but hopeful')
rama('cony_pareja_si','Una relación elegida','Plaza',[
 ('Cony','Me gusta que me lo preguntís. Si vamos a ser pareja, quiero exclusividad y seguir teniendo mi vida.'),
 ('Cáceres','Eso también quiero. Si algo cambia, lo hablamos antes de hacernos daño.'),
 ('Narrador','Cony le toma la mano. Por primera vez, la palabra pareja nació de ella sin que nadie la presionara.')
])
rama('cony_pareja_casual','Seguimos como hablamos','Plaza',[
 ('Cony','Me dio nervio preguntarlo, pero gracias por ser claro.'),
 ('Cáceres','Me gusta compartir contigo. Solo que no quiero prometer una relación que no siento.'),
 ('Narrador','Deciden seguir viéndose sin exclusividad. La conversación evita falsas expectativas.')
])
rama('cony_pareja_no','El cierre amable','Plaza',[
 ('Cony','Me da pena, pero prefiero esto a hacer como que queremos lo mismo.'),
 ('Narrador','Se despiden bien. A veces una historia se termina por respeto, no por una pelea.')
])

evento('evento_cony_reglas','Lo casual también se conversa','Chat nocturno · Cony',[
 ('Cony','Oe, necesito confirmar algo. Dijimos que lo nuestro puede ser casual. ¿Seguimos bien con eso?'),
 ('Cony','No quiero que ninguno asuma que puede exigir exclusividad sin hablarlo.'),
 ('Cáceres','Sí, prefiero ser claro ahora a que después sea una cagá.')
],[
 C('«Sí. Si conocemos a otra persona lo conversamos con respeto.»','cony_reglas_si',{'confianza_cony':12,'honestidad':9,'claridad':6},['cony_limites_revisados']),
 C('«En verdad necesito exclusividad. No quiero mentirte.»','cony_reglas_no',{'confianza_cony':7,'honestidad':10,'claridad':7},['cony_necesita_revisar'],['cony_limites_revisados']),
 C('«No preguntís tanto; mejor no hablemos de eso.»','cony_reglas_oculta',{'confianza_cony':-12,'honestidad':-12,'sospecha':12})
], 'adult young woman on phone at night in her living room, thoughtfully discussing boundaries, fully clothed')
rama('cony_reglas_si','Sin trampas','Chat',[
 ('Cony','Perfecto. Eso me deja tranquila.'),
 ('Narrador','No se prometieron lo que no quieren. Es un acuerdo concreto, no una insinuación romántica automática.')
])
rama('cony_reglas_no','Cambiar de opinión','Chat',[
 ('Cony','Gracias por hablarlo. Yo todavía no sé si quiero eso.'),
 ('Narrador','Quedan de pensar antes de seguir. El deseo no reemplaza la compatibilidad.')
])
rama('cony_reglas_oculta','La pregunta sigue ahí','Chat',[
 ('Cony','Para mí no es drama hablarlo, pero sí lo sería que lo escondieras.'),
 ('Narrador','Cáceres deja el chat en silencio. Lo pendiente no desaparece.')
])

evento('evento_acuerdos','Una mesa para hablar sin secretos','Café frente al mar · Viña',[
 ('Narrador','Cáceres propone una conversación transparente. Lumi ya terminó su relación con Max y tomó tiempo para sí misma. Cony conoce el interés de Cáceres por Lumi.'),
 ('Cáceres','Me importan las dos, pero no voy a fingir que los sentimientos son una competencia. Quiero preguntar algo, sin esperar que acepten.'),
 ('Lumi','Yo suelo vincularme con mucha intensidad. No voy a tolerar secretos ni sentir que pierdo mi espacio.'),
 ('Cony','Y yo no quiero que me presenten como la polola de nadie si no elegí serlo.'),
 ('Narrador','Nadie está obligado a decir que sí. Se conversa sobre límites, cuidado, comunicación y el derecho a retirarse.')
],[
 C('Proponer vínculos no exclusivos, con acuerdos explícitos de ambas.','acuerdos_si',{'honestidad':10,'confianza_lumi':8,'confianza_cony':8,'claridad':10},['acuerdo_conversado','acuerdo_lumi','acuerdo_cony'],['lumi_exclusiva','cony_pareja']),
 C('Aceptar que no estás listo para ese acuerdo.','acuerdos_no',{'honestidad':8,'claridad':8},['acuerdo_conversado'],['acuerdo_lumi','acuerdo_cony']),
 C('Insistir en que deben aceptarlo porque las quieres.','acuerdos_presion',{'honestidad':-20,'sospecha':20,'confianza_lumi':-15,'confianza_cony':-15},[],['acuerdo_lumi','acuerdo_cony'])
], 'three fully clothed adults at bright seaside cafe discussing boundaries respectfully with clear physical space between them')
rama('acuerdos_si','Sí, pero con condiciones','Café frente al mar',[
 ('Lumi','Quiero intentar un vínculo no exclusivo contigo, pero sin controlarnos. Y quiero poder decir basta si me supera.'),
 ('Cony','Yo también acepto seguir viéndote sabiendo que estás con Lumi. No estoy prometiendo pololeo ni que esto dure para siempre.'),
 ('Cáceres','Entonces ninguno se entera por terceros. Hablamos de límites, protección y expectativas antes de cualquier paso íntimo.'),
 ('Narrador','Las tres personas consienten libremente a explorar relaciones no exclusivas. Esto NO implica que Lumi y Cony tengan una relación entre ellas ni encuentros sexuales compartidos.'),
 ('Narrador','El acuerdo recién comienza. Será tan honesto como las conversaciones que mantengan después.')
])
rama('acuerdos_no','Más fácil decir no a tiempo','Café',[
 ('Cáceres','No quiero prometer algo que no sé manejar. Prefiero decidir con quién quiero estar o tomarme un tiempo.'),
 ('Lumi','Gracias por decirlo.'),('Cony','Sí, mucho mejor que improvisar a escondidas.'),
 ('Narrador','La mesa se vacía sin escándalo. Todavía quedan decisiones.')
])
rama('acuerdos_presion','Eso no es un acuerdo','Café',[
 ('Cony','No, Cáceres. Un acuerdo no sirve si alguien siente que tiene que aceptarlo.'),
 ('Lumi','Yo vine a conversar, no a que me vendan una decisión tomada.'),
 ('Narrador','Ambas se van. La falta de consentimiento no se arregla con mejores estadísticas.')
])

evento('evento_definir_rumbo','El futuro no se decide solo','Mirador · Noche',[
 ('Narrador','Falta poco para terminar el mes. Cáceres se queda mirando las luces y revisa las conversaciones importantes que tuvo.'),
 ('Cáceres','No puedo tener todo lo que quiera sin hablar de lo que eso significa para los demás.'),
 ('Narrador','Puede elegir una forma de vincularse o reconocer que necesita estar solo. Algunas opciones dependen de lo que ocurrió antes.')
],[
 C('Elegir exclusividad con Lumi, y explicárselo a Cony.','rumbo_lumi',{'honestidad':8,'claridad':7},['lumi_exclusiva'],['cony_pareja','cony_casual_acordado','acuerdo_lumi','acuerdo_cony','acuerdo_conversado'],[{'evento':'lumi_romance'},{'evento':'max_ruptura'}]),
 C('Elegir una relación con Cony, y cerrar otros vínculos con respeto.','rumbo_cony',{'honestidad':8,'claridad':7},['cony_pareja'],['lumi_romance','lumi_exclusiva','acuerdo_lumi','acuerdo_cony','acuerdo_conversado'],[{'evento':'cony_pareja_posible'}]),
 C('Mantener el acuerdo casual con Cony, sin fingir noviazgo.','rumbo_casual',{'honestidad':6,'claridad':8},['cony_casual_acordado','cony_limites_revisados'],['cony_pareja','lumi_romance','lumi_exclusiva','acuerdo_lumi','acuerdo_cony','acuerdo_conversado'],[{'evento':'cony_casual_acordado'}]),
 C('Continuar con los acuerdos no exclusivos ya aceptados.','rumbo_acuerdos',{'honestidad':5,'claridad':5},[],[],[{'evento':'acuerdo_lumi'},{'evento':'acuerdo_cony'},{'evento':'acuerdo_conversado'}]),
 C('Priorizar crecer personalmente y no iniciar un compromiso.','rumbo_solo',{'honestidad':9,'claridad':15,'amistad':7},['priorizo_crecer'],['lumi_exclusiva','cony_pareja','cony_casual_acordado','acuerdo_lumi','acuerdo_cony','acuerdo_conversado','lumi_romance'])
], 'adult young man contemplative on night viewpoint over Viña del Mar, moonlight, crossroads of relationships')
rama('rumbo_lumi','Hablar con ambas','Mensajes y costa',[
 ('Cony','Gracias por decírmelo. No me agrada perder algo bonito, pero no voy a aceptar que me mientan.'),
 ('Lumi','Si vamos a intentar ser pareja, hagámoslo sin juegos de control ni secretos.'),
 ('Cáceres','Eso quiero. No porque me sienta obligado, sino porque lo elijo.'),
 ('Narrador','La exclusividad nace de un acuerdo real. La historia casual con Cony se cierra con claridad.')
])
rama('rumbo_cony','Ponerle nombre a lo elegido','Plaza y mensajes',[
 ('Lumi','Entiendo. Necesitaba saberlo. Gracias por no dejarme esperando.'),
 ('Cony','Entonces lo intentamos de verdad, pero sin convertirnos en otra gente.'),
 ('Cáceres','Trato hecho.'),('Narrador','Cony eligió explorar una relación; no fue un premio por insistir.')
])
rama('rumbo_casual','Elegir sin inventar un pololeo','Mensajes',[
 ('Cony','Me gusta así, con confianza y sin inventarnos algo que no hemos querido.'),
 ('Cáceres','Y si algún día cambia, te lo digo antes, no después.'),
 ('Narrador','Siguen viéndose sin exclusividad. Existe intimidad si ambos la desean, pero no hay obligación ni promesa de ser pareja.')
])
rama('rumbo_acuerdos','Una elección revisable','Café',[
 ('Lumi','Seguimos con lo que conversamos. Necesito poder decir si algo me incomoda.'),
 ('Cony','Y cero secretos. Lo demás, lo veremos.'),
 ('Narrador','No se trata de coleccionar parejas; se trata de continuar solo con el consentimiento de todos.')
])
rama('rumbo_solo','La libertad de no elegir pareja','Mirador',[
 ('Cáceres','Me gustó conocerlas, pero necesito aprender a decidir sin miedo a quedarme solo.'),
 ('Kevin (mensaje)','Buena, wn. Y si necesitai una noche de juegos, estamos.'),
 ('Narrador','Elegir estar solo es una opción, no una derrota.')
])

evento('evento_vispera','Una última conversa','Discord · Noche',[
 ('Seba','Y entonces, ¿qué aprendimos de este mes de drama premium?'),
 ('Cáceres','Que escuchar no te garantiza un beso. Y un beso no garantiza una relación.'),
 ('Kevin','Mira qué bonito habla el wn. Casi me emociono.'),
 ('Jairo','Yo aprendí que no hay que mandar un Excel con horarios a la primera cita.'),
 ('Kevin','ESO ES LO ÚNICO QUE TENÍAS QUE APRENDER, JAIRO.'),
 ('Narrador','Los cabros se ríen. Lo que ocurra mañana dependerá de las decisiones ya tomadas.')
],[
 C('«Quiero seguir creciendo, tenga o no pareja.»','vispera_crece',{'amistad':10,'claridad':12,'honestidad':5},['priorizo_crecer']),
 C('«Lo importante es no seguir mintiéndome.»','vispera_sincero',{'amistad':8,'honestidad':8,'claridad':5}),
 C('«Lo importante es que no me quede más calvo.»','vispera_pelo',{'amistad':10})
], 'humorous heartfelt final Discord gaming call among four adult Chilean friends')
rama('vispera_crece','Hay vida más allá del chat','Discord',[
 ('Seba','Por fin una frase con sentido.'),('Jairo','¿Y la calvicie?'),('Kevin','La calvicie es el DLC inevitable.'),
 ('Narrador','Cáceres se despide del grupo y mira su agenda. Mañana habrá otra página.')
])
rama('vispera_sincero','Una conclusión útil','Discord',[
 ('Kevin','Esa es la idea, hermano. La honestidad no asegura un final perfecto, pero evita muchas cagadas.'),
 ('Narrador','La llamada se alarga entre chistes y recuerdos.')
])
rama('vispera_pelo','Un tema eterno','Discord',[
 ('Seba','JAJAJA. No puedo hacerte promesas ahí.'),
 ('Jairo','Yo tengo un enlace a un tratamiento milagroso.'),
 ('Kevin','BORRA ESE ENLACE, JAIRO.'),('Narrador','El grupo estalla de risa.')
])

# FINALES: cada uno describe de modo literal qué relación existe y qué NO sucedió.
S('finales','final_lumi','FINAL AZUL — Elegirse sin perderse','Costanera · Un mes después',[
 ('Narrador','La relación entre Lumi y Max terminó antes de que empezara este romance. Lumi se dio tiempo para recuperar sus propias rutinas.'),
 ('Lumi','Te quiero, Cáceres. Mucho. Tanto que a veces me asusta volver a estar pendiente del celular.'),
 ('Cáceres','Podemos querernos y seguir haciendo nuestras cosas. No quiero que te pierdas por estar conmigo.'),
 ('Lumi','Entonces hagamos eso: pareja, exclusividad conversada, y espacios propios.'),
 ('Narrador','Se besan frente al mar. No hay un salvador ni una víctima agradecida: dos adultos eligieron una relación y decidieron cuidarla.'),
 ('Kevin (mensaje)','Oe, ¿y entonces dejaste la soltería? Milagro de la Quinta.'),
 ('Seba (mensaje)','Déjalo ser feliz, wn. Y que no se olvide de los cabros.'),
 ('Narrador','FIN AZUL: Cáceres y Lumi empiezan una relación. Cony siguió su camino, con un cierre honesto.')
],prompt=visual('Viña del Mar seaside promenade sunset','adult blue-haired woman with glasses and adult man holding hands and kissing briefly, sea breeze, hopeful atmosphere'))
S('finales','final_cony_pareja','FINAL ROSA — La que no quería pololear','Plaza de Villa Alemana',[
 ('Narrador','Cony llegó a la historia queriendo algo casual. Nadie la convenció ni la ganó; con el tiempo descubrió que quería explorar otra cosa.'),
 ('Cony','Todavía me parece raro decir «mi pololo». No porque me dé vergüenza, sino porque pensé que no iba a quererlo.'),
 ('Cáceres','Si algún día dejai de quererlo, hablémoslo. No quiero que te sintai encerrada.'),
 ('Cony','Ya, pero por ahora ven acá, tontito.'),
 ('Narrador','Cony lo besa en plena plaza. Los dos acuerdan exclusividad y seguir teniendo sus vidas propias.'),
 ('Jairo (mensaje)','Tengo un formato de Excel para aniversario.'),
 ('Kevin (mensaje)','QUE ALGUIEN LE QUITE EL COMPUTADOR A ESTE WN.'),
 ('Narrador','FIN ROSA: Cáceres y Cony son pareja por elección mutua. Lo casual no fue un contrato eterno.')
],prompt=visual('Villa Alemana town plaza daylight','adult couple embracing and kissing, young woman playful confident, everyday romantic ending'))
S('finales','final_cony_casual','FINAL MAGENTA — Sin ponerle nombre','Café · Villa Alemana',[
 ('Cony','¿Seguimos en la misma? Me gustai y la paso bien contigo, pero hoy no quiero pololeo.'),
 ('Cáceres','Sí. Y si un día deja de hacerme bien, te lo voy a decir.'),
 ('Cony','Eso. Cero dramas de pololeo imaginario.'),
 ('Narrador','Su relación es casual y acordada, sin exclusividad. Se gustan, salen y deciden juntos cuándo hay intimidad. No hay una obligación sexual ni una promesa futura.'),
 ('Seba (mensaje)','Ojo, hermano, un vínculo casual igual se cuida.'),
 ('Kevin (mensaje)','Y si querís algo distinto algún día, dilo antes de hacerte el mártir.'),
 ('Narrador','FIN MAGENTA: No son pololos. Son dos adultos que mantienen un vínculo casual por voluntad de ambos.')
],prompt=visual('cozy cafe in Villa Alemana','two fictional adults laughing over coffee, relaxed independent atmosphere, no physical intimacy'))
S('finales','final_acuerdos','FINAL VIOLETA — Todas las cartas sobre la mesa','Mesa frente al mar',[
 ('Narrador','Cáceres habló con ambas antes de que existieran compromisos contradictorios. Lumi terminó con Max y eligió explorar un vínculo nuevo; Cony mantuvo su deseo de no ser polola.'),
 ('Lumi','Yo te quiero, pero necesito cuidar mis espacios. Y poder decir si esto ya no me hace bien.'),
 ('Cony','A mí me gustai y acepto seguir viéndote sabiendo que también sales con Lumi. No me prometai lo que no quiero.'),
 ('Cáceres','Entonces acordamos transparencia, límites, protección y la libertad de cualquiera para cambiar de opinión.'),
 ('Narrador','Los tres consienten relaciones no exclusivas entre Cáceres y cada una. Lumi y Cony no son pareja entre sí; nadie asume encuentros sexuales compartidos.'),
 ('Jairo (mensaje)','¿Y eso lo pongo en una planilla...?'),
 ('Kevin (mensaje)','NO, JAIRO.'),
 ('Narrador','FIN VIOLETA: vínculos no exclusivos claros. No significa pareja entre ellas ni escenas íntimas compartidas.')
],prompt=visual('bright seaside cafe in Viña del Mar','three adult friends seated at a table with comfortable interpersonal distance, honest conversation and sea in background'))
S('finales','final_descubierto','FINAL GRIS — La mentira tiene piernas','Terminal · Valparaíso',[
 ('Narrador','Cáceres intentó evitar una conversación incómoda inventando versiones diferentes de la misma historia.'),
 ('Cony','No me molesta que hablís con otras personas. Me molesta que mintai cuando te pregunto de frente.'),
 ('Lumi','Yo ya venía de una relación donde me ocultaban cosas. No voy a quedarme para repetir eso.'),
 ('Cáceres','La cagué. No fue una confusión: elegí esconderlo.'),
 ('Seba','No te vamos a celebrar una cagá porque te caemos bien.'),
 ('Kevin','Podís aprender, pero no exigir que te perdonen.'),
 ('Narrador','Ambas deciden tomar distancia. El final no castiga el deseo; muestra la consecuencia de mentir.'),
 ('Jairo','Yo había dicho que no hiciera Excel... bueno, técnicamente sí lo dije.'),
 ('Narrador','FIN GRIS: Cáceres queda soltero. La confianza no se recupera con un botón.')
],prompt=visual('Valparaiso bus terminal, overcast daylight','adult man standing by himself as two adult women walk away in different directions, nonviolent dramatic scene'))
S('finales','final_crecimiento','FINAL SECRETO — La Quinta sigue girando','Mirador · Amanecer',[
 ('Narrador','Cáceres cierra un mes lleno de conversaciones, besos posibles y decisiones difíciles. No comienza una relación nueva.'),
 ('Cáceres','Me gustó conocerlas. Pero también aprendí que puedo estar tranquilo conmigo mismo.'),
 ('Seba','Hermano, eso es más maduro que la mitad de las cosas que dijimos en Discord.'),
 ('Kevin','Habla por ti, wn. Yo di consejos de lujo.'),
 ('Jairo','Yo propuse lo del Excel. Claramente fui clave.'),
 ('Kevin','LA CONCHETUMARE, JAIRO.'),
 ('Narrador','Lumi sigue construyendo su propia vida. Cony sigue decidiendo qué vínculos quiere. Cáceres se reúne con sus amigos para desayunar.'),
 ('Narrador','FIN SECRETO: no es necesario conquistar a nadie para terminar una historia con dignidad.')
],prompt=visual('Viña del Mar sunrise lookout','four adult male friends laughing over takeaway breakfasts, warm pastel dawn and hills'))
S('finales','final_lumi_max','FINAL ÍNDIGO — Ella decidió quedarse','Parque de Viña',[
 ('Narrador','Lumi eligió intentar otra vez su relación con Max. No fue una decisión de Cáceres; él no podía decidir por ella.'),
 ('Lumi','Te agradezco que me escucharas. Sigo intentando entender qué necesito, pero no quiero darte falsas esperanzas.'),
 ('Cáceres','Está bien. Tampoco voy a quedarme esperando a que cambies de opinión.'),
 ('Narrador','Se despiden. Cáceres se aleja sin sentirse dueño de ninguna respuesta.'),
 ('Seba (mensaje)','No te estacionís sentimentalmente esperando algo que no depende de ti.'),
 ('Narrador','FIN ÍNDIGO: Lumi continúa con Max por ahora. Cáceres sigue su vida.')
],prompt=visual('urban park in Viña del Mar','adult blue-haired woman with glasses and adult man saying friendly goodbye at a crossroads, respectful bittersweet ending'))
S('finales','final_calvo','FINAL CALVO — La última entrada','Discord · Epílogo',[
 ('Narrador','Treinta días. Docenas de mensajes. Montones de bromas. Ningún acuerdo romántico se convirtió en una relación.'),
 ('Cáceres','No voy a mentir: pensé que iba a salir pololeando. Capaz no era el momento.'),
 ('Kevin','Estar soltero no es perder, hermano. Pero te pegaste el speedrun de indecisión más largo del mundo.'),
 ('Seba','Lo importante es que no te inventís un final que nadie aceptó.'),
 ('Jairo','Yo propongo un DLC de peluquería para cerrar el arco.'),
 ('Cáceres','JAIRO CONCHETUMARE.'),
 ('Narrador','Se ríen. Cáceres apaga el monitor y sale a vivir otro día.'),
 ('Narrador','FIN CALVO: no hubo romance esta vez, pero sí una historia que recordar.')
],prompt=visual('adult bedroom with gaming computer', 'adult man at computer with friends on virtual voice chat, funny relaxed ending, violet screen glow'))

# Recupera la asignación visual, sin modificar decisiones ni diálogos nuevos.
for grupo in scenes.values():
    for escena in grupo:
        escena.update(VISUALES_PREVIOS.get(escena['id'], {}))

# Corrige la rama del final violeta si quedase mal escrito / evita semicolon accidental.
for name, data in scenes.items():
    with (DEST/f'{name}.json').open('w',encoding='utf-8') as fh:
        json.dump(data,fh,ensure_ascii=False,indent=2)
        fh.write('\n')
rows = [(name,s['id'],s['titulo'],s['lugar'],s['direccionVisual'],s['promptImagen']) for name,group in scenes.items() for s in group]
with (ROOT/'docs/PROMPTS_VISUALES.csv').open('w',newline='',encoding='utf-8-sig') as fh:
    out=csv.writer(fh); out.writerow(['archivo','id','titulo','lugar','direccionVisual','promptImagen']); out.writerows(rows)
print('ESCENAS',len(rows),'DIALOGOS',sum(len(s['dialogos']) for g in scenes.values() for s in g),'DECISIONES',sum(len(s.get('decisiones',[])) for g in scenes.values() for s in g))
