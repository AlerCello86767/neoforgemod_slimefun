package com.AlerCello86767.Slimefun.machines.basic;

import com.AlerCello86767.Slimefun.Regist;
import com.AlerCello86767.Slimefun.api.RecipeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

/**
 * 熔炼机 - 基于发射器的机器（简化版）
 * 结构：发射器 + 下界砖栅栏 + 下界砖块 + 火
 * 功能：将金属粉尘熔炼成金属锭
 */
public class SmelteryMachine extends DispenserBasedMachine {
    
    private static final Map<BlockPos, Block> PATTERN = new HashMap<>();
    private static final RecipeManager recipeManager = RecipeManager.getManager("smeltery");
    private static final Map<BlockPos, Integer> smeltingCounts = new HashMap<>();
    private static final Random random = new Random();
    
    static {
        // 结构模式（以发射器为中心）
        // y=1: 下界砖栅栏（上方）
        PATTERN.put(new BlockPos(0, 1, 0), Blocks.NETHER_BRICK_FENCE);
        
        // y=0: 发射器（中心）
        PATTERN.put(new BlockPos(0, 0, 0), Blocks.DISPENSER);
        
        // y=0: 左右下界砖块
        PATTERN.put(new BlockPos(-1, 0, 0), Blocks.NETHER_BRICKS);
        PATTERN.put(new BlockPos(1, 0, 0), Blocks.NETHER_BRICKS);
        
        // y=-1: 火（下方，不检查方块类型，只检查是否存在）
    }
    
    public SmelteryMachine() {
        super("smeltery", 3, 2, 1, PATTERN, "熔炼机", 
            input -> {
                // 使用RecipeManager获取配方
                RecipeManager.Recipe recipe = recipeManager.getRecipe(input.getItem());
                if (recipe != null) {
                    return new ItemStack[] { recipe.getOutput() };
                }
                return null;
            });
    }
    
    @Override
    public boolean isStructureComplete(Level level, BlockPos centerPos) {
        // 检查所有必需方块
        for (Map.Entry<BlockPos, Block> entry : structurePattern.entrySet()) {
            BlockPos relativePos = entry.getKey();
            Block requiredBlock = entry.getValue();
            BlockPos worldPos = centerPos.offset(relativePos);
            
            if (requiredBlock == null) {
                continue;
            }
            
            BlockState state = level.getBlockState(worldPos);
            if (!state.is(requiredBlock)) {
                return false;
            }
        }
        
        // 特殊检查：发射器必须朝上
        BlockState dispenserState = level.getBlockState(centerPos);
        if (!dispenserState.is(Blocks.DISPENSER)) {
            return false;
        }
        
        // 检查发射器朝向
        if (dispenserState.getValue(DispenserBlock.FACING) != net.minecraft.core.Direction.UP) {
            return false;
        }
        
        // 检查火是否在发射器正下方
        BlockPos firePos = centerPos.below();
        if (!level.getBlockState(firePos).is(Blocks.FIRE)) {
            return false;
        }
        
        return true;
    }
    
    // 金属元素相对原子质量映射表
    private static final Map<Item, Double> ELEMENT_MASS = new HashMap<>();
    
    static {
        // 初始化元素质量表（按相对原子质量从高到低排序）
        // 金
        ELEMENT_MASS.put(Regist.GOLD_DUST.get(), 196.97);
        // 银
        ELEMENT_MASS.put(Regist.SILVER_DUST.get(), 107.87);
        // 铅
        ELEMENT_MASS.put(Regist.LEAD_DUST.get(), 207.2);
        // 铜
        ELEMENT_MASS.put(Regist.COPPER_DUST.get(), 63.55);
        // 铁
        ELEMENT_MASS.put(Regist.IRON_DUST.get(), 55.85);
        // 锡
        ELEMENT_MASS.put(Regist.TIN_DUST.get(), 118.71);
        // 锌
        ELEMENT_MASS.put(Regist.ZINC_DUST.get(), 65.38);
        // 铝
        ELEMENT_MASS.put(Regist.ALUMINUM_DUST.get(), 26.98);
        // 镁
        ELEMENT_MASS.put(Regist.MAGNESIUM_DUST.get(), 24.31);
    }
    
    @Override
    protected ItemStack findSupportedItem(Container container) {
        ItemStack highestPriorityStack = ItemStack.EMPTY;
        double highestMass = 0;
        
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && recipeManager.hasRecipe(stack.getItem())) {
                // 获取元素质量
                double mass = ELEMENT_MASS.getOrDefault(stack.getItem(), 0.0);
                
                // 如果质量更高，更新最高优先级物品
                if (mass > highestMass) {
                    highestMass = mass;
                    highestPriorityStack = stack;
                }
            }
        }
        
        return highestPriorityStack;
    }
    
    /**
     * 检查火是否活跃
     */
    public boolean isFireActive(Level level, BlockPos centerPos) {
        BlockPos firePos = centerPos.below();
        return level.getBlockState(firePos).is(Blocks.FIRE);
    }
    
    /**
     * 重新点火
     */
    public boolean relightFire(Level level, BlockPos centerPos, net.minecraft.world.entity.player.Player player) {
        BlockPos firePos = centerPos.below();
        
        // 检查是否可以点火
        BlockState belowState = level.getBlockState(firePos.below());
        if (!belowState.isFlammable(level, firePos.below(), net.minecraft.core.Direction.UP)) {
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§c此处无法点火！"), 
                true
            );
            return false;
        }
        
        // 点火
        level.setBlockAndUpdate(firePos, Blocks.FIRE.defaultBlockState());
        
        player.displayClientMessage(
            net.minecraft.network.chat.Component.literal("§a已重新点火！"), 
            true
        );
        
        return true;
    }
    
    /**
     * 重写右键点击方法以检查火的状态并添加声音和粒子效果
     */
    @Override
    public net.minecraft.world.InteractionResult onRightClick(net.minecraft.world.level.Level level, 
                                                             net.minecraft.core.BlockPos centerPos, 
                                                             net.minecraft.world.entity.player.Player player, 
                                                             net.minecraft.world.item.ItemStack handItem) {
        // 检查火是否活跃
        if (!isFireActive(level, centerPos)) {
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§c熔炼机需要火，请点火！"), 
                true
            );
            return net.minecraft.world.InteractionResult.FAIL;
        }
        
        // 检查是否使用打火石点火
        if (handItem.is(net.minecraft.world.item.Items.FLINT_AND_STEEL)) {
            // 重置冶炼计数
            smeltingCounts.remove(centerPos);
            return relightFire(level, centerPos, player) 
                ? net.minecraft.world.InteractionResult.SUCCESS 
                : net.minecraft.world.InteractionResult.FAIL;
        }
        
        // 调用父类的处理逻辑
        net.minecraft.world.InteractionResult result = super.onRightClick(level, centerPos, player, handItem);
        
        // 如果处理成功，添加声音和粒子效果
        if (result == net.minecraft.world.InteractionResult.SUCCESS && !level.isClientSide) {
            // 播放熔岩声音
            level.playSound(null, centerPos, net.minecraft.sounds.SoundEvents.LAVA_POP, 
                net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
            
            // 生成火焰粒子
            for (int i = 0; i < 3; i++) {
                double offsetX = centerPos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.8;
                double offsetY = centerPos.getY() + 1.0;
                double offsetZ = centerPos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.8;
                
                level.addParticle(net.minecraft.core.particles.ParticleTypes.FLAME, 
                    offsetX, offsetY, offsetZ, 
                    0.0, 0.1, 0.0);
            }
            
            // 增加冶炼次数
            int count = smeltingCounts.getOrDefault(centerPos, 0) + 1;
            smeltingCounts.put(centerPos, count);
            
            // 随机决定熄火阈值（3-6次）
            int maxUses = 3 + random.nextInt(4); // 3-6次
            
            // 检查是否达到使用次数
            if (count >= maxUses) {
                // 熄灭火焰
                BlockPos firePos = centerPos.below();
                level.setBlockAndUpdate(firePos, Blocks.AIR.defaultBlockState());
                
                // 重置计数
                smeltingCounts.remove(centerPos);
                
                // 提示玩家
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§c熔炼机的火已熄灭，需要重新点火！"), 
                    true
                );
            }
        }
        
        return result;
    }
}