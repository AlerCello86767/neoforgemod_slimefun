package com.AlerCello86767.Slimefun;

import com.AlerCello86767.Slimefun.items.miscellaneous.SiftedOreItem;
import com.AlerCello86767.Slimefun.items.resources.DustItem;
import com.AlerCello86767.Slimefun.items.resources.StoneChunkItem;
import com.AlerCello86767.Slimefun.items.tools.GoldPanItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Regist {
    
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SlimefunMod.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, SlimefunMod.MOD_ID);
    
    // ==================== 工具 ====================
    public static final DeferredItem<Item> GOLD_PAN = ITEMS.register("gold_pan", 
        () -> new GoldPanItem(new Item.Properties().durability(128)));
    
    // ==================== 资源 ====================
    // 筛矿
    public static final DeferredItem<Item> SIFTED_ORE = ITEMS.register("sifted_ore", 
        () -> new SiftedOreItem(new Item.Properties()));
    
    // 石块
    public static final DeferredItem<Item> STONE_CHUNK = ITEMS.register("stone_chunk", 
        () -> new StoneChunkItem(new Item.Properties()));
    // 粉尘
    public static final DeferredItem<Item> ALUMINUM_DUST = ITEMS.register("aluminum_dust", 
        () -> new DustItem(new Item.Properties()));
    
    public static final DeferredItem<Item> COPPER_DUST = ITEMS.register("copper_dust", 
        () -> new DustItem(new Item.Properties()));
    
    public static final DeferredItem<Item> GOLD_DUST = ITEMS.register("gold_dust", 
        () -> new DustItem(new Item.Properties()));
    
    public static final DeferredItem<Item> IRON_DUST = ITEMS.register("iron_dust", 
        () -> new DustItem(new Item.Properties()));
    
    public static final DeferredItem<Item> LEAD_DUST = ITEMS.register("lead_dust", 
        () -> new DustItem(new Item.Properties()));
    
    public static final DeferredItem<Item> MAGNESIUM_DUST = ITEMS.register("magnesium_dust", 
        () -> new DustItem(new Item.Properties()));
    
    public static final DeferredItem<Item> SILVER_DUST = ITEMS.register("silver_dust", 
        () -> new DustItem(new Item.Properties()));
    
    public static final DeferredItem<Item> TIN_DUST = ITEMS.register("tin_dust", 
        () -> new DustItem(new Item.Properties()));
    
    public static final DeferredItem<Item> ZINC_DUST = ITEMS.register("zinc_dust", 
        () -> new DustItem(new Item.Properties()));

    // 锭
    public static final DeferredItem<Item> ALUMINUM_BRASS_INGOT = ITEMS.register("aluminum_brass_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ALUMINUM_BRONZE_INGOT = ITEMS.register("aluminum_bronze_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ALUMINUM_INGOT = ITEMS.register("aluminum_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BILLON_INGOT = ITEMS.register("billon_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BRASS_INGOT = ITEMS.register("brass_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BRONZE_INGOT = ITEMS.register("bronze_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COBALT_INGOT = ITEMS.register("cobalt_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_INGOT = ITEMS.register("copper_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORINTHIAN_BRONZE_INGOT = ITEMS.register("corinthian_bronze_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DAMASCUS_STEEL_INGOT = ITEMS.register("damascus_steel_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DURALUMIN_INGOT = ITEMS.register("duralumin_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FERROSILICON = ITEMS.register("ferrosilicon",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GILDED_IRON = ITEMS.register("gilded_iron",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HARDENED_METAL = ITEMS.register("hardened_metal",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LEAD_INGOT = ITEMS.register("lead_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MAGNESIUM_INGOT = ITEMS.register("magnesium_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NICKEL_INGOT = ITEMS.register("nickel_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> REDSTONE_ALLOY_INGOT = ITEMS.register("redstone_alloy_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> REINFORCED_ALLOY = ITEMS.register("reinforced_alloy",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SILVER_INGOT = ITEMS.register("silver_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SOLDER_INGOT = ITEMS.register("solder_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_INGOT = ITEMS.register("steel_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TIN_INGOT = ITEMS.register("tin_ingot",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ZINC_INGOT = ITEMS.register("zinc_ingot",
        () -> new Item(new Item.Properties()));

    // ==================== 创造模式标签 ====================
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SLIMEFUN_TAB = TABS.register("slimefun_tab", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.slimefun"))
        .icon(() -> new ItemStack(Items.SLIME_BALL))
        .displayItems((parameters, output) -> {
            output.accept(GOLD_PAN.get());

            output.accept(SIFTED_ORE.get());
            output.accept(STONE_CHUNK.get());
            
            output.accept(ALUMINUM_DUST.get());
            output.accept(COPPER_DUST.get());
            output.accept(GOLD_DUST.get());
            output.accept(IRON_DUST.get());
            output.accept(LEAD_DUST.get());
            output.accept(MAGNESIUM_DUST.get());
            output.accept(SILVER_DUST.get());
            output.accept(TIN_DUST.get());
            output.accept(ZINC_DUST.get());
            output.accept(ALUMINUM_BRASS_INGOT.get());
            output.accept(ALUMINUM_BRONZE_INGOT.get());
            output.accept(ALUMINUM_INGOT.get());
            output.accept(BILLON_INGOT.get());
            output.accept(BRASS_INGOT.get());
            output.accept(BRONZE_INGOT.get());
            output.accept(COBALT_INGOT.get());
            output.accept(COPPER_INGOT.get());
            output.accept(CORINTHIAN_BRONZE_INGOT.get());
            output.accept(DAMASCUS_STEEL_INGOT.get());
            output.accept(DURALUMIN_INGOT.get());
            output.accept(FERROSILICON.get());
            output.accept(GILDED_IRON.get());
            output.accept(HARDENED_METAL.get());
            output.accept(LEAD_INGOT.get());
            output.accept(MAGNESIUM_INGOT.get());
            output.accept(NICKEL_INGOT.get());
            output.accept(REDSTONE_ALLOY_INGOT.get());
            output.accept(REINFORCED_ALLOY.get());
            output.accept(SILVER_INGOT.get());
            output.accept(SOLDER_INGOT.get());
            output.accept(STEEL_INGOT.get());
            output.accept(TIN_INGOT.get());
            output.accept(ZINC_INGOT.get());
        })
        .build());
}