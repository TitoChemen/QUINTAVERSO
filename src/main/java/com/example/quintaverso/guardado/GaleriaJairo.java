package com.example.quintaverso.guardado;

import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedHashSet;
import java.util.Set;
import com.example.quintaverso.motor.FinalesJairo;

/** Desbloqueos persistentes independientes del autoguardado y de las ranuras. */
public final class GaleriaJairo {
    private final Path archivo;
    public GaleriaJairo() { this(Path.of(System.getProperty("user.home"), ".quintaverso", "partidas", "jairo-galeria.txt")); }
    public GaleriaJairo(Path archivo) { this.archivo=archivo; }
    public Set<String> leer() throws IOException {
        Set<String> ids=new LinkedHashSet<>();
        if(Files.exists(archivo)) for(String id:Files.readAllLines(archivo))
            if(FinalesJairo.COLORES.stream().anyMatch(c->id.equals("final_jairo_"+c))) ids.add(id);
        return ids;
    }
    public void desbloquear(String id) throws IOException {
        if(FinalesJairo.COLORES.stream().noneMatch(c->id.equals("final_jairo_"+c))) return;
        Set<String> ids=leer();
        if(!ids.add(id)) return;
        Path carpeta=archivo.toAbsolutePath().getParent();
        Files.createDirectories(carpeta);
        Path tmp=Files.createTempFile(carpeta,"galeria-",".tmp");
        try {
            Files.write(tmp,ids);
            try { Files.move(tmp,archivo,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE); }
            catch(AtomicMoveNotSupportedException ex) { Files.move(tmp,archivo,StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(tmp); }
    }
}
