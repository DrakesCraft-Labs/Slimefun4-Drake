package cl.jackstar.slimefun4.api.services;

import javax.annotation.Nonnull;

/**
 * Public bridge for deterministic calculations delegated to Slimefun-Rust.
 * Bukkit objects and world mutations must never cross this boundary.
 */
public interface NativeAccelerationService {

    boolean isAvailable();

    int getAbiVersion();

    int sumSaturating(@Nonnull int[] values);

    double calculateMarketPrice(
            double basePrice,
            long demand,
            double totalWealth,
            double referenceWealth,
            double minimumFactor,
            double maximumFactor,
            double demandStep,
            double maximumDemandFactor,
            double pulseFactor);

    long getNativeCalls();

    long getFallbackCalls();

    long getFailures();
}
