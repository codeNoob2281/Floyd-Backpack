package com.floyd.backpack;

import com.floyd.backpack.event.BackpackEventListener;
import com.floyd.backpack.service.ConfirmOperationManager;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.backpack.setting.SettingsReloadManager;
import com.floyd.core.FloydPlugin;
import com.floyd.core.PluginBizException;
import com.floyd.core.logging.ConsoleLogger;
import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.settings.PluginSettingsManager;
import org.bukkit.Bukkit;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 *
 * floyd backpack plugin main class
 *
 * @author floyd
 * @date 2026/03/22
 * @since 1.0.0
 */

public class FloydBackpackPlugin extends FloydPlugin {

    private static final ConsoleLogger logger = ConsoleLoggerFactory.get(FloydBackpackPlugin.class);

    @Override
    public String getPluginName() {
        return "Floyd-Backpack";
    }

    @Override
    protected void initialize() {
        // 绑定指令
        //Bukkit.getPluginCommand("backpack")
        //        .setExecutor(getApplicationContext().getBean(BackpackCmdExecutor.class));
        // 注册事件监听器
        registerEventListener();
        // 初始化目录
        initDataDirs();
    }

    private void registerEventListener() {
        BackpackEventListener eventListener = getApplicationContext().getBean(BackpackEventListener.class);
        Bukkit.getPluginManager().registerEvents(eventListener, this);
    }

    @Override
    protected void cleanup() {
        // 保存背包数据
        PlayerBackpackManager playerBackpackManager = getApplicationContext().getBean(PlayerBackpackManager.class);
        playerBackpackManager.saveAllBackpack();
    }

    /**
     * 重载插件
     */
    protected synchronized boolean reload() {
        try {
            logger.info("正在重载配置...");
            ApplicationContext applicationContext = getApplicationContext();
            // 重新加载配置
            SettingsReloadManager settingsReloadManager = applicationContext.getBean(SettingsReloadManager.class);
            settingsReloadManager.reload();
            // 重载定时任务
            ConfirmOperationManager confirmOperationManager = applicationContext.getBean(ConfirmOperationManager.class);
            confirmOperationManager.reload();
            // 重载日志系统
            PluginSettingsManager pluginSettingsManager = applicationContext.getBean(PluginSettingsManager.class);
            ConsoleLoggerFactory.reloadFromConfig(pluginSettingsManager);
            logger.info("配置重载完成！");
            return true;
        } catch (Exception e) {
            logger.error("配置重载失败！", e);
            return false;
        }
    }

    private void initDataDirs() {
        // 创建背包数据目录
        File backpackDataDir = BackpackPluginAccessor.getBackpackDataPath().toFile();
        if (!backpackDataDir.exists()) {
            if (!backpackDataDir.mkdirs()) {
                throw new PluginBizException("创建数据目录失败：" + backpackDataDir.getAbsolutePath());
            }
        }
    }

    @Override
    protected List<Class<?>> getCustomConfigClasses() {
        return Collections.singletonList(SpringApplication.class);
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

    @Configuration
    @ComponentScan(basePackages = {"com.floyd.backpack", "com.floyd.core"})
    public static class SpringApplication {
    }

}
