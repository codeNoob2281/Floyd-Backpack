package com.floyd.backpack;

import java.nio.file.Path;
import java.nio.file.Paths;

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
        return Paths.get(getPlugin().getDataPath().toString(), "backpack");
    }

    public static boolean reload() {
        return getPlugin().reload();
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
