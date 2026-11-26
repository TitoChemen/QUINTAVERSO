#!/usr/bin/env python3
# Auditoria de finales / recursos. Biblioteca estandar; no instala dependencias.
from pathlib import Path
from collections import Counter
import json
import struct
import sys

RAIZ = Path(__file__).resolve().parents[1]
RECURSOS = RAIZ / "src" / "main" / "resources"
HISTORIAS = RECURSOS / "historias"
FINALES = RECURSOS / "historias" / "seba" / "finales.json"
IMAGENES = RECURSOS / "imagenes"

ESPERADOS = {
    "final_seba_verde": "final_verde.png",
    "final_seba_azul": "final_azul.png",
    "final_seba_gris": "final_gris.png",
    "final_seba_violeta": "final_violeta.png",
    "final_seba_rojo": "final_rojo.png",
    "final_seba_celeste": "final_celeste.png",
    "final_seba_naranja": "final_naranja_sebaxfena.png",
    "final_seba_naranja_picnic": "final_naranja_richiexfena.png",
    "final_seba_turquesa": "final_turquesa.png",
    "final_seba_harem": "final_negro.png",
    "final_seba_blanco": "final_blanco.png",
    "final_seba_femboy": "final_rosa_secreto.png",
    "final_seba_amarillo": "final_amarillo.png",
}


def dimensiones_png(p):
    with p.open("rb") as entrada:
        cabecera = entrada.read(24)
    if len(cabecera) < 24 or cabecera[:8] != b"\x89PNG\r\n\x1a\n":
        raise ValueError("No es un PNG valido")
    return struct.unpack(">II", cabecera[16:24])


def main():
    fallos = []
    escenas = json.loads(FINALES.read_text(encoding="utf-8"))
    recuento = Counter(e.get("id") for e in escenas)
    print("QUINTAVERSO - Auditoria de recursos / finales")
    print("=" * 60)
    for ident, png in ESPERADOS.items():
        coincidencias = [e for e in escenas if e.get("id") == ident]
        ruta_esperada = "seba/" + png
        if len(coincidencias) != 1:
            fallos.append(f"{ident}: {len(coincidencias)} escenas, se esperaba 1")
            continue
        e = coincidencias[0]
        if e.get("cg") != ruta_esperada:
            fallos.append(f"{ident}: CG {e.get('cg')!r}, esperado {ruta_esperada!r}")
            continue
        ruta = IMAGENES / "cg" / ruta_esperada
        if not ruta.is_file():
            fallos.append(f"{ident}: no existe {ruta}")
            continue
        try:
            w, h = dimensiones_png(ruta)
            if w < 640 or h < 360:
                fallos.append(f"{ident}: imagen demasiado pequena ({w}x{h})")
            print(f"[OK] {ident:<29} {png:<35} {w}x{h}")
        except Exception as ex:
            fallos.append(f"{ident}: {ex}")

    # Todas las CG y fondos citados directamente en escenas deben existir.
    total_escenas = 0
    total_cg = 0
    for ruta_json in HISTORIAS.rglob("*.json"):
        contenido = json.loads(ruta_json.read_text(encoding="utf-8"))
        if not isinstance(contenido, list):
            continue
        for e in contenido:
            total_escenas += 1
            cg = e.get("cg")
            fondo = e.get("fondo")
            if cg:
                total_cg += 1
                ruta = IMAGENES / "cg" / (cg if "/" in cg else "caceres/" + cg)
                if not ruta.is_file():
                    fallos.append(f"{ruta_json.name} / {e.get('id')}: CG ausente {ruta}")
            if fondo:
                ruta = IMAGENES / "fondos" / fondo
                if not ruta.is_file():
                    fallos.append(f"{ruta_json.name} / {e.get('id')}: Fondo ausente {ruta}")

    print("-" * 60)
    print(f"Finales Seba: {len(ESPERADOS)} | Escenas analizadas: {total_escenas} | CG referenciados: {total_cg}")
    if fallos:
        print(f"FALLOS: {len(fallos)}")
        for mensaje in fallos:
            print("[ERROR]", mensaje)
        return 1
    print("AUDITORIA CORRECTA. No se encontraron recursos faltantes ni CG cruzados.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
