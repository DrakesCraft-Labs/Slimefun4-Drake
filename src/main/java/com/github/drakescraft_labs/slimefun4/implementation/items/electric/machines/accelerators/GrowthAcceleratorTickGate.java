package com.github.drakescraft_labs.slimefun4.implementation.items.electric.machines.accelerators;

import com.github.drakescraft_labs.slimefun4.implementation.Slimefun;

final class GrowthAcceleratorTickGate {

    /** Si no se puede leer la configuracion, se asume el valor por defecto de Slimefun. */
    private static final int RITMO_POR_DEFECTO = 20;

    private GrowthAcceleratorTickGate() {
    }

    /**
     * Cada cuantos ticks del juego corre el ticker de Slimefun.
     *
     * Se lee una sola vez: cambiarlo requiere reiniciar de todos modos.
     */
    private static final int RITMO_TICKER = leerRitmo();

    private static int leerRitmo() {
        try {
            int valor = Slimefun.getCfg().getInt("URID.custom-ticker-delay");
            return valor > 0 ? valor : RITMO_POR_DEFECTO;
        } catch (Exception ignorado) {
            return RITMO_POR_DEFECTO;
        }
    }

    /**
     * Reparte las maquinas entre ciclos para que las granjas cargadas no escaneen todas a la vez.
     *
     * OJO CON EL TIEMPO QUE SE USA AQUI. La version anterior comparaba directamente contra
     * {@code worldTime}, y eso dejaba maquinas muertas para siempre:
     *
     * El ticker de Slimefun no corre cada tick, sino cada {@code URID.custom-ticker-delay} (20 por
     * defecto). Asi que {@code worldTime} solo toma multiplos de 20, y {@code worldTime % 8} solo
     * puede valer 0 o 4 -- nunca 1, 2, 3, 5, 6 ni 7. Con el intervalo en 8, seis de cada ocho
     * aceleradores no tickeaban NUNCA: energia llena, cero efecto. Cuanto peor, mas invisible,
     * porque los otros dos de cada ocho funcionaban de maravilla.
     *
     * Se arregla contando CICLOS DE TICKER en vez de ticks del mundo: dividiendo por el ritmo, el
     * contador avanza de uno en uno y recorre todas las fases.
     */
    static boolean shouldTick(long worldTime, int x, int y, int z, int interval) {
        int normalizedInterval = Math.max(1, interval);

        if (normalizedInterval == 1) {
            return true;
        }

        long ciclo = worldTime / Math.max(1, RITMO_TICKER);
        long locationHash = 73428767L * x ^ 912931L * y ^ 19349663L * z;
        long phase = Math.floorMod(locationHash, normalizedInterval);
        return Math.floorMod(ciclo, normalizedInterval) == phase;
    }

}
