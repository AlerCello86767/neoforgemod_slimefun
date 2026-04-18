package com.AlerCello86767.Slimefun.machines.basic;

import com.AlerCello86767.Slimefun.Regist;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Map;
import java.util.HashMap;

/**
 * 洗矿机 - 基于发射器的机器
 * 结构：炼药锅 + 栅栏 + 发射器
 * 功能：将筛矿加工成石块和随机金属粉尘
 */
public class OreWasherMachine extends DispenserBasedMachine {
    
    private static final Map<BlockPos, Block> PATTERN = new HashMap<>();
    
    static {
        PATTERN.put(new BlockPos(0, 0, 0), Blocks.CAULDRON);
        PATTERN.put(new BlockPos(0, 1, 0), null);
        PATTERN.put(new BlockPos(0, 2, 0), Blocks.DISPENSER);
    }
    
    // 可输出的粉尘列表
    private static final ItemStack[] DUSTS = new ItemStack[] {
        Regist.ALUMINUM_DUST.get().getDefaultInstance(),
        Regist.COPPER_DUST.get().getDefaultInstance(),
        Regist.GOLD_DUST.get().getDefaultInstance(),
        Regist.IRON_DUST.get().getDefaultInstance(),
        Regist.LEAD_DUST.get().getDefaultInstance(),
        Regist.MAGNESIUM_DUST.get().getDefaultInstance(),
        Regist.SILVER_DUST.get().getDefaultInstance(),
        Regist.TIN_DUST.get().getDefaultInstance(),
        Regist.ZINC_DUST.get().getDefaultInstance()
    };
    
    public OreWasherMachine() {
        super("ore_washer", 1, 3, 1, PATTERN, "洗矿机", 
            input -> {
                // 配方：筛矿 -> 石块 + 随机粉尘
                if (input.is(Regist.SIFTED_ORE.get())) {
                    return new ItemStack[] {
                        Regist.STONE_CHUNK.get().getDefaultInstance(),
                        DUSTS[0].copy() // 这里需要在实际使用时随机选择
                    };
                }
                return null;
            });
    }
    
    @Override
    public boolean isStructureComplete(Level level, BlockPos centerPos) {
        // 检查炼药锅
        if (!level.getBlockState(centerPos).is(Blocks.CAULDRON)) {
            return false;
        }
        
        // 检查栅栏
        BlockState fenceState = level.getBlockState(centerPos.above());
        if (!(fenceState.getBlock() instanceof FenceBlock)) {
            return false;
        }
        
        // 检查发射器
        BlockState dispenserState = level.getBlockState(centerPos.above(2));
        if (!dispenserState.is(Blocks.DISPENSER)) {
            return false;
        }
        
        return true;
    }
    
    @Override
    protected BlockPos getDispenserPosition(BlockPos centerPos) {
        return centerPos.above(2);
    }
    
    @Override
    protected ItemStack findSupportedItem(Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && stack.is(Regist.SIFTED_ORE.get())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
    
    /**
     * 重写右键点击方法以添加随机性
     */
    @Override
    public net.minecraft.world.InteractionResult onRightClick(net.minecraft.world.level.Level level, 
                                                             net.minecraft.core.BlockPos centerPos, 
                                                             net.minecraft.world.entity.player.Player player, 
                                                             net.minecraft.world.item.ItemStack handItem) {
        if (level.isClientSide) {
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        net.minecraft.core.BlockPos dispenserPos = getDispenserPosition(centerPos);
        if (!(level.getBlockEntity(dispenserPos) instanceof net.minecraft.world.level.block.entity.DispenserBlockEntity dispenser)) {
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§c结构未完成或未找到发射器！"),
                true
            );
            return net.minecraft.world.InteractionResult.FAIL;
        }

        net.minecraft.world.Container container = dispenser;
        
        // 检查是否有支持的物品
        net.minecraft.world.item.ItemStack inputStack = findSupportedItem(container);
        if (inputStack.isEmpty()) {
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§c当前待加工已空，请添加物品"),
                true
            );
            return net.minecraft.world.InteractionResult.FAIL;
        }

        // 随机选择粉尘
        net.minecraft.world.item.ItemStack randomDust = DUSTS[level.random.nextInt(DUSTS.length)].copy();
        
        // 检查是否有足够的空间存放输出
        net.minecraft.world.item.ItemStack[] outputs = new net.minecraft.world.item.ItemStack[] {
            Regist.STONE_CHUNK.get().getDefaultInstance(),
            randomDust
        };
        
        if (!canInsertAll(container, outputs)) {
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§c物品已满！请取出部分后再试"),
                true
            );
            return net.minecraft.world.InteractionResult.FAIL;
        }

        // 播放处理音效
        if (level.isAreaLoaded(dispenserPos, 1)) {
            level.playSound(null, dispenserPos, net.minecraft.sounds.SoundEvents.WOODEN_BUTTON_CLICK_OFF,
                net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        // 消耗输入物品
        inputStack.shrink(1);
        
        // 添加输出物品
        for (net.minecraft.world.item.ItemStack output : outputs) {
            if (!output.isEmpty()) {
                insertItem(container, output.copy());
            }
        }
        
        dispenser.setChanged();

        return net.minecraft.world.InteractionResult.SUCCESS;
    }
}