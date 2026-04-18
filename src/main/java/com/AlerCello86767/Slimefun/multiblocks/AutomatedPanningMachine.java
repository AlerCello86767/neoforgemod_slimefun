package com.AlerCello86767.Slimefun.multiblocks;

import com.AlerCello86767.Slimefun.Regist;
import com.AlerCello86767.Slimefun.api.MultiblockStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import java.util.Queue;
import java.util.HashMap;
import java.util.UUID;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AutomatedPanningMachine extends MultiblockStructure {
    
    private static final int MAX_QUEUE_SIZE = 128;
    private static final Map<BlockPos, Queue<ProcessingTask>> PROCESSING_QUEUES = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(4);
    private static final Map<ProcessingTask, ScheduledFuture<?>> TASK_SOUNDS = new ConcurrentHashMap<>();
    
    private static final Map<BlockPos, Block> PATTERN = new HashMap<>();
    
    static {
        PATTERN.put(new BlockPos(0, 0, 0), Blocks.CAULDRON);
        PATTERN.put(new BlockPos(0, 1, 0), null);
    }
    
    public AutomatedPanningMachine() {
        super("automated_panning_machine", 1, 2, 1, PATTERN);
    }
    
    @Override
    protected boolean onActivate(Level level, BlockPos centerPos) {
        return true;
    }
    
    @Override
    protected void onDeactivate(Level level, BlockPos centerPos) {
        PROCESSING_QUEUES.remove(centerPos);
    }
    
    @Override
    public boolean isStructureComplete(Level level, BlockPos centerPos) {
        BlockPos cauldronPos = centerPos;
        if (!level.getBlockState(cauldronPos).is(Blocks.CAULDRON)) {
            return false;
        }
        
        BlockPos trapdoorPos = centerPos.above();
        BlockState state = level.getBlockState(trapdoorPos);
        Block block = state.getBlock();
        
        if (!(block instanceof TrapDoorBlock) || block == Blocks.IRON_TRAPDOOR) {
            return false;
        }
        
        return true;
    }
    
    public InteractionResult onRightClick(Level level, BlockPos centerPos, Player player, ItemStack handItem) {
        if (!handItem.is(Items.GRAVEL)) {
            player.displayClientMessage(
                Component.literal("§c不支持的物品，请检查配方后再试！"), 
                true
            );
            return InteractionResult.FAIL;
        }
        
        Queue<ProcessingTask> queue = PROCESSING_QUEUES.computeIfAbsent(centerPos, k -> new ConcurrentLinkedQueue<>());
        
        if (queue.size() >= MAX_QUEUE_SIZE) {
            player.displayClientMessage(
                Component.literal("§c此机器加工队列已满！"), 
                true
            );
            return InteractionResult.FAIL;
        }
        
        ProcessingTask task = new ProcessingTask(player.getUUID(), centerPos);
        queue.add(task);
        handItem.shrink(1);
        
        player.displayClientMessage(
            Component.literal("§a已添加至队列，当前队列：" + queue.size() + "/" + MAX_QUEUE_SIZE), 
            true
        );
        
        // 启动这个任务的处理（独立计时）
        startTaskProcessing(level, task);
        
        return InteractionResult.SUCCESS;
    }
    
    private void startTaskProcessing(Level level, ProcessingTask task) {
        if (level.isClientSide) return;
        
        // 播放6次音效（每0.5秒一次），并保存Future以便取消
        ScheduledFuture<?> soundTask = scheduleSound(level, task.centerPos, 0, task);
        if (soundTask != null) {
            TASK_SOUNDS.put(task, soundTask);
        }
        
        // 3秒后完成
        SCHEDULER.schedule(() -> {
            if (level instanceof ServerLevel serverLevel) {
                // 在主线程中执行完成处理
                serverLevel.getServer().execute(() -> {
                    completeProcessing(serverLevel, task);
                });
            }
        }, 3, TimeUnit.SECONDS);
    }
    
    private ScheduledFuture<?> scheduleSound(Level level, BlockPos centerPos, int step, ProcessingTask task) {
        if (step >= 6) return null;
        
        return SCHEDULER.schedule(() -> {
            // 检查任务是否还在队列中（是否已经被完成）
            Queue<ProcessingTask> queue = PROCESSING_QUEUES.get(centerPos);
            if (queue != null && queue.contains(task)) {
                if (level instanceof ServerLevel serverLevel) {
                    // 在主线程中播放音效和粒子
                    serverLevel.getServer().execute(() -> {
                        if (serverLevel.isAreaLoaded(centerPos, 1)) {
                            serverLevel.playSound(null, centerPos, SoundEvents.SAND_BREAK, 
                                SoundSource.BLOCKS, 1.0F, 1.0F);

                            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.GRAVEL.defaultBlockState()),
                                centerPos.getX() + 0.5, centerPos.getY() + 1.5, centerPos.getZ() + 0.5,
                                10, 0.5, 0.5, 0.5, 0.1);
                        }
                    });
                }
                scheduleSound(level, centerPos, step + 1, task);
            }
        }, step * 500, TimeUnit.MILLISECONDS);
    }
    
    private void completeProcessing(ServerLevel level, ProcessingTask task) {
        BlockPos centerPos = task.centerPos;
        
        // 取消还未播放的音效
        ScheduledFuture<?> soundTask = TASK_SOUNDS.remove(task);
        if (soundTask != null) {
            soundTask.cancel(false);
        }
        
        // 从队列中移除这个任务
        Queue<ProcessingTask> queue = PROCESSING_QUEUES.get(centerPos);
        if (queue == null) return;
        
        boolean removed = queue.remove(task);
        if (!removed) return;
        
        // 播放完成音效
        level.playSound(null, centerPos, SoundEvents.EXPERIENCE_ORB_PICKUP, 
            SoundSource.PLAYERS, 0.5F, 1.0F);
        
        // 随机选择物品（现在在主线程中执行）
        Player player = level.getPlayerByUUID(task.playerUUID);
        if (player != null) {
            ItemStack itemToGive;
            int rand = level.random.nextInt(100);
            if (rand < 27) {
                itemToGive = Regist.SIFTED_ORE.get().getDefaultInstance();
            } else if (rand < 36) { // 27+9=36
                itemToGive = Items.IRON_NUGGET.getDefaultInstance();
            } else if (rand < 81) { // 36+45=81
                itemToGive = Items.FLINT.getDefaultInstance();
            } else {
                itemToGive = Items.CLAY_BALL.getDefaultInstance();
            }
            player.getInventory().add(itemToGive);
        }
    }
    
    private static class ProcessingTask {
        final UUID playerUUID;
        final BlockPos centerPos;
        
        ProcessingTask(UUID playerUUID, BlockPos centerPos) {
            this.playerUUID = playerUUID;
            this.centerPos = centerPos;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ProcessingTask that = (ProcessingTask) obj;
            return Objects.equals(playerUUID, that.playerUUID) && 
                   Objects.equals(centerPos, that.centerPos);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(playerUUID, centerPos);
        }
    }
}