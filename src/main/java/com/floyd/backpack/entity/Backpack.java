package com.floyd.backpack.entity;

import com.floyd.backpack.message.ChestUIMsg;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author floyd
 * @date 2026/3/23
 */
public class Backpack implements InventoryHolder {

    private volatile Inventory inventory;

    public static final int MIN_SIZE = 9;

    public static final int MAX_SIZE = 54;

    private static final NamespacedKey PLACEHOLDER_KEY = new NamespacedKey("floydbackpack", "placeholder");

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    @Getter
    private final String playerUuid;

    @Getter
    private final String playerName;

    @Getter
    private int size;

    @Getter
    private int level;

    @Getter
    private int usableSlots;

    /**
     * 背包数据同步锁，解决并发访问问题
     */
    @Getter
    private final Lock lock = new ReentrantLock();

    @Getter
    @Setter
    private String title;

    private int cachedLevel;

    private String placeholderMaterial;

    private String placeholderName;

    /**
     * 溢出物品映射（slot → base64），存储因降级而隐藏但未丢失的物品
     */
    @Getter
    private final Map<Integer, String> overflowItems = new LinkedHashMap<>();

    public Backpack(@NotNull Player player, int level, int usableSlots,
                    String placeholderMaterial, String placeholderName) {
        this(player.getUniqueId().toString(), player.getName(), level, usableSlots,
                placeholderMaterial, placeholderName);
    }

    public Backpack(String playerUuid, String playerName, int level, int usableSlots,
                    String placeholderMaterial, String placeholderName) {
        int size = calculateInventorySize(usableSlots);
        if (size <= 0 || size % 9 != 0) {
            throw new IllegalArgumentException("illegal size: " + size);
        }
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.level = level;
        this.usableSlots = usableSlots;
        this.size = size;
        this.cachedLevel = level;
        this.placeholderMaterial = placeholderMaterial;
        this.placeholderName = placeholderName;
    }

    public static int calculateInventorySize(int usableSlots) {
        return (int) Math.ceil((double) usableSlots / 9) * 9;
    }

    /**
     * 更新背包等级和容量。调用后下次 getInventory() 会触发重建。
     */
    public void setUpgrade(int newLevel, int newUsableSlots) {
        this.level = newLevel;
        this.usableSlots = newUsableSlots;
        this.size = calculateInventorySize(newUsableSlots);
    }

    @Override
    public @NotNull Inventory getInventory() {
        rebuildIfNeeded();
        return this.inventory;
    }

    private void rebuildIfNeeded() {
        String localeTitle = ChestUIMsg.BACKPACK_TITLE.content(playerName);
        boolean levelChanged = this.level != this.cachedLevel;
        boolean titleChanged = !localeTitle.equals(title);

        if (inventory != null && !titleChanged && !levelChanged) {
            return;
        }

        synchronized (this) {
            if (inventory != null && localeTitle.equals(title) && this.level == this.cachedLevel) {
                return;
            }
            rebuild(localeTitle);
        }
    }

    private void rebuild(String localeTitle) {
        Inventory oldInv = this.inventory;
        this.size = calculateInventorySize(usableSlots);
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

        fillPlaceholders();
        this.title = localeTitle;
        this.cachedLevel = this.level;
    }

    private void fillPlaceholders() {
        if (placeholderMaterial == null) {
            return;
        }
        Material material = Material.getMaterial(placeholderMaterial);
        if (material == null) {
            return;
        }

        for (int i = usableSlots; i < size; i++) {
            ItemStack placeholder = new ItemStack(material);
            ItemMeta meta = placeholder.getItemMeta();
            meta.displayName(LEGACY_SERIALIZER.deserialize(placeholderName));
            meta.getPersistentDataContainer().set(
                    PLACEHOLDER_KEY, PersistentDataType.BOOLEAN, true);
            placeholder.setItemMeta(meta);
            this.inventory.setItem(i, placeholder);
        }
    }

    public static boolean isPlaceholder(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(PLACEHOLDER_KEY, PersistentDataType.BOOLEAN);
    }
}
