package com.dvox.zahori.client;

import com.dvox.zahori.logic.OreTargets;
import com.dvox.zahori.network.ModPackets;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Codigo solo de cliente: abrir la GUI o repetir la ultima busqueda. */
@OnlyIn(Dist.CLIENT)
public final class ZahoriClient {
    private ZahoriClient() {
    }

    public static void handleVaraUse(boolean shift, ItemStack stack) {
        if (shift) {
            String last = stack.getOrCreateTag().getString("LastOre");
            if (!last.isEmpty() && OreTargets.get(last) != null) {
                ModPackets.INSTANCE.sendToServer(new ModPackets.ProspectPacket(last));
                return;
            }
        }
        Minecraft.getInstance().setScreen(new ProspectScreen());
    }

    public static void handleWaypoint(boolean has, BlockPos pos, String oreKey) {
        if (!has || oreKey.isEmpty()) {
            ClientWaypoints.clear();
            return;
        }
        OreTargets.Target target = OreTargets.get(oreKey);
        Minecraft mc = Minecraft.getInstance();
        if (target == null || mc.level == null) {
            ClientWaypoints.clear();
            return;
        }
        ClientWaypoints.set(pos, mc.level.dimension(), oreKey, target.color(), mc.level.getGameTime());
    }
}
