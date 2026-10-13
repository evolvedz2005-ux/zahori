package com.dvox.zahori.item;

import com.dvox.zahori.client.ZahoriClient;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class VaraZahoriItem extends Item {
    public VaraZahoriItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            boolean shift = player.isShiftKeyDown();
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ZahoriClient.handleVaraUse(shift, stack));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
