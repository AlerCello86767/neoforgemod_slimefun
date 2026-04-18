package com.AlerCello86767.Slimefun.api;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 配方管理器
 * 用于管理多方块机器的配方
 */
public class RecipeManager {
    
    private static final Map<String, RecipeManager> managers = new HashMap<>();
    
    private final Map<Item, Recipe> recipes = new HashMap<>();
    private final String machineId;
    
    private RecipeManager(String machineId) {
        this.machineId = machineId;
    }
    
    /**
     * 获取或创建配方管理器
     * @param machineId 机器ID
     * @return 配方管理器
     */
    public static RecipeManager getManager(String machineId) {
        return managers.computeIfAbsent(machineId, RecipeManager::new);
    }
    
    /**
     * 注册配方
     * @param input 输入物品
     * @param output 输出物品
     * @param processingTime 处理时间（秒）
     */
    public void registerRecipe(Item input, ItemStack output, int processingTime) {
        recipes.put(input, new Recipe(input, output, processingTime));
    }
    
    /**
     * 注册配方
     * @param input 输入物品
     * @param output 输出物品
     * @param processingTime 处理时间（秒）
     */
    public void registerRecipe(Item input, Item output, int processingTime) {
        registerRecipe(input, output.getDefaultInstance(), processingTime);
    }
    
    /**
     * 获取配方
     * @param input 输入物品
     * @return 配方，如果没有则返回null
     */
    public Recipe getRecipe(Item input) {
        return recipes.get(input);
    }
    
    /**
     * 检查是否有配方
     * @param input 输入物品
     * @return 是否有配方
     */
    public boolean hasRecipe(Item input) {
        return recipes.containsKey(input);
    }
    
    /**
     * 获取所有配方
     * @return 配方映射
     */
    public Map<Item, Recipe> getAllRecipes() {
        return new HashMap<>(recipes);
    }
    
    /**
     * 配方类
     */
    public static class Recipe {
        private final Item input;
        private final ItemStack output;
        private final int processingTime; // 秒
        
        public Recipe(Item input, ItemStack output, int processingTime) {
            this.input = input;
            this.output = output;
            this.processingTime = processingTime;
        }
        
        public Item getInput() {
            return input;
        }
        
        public ItemStack getOutput() {
            return output.copy();
        }
        
        public int getProcessingTime() {
            return processingTime;
        }
    }
    
    /**
     * 初始化默认配方
     */
    public static void initDefaultRecipes() {
        // 熔炼机配方
        RecipeManager smelteryManager = getManager("smeltery");
        
        // ==================== 金属粉尘 -> 金属锭 ====================
        // 铁粉 -> 铁锭 (您要求的配方)
        smelteryManager.registerRecipe(com.AlerCello86767.Slimefun.Regist.IRON_DUST.get(), Items.IRON_INGOT, 4);
        
        // 金粉 -> 金锭
        smelteryManager.registerRecipe(com.AlerCello86767.Slimefun.Regist.GOLD_DUST.get(), Items.GOLD_INGOT, 4);
        
        // 铜粉 -> 铜锭
        smelteryManager.registerRecipe(com.AlerCello86767.Slimefun.Regist.COPPER_DUST.get(), Items.COPPER_INGOT, 4);
        
        // 铝粉 -> 铝锭
        smelteryManager.registerRecipe(com.AlerCello86767.Slimefun.Regist.ALUMINUM_DUST.get(), 
            com.AlerCello86767.Slimefun.Regist.ALUMINUM_INGOT.get(), 4);
        
        // 铅粉 -> 铅锭
        smelteryManager.registerRecipe(com.AlerCello86767.Slimefun.Regist.LEAD_DUST.get(), 
            com.AlerCello86767.Slimefun.Regist.LEAD_INGOT.get(), 4);
        
        // 银粉 -> 银锭
        smelteryManager.registerRecipe(com.AlerCello86767.Slimefun.Regist.SILVER_DUST.get(), 
            com.AlerCello86767.Slimefun.Regist.SILVER_INGOT.get(), 4);
        
        // 锡粉 -> 锡锭
        smelteryManager.registerRecipe(com.AlerCello86767.Slimefun.Regist.TIN_DUST.get(), 
            com.AlerCello86767.Slimefun.Regist.TIN_INGOT.get(), 4);
        
        // 锌粉 -> 锌锭
        smelteryManager.registerRecipe(com.AlerCello86767.Slimefun.Regist.ZINC_DUST.get(), 
            com.AlerCello86767.Slimefun.Regist.ZINC_INGOT.get(), 4);
        
        // 镁粉 -> 镁锭
        smelteryManager.registerRecipe(com.AlerCello86767.Slimefun.Regist.MAGNESIUM_DUST.get(), 
            com.AlerCello86767.Slimefun.Regist.MAGNESIUM_INGOT.get(), 4);
    }
}
