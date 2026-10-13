# Zahori

Mod de Forge para **Minecraft 1.20.1** que añade la **Vara de Zahori**: un item temprano para localizar las menas mas cercanas sin pasearte a ciegas por las cuevas.

## La Vara de Zahori

Un baston de madera con punta de cobre que detecta menas a tu alrededor y te guia hasta ellas.

### Receta

Vertical, en la mesa de crafteo:

```
C
S
S
```

- `C` = lingote de cobre (arriba)
- `S` = palo (medio y abajo)

Pensada para el inicio de la partida: madera, pico de piedra, un poco de cobre y listo. Solo tiene **3 usos** y no se puede reparar, asi que toca volver a craftearla.

## Como se usa

1. **Click derecho**: abre una GUI minimalista con los iconos de las 10 menas buscables. Las menas de otra dimension aparecen en gris.
2. **Shift + click derecho**: repite tu ultima busqueda sin abrir la GUI.
3. Al encontrar la mena veras:
   - Mensaje en el chat con las coordenadas y la distancia.
   - Una **cruz en el HUD** del color de la mena, siempre visible, apuntando hacia el objetivo aunque este tras paredes.
   - Un **texto flotante** en el mundo con el nombre y la distancia.
   - Un **contorno** marcando el bloque al tener vision directa.
4. Al acercarte a **3 bloques**, suena un aviso, sale "Llegaste a X" y la marca desaparece.

Cada uso gasta 1 de durabilidad y aplica 10 segundos de cooldown.

## Menas buscables

Overworld: carbon, cobre, hierro, oro, lapislazuli, piedra roja, diamante, esmeralda.
Nether: cuarzo, oro del Nether (dentro de oro) y escombros ancestrales.

La busqueda solo rastrea tu dimension actual, en un radio de 32 bloques (configurable).

## Configuracion

Archivo `zahori-common.toml`:

| Opcion         | Defecto | Descripcion                              |
|----------------|---------|------------------------------------------|
| `radius`       | 32      | Radio de busqueda en bloques (8-64)      |
| `cooldownTicks`| 200     | Cooldown por uso en ticks (20 = 1 seg)   |

## Requisitos

- Minecraft 1.20.1
- Forge 47.2.0
- Java 17

## Compilar

```bash
gradle build
```

El jar sale en `build/libs/`. Copialo a la carpeta `mods` de tu instalacion Forge.

## Idiomas

Español (`es_es`) e ingles (`en_us`).

---

Hecho 100% con IA por **Nyxen**, como prueba del desarrollo de mods con IA para Minecraft.
