package com.AlerCello86767.Slimefun.event;

import com.AlerCello86767.Slimefun.SlimefunMod;
import com.AlerCello86767.Slimefun.api.MultiblockRegistry;
import com.AlerCello86767.Slimefun.api.MultiblockStructure;
import com.AlerCello86767.Slimefun.multiblocks.AutomatedPanningMachine;
import com.AlerCello86767.Slimefun.machines.basic.OreWasherMachine;
import com.AlerCello86767.Slimefun.machines.basic.SmelteryMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
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
        
        // 自动淘金机 - 点击活板门
        if (state.getBlock() instanceof TrapDoorBlock) {
            AutomatedPanningMachine machine = (AutomatedPanningMachine)
                MultiblockRegistry.getStructure("automated_panning_machine");
            if (machine == null) return;
            handleMachineClick(event, level, pos, machine, machine::onRightClick);
            return;
        }

        // 洗矿机 - 点击栅栏
        if (state.getBlock() instanceof FenceBlock && state.is(Blocks.OAK_FENCE)) {
            OreWasherMachine machine = (OreWasherMachine)
                MultiblockRegistry.getStructure("ore_washer");
            if (machine == null) return;
            handleMachineClick(event, level, pos, machine, machine::onRightClick);
            return;
        }

        // 熔炼机 - 点击下界砖栅栏
        if (state.getBlock() instanceof FenceBlock && state.is(Blocks.NETHER_BRICK_FENCE)) {
            SmelteryMachine machine = (SmelteryMachine)
                MultiblockRegistry.getStructure("smeltery");
            if (machine == null) return;
            handleSmelteryClick(event, level, pos, machine);
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

    private static void handleSmelteryClick(PlayerInteractEvent.RightClickBlock event, Level level,
                                            BlockPos pos, SmelteryMachine machine) {
        // 熔炼机的中心位置是下界砖栅栏下方（发射器位置）
        BlockPos centerPos = pos.below();
        
        if (!machine.isStructureComplete(level, centerPos)) {
            return;
        }

        event.setCanceled(true);
        if (!level.isClientSide) {
            // 检查是否使用打火石点火
            if (event.getItemStack().is(Items.FLINT_AND_STEEL)) {
                InteractionResult result = machine.relightFire(level, centerPos, event.getEntity()) 
                    ? InteractionResult.SUCCESS : InteractionResult.FAIL;
                event.setCancellationResult(result);
            } else {
                // 正常处理熔炼
                InteractionResult result = machine.onRightClick(level, centerPos, event.getEntity(), event.getItemStack());
                event.setCancellationResult(result);
            }
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