# Slimefun 4 — fork de DrakesCraft

Fork de [SlimefunGuguProject/Slimefun4](https://github.com/SlimefunGuguProject/Slimefun4), la
versión china de [Slimefun4](https://github.com/Slimefun/Slimefun4). El README original está en
chino; abajo va traducido lo que importa, y antes lo nuestro.

## Por qué este fork y no el original

No es por la traducción al chino, que a nosotros no nos sirve de nada. Es por lo que hay debajo:
**han sustituido el almacenamiento por fichero y chunk por una capa de base de datos real.**

Frente al upstream de Slimefun van **2.929 commits por delante** y 234 ficheros Java cambiados. El
grueso es `com.xzavier0722.mc.plugin.slimefun4.storage`:

| Componente | Qué hace |
|---|---|
| `BlockDataController` | El controlador central de datos de bloques (1.632 líneas) |
| `ProfileDataController` | Lo mismo para perfiles de jugador |
| `SqliteAdapter` / `MysqlAdapter` / `PostgreSqlAdapter` | Tres motores de base de datos a elegir |
| `StorageCacheUtils` | Caché en memoria delante de la base |
| `BlockStorageMigrator` | Migra desde el formato viejo sin perder nada |
| `SlimefunChunkData` | El chunk como contenedor de primera clase |

Para comparar el efecto: nuestro `BlockStorage` tenía **1.183 líneas**; el suyo tiene **209**,
porque es una fachada sobre el controlador. `BlockMenu` pasa de ~200 a **129**, y
`AutoSavingService` a **76**.

Eso resuelve de raíz cosas que llevábamos parcheando: errores de guardado al apagar, reactores
explotando en cada reinicio y arranques que habían subido de 72 a 118 segundos.

## Qué añadimos nosotros

Todo lo nuestro vive bajo **`cl.jackstar`**. Lo heredado conserva su paquete y su cabecera de
copyright: lo exige la GPL-3.0 y es lo correcto con quien hizo el trabajo.

| Nuestro | Para qué |
|---|---|
| `core/services/CheatPolicy` | Límite de seguridad del cheat de SFMaster: ventana móvil persistente, lista de addons permitidos |
| `core/services/ClaimWindow` | El limitador de ventana móvil, puro y probado |
| `core/services/nativeengine/RustNativeEngine` | Motor nativo en Rust para cálculo, con respaldo en Java |
| `api/services/NativeServices` | Punto de acceso al motor. **A propósito no toca su clase `Slimefun`** |
| `core/commands/subcommands/NativeCommand` | `/sf native` — estado del motor nativo |
| `core/commands/subcommands/RepairCommand` | Repara registros de bloques huérfanos |
| `…/accelerators/GrowthAcceleratorTickGate` | Reparte el trabajo de los aceleradores entre ticks |

### Un agujero que encontramos al portar

En su árbol, la vista de categoría de la guía comprueba `slimefun.cheat.items` antes de entregar
un ítem, pero **la búsqueda no comprobaba nada**: solo `if (!isSurvivalMode()) { addItem }`. Quien
pudiera abrir la guía en modo cheat se saltaba el permiso simplemente buscando en vez de navegar.

Nuestro `CheatPolicy` ya cerraba esa asimetría. Ahora las dos rutas pasan por la misma verja, y hay
una prueba (`CheatDeliveryPathTest`) que lo vigila.

### Regla de oro del fork

**No tocamos su core salvo que no quede más remedio.** Ellos van a un ritmo de miles de commits, y
cada línea que modifiquemos es un conflicto en cada `pull upstream`. Por eso el enganche del motor
nativo vive en `NativeServices` y no como campo de su clase `Slimefun`.

## Estado de compatibilidad

> **1.21.11 pendiente de validar.** Compila y las 10 pruebas pasan, pero todavía no se ha probado
> en un servidor real. El paso siguiente es un servidor de pruebas con copia de los datos de
> producción, para verificar que `BlockStorageMigrator` convierte bien nuestras bases.

## Cómo compilar

```bash
./gradlew build
```

El jar sale en `build/libs/`.

---

# Traducción del README original

Lo que sigue es la traducción del README de SlimefunGuguProject, para que se entienda de dónde
viene esto.

## Slimefun versión china

> Únete al grupo de QQ: **807302496**
>
> Descarga [SlimeGlue](https://github.com/Xzavier0722/SlimeGlue/) para asegurar la compatibilidad
> de Slimefun con otros plugins de protección.

### Plan de suscripción

> ⚡ Traducir no es fácil; se agradece el apoyo en [afdian.net](https://afdian.net/a/nora1ncity).
>
> Suscribirse da acceso anticipado a las compilaciones más recientes de Slimefun y soporte técnico
> prioritario.
>
> Nota: **quien no tenga plan anual** obtiene el código fuente sincronizado automáticamente 15 días
> después de la última actualización del repositorio público. **No es código cerrado**: cumplimos
> escrupulosamente la licencia GPLv3. :)

### Comunidad

- Grupo de QQ de la versión traducida
- Canal de KOOK
- [Discord oficial de Slimefun](https://discord.gg/slimefun)

### Wiki

El wiki en chino está en [slimefun-wiki.guizhanss.cn](https://slimefun-wiki.guizhanss.cn/).

### Aviso legal

Slimefun 4 es un proyecto de la comunidad, mantenido por voluntarios. Esta versión china es una
traducción y adaptación no oficial.

---

## Licencia y crédito

GPL-3.0, como el original. El trabajo de fondo es de la comunidad de Slimefun (TheBusyBiscuit y
colaboradores) y de SlimefunGuguProject / Xzavier0722, que escribieron la capa de almacenamiento.
Nosotros solo añadimos encima.
