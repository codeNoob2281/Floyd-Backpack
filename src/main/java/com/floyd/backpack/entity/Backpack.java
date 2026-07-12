package com.floyd.backpack.entity;

import com.floyd.backpack.message.ChestUIMsg;
import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author floyd
 * @since 2026/3/23
 */
public class Backpack implements InventoryHolder {

    private static final Logger logger = ConsoleLoggerFactory.get(Backpack.class);

    private volatile Inventory inventory;

    public static final int MIN_SIZE = 1;

    public static final int MAX_SIZE = 54;

    @Getter
    private final String playerUuid;

    @Getter
    private final String playerName;

    @Getter
    private final int size = MAX_SIZE;

    @Getter
    private int level;

    @Getter
    private int usableSlots;

    /**
     * 背包数据同步锁，解决并发访问问题
     */
    @Getter
    private final Lock lock = new ReentrantLock();

    /**
     * 脏标记：true 表示自上次持久化以来发生过修改，定时任务仅写盘此类背包
     */
    @Getter
    private volatile boolean dirty = false;

    /**
     * 标记背包为已修改。任何对 inventory/等级/容量的写操作完成后调用此方法。
     */
    public void markDirty() {
        this.dirty = true;
    }

    /**
     * 清除脏标记，定时任务成功写盘后调用。
     */
    public void clearDirty() {
        this.dirty = false;
    }

    @Getter
    @Setter
    private String title;

    private int cachedLevel;

    private String placeholderName;

    private final String placeholderMaterial;

    private final String nextLevelMaterial;

    private String nextLevelName;

    private String nextLevelLore;

    /**
     * 更新下一级可解锁容量（等级变化时调用）
     */
    @Setter
    private int nextLevelUsableSlots;

    /**
     * 溢出物品映射（slot → base64），存储因降级而隐藏但未丢失的物品
     */
    @Getter
    private final Map<Integer, String> overflowItems = new LinkedHashMap<>();

    public Backpack(@NotNull Player player, int level, int usableSlots,
                    String placeholderMaterial, String placeholderName,
                    String nextLevelMaterial, String nextLevelName, int nextLevelUsableSlots) {
        this(player.getUniqueId().toString(), player.getName(), level, usableSlots,
                placeholderMaterial, placeholderName,
                nextLevelMaterial, nextLevelName, nextLevelUsableSlots);
    }

    public Backpack(String playerUuid, String playerName, int level, int usableSlots,
                    String placeholderMaterial, String placeholderName,
                    String nextLevelMaterial, String nextLevelName, int nextLevelUsableSlots) {
        usableSlots = fixedUsableSlots(usableSlots);
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.level = level;
        this.usableSlots = usableSlots;
        this.cachedLevel = level;
        this.placeholderMaterial = placeholderMaterial;
        this.placeholderName = placeholderName;
        this.nextLevelMaterial = nextLevelMaterial;
        this.nextLevelName = nextLevelName;
        this.nextLevelUsableSlots = nextLevelUsableSlots;
    }

    /**
     * 更新背包等级和容量。调用后下次 getInventory() 会触发重建。
     */
    public void setUpgrade(int newLevel, int newUsableSlots) {
        newUsableSlots = fixedUsableSlots(newUsableSlots);
        this.level = newLevel;
        this.usableSlots = newUsableSlots;
        markDirty();
    }

    @Override
    public @NotNull Inventory getInventory() {
        rebuildIfNeeded();
        return this.inventory;
    }

    private void rebuildIfNeeded() {
        if (inventory != null && !inventory.getViewers().isEmpty()) {
            //如果仍有玩家正在查看，则暂不重建，等玩家关闭背包后再在下一次打开时重建。
            return;
        }

        String localeTitle = ChestUIMsg.BACKPACK_TITLE.content(playerName);
        String currentPlaceholderName = ChestUIMsg.PLACEHOLDER_LOCKED_SLOT_NAME.content();
        String currentNextLevelName = ChestUIMsg.PLACEHOLDER_NEXT_LEVEL_SLOT_NAME.content();
        String currentNextLevelLore = ChestUIMsg.PLACEHOLDER_NEXT_LEVEL_SLOT_LORE.content();
        boolean levelChanged = this.level != this.cachedLevel;
        boolean titleChanged = !localeTitle.equals(title);
        boolean placeholderChanged = !currentPlaceholderName.equals(this.placeholderName)
                || !currentNextLevelName.equals(this.nextLevelName)
                || !currentNextLevelLore.equals(this.nextLevelLore);

        if (inventory != null && !titleChanged && !levelChanged && !placeholderChanged) {
            return;
        }

        synchronized (this) {
            if (inventory != null && localeTitle.equals(title) && this.level == this.cachedLevel
                    && currentPlaceholderName.equals(this.placeholderName)
                    && currentNextLevelName.equals(this.nextLevelName)
                    && currentNextLevelLore.equals(this.nextLevelLore)) {
                return;
            }
            rebuild(localeTitle, currentPlaceholderName, currentNextLevelName, currentNextLevelLore);
        }
    }

    private void rebuild(String localeTitle, String currentPlaceholderName, String currentNextLevelName, String currentNextLevelLore) {
        Inventory oldInv = this.inventory;
        this.inventory = Bukkit.createInventory(this, this.size, localeTitle);

        if (oldInv != null) {
            int copyLimit = Math.min(oldInv.getSize(), usableSlots);
            for (int i = 0; i < copyLimit; i++) {
                ItemStack item = oldInv.getItem(i);
                if (item != null) {
                    this.inventory.setItem(i, item);
                }
            }
        }

        this.placeholderName = currentPlaceholderName;
        this.nextLevelName = currentNextLevelName;
        this.nextLevelLore = currentNextLevelLore;
        reFillPlaceholders();
        this.title = localeTitle;
        this.cachedLevel = this.level;
    }

    private void reFillPlaceholders() {
        // 清除可见范围内的过期占位符
        for (int i = 0; i < usableSlots; i++) {
            ItemStack item = this.inventory.getItem(i);
            if (PlaceHolderItem.isPlaceholder(item)) {
                this.inventory.setItem(i, null);
            }
        }
        // 下一级可解锁槽位（绿色）
        int nextBoundary = Math.min(nextLevelUsableSlots, size);
        for (int i = usableSlots; i < nextBoundary; i++) {
            this.inventory.setItem(i, new PlaceHolderItem(nextLevelMaterial, nextLevelName, true, nextLevelLore).getItemStack());
        }
        // 彻底锁定槽位（灰色）
        for (int i = nextBoundary; i < size; i++) {
            this.inventory.setItem(i, new PlaceHolderItem(placeholderMaterial, placeholderName).getItemStack());
        }
    }

    private int fixedUsableSlots(int usableSlotsToCheck) {
        if (usableSlotsToCheck < MIN_SIZE || usableSlotsToCheck > MAX_SIZE) {
            int fitUsableSlots = Math.max(MIN_SIZE, Math.min(MAX_SIZE, usableSlotsToCheck));
            logger.warn("UsableSlots value should between {} and {} but got {}, using default value {}",
                    MIN_SIZE, MAX_SIZE, usableSlotsToCheck, fitUsableSlots);
            return fitUsableSlots;
        }
        return usableSlotsToCheck;
    }

}
