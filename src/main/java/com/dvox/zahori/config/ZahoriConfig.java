package com.dvox.zahori.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ZahoriConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue RADIUS;
    public static final ForgeConfigSpec.IntValue COOLDOWN_TICKS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("zahori");
        RADIUS = builder.comment("Radio de busqueda en bloques (cubico, max en cada eje)")
                .defineInRange("radius", 32, 8, 64);
        COOLDOWN_TICKS = builder.comment("Cooldown tras cada uso, en ticks (20 = 1 segundo)")
                .defineInRange("cooldownTicks", 200, 20, 1200);
        builder.pop();
        SPEC = builder.build();
    }
}
