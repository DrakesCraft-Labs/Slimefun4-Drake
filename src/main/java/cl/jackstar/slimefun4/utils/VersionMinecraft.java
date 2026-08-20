package cl.jackstar.slimefun4.utils;

import org.bukkit.Bukkit;

/**
 * Comparacion de version del servidor.
 *
 * Sustituye a {@code net.guizhanss.guizhanlib.minecraft.utils.MinecraftVersionUtil}, del que la
 * capa de almacenamiento solo usaba esto: preguntar si el servidor va por una version igual o
 * superior a otra. Traer la libreria entera por dos llamadas no compensaba, y ademas arrastra
 * los ayudantes de nombres en chino y un autoactualizador que sustituiria nuestro jar.
 *
 * La version se lee una sola vez: no cambia mientras el servidor esta en pie.
 */
public final class VersionMinecraft {

    private static final int[] ACTUAL = leer();

    private VersionMinecraft() {}

    /**
     * Los tres numeros de la version, con 0 en lo que falte.
     *
     * Bukkit.getBukkitVersion() devuelve cosas como "1.21.11-R0.1-SNAPSHOT", asi que se corta por
     * el guion y se parte por puntos. Si algo no encaja se devuelve 0.0.0, que hace que cualquier
     * comparacion diga "no llega": preferimos desactivar una funcion antes que activarla creyendo
     * que estamos en una version que no es.
     */
    private static int[] leer() {
        int[] salida = {0, 0, 0};
        try {
            String bruto = Bukkit.getBukkitVersion().split("-")[0];
            String[] partes = bruto.split("\\.");
            for (int i = 0; i < Math.min(3, partes.length); i++) {
                salida[i] = Integer.parseInt(partes[i].trim());
            }
        } catch (Exception ignorado) {
            // Se queda en 0.0.0 a proposito; ver el comentario de arriba.
        }
        return salida;
    }

    public static boolean alMenos(int mayor, int menor) {
        return alMenos(mayor, menor, 0);
    }

    /** Si el servidor va por esa version o una posterior. */
    public static boolean alMenos(int mayor, int menor, int parche) {
        if (ACTUAL[0] != mayor) {
            return ACTUAL[0] > mayor;
        }
        if (ACTUAL[1] != menor) {
            return ACTUAL[1] > menor;
        }
        return ACTUAL[2] >= parche;
    }

    /** La version leida, para diagnostico. */
    public static String actual() {
        return ACTUAL[0] + "." + ACTUAL[1] + "." + ACTUAL[2];
    }
}
