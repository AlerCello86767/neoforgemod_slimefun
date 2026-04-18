package com.AlerCello86767.Slimefun.machines.basic;

import com.AlerCello86767.Slimefun.api.MultiblockStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;

/**
 * 基于发射器的机器抽象类
 * 所有使用发射器作为输入/输出容器的机器都应该继承此类
 */
public abstract class DispenserBasedMachine extends MultiblockStructure {
    
    protected final String machineName;
    protected final Function<ItemStack, ItemStack[]> recipeFunction;
    
    /**
     * 构造函数
     * @param name 机器名称
     * @param width 宽度
     * @param height 高度
     * @param depth 深度
     * @param pattern 结构模式
     * @param machineName 机器显示名称
     * @param recipeFunction 配方处理函数，输入物品 -> 输出物品数组
     */
    public DispenserBasedMachine(String name, int width, int height, int depth, 
                                Map<BlockPos, Block> pattern, String machineName,
                                Function<ItemStack, ItemStack[]> recipeFunction) {
        super(name, width, height, depth, pattern);
        this.machineName = machineName;
        this.recipeFunction = recipeFunction;
    }
    
    @Override
    protected boolean onActivate(Level level, BlockPos centerPos) {
        return true;
    }
    
    @Override
    protected void onDeactivate(Level level, BlockPos centerPos) {
        // 基础机器没有持久化数据
    }
    
    /**
     * 右键点击机器时的处理
     */
    public InteractionResult onRightClick(Level level, BlockPos centerPos, Player player, ItemStack handItem) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos dispenserPos = getDispenserPosition(centerPos);
        if (!(level.getBlockEntity(dispenserPos) instanceof DispenserBlockEntity dispenser)) {
            player.displayClientMessage(
                Component.literal("§c结构未完成或未找到发射器！"),
                true
            );
            return InteractionResult.FAIL;
        }

        Container container = dispenser;
        
        // 检查是否有支持的物品
        ItemStack inputStack = findSupportedItem(container);
        if (inputStack.isEmpty()) {
            player.displayClientMessage(
                Component.literal("§c当前待加工已空，请添加物品"),
                true
            );
            return InteractionResult.FAIL;
        }

        // 获取配方输出
        ItemStack[] outputs = recipeFunction.apply(inputStack);
        if (outputs == null || outputs.length == 0) {
            player.displayClientMessage(
                Component.literal("§c不支持的物品，请检查配方后再试"),
                true
            );
            return InteractionResult.FAIL;
        }

        // 检查是否有足够的空间存放输出
        if (!canInsertAll(container, outputs)) {
            player.displayClientMessage(
                Component.literal("§c物品已满！请取出部分后再试"),
                true
            );
            return InteractionResult.FAIL;
        }

        // 播放处理音效
        if (level.isAreaLoaded(dispenserPos, 1)) {
            level.playSound(null, dispenserPos, SoundEvents.WOODEN_BUTTON_CLICK_OFF,
                SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        // 消耗输入物品
        inputStack.shrink(1);
        
        // 添加输出物品
        for (ItemStack output : outputs) {
            if (!output.isEmpty()) {
                insertItem(container, output.copy());
            }
        }
        
        dispenser.setChanged();

        return InteractionResult.SUCCESS;
    }
    
    /**
     * 获取发射器位置（子类可以重写此方法）
     */
    protected BlockPos getDispenserPosition(BlockPos centerPos) {
        return centerPos;
    }
    
    /**
     * 在容器中查找支持的物品
     */
    protected abstract ItemStack findSupportedItem(Container container);
    
    /**
     * 检查是否可以插入所有物品
     */
    protected boolean canInsertAll(Container container, ItemStack[] stacks) {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty() && !canInsert(container, stack)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 检查是否可以插入单个物品
     */
    protected static boolean canInsert(Container container, ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack existing = container.getItem(i);
            if (existing.isEmpty()) {
                return true;
            }
            if (sameStack(existing, stack) && existing.getCount() < existing.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 插入物品到容器
     */
    protected static void insertItem(Container container, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        // 先尝试堆叠到现有物品
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack existing = container.getItem(i);
            if (!existing.isEmpty() && sameStack(existing, stack)) {
                int space = Math.min(existing.getMaxStackSize() - existing.getCount(), stack.getCount());
                if (space > 0) {
                    existing.grow(space);
                    stack.shrink(space);
                    container.setItem(i, existing);
                    if (stack.isEmpty()) {
                        return;
                    }
                }
            }
        }

        // 再尝试放入空槽
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack existing = container.getItem(i);
            if (existing.isEmpty()) {
                container.setItem(i, stack.copy());
                return;
            }
        }
    }
    
    /**
     * 检查两个物品堆是否相同
     */
    protected static boolean sameStack(ItemStack a, ItemStack b) {
        return a.getItem() == b.getItem();
    }
    
    /**
     * 获取机器显示名称
     */
    public String getMachineName() {
        return machineName;
    }
}