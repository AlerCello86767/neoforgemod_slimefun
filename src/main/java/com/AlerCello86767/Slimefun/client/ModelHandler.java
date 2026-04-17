package com.AlerCello86767.Slimefun.client;

import com.AlerCello86767.Slimefun.SlimefunMod;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = SlimefunMod.MOD_ID, value = Dist.CLIENT)
public class ModelHandler {
    
    private static final Map<String, String> MODEL_MAP = new HashMap<>();
    
    static {
        // 工具
        MODEL_MAP.put("gold_pan", "item/tools/gold_pan");
        
        // 资源
        MODEL_MAP.put("sifted_ore", "item/miscellaneous/sifted_ore");
        MODEL_MAP.put("stone_chunk", "item/miscellaneous/stone_chunk");
        
        // 粉尘
        MODEL_MAP.put("aluminum_dust", "item/resources/dusts/aluminum_dust");
        MODEL_MAP.put("copper_dust", "item/resources/dusts/copper_dust");
        MODEL_MAP.put("gold_dust", "item/resources/dusts/gold_dust");
        MODEL_MAP.put("iron_dust", "item/resources/dusts/iron_dust");
        MODEL_MAP.put("lead_dust", "item/resources/dusts/lead_dust");
        MODEL_MAP.put("magnesium_dust", "item/resources/dusts/magnesium_dust");
        MODEL_MAP.put("silver_dust", "item/resources/dusts/silver_dust");
        MODEL_MAP.put("tin_dust", "item/resources/dusts/tin_dust");
        MODEL_MAP.put("zinc_dust", "item/resources/dusts/zinc_dust");
    }
    
    @SubscribeEvent
    public static void onModelRegister(ModelEvent.RegisterAdditional event) {
        for (String modelPath : MODEL_MAP.values()) {
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(
                SlimefunMod.MOD_ID, modelPath
            );
            event.register(ModelResourceLocation.standalone(location));
        }
    }
    
    @SubscribeEvent
    public static void onModelBaking(ModelEvent.ModifyBakingResult event) {
        for (Map.Entry<String, String> entry : MODEL_MAP.entrySet()) {
            String itemId = entry.getKey();
            String modelPath = entry.getValue();
            
            ModelResourceLocation defaultModel = ModelResourceLocation.standalone(
                ResourceLocation.fromNamespaceAndPath(SlimefunMod.MOD_ID, modelPath)
            );
            
            ResourceLocation itemRegistryName = ResourceLocation.fromNamespaceAndPath(
                SlimefunMod.MOD_ID, itemId
            );
            
            ModelResourceLocation itemModelLocation = new ModelResourceLocation(itemRegistryName, "inventory");
            
            if (event.getModels().containsKey(defaultModel)) {
                event.getModels().put(itemModelLocation, event.getModels().get(defaultModel));
            }
        }
    }
}