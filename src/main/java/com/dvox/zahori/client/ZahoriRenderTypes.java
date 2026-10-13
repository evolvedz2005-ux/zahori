package com.dvox.zahori.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Subclase solo para acceder a los shards protegidos y crear un RenderType
 * translucido sin depth-test: el waypoint se ve a traves de los bloques.
 */
@OnlyIn(Dist.CLIENT)
public final class ZahoriRenderTypes extends RenderStateShard {
    private ZahoriRenderTypes() {
        super("zahori", () -> {
        }, () -> {
        });
    }

    public static final RenderType BEAM = RenderType.create("zahori_beam",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(POSITION_COLOR_SHADER)
                    .setTextureState(NO_TEXTURE)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setOverlayState(NO_OVERLAY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false));
}
