package com.floyd.backpack.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Vault 经济接入。懒加载：首次使用时尝试从 ServicesManager 获取 Economy 实现。
 * 服务器需安装 Vault（或兼容实现，如 VaultUnlocked）并配置经济插件。
 */
public final class EconomyProvider {

    private static Economy economy;

    private EconomyProvider() {
    }

    public static synchronized boolean setup() {
        if (economy != null) {
            return true;
        }
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null
                && Bukkit.getServer().getPluginManager().getPlugin("VaultUnlocked") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp =
                Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static Economy get() {
        if (economy == null) {
            setup();
        }
        return economy;
    }

    public static boolean isReady() {
        return economy != null;
    }
}
