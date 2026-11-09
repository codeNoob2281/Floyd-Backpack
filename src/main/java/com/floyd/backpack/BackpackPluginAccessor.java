package com.floyd.backpack;

import com.floyd.backpack.service.PlayerBackpackManager;
import org.springframework.context.ApplicationContext;

import java.nio.file.Path;

/**
 * 背包插件Accessor
 *
 * @author floyd
 */
public class BackpackPluginAccessor {

    private static volatile FloydBackpackPlugin INSTANCE;

    public static String getPluginName() {
        return getPlugin().getPluginName();
    }

    public static Path getBackpackDataPath() {
        return getPlugin().getDataFolder().toPath().resolve("backpack");
    }

    public static boolean reload() {
        return getPlugin().reload();
    }

    /**
     * 获取背包管理 Spring Bean（供实体 / PAPI 扩展等非 Spring 托管类调用）。
     */
    public static PlayerBackpackManager getPlayerBackpackManager() {
        ApplicationContext ctx = getPlugin().getApplicationContext();
        return ctx.getBean(PlayerBackpackManager.class);
    }

    public static FloydBackpackPlugin getPlugin() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (BackpackPluginAccessor.class) {
            if (INSTANCE == null) {
                INSTANCE = (FloydBackpackPlugin) FloydBackpackPlugin.instance();
            }
            return INSTANCE;
        }
    }
}
