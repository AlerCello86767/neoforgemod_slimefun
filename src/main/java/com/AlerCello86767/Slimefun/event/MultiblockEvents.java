package com.AlerCello86767.Slimefun.event;

import com.AlerCello86767.Slimefun.SlimefunMod;
import com.AlerCello86767.Slimefun.api.MultiblockRegistry;
import com.AlerCello86767.Slimefun.api.MultiblockStructure;
import com.AlerCello86767.Slimefun.multiblocks.AutomatedPanningMachine;
import com.AlerCello86767.Slimefun.multiblocks.OreWasherMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = SlimefunMod.MOD_ID)
public class MultiblockEvents {
    
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        
        if (state.getBlock() instanceof TrapDoorBlock) {
            AutomatedPanningMachine machine = (AutomatedPanningMachine)
                MultiblockRegistry.getStructure("automated_panning_machine");
            if (machine == null) return;
            handleMachineClick(event, level, pos, machine, machine::onRightClick);
            return;
        }

        if (state.getBlock() instanceof FenceBlock) {
            OreWasherMachine machine = (OreWasherMachine)
                MultiblockRegistry.getStructure("ore_washer");
            if (machine == null) return;
            handleMachineClick(event, level, pos, machine, machine::onRightClick);
        }
    }

    private static void handleMachineClick(PlayerInteractEvent.RightClickBlock event, Level level,
                                           BlockPos pos, MultiblockStructure machine,
                                           MachineClickHandler handler) {
        BlockPos cauldronPos = pos.below();
        if (!level.getBlockState(cauldronPos).is(Blocks.CAULDRON)) {
            return;
        }

        if (!machine.isStructureComplete(level, cauldronPos)) {
            return;
        }

        event.setCanceled(true);
        if (!level.isClientSide) {
            InteractionResult result = handler.apply(level, cauldronPos, event.getEntity(), event.getItemStack());
            event.setCancellationResult(result);
        } else {
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    @FunctionalInterface
    private interface MachineClickHandler {
        InteractionResult apply(Level level, BlockPos centerPos, net.minecraft.world.entity.player.Player player,
                                net.minecraft.world.item.ItemStack stack);
    }
}