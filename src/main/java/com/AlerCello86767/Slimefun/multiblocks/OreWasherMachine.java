package com.AlerCello86767.Slimefun.multiblocks;

import com.AlerCello86767.Slimefun.Regist;
import com.AlerCello86767.Slimefun.api.MultiblockStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OreWasherMachine extends MultiblockStructure {

    private static final Map<BlockPos, Block> PATTERN = new HashMap<>();
    private static final Item[] DUSTS = new Item[] {
        Regist.ALUMINUM_DUST.get(),
        Regist.COPPER_DUST.get(),
        Regist.GOLD_DUST.get(),
        Regist.IRON_DUST.get(),
        Regist.LEAD_DUST.get(),
        Regist.MAGNESIUM_DUST.get(),
        Regist.SILVER_DUST.get(),
        Regist.TIN_DUST.get(),
        Regist.ZINC_DUST.get()
    };

    static {
        PATTERN.put(new BlockPos(0, 0, 0), Blocks.CAULDRON);
        PATTERN.put(new BlockPos(0, 1, 0), null);
        PATTERN.put(new BlockPos(0, 2, 0), Blocks.DISPENSER);
    }

    public OreWasherMachine() {
        super("ore_washer", 1, 3, 1, PATTERN);
    }

    @Override
    protected boolean onActivate(Level level, BlockPos centerPos) {
        return true;
    }

    @Override
    protected void onDeactivate(Level level, BlockPos centerPos) {
        // no persistent data to clear
    }

    @Override
    public boolean isStructureComplete(Level level, BlockPos centerPos) {
        if (!level.getBlockState(centerPos).is(Blocks.CAULDRON)) {
            return false;
        }

        BlockState fenceState = level.getBlockState(centerPos.above());
        if (!(fenceState.getBlock() instanceof FenceBlock)) {
            return false;
        }

        BlockState dispenserState = level.getBlockState(centerPos.above(2));
        if (!(dispenserState.getBlock() instanceof DispenserBlock)) {
            return false;
        }

        return true;
    }

    public InteractionResult onRightClick(Level level, BlockPos centerPos, Player player, ItemStack handItem) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos dispenserPos = centerPos.above(2);
        if (!(level.getBlockEntity(dispenserPos) instanceof DispenserBlockEntity dispenser)) {
            player.displayClientMessage(
                Component.literal("§c结构未完成或未找到发射器！"),
                true
            );
            return InteractionResult.FAIL;
        }

        Container container = dispenser;
        int size = container.getContainerSize();
        int recipeSlot = -1;
        boolean hasItem = false;
        boolean hasUnsupported = false;

        for (int i = 0; i < size; i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            hasItem = true;
            if (stack.is(Regist.SIFTED_ORE.get())) {
                recipeSlot = i;
                break;
            } else {
                hasUnsupported = true;
            }
        }

        if (!hasItem) {
            player.displayClientMessage(
                Component.literal("§c当前待加工已空，请添加物品"),
                true
            );
            return InteractionResult.FAIL;
        }

        if (recipeSlot < 0) {
            player.displayClientMessage(
                Component.literal("§c不支持的物品，请检查配方后再试"),
                true
            );
            return InteractionResult.FAIL;
        }

        ItemStack outputStone = Regist.STONE_CHUNK.get().getDefaultInstance();
        ItemStack outputDust = new ItemStack(DUSTS[level.random.nextInt(DUSTS.length)]);

        if (!canInsert(container, outputStone) || !canInsert(container, outputDust)) {
            player.displayClientMessage(
                Component.literal("§c物品已满！请取出部分后再试"),
                true
            );
            return InteractionResult.FAIL;
        }

        if (level.isAreaLoaded(dispenserPos, 1)) {
            level.playSound(null, dispenserPos, SoundEvents.WOODEN_BUTTON_CLICK_OFF,
                SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        container.removeItem(recipeSlot, 1);
        insertItem(container, outputStone);
        insertItem(container, outputDust);
        dispenser.setChanged();

        return InteractionResult.SUCCESS;
    }

    private static boolean canInsert(Container container, ItemStack stack) {
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

    private static void insertItem(Container container, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

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

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack existing = container.getItem(i);
            if (existing.isEmpty()) {
                container.setItem(i, stack.copy());
                return;
            }
        }
    }

    private static boolean sameStack(ItemStack a, ItemStack b) {
        return a.getItem() == b.getItem();
    }
}
