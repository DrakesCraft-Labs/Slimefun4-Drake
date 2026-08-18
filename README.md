<p align="center">
  <img src="banner.svg" width="100%" alt="SLIMEFUN4 IGDRASSIL CORE Animated Banner" />
</p>

# Slimefun 4 — Fork de Infraestructura Igdrassil (DrakesCraft)

Fork de alta eficiencia de **Slimefun 4** equipado con una arquitectura de persistencia moderna sobre motores de base de datos relacionales (**SQLite, MySQL, PostgreSQL**) en lugar de archivos planos por chunk. Mantenido por **DrakesCraft Labs** para Paper/Purpur 1.21.11 en Java 21.

> ### 🏰 ¡Únete a la Comunidad Oficial de DrakesCraft!
> 
> * 🎮 **IP del Servidor**: `play.drakescraft.net` *(Java 1.21.11 & Bedrock)*
> * 💬 **Discord Oficial**: [discord.gg/drakescraft](https://discord.gg/rR7FbfCt9Y)
> * 🌐 **Web & Guía**: [drakescraft.net](https://drakescraft.net) — 🛒 **Tienda**: [tienda.drakescraft.net](https://tienda.drakescraft.net)
> 
> *¡Juega con este addon y más de 80 expansiones optimizadas en vivo en nuestra network de supervivencia técnica!*

---

---

## 🎯 Arquitectura de Persistencia SQL

Frente al almacenamiento legado por archivos YAML individuales de chunks, este núcleo desacopla el almacenamiento mediante controladores dedicados:

- **`BlockDataController`**: Controlador central de estados, ticking de máquinas e inventarios en memoria con flush transaccional a BD.
- **`ProfileDataController`**: Gestión persistente e indexada de progresos, investigaciones y mochilas de jugadores.
- **Caché en Memoria `StorageCacheUtils`**: Lecturas sin latencia de I/O de disco para bloques industriales de alto tráfico.
- **Migrador Automático `BlockStorageMigrator`**: Conversión transparente del formato plano tradicional a SQL sin pérdida de datos.

---

## ⚡ Beneficios en Producción

- **Eliminación de Corrupciones de Chunks**: Los reinicios o caídas ya no truncan datos de bloques de Slimefun.
- **Arranque Ultrarrápido**: Carga de perfiles y bloques en milisegundos mediante índices SQL.
- **Sin Bloqueo del Bucle Principal**: Operaciones de guardado completamente asíncronas.

---

## 🛠️ Entorno y Compilación

- **Servidor**: Paper / Purpur 1.21.11
- **Java**: 21
- **Construcción**: Gradle Kotlin DSL (`./gradlew build`)
