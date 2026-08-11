package cl.jackstar.slimefun4.api.services;

import javax.annotation.Nonnull;

import cl.jackstar.slimefun4.core.services.nativeengine.RustNativeEngine;

/**
 * Punto de acceso al motor nativo en Rust.
 *
 * En nuestro fork viejo esto vivia como un campo de la clase {@code Slimefun} y se exponia con
 * {@code Slimefun.getNativeAccelerationService()}. Aqui **no se hace asi a proposito**: cada linea
 * que toquemos de su core es un conflicto en cada {@code pull upstream}, y ellos van a 2900
 * commits de ritmo. Manteniendo el enganche en nuestro propio espacio, sus actualizaciones entran
 * limpias.
 *
 * El arranque es perezoso en vez de atado a {@code onEnable}. Cuesta una comprobacion por llamada
 * y a cambio no dependemos de tocar su ciclo de vida. Si algun dia hace falta control fino del
 * apagado, {@link #stop()} esta disponible para engancharlo desde un listener nuestro.
 */
public final class NativeServices {

    private static volatile RustNativeEngine motor;

    private NativeServices() {
    }

    /**
     * El servicio de aceleracion nativa, arrancandolo si hace falta.
     *
     * Nunca devuelve null: si el binario de Rust no esta disponible, el propio motor responde en
     * modo respaldo sobre Java. Quien llama no tiene que distinguir los dos casos, solo consultar
     * {@code isAvailable()} si quiere informarlo.
     */
    @Nonnull
    public static NativeAccelerationService acceleration() {
        RustNativeEngine local = motor;
        if (local == null) {
            synchronized (NativeServices.class) {
                local = motor;
                if (local == null) {
                    local = new RustNativeEngine();
                    motor = local;
                }
            }
        }
        return local;
    }

    /** Para el motor, si se llego a arrancar. Idempotente. */
    public static void stop() {
        synchronized (NativeServices.class) {
            motor = null;
        }
    }
}
