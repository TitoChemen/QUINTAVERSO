package com.example.quintaverso.motor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class GestorEventosJairo {
    private GestorEventosJairo() {}
    public static List<GestorEventos.Accion> disponibles(EstadoPartida e) {
        List<GestorEventos.Accion> a=new ArrayList<>();
        if(e.getDia()>30 || HitosJairo.tocaCapitulo(e,HitosJairo.pendiente(e))) return a;
        String id=String.format(Locale.ROOT,"jairo_discord_%02d",e.getDia());
        if(e.getTurno()==CalendarioJuego.Turno.NOCHE && !e.ocurrioEvento("accion:"+id))
            a.add(new GestorEventos.Accion(id,"🎧 Discord · Noche "+e.getDia(),"Una conversa distinta con los cabros",""+id,"Discord",true));
        String[] titulos={"Estudiar","Salir a caminar","Ordenar el día"};
        for(int i=1;i<=3;i++) {
            String escena="jairo_personal_"+i;
            String accion=escena+"_dia"+e.getDia();
            if(!e.ocurrioEvento("accion:"+accion)) a.add(new GestorEventos.Accion(accion,titulos[i-1],"Tiempo propio · una vez por día",escena,"Casa / paseo",true));
        }
        int d=e.getDia();
        boolean pareja=e.ocurrioEvento("j_pareja"), pausa=e.ocurrioEvento("j_pausa"), bloqueo=e.ocurrioEvento("j_bloqueado");
        if(e.getTurno()!=CalendarioJuego.Turno.NOCHE) {
            if(pareja && d>=1 && d<=7) agregar(a,e,"mary_helado","Un helado con Mary","Parque");
            if(pareja && d>=6 && d<=14) agregar(a,e,"mary_proyecto","Escuchar los planes de Mary","Café");
            if(!bloqueo && !pausa && d>=3 && d<=14) agregar(a,e,"ale_almacen","Encontrarse con Ale","Almacén");
            if(!bloqueo && !pausa && d>=6 && d<=17) agregar(a,e,"andrea_apuntes","Repasar con Andrea","Café");
            if(pareja && d>=22) agregar(a,e,"mary_paseo","Paseo con Mary","Costanera");
            if(!bloqueo && !pausa && d>=16 && d<=18 && e.ocurrioEvento("j_cami_conoce")) agregar(a,e,"cami_chat","Conversar con Cami","WhatsApp");
            if(!bloqueo && !pausa && d>=18 && d<=24 && e.ocurrioEvento("j_anto_conoce")) agregar(a,e,"anto_practica","Hablar con Anto","Plaza");
            if(pausa) agregar(a,e,"pausa","Respetar la distancia","Casa");
            if(!pareja && !pausa && !bloqueo && d>=14) agregar(a,e,"soltero","Salir por mi cuenta","Costanera");
            if(!pareja && !pausa && !bloqueo && d>=25 && d<=28) {
                for(String k:new String[]{"ale","andrea","cami","anto"}) if(e.ocurrioEvento("j_interes_"+k) && !e.ocurrioEvento("j_anto_perdida"))
                    agregar(a,e,k+"_ruta","Conocer mejor a "+Character.toUpperCase(k.charAt(0))+k.substring(1),"Encuentro / mensajes");
            }
            if(pareja && e.ocurrioEvento("j_reconciliados") && d>=24) agregar(a,e,"mary_reparacion","Conversación de reparación","Mirador");
            if(bloqueo) agregar(a,e,"bloqueado","Asumir las consecuencias","Casa");
        } else if(d>=4) agregar(a,e,"amigos","Ayudar a los cabros","Discord");
        return a;
    }
    private static void agregar(List<GestorEventos.Accion> a, EstadoPartida e,String k,String titulo,String lugar) {
        String id="jairo_opt_"+k;
        if(!e.ocurrioEvento("accion:"+id)) a.add(new GestorEventos.Accion(id,titulo,"Escena única · cambia según tu situación",id,lugar,true));
    }
}
