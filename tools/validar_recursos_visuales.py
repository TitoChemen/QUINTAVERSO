"""Comprueba 205 escenas, 8 CG, fondos y PNG. Sin dependencias externas."""
import json
import pathlib
import struct
from collections import Counter

ROOT = pathlib.Path(__file__).resolve().parents[1]
RES = ROOT / 'src/main/resources'
HISTORIAS = RES / 'historias/caceres'
IMAGENES = RES / 'imagenes'
FINAL_IDS = {'final_lumi','final_cony_pareja','final_cony_casual','final_acuerdos',
             'final_descubierto','final_crecimiento','final_lumi_max','final_calvo'}
ERRORES = []

def png_valido(archivo):
    if not archivo.is_file():
        ERRORES.append('FALTA '+str(archivo.relative_to(ROOT)));return False
    data=archivo.open('rb').read(24)
    if not data.startswith(b'\x89PNG\r\n\x1a\n') or len(data)<24:
        ERRORES.append('No es PNG valido: '+str(archivo));return False
    w,h=struct.unpack('>II',data[16:24]);
    if w < 100 or h < 100:
        ERRORES.append('Resolucion invalida: '+str(archivo));return False
    return True

escenas=[]
for archivo in HISTORIAS.glob('*.json'):
    escenas.extend(json.loads(archivo.read_text(encoding='utf-8')))
for escena in escenas:
    ruta=IMAGENES/'fondos'/escena.get('fondo','')
    png_valido(ruta)
    if escena['id'] in FINAL_IDS:
        cg=escena.get('cg')
        if not cg:
            ERRORES.append('Final sin CG: '+escena['id'])
        else:png_valido(IMAGENES/'cg'/cg)
    elif escena.get('cg'):
        ERRORES.append('CG se mostraria fuera del epilogo: '+escena['id'])

n_personajes={}
for personaje in ('caceres','cony','jairo','kevin','lumi','seba'):
    files=list((IMAGENES/'personajes'/personaje).glob('*.png'))
    n_personajes[personaje]=len(files)
    for archivo in files:png_valido(archivo)
if len(escenas)!=205:ERRORES.append(f'Se esperaban 205 escenas, hay {len(escenas)}')
if n_personajes['kevin']:
    print('Kevin tiene sprites: puedes cambiar el fallback en RecursosVisuales.java')
for final in FINAL_IDS:
    if sum(e['id']==final for e in escenas)!=1:ERRORES.append('Final no unico: '+final)
print('Escenas:',len(escenas),'| CG:',sum(bool(e.get('cg')) for e in escenas),
      '| Fondos:',len(list((IMAGENES/'fondos').glob('*.png'))))
print('Sprites/retratos:',n_personajes)
print('Uso de fondos:')
for k,v in Counter(e.get('fondo') for e in escenas).most_common():print(' ',k,v)
if ERRORES:
    print('\nERRORES:',*ERRORES,sep='\n  - ')
    raise SystemExit(1)
print('\nVALIDACION VISUAL OK: todas las rutas graficas existen y son PNG.')
