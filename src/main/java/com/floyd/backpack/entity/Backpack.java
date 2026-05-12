package com.floyd.backpack.entity;

import com.floyd.backpack.message.ChestUIMsg;
import lombok.Getter;
import lombok.Setter;
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

    private volatile Inventory inventory;

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

    @Getter
    @Setter
    private String title;

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
    }

    @Override
    public @NotNull Inventory getInventory() {
        createNewInventoryIfTitleChange();
        return this.inventory;
    }

    /**
     * 创建新的背包
     */
    protected void createNewInventoryIfTitleChange() {
        String localeTitle = ChestUIMsg.BACKPACK_TITLE.content(playerName);
        if (inventory != null && localeTitle.equals(title)) {
            return;
        }
        Inventory oldInventory = inventory;
        inventory = null;
        synchronized (this) {
            if (inventory != null) {
                return;
            }
            inventory = Bukkit.createInventory(this, size, localeTitle);
            if (oldInventory != null) {
                inventory.setContents(oldInventory.getContents());
            }
            title = localeTitle;
        }
    }
}
