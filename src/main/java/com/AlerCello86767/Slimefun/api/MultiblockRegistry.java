package com.AlerCello86767.Slimefun.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

/**
 * 多方块结构注册器
 * 用于注册和管理多方块结构
 */
public class MultiblockRegistry {

    private static final Map<String, MultiblockAPI> registeredStructures = new HashMap<>();
    private static final Map<BlockPos, MultiblockAPI> activeStructures = new HashMap<>();

    /**
     * 注册多方块结构
     * @param structure 多方块结构实例
     */
    public static void register(MultiblockAPI structure) {
        registeredStructures.put(structure.getStructureName(), structure);
    }

    /**
     * 获取已注册的结构
     * @param name 结构名称
     * @return 多方块结构实例
     */
    public static MultiblockAPI getStructure(String name) {
        return registeredStructures.get(name);
    }

    /**
     * 激活结构并记录
     * @param level 世界
     * @param centerPos 中心位置
     * @param structure 结构实例
     * @return 是否成功
     */
    public static boolean activateAndRecord(Level level, BlockPos centerPos, MultiblockAPI structure) {
        if (structure.activateStructure(level, centerPos)) {
            activeStructures.put(centerPos, structure);
            return true;
        }
        return false;
    }

    /**
     * 停用并移除记录
     * @param level 世界
     * @param centerPos 中心位置
     */
    public static void deactivateAndRemove(Level level, BlockPos centerPos) {
        MultiblockAPI structure = activeStructures.get(centerPos);
        if (structure != null) {
            structure.deactivateStructure(level, centerPos);
            activeStructures.remove(centerPos);
        }
    }

    /**
     * 检查位置是否有活跃结构
     * @param centerPos 中心位置
     * @return 是否有活跃结构
     */
    public static boolean hasActiveStructure(BlockPos centerPos) {
        return activeStructures.containsKey(centerPos);
    }

    /**
     * 获取活跃结构
     * @param centerPos 中心位置
     * @return 多方块结构实例
     */
    public static MultiblockAPI getActiveStructure(BlockPos centerPos) {
        return activeStructures.get(centerPos);
    }
}