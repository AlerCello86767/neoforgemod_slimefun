package com.AlerCello86767.Slimefun.items.tools;

import com.AlerCello86767.Slimefun.Regist;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class GoldPanItem extends Item {

    public GoldPanItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();

        if (state.is(Blocks.GRAVEL)) {
            if (!level.isClientSide) {
                level.destroyBlock(pos, false, player);
                level.playSound(null, pos, SoundEvents.SAND_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                
                // 随机选择物品，与自动淘金机概率相同
                ItemStack itemToDrop;
                int rand = level.random.nextInt(100);
                if (rand < 27) {
                    itemToDrop = Regist.SIFTED_ORE.get().getDefaultInstance();
                } else if (rand < 36) { // 27+9=36
                    itemToDrop = Items.IRON_NUGGET.getDefaultInstance();
                } else if (rand < 81) { // 36+45=81
                    itemToDrop = Items.FLINT.getDefaultInstance();
                } else {
                    itemToDrop = Items.CLAY_BALL.getDefaultInstance();
                }
                
                // 掉落物品
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemToDrop);
                level.addFreshEntity(itemEntity);
                
                context.getItemInHand().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        
        return InteractionResult.PASS;
    }
}