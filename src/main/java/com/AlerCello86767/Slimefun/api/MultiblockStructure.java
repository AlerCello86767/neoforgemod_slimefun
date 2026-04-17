package com.AlerCello86767.Slimefun.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

/**
 * 抽象多方块结构类
 * 提供基础的多方块结构实现
 */
public abstract class MultiblockStructure implements MultiblockAPI {

    protected final String name;
    protected final int width;
    protected final int height;
    protected final int depth;
    protected final Map<BlockPos, Block> structurePattern;

    /**
     * 构造函数
     * @param name 结构名称
     * @param width 宽度
     * @param height 高度
     * @param depth 深度
     * @param pattern 结构模式 (相对位置 -> 方块类型)
     */
    public MultiblockStructure(String name, int width, int height, int depth, Map<BlockPos, Block> pattern) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.structurePattern = pattern;
    }

    @Override
    public boolean isStructureComplete(Level level, BlockPos centerPos) {
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
        return true;
    }

    @Override
    public boolean activateStructure(Level level, BlockPos centerPos) {
        if (!isStructureComplete(level, centerPos)) {
            return false;
        }
        // 子类实现激活逻辑
        return onActivate(level, centerPos);
    }

    @Override
    public void deactivateStructure(Level level, BlockPos centerPos) {
        // 子类实现停用逻辑
        onDeactivate(level, centerPos);
    }

    @Override
    public String getStructureName() {
        return name;
    }

    @Override
    public int[] getStructureSize() {
        return new int[]{width, height, depth};
    }

    /**
     * 激活时的回调方法
     * @param level 世界
     * @param centerPos 中心位置
     * @return 是否成功
     */
    protected abstract boolean onActivate(Level level, BlockPos centerPos);

    /**
     * 停用时的回调方法
     * @param level 世界
     * @param centerPos 中心位置
     */
    protected abstract void onDeactivate(Level level, BlockPos centerPos);
}