# Guía de contribución

Traducida del original en chino de SlimefunGuguProject. Léela antes de tocar código.

## Preparar el entorno

Hay un sistema automático de formato. Inicialízalo con:

```bash
./gradlew spotlessApply
```

El repositorio incluye un `.editorconfig` que fija el estilo. Si tienes tu propia configuración,
cámbiala a la del repositorio antes de contribuir.

## Rama

Antes de empezar, asegúrate de que tu código parte de la rama `dev`.

En **este fork** trabajamos además sobre `integracion/drake`, que es donde vive lo de DrakesCraft.

## Formato de los mensajes de commit

El proyecto **obliga** a usar [Commits Convencionales](https://www.conventionalcommits.org/es/v1.0.0/):

```
<tipo>[ámbito opcional]: <descripción>
```

Por ejemplo, un commit que añade una funcionalidad sería:

```
feat(item): add new item to Slimefun
```

Si tu código resuelve un issue, decláralo **fuera** del mensaje principal (`resolves #114514`,
`fix #114514`…). Si es una corrección, decláralo en el mensaje principal y no lo repitas.

Los prefijos admitidos son:

```
(feat(ure)?|fix|docs|style|refactor|ci|chore|perf|build|test|revert|trans)
```

Los commits relacionados con traducción usan el tipo `trans`.

## Estilo de código

**El proyecto usa indentación de 4 espacios.**

No comprimas el código en exceso: Slimefun no va a correr más rápido por tener menos espacios.

Se usa Spotless como formateador. Antes de enviar **tienes que** ejecutar:

```bash
./gradlew spotlessCheck spotlessApply
```

Si no, el verificador de formato bloqueará el pull request.

## Qué se puede contribuir

Correcciones, contenido nuevo y API. El código de terceros puede aportar API: se puede depender de
esta versión a través de JitPack.

---

## Añadido de DrakesCraft

Reglas propias de este fork, encima de las de arriba:

1. **Todo fichero nuevo nuestro va bajo `cl.jackstar`.** Los heredados conservan su paquete y su
   cabecera de copyright original.
2. **No tocar su core salvo que no quede más remedio.** Van a un ritmo de miles de commits y cada
   línea que modifiquemos es un conflicto en cada `pull upstream`. Si hace falta un enganche,
   ponlo en nuestro espacio de nombres, como `cl.jackstar.slimefun4.api.services.NativeServices`.
3. **Todo arreglo nuestro lleva prueba.** Este proyecto casi no tenía; sin prueba, nada impide que
   alguien deshaga el arreglo sin enterarse.
4. Los mensajes de commit van en castellano y explican **por qué**, no solo qué.
