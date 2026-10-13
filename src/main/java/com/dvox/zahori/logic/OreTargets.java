package com.dvox.zahori.logic;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/** Diccionario de menas buscables: bloques + dimensiones validas + icono para la GUI. */
public final class OreTargets {
    public record Target(Set<Block> blocks, Set<ResourceKey<Level>> dimensions, Supplier<ItemStack> icon,
            int color) {
    }

    /** Orden de los botones en la GUI minimalista. */
    public static final List<String> ORDER = List.of(
            "carbon", "cobre", "hierro", "oro", "lapis",
            "redstone", "diamante", "esmeralda", "cuarzo", "netherita");

    private static final Set<ResourceKey<Level>> OVERWORLD = Set.of(Level.OVERWORLD);
    private static final Set<ResourceKey<Level>> NETHER = Set.of(Level.NETHER);
    private static final Set<ResourceKey<Level>> BOTH = Set.of(Level.OVERWORLD, Level.NETHER);

    private static final Map<String, Target> TARGETS = Map.ofEntries(
            Map.entry("carbon", new Target(Set.of(Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE), OVERWORLD,
                    () -> new ItemStack(Blocks.COAL_ORE), 0x8A8A8A)),
            Map.entry("cobre", new Target(Set.of(Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE), OVERWORLD,
                    () -> new ItemStack(Blocks.COPPER_ORE), 0xE08A4E)),
            Map.entry("hierro", new Target(Set.of(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE), OVERWORLD,
                    () -> new ItemStack(Blocks.IRON_ORE), 0xD8BFAE)),
            Map.entry("oro", new Target(Set.of(Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.NETHER_GOLD_ORE), BOTH,
                    () -> new ItemStack(Blocks.GOLD_ORE), 0xF5D73E)),
            Map.entry("lapis", new Target(Set.of(Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE), OVERWORLD,
                    () -> new ItemStack(Blocks.LAPIS_ORE), 0x4A6FE3)),
            Map.entry("redstone", new Target(Set.of(Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE), OVERWORLD,
                    () -> new ItemStack(Blocks.REDSTONE_ORE), 0xFF2A2A)),
            Map.entry("diamante", new Target(Set.of(Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE), OVERWORLD,
                    () -> new ItemStack(Blocks.DIAMOND_ORE), 0x5DF2E0)),
            Map.entry("esmeralda", new Target(Set.of(Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE), OVERWORLD,
                    () -> new ItemStack(Blocks.EMERALD_ORE), 0x3ED97E)),
            Map.entry("cuarzo", new Target(Set.of(Blocks.NETHER_QUARTZ_ORE), NETHER,
                    () -> new ItemStack(Blocks.NETHER_QUARTZ_ORE), 0xEFE7D3)),
            Map.entry("netherita", new Target(Set.of(Blocks.ANCIENT_DEBRIS), NETHER,
                    () -> new ItemStack(Blocks.ANCIENT_DEBRIS), 0x7A5A48)));

    private OreTargets() {
    }

    public static Target get(String key) {
        return TARGETS.get(key);
    }

    public static boolean availableIn(String key, ResourceKey<Level> dimension) {
        Target target = TARGETS.get(key);
        return target != null && target.dimensions().contains(dimension);
    }
}
