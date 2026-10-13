package com.dvox.zahori.client;

import org.joml.Matrix4f;

import com.dvox.zahori.client.ClientWaypoints.Waypoint;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Render del waypoint: columna de luz + caja + texto con distancia, visible a traves de bloques. */
@Mod.EventBusSubscriber(modid = "zahori", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class WaypointRenderer {
    private static final double ARRIVE_DIST_SQR = 9.0;
    private static final long EXPIRE_TICKS = 12000L;

    private static final RenderType BEAM = ZahoriRenderTypes.BEAM;

    private WaypointRenderer() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Waypoint wp = ClientWaypoints.get();
        if (wp == null || mc.level == null || mc.player == null) {
            return;
        }
        if (!mc.level.dimension().equals(wp.dim())) {
            ClientWaypoints.clear();
            return;
        }
        if (mc.level.getGameTime() - wp.setTime() > EXPIRE_TICKS) {
            ClientWaypoints.clear();
            return;
        }
        if (mc.player.blockPosition().distSqr(wp.pos()) <= ARRIVE_DIST_SQR) {
            ClientWaypoints.clear();
            mc.player.sendSystemMessage(Component.translatable("msg.zahori.arrived",
                    Component.translatable("zahori.ore." + wp.oreKey())));
            mc.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        }
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientWaypoints.clear();
    }

    @SubscribeEvent
    public static void onRenderStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Waypoint wp = ClientWaypoints.get();
        if (wp == null || mc.level == null || mc.player == null) {
            return;
        }
        if (!mc.level.dimension().equals(wp.dim())) {
            return;
        }
        float r = ((wp.color() >> 16) & 0xFF) / 255f;
        float g = ((wp.color() >> 8) & 0xFF) / 255f;
        float b = (wp.color() & 0xFF) / 255f;

        Camera camera = event.getCamera();
        Vec3 cam = camera.getPosition();
        PoseStack pose = event.getPoseStack();
        pose.pushPose();
        pose.translate(-cam.x, -cam.y, -cam.z);

        double x = wp.pos().getX();
        double y = wp.pos().getY();
        double z = wp.pos().getZ();
        VertexConsumer vc = mc.renderBuffers().bufferSource().getBuffer(BEAM);
        Matrix4f m = pose.last().pose();
        fillBox(m, vc, x - 0.05, y - 0.05, z - 0.05, x + 1.05, y + 1.05, z + 1.05, r, g, b, 0.25f);
        mc.renderBuffers().bufferSource().endBatch(BEAM);
        VertexConsumer lines = mc.renderBuffers().bufferSource().getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(pose, lines,
                new AABB(x, y, z, x + 1, y + 1, z + 1), r, g, b, 1.0f);
        mc.renderBuffers().bufferSource().endBatch(RenderType.lines());
        pose.popPose();

        int dist = (int) Math.sqrt(mc.player.blockPosition().distSqr(wp.pos()));
        String text = Component.translatable("zahori.ore." + wp.oreKey()).getString() + " \u00B7 " + dist + "m";
        pose.pushPose();
        pose.translate(-cam.x, -cam.y, -cam.z);
        pose.translate(x + 0.5, y + 2.6, z + 0.5);
        pose.mulPose(camera.rotation());
        pose.scale(-0.035f, -0.035f, 0.035f);
        Font font = mc.font;
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        font.drawInBatch(text, -font.width(text) / 2f, 0, 0xFFFFFFFF, false, pose.last().pose(), buffers,
                Font.DisplayMode.SEE_THROUGH, 15728880, OverlayTexture.NO_OVERLAY);
        buffers.endBatch();
        pose.popPose();
    }

    private static void quad(Matrix4f m, VertexConsumer vc,
            double x1, double y1, double z1,
            double x2, double y2, double z2,
            double x3, double y3, double z3,
            double x4, double y4, double z4,
            float r, float g, float b, float a) {
        vc.vertex(m, (float) x1, (float) y1, (float) z1).color(r, g, b, a).endVertex();
        vc.vertex(m, (float) x2, (float) y2, (float) z2).color(r, g, b, a).endVertex();
        vc.vertex(m, (float) x3, (float) y3, (float) z3).color(r, g, b, a).endVertex();
        vc.vertex(m, (float) x4, (float) y4, (float) z4).color(r, g, b, a).endVertex();
    }

    private static void fillBox(Matrix4f m, VertexConsumer vc,
            double x0, double y0, double z0, double x1, double y1, double z1,
            float r, float g, float b, float a) {
        quad(m, vc, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1, r, g, b, a);
        quad(m, vc, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0, r, g, b, a);
        quad(m, vc, x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0, r, g, b, a);
        quad(m, vc, x1, y0, z0, x1, y1, z0, x1, y1, z1, x1, y0, z1, r, g, b, a);
        quad(m, vc, x0, y0, z0, x0, y1, z0, x1, y1, z0, x1, y0, z0, r, g, b, a);
        quad(m, vc, x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1, r, g, b, a);
    }
}
