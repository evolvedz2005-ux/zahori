package com.dvox.zahori.registry;

import com.dvox.zahori.ZahoriMod;
import com.dvox.zahori.item.VaraZahoriItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ZahoriMod.MOD_ID);

    public static final RegistryObject<Item> VARA_ZAHORI = ITEMS.register("vara_zahori",
            () -> new VaraZahoriItem(new Item.Properties().stacksTo(1).durability(3)));
}
