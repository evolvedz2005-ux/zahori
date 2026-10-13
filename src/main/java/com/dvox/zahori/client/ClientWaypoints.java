package com.dvox.zahori.client;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Waypoint actual, solo cliente. Nueva busqueda lo reemplaza. */
@OnlyIn(Dist.CLIENT)
public final class ClientWaypoints {
    public record Waypoint(BlockPos pos, ResourceKey<Level> dim, String oreKey, int color, long setTime) {
    }

    private static Waypoint current;

    private ClientWaypoints() {
    }

    public static void set(BlockPos pos, ResourceKey<Level> dim, String oreKey, int color, long gameTime) {
        current = new Waypoint(pos.immutable(), dim, oreKey, color, gameTime);
    }

    public static void clear() {
        current = null;
    }

    public static Waypoint get() {
        return current;
    }
}
