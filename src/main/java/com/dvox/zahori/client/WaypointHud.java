package com.dvox.zahori.client;

import com.dvox.zahori.client.ClientWaypoints.Waypoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Marcador del waypoint en el HUD: siempre visible, apunta aunque este tras paredes. */
@Mod.EventBusSubscriber(modid = "zahori", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class WaypointHud {
    private WaypointHud() {
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Waypoint wp = ClientWaypoints.get();
        if (wp == null || mc.level == null || mc.player == null || mc.screen != null) {
            return;
        }
        if (!mc.level.dimension().equals(wp.dim())) {
            return;
        }
        Vec3 eye = mc.player.getEyePosition();
        double dx = (wp.pos().getX() + 0.5) - eye.x;
        double dy = (wp.pos().getY() + 1.0) - eye.y;
        double dz = (wp.pos().getZ() + 0.5) - eye.z;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 4) {
            return;
        }
        double targetYaw = Math.toDegrees(Mth.atan2(-dx, dz));
        double horiz = Math.sqrt(dx * dx + dz * dz);
        double targetPitch = Math.toDegrees(Mth.atan2(-dy, horiz));
        float relYaw = Mth.wrapDegrees((float) (targetYaw - mc.player.getYRot()));
        float relPitch = Mth.clamp((float) (targetPitch - mc.player.getXRot()), -89f, 89f);

        int sw = event.getWindow().getGuiScaledWidth();
        int sh = event.getWindow().getGuiScaledHeight();
        double vFov = mc.options.fov().get();
        double hFov = 2 * Math.toDegrees(Math.atan(Math.tan(Math.toRadians(vFov / 2)) * ((double) sw / sh)));
        double nx = relYaw / (hFov / 2);
        double ny = relPitch / (vFov / 2);
        int margin = 14;
        int mx = Mth.clamp((int) (sw / 2.0 + nx * (sw / 2.0 - margin)), margin, sw - margin);
        int my = Mth.clamp((int) (sh / 2.0 + ny * (sh / 2.0 - margin)), margin, sh - margin);

        int color = 0xFF000000 | wp.color();
        GuiGraphics g = event.getGuiGraphics();
        g.fill(mx - 1, my - 4, mx + 2, my + 4, color);
        g.fill(mx - 4, my - 1, mx + 4, my + 2, color);
    }
}
