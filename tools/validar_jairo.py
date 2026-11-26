#!/usr/bin/env python3
"""Valida JSON/recursos y ejecuta los recorridos con las clases Java reales, sin Maven."""
import json, subprocess, tempfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/'src/main/resources'; JAVA=ROOT/'src/main/java/com/example/quintaverso'
scenes={}
for p in (RES/'historias/jairo').glob('*.json'):
 for s in json.loads(p.read_text(encoding='utf-8')):
  assert s['id'] not in scenes,s['id']; scenes[s['id']]=s
for s in scenes.values():
 for item in [s,*s.get('decisiones',[])]:
  assert not item.get('siguiente') or item['siguiente'] in scenes,item
 for key,folder in [('cg','cg'),('fondo','fondos')]:
  if s.get(key): assert (RES/'imagenes'/folder/s[key]).is_file(),s[key]
 assert s.get('dialogos'),s['id']
# Generate Java Decision/Condicion instances straight from authored JSON, including effects and flags.
def q(x):return json.dumps(x,ensure_ascii=False)
def lst(x):return 'List.of('+','.join(q(t) for t in x)+')'
code=['import java.util.*;','import java.nio.file.*;','import com.example.quintaverso.motor.*;','import com.example.quintaverso.guardado.*;','import com.example.quintaverso.RecursosVisuales;','public class VerificarJairo {','static Map<String,List<Decision>> escenas=new HashMap<>();','static Map<String,String> siguientes=new HashMap<>();']
cargas=[]
for n,s in enumerate(scenes.values()):
 cargas.append(f'cargar{n}();')
 code.append(f'static void cargar{n}(){{ List<Decision> ds=new ArrayList<>();')
 if s.get('siguiente'):code.append('siguientes.put('+q(s['id'])+','+q(s['siguiente'])+');')
 for d in s.get('decisiones',[]):
  code.append('{ Decision x=new Decision(); x.setTexto('+q(d['texto'])+');')
  if d.get('siguiente'):code.append('x.setSiguiente('+q(d['siguiente'])+');')
  code.append('x.setEventos('+lst(d.get('eventos',[]))+'); x.setDesactivarEventos('+lst(d.get('desactivarEventos',[]))+');')
  code.append('x.setEfectos(Map.ofEntries('+','.join('Map.entry('+q(k)+','+str(v)+')' for k,v in d.get('efectos',{}).items())+'));')
  for c in d.get('condiciones',[]):
   code.append('{ Condicion z=new Condicion();')
   for k,v in c.items():
    val=q(v) if isinstance(v,str) else str(v).lower()
    if k=='turno': val='CalendarioJuego.Turno.'+v
    code.append('z.set'+k[0].upper()+k[1:]+'('+val+');')
   code.append('x.getCondiciones().add(z); }')
  code.append('ds.add(x); }')
 code.append('escenas.put('+q(s['id'])+',ds); }')
code.append('static void cargar(){'+''.join(cargas)+'}')
code.append('''static void check(boolean c,String m){if(!c)throw new AssertionError(m);}
static void ruta(String color,Map<Integer,Integer> elecciones){ruta(color,elecciones,Map.of());}
static void ruta(String color,Map<Integer,Integer> elecciones,Map<Integer,Integer> extras){
 EstadoPartida e=new EstadoPartida("jairo"); CalendarioJuego cal=new CalendarioJuego(e);
 for(int dia=1;dia<=30;dia++){
  var h=HitosJairo.pendiente(e); check(h!=null,"Falta hito "+dia);
  String nodo=h.escenaId(); boolean primero=true; int pasos=0;
  while(nodo!=null){check(++pasos<=8,"Ciclo "+nodo);var ds=escenas.get(nodo);check(ds!=null,"Escena inexistente "+nodo);
    if(ds.isEmpty()){nodo=siguientes.get(nodo);continue;}
    var disponibles=ds.stream().filter(d->d.disponible(e)).toList();check(!disponibles.isEmpty(),"Sin decisión "+nodo);
    Decision dec=primero&&elecciones.containsKey(dia)?ds.get(elecciones.get(dia)):nodo.startsWith("jairo_extra_")&&extras.containsKey(dia)?ds.get(extras.get(dia)):disponibles.get(0);check(dec.disponible(e),color+" decisión inválida día "+dia+" "+nodo+" "+e.getEstadisticas());dec.aplicar(e);nodo=dec.getSiguiente();primero=false;
  }
  check(pasos>=2,"Falta interludio día "+dia);
  e.registrarEvento("jairo_hito:"+h.id()); check(HitosJairo.pendiente(e)==null,"Hito repetido");
  cal.dormirHastaManana();
 }
 String fin=FinalesJairo.calcular(e); check(fin.equals("final_jairo_"+color),color+" -> "+fin+" "+e.getEventos()+" "+e.getEstadisticas());
 check(escenas.containsKey(fin),fin); System.out.println("OK: "+color);
}
static void opcionAleatoria(EstadoPartida e, Random rnd){
 var opciones=GestorEventosJairo.disponibles(e);
 for(var a:opciones)check(escenas.containsKey(a.escenaId()),"Actividad sin escena "+a.escenaId());
 if(opciones.isEmpty()||!rnd.nextBoolean())return;
 var a=opciones.get(rnd.nextInt(opciones.size())); var ds=escenas.get(a.escenaId()).stream().filter(d->d.disponible(e)).toList();
 check(!ds.isEmpty(),"Actividad sin opción "+a.id());ds.get(rnd.nextInt(ds.size())).aplicar(e);e.registrarEvento("accion:"+a.id());
}
static void stress(){
 Random rnd=new Random(20261126);Map<String,Integer> totales=new TreeMap<>();
 for(int n=0;n<5000;n++){
  EstadoPartida e=new EstadoPartida("jairo");CalendarioJuego cal=new CalendarioJuego(e);
  for(int dia=1;dia<=30;dia++){
   var h=HitosJairo.pendiente(e);check(h!=null,"Falta hito "+dia);
   while(!HitosJairo.tocaCapitulo(e,h)){opcionAleatoria(e,rnd);cal.avanzarTurno();}
   check(GestorEventosJairo.disponibles(e).isEmpty(),"Actividad durante capítulo");
   String nodo=HitosJairo.pendiente(e).escenaId();int pasos=0;
   while(nodo!=null){check(++pasos<=8,"Ciclo "+nodo);var ds=escenas.get(nodo);check(ds!=null,"Nodo "+nodo);
    if(ds.isEmpty()){nodo=siguientes.get(nodo);continue;}
    var disponibles=ds.stream().filter(d->d.disponible(e)).toList();check(!disponibles.isEmpty(),"Sin opción "+nodo+" "+e.getEventos());
    var dec=disponibles.get(rnd.nextInt(disponibles.size()));dec.aplicar(e);nodo=dec.getSiguiente();
    check(!(e.ocurrioEvento("j_pareja")&&e.ocurrioEvento("j_pausa")),"Pareja y pausa simultáneas");
   }
   e.registrarEvento("jairo_hito:"+h.id());e.setTurno(CalendarioJuego.Turno.NOCHE);opcionAleatoria(e,rnd);cal.dormirHastaManana();
  }
  String fin=FinalesJairo.calcular(e);check(escenas.containsKey(fin),fin);totales.merge(fin,1,Integer::sum);
  check(!e.ocurrioEvento("j_pareja")||fin.endsWith("verde")||fin.endsWith("azul")||fin.endsWith("dorado"),"Pareja incompatible con "+fin+" "+e.getEventos());
 }
 System.out.println("OK: 5000 campañas aleatorias, "+totales);
}
public static void main(String[] args)throws Exception {cargar();
 ruta("dorado",Map.of());
 ruta("verde",Map.of(1,1));
 ruta("azul",Map.of(15,2));
 ruta("gris",Map.of(14,1,23,6));
 ruta("rojo",Map.of(17,1,19,1));
 ruta("violeta",Map.of(14,1,23,0));
 ruta("naranja",Map.of(14,1,15,3,23,1));
 ruta("naranja",Map.of(15,2,24,1));
 ruta("celeste",Map.of(14,1,23,4));
 ruta("rosa",Map.of(14,1,23,3));
 ruta("blanco",Map.of(14,1,23,5));
 ruta("negro",Map.of(2,1,5,1,15,2,18,2,19,1));
 ruta("turquesa",Map.of(14,1,18,1,23,4,25,1,27,1));
 // Same primary path, different interlude choice => different ending.
 ruta("verde",Map.of(),Map.of(3,1));
 ruta("blanco",Map.of(14,1,23,0),Map.of(28,2));
 ruta("blanco",Map.of(14,1,23,4,27,2));
 System.out.println("OK: decisiones secundarias alteran finales y abandono voluntario de Anto");
 stress();
 Path p=Files.createTempDirectory("galeria").resolve("jairo.txt"); GaleriaJairo g=new GaleriaJairo(p);g.desbloquear("final_jairo_verde");g.desbloquear("final_jairo_verde");g.desbloquear("final_jairo_azul");check(new GaleriaJairo(p).leer().size()==2,"Galería"); Files.delete(p);Files.delete(p.getParent());
 for(String n:List.of("neutral","pensativo","manos_bolsillos","feliz","sentado","avergonzado","sorprendido","senalando"))check(VerificarJairo.class.getResource(RecursosVisuales.spriteJairo(n))!=null,"Sprite "+n);
 System.out.println("OK: galería y 8 sprites de Jairo");
}}
''')
with tempfile.TemporaryDirectory() as td:
 p=Path(td); f=p/'VerificarJairo.java';f.write_text('\n'.join(code),encoding='utf-8')
 sources=[JAVA/'motor'/f'{x}.java' for x in ['CalendarioJuego','EstadoPartida','Dialogo','Decision','Condicion','Escena','HitosJairo','FinalesJairo','GestorEventos','GestorEventosJairo','GestorEventosSeba','HitosSeba']]+[JAVA/'guardado/GaleriaJairo.java',JAVA/'RecursosVisuales.java']
 subprocess.run(['javac','-encoding','UTF-8','-d',td,*map(str,sources),str(f)],check=True)
 import os
 subprocess.run(['java','-cp',os.pathsep.join([td,str(RES)]),'VerificarJairo'],check=True)
print(f'{len(scenes)} escenas; {sum(len(s.get("dialogos",[])) for s in scenes.values())} intervenciones principales; {sum(len(s.get("decisiones",[])) for s in scenes.values())} elecciones; 12 CG verificadas.')
