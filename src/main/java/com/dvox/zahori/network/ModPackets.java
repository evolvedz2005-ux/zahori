package com.dvox.zahori.network;

import java.util.function.Supplier;

import com.dvox.zahori.ZahoriMod;
import com.dvox.zahori.config.ZahoriConfig;
import com.dvox.zahori.logic.OreFinder;
import com.dvox.zahori.logic.OreTargets;
import com.dvox.zahori.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModPackets {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ZahoriMod.MOD_ID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    private static int nextId = 0;

    public static void register() {
        INSTANCE.registerMessage(nextId++, ProspectPacket.class,
                ProspectPacket::encode, ProspectPacket::new, ProspectPacket::handle);
        INSTANCE.registerMessage(nextId++, WaypointPacket.class,
                WaypointPacket::encode, WaypointPacket::new, WaypointPacket::handle);
    }

    public static class ProspectPacket {
        private final String oreKey;

        public ProspectPacket(String oreKey) {
            this.oreKey = oreKey;
        }

        public ProspectPacket(FriendlyByteBuf buf) {
            this.oreKey = buf.readUtf(32);
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeUtf(oreKey, 32);
        }

        public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
            NetworkEvent.Context ctx = ctxSupplier.get();
            ctx.enqueueWork(() -> {
                ServerPlayer player = ctx.getSender();
                if (player == null) {
                    return;
                }
                ServerLevel level = player.serverLevel();
                Item vara = ModItems.VARA_ZAHORI.get();
                if (player.getCooldowns().isOnCooldown(vara)) {
                    return;
                }
                OreTargets.Target target = OreTargets.get(oreKey);
                if (target == null) {
                    return;
                }
                InteractionHand usedHand = null;
                for (InteractionHand hand : InteractionHand.values()) {
                    if (player.getItemInHand(hand).is(vara)) {
                        usedHand = hand;
                        break;
                    }
                }
                if (usedHand == null) {
                    return;
                }
                ItemStack stack = player.getItemInHand(usedHand);
                Component oreName = Component.translatable("zahori.ore." + oreKey);
                if (!target.dimensions().contains(level.dimension())) {
                    player.sendSystemMessage(Component.translatable("msg.zahori.wrong_dimension", oreName));
                    return;
                }
                int radius = ZahoriConfig.RADIUS.get();
                int cooldown = ZahoriConfig.COOLDOWN_TICKS.get();
                BlockPos found = OreFinder.findNearest(level, player.blockPosition(), target.blocks(), radius);
                stack.getOrCreateTag().putString("LastOre", oreKey);
                final InteractionHand handToBreak = usedHand;
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(handToBreak));
                player.getCooldowns().addCooldown(vara, cooldown);
                if (found != null) {
                    int distance = (int) Math.sqrt(found.distSqr(player.blockPosition()));
                    player.sendSystemMessage(Component.translatable("msg.zahori.found",
                            oreName, found.getX(), found.getY(), found.getZ(), distance));
                    level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            found.getX() + 0.5, found.getY() + 1.2, found.getZ() + 0.5,
                            24, 0.5, 0.5, 0.5, 0.1);
                    ModPackets.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                            new WaypointPacket(found, oreKey));
                } else {
                    player.sendSystemMessage(Component.translatable("msg.zahori.not_found", oreName, radius));
                    ModPackets.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                            WaypointPacket.clear());
                }
            });
            ctx.setPacketHandled(true);
        }
    }

    /** Servidor -> cliente: coloca o limpia el waypoint. */
    public static class WaypointPacket {
        private final boolean has;
        private final BlockPos pos;
        private final String oreKey;

        public WaypointPacket(BlockPos pos, String oreKey) {
            this.has = true;
            this.pos = pos.immutable();
            this.oreKey = oreKey;
        }

        private WaypointPacket(boolean has, BlockPos pos, String oreKey) {
            this.has = has;
            this.pos = pos;
            this.oreKey = oreKey;
        }

        public static WaypointPacket clear() {
            return new WaypointPacket(false, BlockPos.ZERO, "");
        }

        public WaypointPacket(FriendlyByteBuf buf) {
            this.has = buf.readBoolean();
            if (has) {
                this.pos = buf.readBlockPos();
                this.oreKey = buf.readUtf(32);
            } else {
                this.pos = BlockPos.ZERO;
                this.oreKey = "";
            }
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeBoolean(has);
            if (has) {
                buf.writeBlockPos(pos);
                buf.writeUtf(oreKey, 32);
            }
        }

        public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
            NetworkEvent.Context ctx = ctxSupplier.get();
            ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> com.dvox.zahori.client.ZahoriClient.handleWaypoint(has, pos, oreKey)));
            ctx.setPacketHandled(true);
        }
    }
}
