package com.dvox.zahori.logic;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

/** Busqueda del bloque mas cercano por capas (distancia Chebyshev), con salida temprana. */
public final class OreFinder {
    private OreFinder() {
    }

    public static BlockPos findNearest(ServerLevel level, BlockPos origin, Set<Block> targets, int radius) {
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();
        for (int d = 1; d <= radius; d++) {
            BlockPos best = null;
            double bestDist = Double.MAX_VALUE;
            for (int dx = -d; dx <= d; dx++) {
                for (int dy = -d; dy <= d; dy++) {
                    for (int dz = -d; dz <= d; dz++) {
                        if (Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz)) != d) {
                            continue;
                        }
                        BlockPos pos = origin.offset(dx, dy, dz);
                        if (pos.getY() < minY || pos.getY() >= maxY) {
                            continue;
                        }
                        if (targets.contains(level.getBlockState(pos).getBlock())) {
                            double dist = pos.distSqr(origin);
                            if (dist < bestDist) {
                                bestDist = dist;
                                best = pos.immutable();
                            }
                        }
                    }
                }
            }
            if (best != null) {
                return best;
            }
        }
        return null;
    }
}
