package com.floyd.backpack;

import com.floyd.backpack.command.BackpackCmdExecutor;
import com.floyd.backpack.injection.CommandRegistry;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.core.FloydPlugin;
import com.floyd.core.PluginBizException;
import org.bukkit.Bukkit;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * floyd backpack plugin main class
 *
 * @author floyd
 * @date 2026/03/22
 * @since 1.0.0
 */
public final class FloydBackpackPlugin extends FloydPlugin {

    @Override
    public String getPluginName() {
        return "Floyd-Backpack";
    }

    @Override
    protected void initialize() {
        // 绑定指令
        Bukkit.getPluginCommand("backpack")
                .setExecutor(getApplicationContext().getBean(BackpackCmdExecutor.class));
        // 初始化目录
        initDataDirs();
    }

    @Override
    protected void cleanup() {
        // 保存背包数据
        PlayerBackpackManager.saveAllBackpack();
    }

    private void initDataDirs() {
        // 创建背包数据目录
        File backpackDataDir = getBackpackDataPath().toFile();
        if (!backpackDataDir.exists()) {
            if (!backpackDataDir.mkdirs()) {
                throw new PluginBizException("创建数据目录失败：" + backpackDataDir.getAbsolutePath());
            }
        }
    }

    public Path getBackpackDataPath() {
        return Paths.get(getDataPath().toString(), "backpack");
    }

    @Override
    protected Class<?>[] getConfigClasses() {
        return new Class<?>[]{CommandRegistry.class};
    }

    @Override
    protected String getBanner() {
        return " _______ _                 _    ______              _                       _     \n" +
                "(_______) |               | |  (____  \\            | |                     | |    \n" +
                " _____  | | ___  _   _  _ | |   ____)  ) ____  ____| |  _ ____   ____  ____| |  _ \n" +
                "|  ___) | |/ _ \\| | | |/ || |  |  __  ( / _  |/ ___) | / )  _ \\ / _  |/ ___) | / )\n" +
                "| |     | | |_| | |_| ( (_| |  | |__)  | ( | ( (___| |< (| | | ( ( | ( (___| |< ( \n" +
                "|_|     |_|\\___/ \\__  |\\____|  |______/ \\_||_|\\____)_| \\_) ||_/ \\_||_|\\____)_| \\_)\n" +
                "                (____/                                   |_|                      ";
    }
}
