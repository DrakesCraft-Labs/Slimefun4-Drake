<div align="center">

  <img src="https://raw.githubusercontent.com/DrakesCraft-Labs/Slimefun4-Drake/main/slimefun_core_banner.svg" alt="Slimefun4-Drake Banner" width="920" />

# 🧪 Slimefun4-Drake Core

**Núcleo Principal de Slimefun4 Adapted for Paper/Purpur 1.21.11 con Carga de Addons y Aceleración Nativa en Rust**

<p>
  <a href="https://github.com/DrakesCraft-Labs/Slimefun4-Drake"><img src="https://img.shields.io/badge/GitHub-Slimefun4--Drake-181717?style=for-the-badge&logo=github" alt="GitHub"/></a>
  <img src="https://img.shields.io/badge/Java-21_FFM_Panama-F89820?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21 FFM"/>
  <img src="https://img.shields.io/badge/Rust-FFM_Accelerated-FF4500?style=for-the-badge&logo=rust&logoColor=white" alt="Rust Native"/>
  <img src="https://img.shields.io/badge/Paper-1.21.11-00FF66?style=for-the-badge&logo=minecraft&logoColor=white" alt="Paper 1.21.11"/>
</p>

</div>

---

## 📖 Descripción del Núcleo Slimefun4-Drake

`Slimefun4-Drake` es el núcleo central que gestiona el registro de recetas, categorías, inventarios, guías interactivas y carga de los 44 Addons en el servidor Minecraft de DrakesCraft.

---

## ⚡ Aceleración Nativa en Rust (Modelo Híbrido Cero-Riesgo)

`Slimefun4-Drake` integra el puente **`RustNativeBridge`** utilizando **Java 21 Project Panama (FFM API)**.

Conecta el motor de Slimefun4 Core directamente al binario/librería `Slimefun-Rust` (`slimefun_ffi`):
- 🚀 **Procesamiento de Ticks de Máquinas en Nanosegundos**: Multi-hilo paralelo real en CPU sin Garbage Collector (GC).
- 🛡️ **Prevención Total de Duplicados (Anti-Dupe Atómico)**: Mutaciones atómicas en memoria en Rust que evitan glitches de clics simultáneos, hoppers o desconexiones bruscas.
- 💾 **Persistencia SQLite 0-Reset**: Lectura/Escritura bidireccional sobre `data-storage/Slimefun/stored-blocks.db`.

---

## 🛠️ Compilación e Instalación

```bash
# Compilar plugin con Maven
mvn clean package
```

Ubica el archivo `Slimefun4-Drake.jar` en la carpeta `plugins/` de tu servidor Minecraft.

---

<div align="center">

**DrakesCraft Labs** · Maintained by [**JackStar6677-1**](https://github.com/JackStar6677-1)

</div>