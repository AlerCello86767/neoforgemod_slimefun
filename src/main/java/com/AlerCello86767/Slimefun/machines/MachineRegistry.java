package com.AlerCello86767.Slimefun.machines;

import com.AlerCello86767.Slimefun.api.MultiblockRegistry;
import com.AlerCello86767.Slimefun.machines.basic.DispenserBasedMachine;
import com.AlerCello86767.Slimefun.machines.basic.OreWasherMachine;
import com.AlerCello86767.Slimefun.machines.basic.SmelteryMachine;
import java.util.HashMap;
import java.util.Map;

/**
 * 机器注册表
 * 统一管理所有基础机器
 */
public class MachineRegistry {
    
    private static final Map<String, DispenserBasedMachine> MACHINES = new HashMap<>();
    
    /**
     * 初始化所有机器
     */
    public static void init() {
        // 注册洗矿机
        OreWasherMachine oreWasher = new OreWasherMachine();
        registerMachine(oreWasher);
        
        // 注册熔炼机
        SmelteryMachine smeltery = new SmelteryMachine();
        registerMachine(smeltery);
        
        // 可以在这里添加更多机器
    }
    
    /**
     * 注册机器
     */
    private static void registerMachine(DispenserBasedMachine machine) {
        MACHINES.put(machine.getStructureName(), machine);
        MultiblockRegistry.register(machine);
    }
    
    /**
     * 获取机器
     */
    public static DispenserBasedMachine getMachine(String name) {
        return MACHINES.get(name);
    }
    
    /**
     * 获取所有机器
     */
    public static Map<String, DispenserBasedMachine> getAllMachines() {
        return new HashMap<>(MACHINES);
    }
    
    /**
     * 获取机器显示名称
     */
    public static String getMachineDisplayName(String name) {
        DispenserBasedMachine machine = MACHINES.get(name);
        return machine != null ? machine.getMachineName() : "未知机器";
    }
}