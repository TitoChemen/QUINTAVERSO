"""Prueba estructural del guion JSON; no requiere Maven ni otras dependencias."""
from pathlib import Path
import json
import re
from collections import Counter, deque

ROOT = Path(__file__).resolve().parents[1]
CARPETA = ROOT / 'src/main/resources/historias/caceres'
ARCHIVOS = ('prologo', 'lumi', 'cony', 'amigos', 'otros', 'eventos', 'finales')
ESCENAS = {}

for archivo in ARCHIVOS:
    ruta = CARPETA / (archivo + '.json')
    if not ruta.exists():
        raise AssertionError(f'Falta {ruta}')
    escenas = json.loads(ruta.read_text('utf-8'))
    if not isinstance(escenas, list) or not escenas:
        raise AssertionError(f'Archivo vacío o mal estructurado: {archivo}')
    for escena in escenas:
        clave = escena.get('id')
        if not clave or clave in ESCENAS:
            raise AssertionError(f'ID ausente o repetida: {clave} en {archivo}')
        if not escena.get('dialogos'):
            raise AssertionError(f'No hay diálogos en {clave}')
        if not escena.get('promptImagen') or not escena.get('direccionVisual'):
            raise AssertionError(f'Falta dirección visual o prompt en {clave}')
        ESCENAS[clave] = escena

for escena in ESCENAS.values():
    enlaces = [escena.get('siguiente')]
    for decision in escena.get('decisiones', []):
        if not decision.get('texto'):
            raise AssertionError(f'Decisión sin texto en {escena["id"]}')
        enlaces.append(decision.get('siguiente'))
        for clave in ('eventos','desactivarEventos'):
            if not all(isinstance(v,str) and v for v in decision.get(clave,[])):
                raise AssertionError(f'Eventos mal declarados en {escena["id"]}')
        for cond in decision.get('condiciones', []):
            if not any(k in cond for k in ('estadistica','evento','diaDesde','diaHasta','turno')):
                raise AssertionError(f'Condición vacía en {escena["id"]}')
    for destino in enlaces:
        if destino and destino not in ESCENAS:
            raise AssertionError(f'{escena["id"]} enlaza a {destino}, que no existe')

for grupo, cantidad in (('lumi',24),('cony',24),('discord',18),('reflexion',12),('paseo',12)):
    for numero in range(1,cantidad+1):
        assert f'{grupo}_{numero}' in ESCENAS, f'Falta {grupo}_{numero}'
    for numero in range(1,5):
        assert f'{grupo}_rutina_{numero}' in ESCENAS, f'Falta {grupo}_rutina_{numero}'

rutas = ('final_lumi','final_cony_pareja','final_cony_casual',
         'final_acuerdos','final_descubierto','final_crecimiento',
         'final_lumi_max','final_calvo')
assert all(r in ESCENAS for r in rutas), 'Falta un epílogo'

java = (ROOT/'src/main/java/com/example/quintaverso/motor/GestorEventos.java').read_text('utf-8')
ids_eventos = set(re.findall(r'"(evento_[a-z0-9_]+)"', java))
for identificador in ids_eventos:
    assert identificador in ESCENAS, f'Java ofrece una escena ausente: {identificador}'

# Las escenas iniciables y sus descendientes deben ser navegables.
inicios = {'prologo_inicio',*ids_eventos,*rutas}
for grupo,cantidad in (('lumi',24),('cony',24),('discord',18),('reflexion',12),('paseo',12)):
    inicios.update(f'{grupo}_{i}' for i in range(1,cantidad+1))
    inicios.update(f'{grupo}_rutina_{i}' for i in range(1,5))
visitadas = set()
cola = deque(inicios)
while cola:
    ident = cola.popleft()
    if ident in visitadas: continue
    visitadas.add(ident)
    escena = ESCENAS[ident]
    destinos = [escena.get('siguiente')]+[d.get('siguiente') for d in escena.get('decisiones',[])]
    for dest in destinos:
        if dest and dest not in visitadas: cola.append(dest)
huerfanas = set(ESCENAS)-visitadas
assert not huerfanas, f'Escenas sin una ruta de entrada: {sorted(huerfanas)}'

print('VALIDACIÓN OK')
print(f'{len(ESCENAS)} escenas | '
      f'{sum(len(s["dialogos"]) for s in ESCENAS.values())} diálogos | '
      f'{sum(len(s.get("decisiones",[])) for s in ESCENAS.values())} decisiones')
print(f'{len(ids_eventos)} eventos únicos | {len(rutas)} finales | '
      f'{len(visitadas)} escenas alcanzables estructuralmente')
