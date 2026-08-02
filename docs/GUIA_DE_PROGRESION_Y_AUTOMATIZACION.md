# Guía de progresión y automatización DrakesCraft

Esta guía explica qué hace cada capa del ecosistema DrakesCraft. Las recetas,
costos y requisitos definitivos viven en la guía de Slimefun in-game; cambian
con el balance y no se duplican aquí.

## Ruta 1: supervivencia y materiales base

1. Reúne madera, comida, hierro, redstone y carbón de forma manual.
2. Desbloquea la guía (`/sf guide`) y las investigaciones básicas.
3. Fabrica energía inicial y una máquina de procesamiento: horno, triturador,
   compresor o equivalente de la categoría elegida.
4. Conecta entrada y salida con CargoNet. No construyas una red enorme de una
   vez: valida un flujo de un material antes de ampliarla.

**Automatizable:** fundición, molienda, compactación, cocina y transporte.
**No automático por diseño:** minerales valiosos ilimitados y botín de bosses.

## Ruta 2: agricultura y Nether

1. Para cultivos vanilla usa un Farmer Android o una instalación de cultivo.
2. Para plantas de Cultivation, descubre cruces en el Diccionario de Cruces y
   mantiene sus bloques de soporte; no todas las parejas tienen receta.
3. DynaTech aporta Growth Chambers por dimensión y Gastronomicon amplía semillas,
   cocina, pesca, fermentación y árboles.
4. Exporta la cosecha hacia un cofre o red, y procesa sólo el excedente.

**Ejemplo Nether Wart:** el Farmer Android puede cosechar la verruga. El bloque
de verruga es una receta vanilla de 9 verrugas; el Grind Stone hace la operación
inversa. No existe un generador directo de Nether Wart Block.

## Ruta 3: energía y procesamiento industrial

1. Empieza con generadores de bajo riesgo y máquinas de consumo conocido.
2. Añade almacenamiento y medición antes de subir la producción.
3. DynaTech ofrece molinos, líquidos, Growth Chambers, autococina y generadores.
4. Galaxyfun añade procesos atmosféricos, gases, oxígeno, electrólisis y energía
   espacial; úsalo cuando la red base ya sea estable.
5. Supreme es endgame: fabricadores, canteras, colectores y generadores no son
   un atajo para saltarse materiales previos.

**Regla:** nunca inyectes un stack nuevo a una red grande sin comprobar primero
que controlador, almacenamiento, importación y exportación siguen sanos.

## Ruta 4: logística y autocrafteo

1. CargoNet mueve inventarios simples del core.
2. ChestTerminal permite acceso, importación, exportación y terminal inalámbrica.
3. Networks añade controladores, celdas, buses, bridges, autocrafters y storage.
4. DrakesTech aporta nodos, buses, almacenamiento y puentes propios.

**Buenas prácticas:** una red por función, límites razonables de almacenamiento,
sin loops de importación/exportación y una prueba tras cada ampliación. Si una
red deja de detectar un bloque, no rompas todo: reporta controlador, chunk,
máquina nueva y hora del incidente.

## Ruta 5: progreso avanzado

1. Automatiza componentes intermedios, no armas ni armaduras finales.
2. Pasa a Supreme, Infinity y tecnología espacial sólo cuando puedas producir
   energía, materiales y almacenamiento de manera controlada.
3. Data Cards, cores, singularities, ítems radiactivos, armas, armaduras y
   herramientas finales deben conservar costos manuales o de alto nivel.
4. Los drops de bosses son recompensa de combate; no son una cadena industrial.

## Qué addon consultar

| Necesidad | Capa principal |
| --- | --- |
| Fundir, triturar, compactar, CargoNet, Androids | Slimefun4-Drake |
| Plantas cruzadas y Garden Cloche | Cultivation_Updated |
| Agricultura dimensional, líquidos y máquinas | DynaTech-drake |
| Comida, semillas, pesca y cocina | Gastronomicon-drake |
| Red de almacenamiento y autocrafteo | NetworksV6-drake / ChestTerminal-drake |
| Espacio, gases y reactores | Galaxyfun-drake |
| Canteras, colectores y endgame industrial | Supreme-Drake |
| Máquinas DrakesCraft y puentes de red | DrakesTech |

## Límites de balance

La automatización sirve para reducir tareas repetitivas. No debe entregar de
forma pasiva netherita, diamantes, esmeraldas, oro, materiales radiactivos,
cores, singularities, Data Cards ni equipamiento final. Si una máquina permite
eso, es un incidente de balance o un bug, no un método de progresión previsto.
