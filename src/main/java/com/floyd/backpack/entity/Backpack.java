package com.floyd.backpack.entity;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author floyd
 * @date 2026/3/23
 */
public class Backpack implements InventoryHolder {

    private final Inventory inventory;

    public static final int DEFAULT_SIZE = 54;

    @Getter
    private final String playerUuid;

    @Getter
    private final String playerName;

    @Getter
    private final int size;

    /**
     * 背包数据同步锁，解决并发访问问题
     */
    @Getter
    private final Lock lock = new ReentrantLock();

    public Backpack(@NotNull Player player) {
        this(player.getUniqueId().toString(), player.getName(), DEFAULT_SIZE);
    }

    public Backpack(String playerUuid, String playerName, int size) {
        if (size <= 0 || size % 9 != 0) {
            throw new IllegalArgumentException("illegal size: " + size);
        }
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.size = size;
        this.inventory = Bukkit.createInventory(this, size, Component.text(playerName + "的背包", NamedTextColor.GOLD));
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
