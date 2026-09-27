package me.mrCookieSlime.Slimefun.api;

import org.bukkit.World;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Nombre de paquete del API clásico de Slimefun. AuraSkills y otros plugins cargan esta clase
 * por reflexión o referencias estáticas; la lógica vive en el fork Drake.
 */
public class BlockStorage extends io.github.thebusybiscuit.slimefun4.legacy.api.BlockStorage {

    @ParametersAreNonnullByDefault
    public BlockStorage(World w) {
        super(w);
    }
}
