package com.AlerCello86767.Slimefun;

import com.AlerCello86767.Slimefun.api.MultiblockRegistry;
import com.AlerCello86767.Slimefun.api.RecipeManager;
import com.AlerCello86767.Slimefun.machines.MachineRegistry;
import com.AlerCello86767.Slimefun.multiblocks.AutomatedPanningMachine;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod("slimefun")
public class SlimefunMod {

    public static final String MOD_ID = "slimefun";

    public SlimefunMod(IEventBus modEventBus) {
        // 注册物品
        Regist.ITEMS.register(modEventBus);
        // 注册创造模式标签
        Regist.TABS.register(modEventBus);
        // 注册事件
        modEventBus.addListener(this::commonSetup);
    }
    
    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 注册自动淘金机（独立的多方块机器）
            MultiblockRegistry.register(new AutomatedPanningMachine());
            
            // 初始化基础机器注册表
            MachineRegistry.init();
            
            // 初始化默认配方
            RecipeManager.initDefaultRecipes();
        });
    }
}