package com.AlerCello86767.Slimefun.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * API接口 for 多方块结构
 * 用于定义和处理多方块机器结构
 */
public interface MultiblockAPI {

    /**
     * 检查指定位置的多方块结构是否完整
     * @param level 世界
     * @param centerPos 中心位置
     * @return 是否完整
     */
    boolean isStructureComplete(Level level, BlockPos centerPos);

    /**
     * 激活多方块结构
     * @param level 世界
     * @param centerPos 中心位置
     * @return 是否成功激活
     */
    boolean activateStructure(Level level, BlockPos centerPos);

    /**
     * 停用多方块结构
     * @param level 世界
     * @param centerPos 中心位置
     */
    void deactivateStructure(Level level, BlockPos centerPos);

    /**
     * 获取多方块结构的名称
     * @return 结构名称
     */
    String getStructureName();

    /**
     * 获取多方块结构的尺寸 (宽度, 高度, 深度)
     * @return 尺寸数组
     */
    int[] getStructureSize();
}