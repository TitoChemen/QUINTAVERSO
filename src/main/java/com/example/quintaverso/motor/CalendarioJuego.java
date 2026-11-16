package com.example.quintaverso.motor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Maneja los tres turnos del día sobre el estado persistente. */
public final class CalendarioJuego {
    public static final int DIAS_CAMPANA = 30;
    public static final LocalDate FECHA_INICIAL = LocalDate.of(2026, 11, 16);
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale.of("es", "CL"));

    public enum Turno {
        MANANA("Mañana", "08:00–12:00"),
        TARDE("Tarde", "12:00–19:00"),
        NOCHE("Noche", "19:00–00:00");

        private final String nombre;
        private final String horario;

        Turno(String nombre, String horario) {
            this.nombre = nombre;
            this.horario = horario;
        }
        public String getNombre() { return nombre; }
        public String getHorario() { return horario; }
    }

    private final EstadoPartida estado;

    public CalendarioJuego(EstadoPartida estado) {
        this.estado = estado;
    }

    public int getDia() { return estado.getDia(); }
    public Turno getTurno() { return estado.getTurno(); }
    public String getNombreTurno() { return estado.getTurno().getNombre(); }
    public LocalDate getFecha() { return FECHA_INICIAL.plusDays(Math.max(0, estado.getDia() - 1)); }
    public String getFechaFormateada() { return getFecha().format(FORMATO); }
    public boolean campañaTerminada() { return estado.getDia() > DIAS_CAMPANA; }

    /** EXACTAMENTE una acción avanza el reloj una vez. */
    public void avanzarTurno() {
        switch (estado.getTurno()) {
            case MANANA -> estado.setTurno(Turno.TARDE);
            case TARDE -> estado.setTurno(Turno.NOCHE);
            case NOCHE -> {
                estado.setTurno(Turno.MANANA);
                estado.setDia(estado.getDia() + 1);
            }
        }
    }

    /** Dormir siempre avanza al día siguiente, incluso desde la mañana. */
    public void dormirHastaManana() {
        estado.setDia(estado.getDia() + 1);
        estado.setTurno(Turno.MANANA);
    }

    public boolean esFinDeSemana() {
        DayOfWeek d = getFecha().getDayOfWeek();
        return d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY;
    }
}
