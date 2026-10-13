package com.dvox.zahori.client;

import com.dvox.zahori.logic.OreTargets;
import com.dvox.zahori.network.ModPackets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** GUI minimalista: rejilla de iconos, grises segun la dimension actual. */
@OnlyIn(Dist.CLIENT)
public class ProspectScreen extends Screen {
    private static final int COLS = 5;
    private static final int BTN = 20;
    private static final int GAP = 4;

    private int panelLeft;
    private int panelTop;
    private int panelW;
    private int panelH;

    protected ProspectScreen() {
        super(Component.translatable("screen.zahori.prospect"));
    }

    @Override
    protected void init() {
        int size = OreTargets.ORDER.size();
        int rows = (size + COLS - 1) / COLS;
        int gridW = COLS * BTN + (COLS - 1) * GAP;
        int gridH = rows * BTN + (rows - 1) * GAP;
        panelW = gridW + 24;
        panelH = gridH + 54;
        panelLeft = (width - panelW) / 2;
        panelTop = (height - panelH) / 2;
        int gx = panelLeft + 12;
        int gy = panelTop + 26;
        ResourceKey<Level> dim = Minecraft.getInstance().level.dimension();
        for (int i = 0; i < size; i++) {
            String key = OreTargets.ORDER.get(i);
            int x = gx + (i % COLS) * (BTN + GAP);
            int y = gy + (i / COLS) * (BTN + GAP);
            boolean active = OreTargets.availableIn(key, dim);
            OreTargets.Target target = OreTargets.get(key);
            Component label = Component.translatable("zahori.ore." + key);
            Tooltip tip = Tooltip.create(active ? label
                    : Component.translatable("zahori.ore.other_dimension", label));
            OreButton button = new OreButton(x, y, label, b -> {
                ModPackets.INSTANCE.sendToServer(new ModPackets.ProspectPacket(key));
                Minecraft.getInstance().setScreen(null);
            }, tip, target.icon().get());
            button.active = active;
            addRenderableWidget(button);
        }
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx);
        gfx.fill(panelLeft, panelTop, panelLeft + panelW, panelTop + panelH, 0xE0101010);
        gfx.drawCenteredString(font, title, width / 2, panelTop + 8, 0xFFFFFF);
        gfx.drawCenteredString(font, Component.translatable("screen.zahori.hint"),
                width / 2, panelTop + panelH - 12, 0x808080);
        super.render(gfx, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    static class OreButton extends Button {
        private final ItemStack icon;

        protected OreButton(int x, int y, Component message, OnPress onPress, Tooltip tooltip, ItemStack icon) {
            super(x, y, BTN, BTN, message, onPress, DEFAULT_NARRATION);
            this.icon = icon;
            setTooltip(tooltip);
        }

        @Override
        protected void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
            int x = getX();
            int y = getY();
            gfx.fill(x, y, x + BTN, y + BTN, active ? 0xCC000000 : 0xCC2A2A2A);
            gfx.renderItem(icon, x + 2, y + 2);
            if (!active) {
                gfx.fill(x, y, x + BTN, y + BTN, 0xAA111111);
            } else if (isHovered()) {
                gfx.fill(x, y, x + BTN, y + BTN, 0x55FFFFFF);
            }
        }
    }
}
