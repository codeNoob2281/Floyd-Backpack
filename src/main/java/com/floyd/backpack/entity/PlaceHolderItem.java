package com.floyd.backpack.entity;

import com.floyd.backpack.ui.ChestButton;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * 占位符物品
 *
 * @author floyd
 */
public class PlaceHolderItem extends ChestButton {

    private static final NamespacedKey PLACEHOLDER_KEY = new NamespacedKey("floydbackpack", "placeholder");
    private static final NamespacedKey NEXT_LEVEL_KEY = new NamespacedKey("floydbackpack", "next_level_placeholder");


    public PlaceHolderItem(String materialName, String displayName) {
        this(materialName, displayName, false);
    }

    public PlaceHolderItem(String materialName, String displayName, boolean isNextLevel) {
        this(materialName, displayName, isNextLevel, null);
    }

    public PlaceHolderItem(String materialName, String displayName, boolean isNextLevel, String lore) {
        Material material = Material.getMaterial(materialName);
        if (material == null) {
            material = isNextLevel ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE;
        }
        ItemStack placeholder = new ItemStack(material);
        ItemMeta meta = placeholder.getItemMeta();
        meta.displayName(textComponent(displayName, false, false));
        if (lore != null && !lore.isEmpty()) {
            meta.lore(List.of(textComponent(lore, false, false)));
        }
        meta.getPersistentDataContainer().set(PLACEHOLDER_KEY, PersistentDataType.BOOLEAN, true);
        if (isNextLevel) {
            meta.getPersistentDataContainer().set(NEXT_LEVEL_KEY, PersistentDataType.BOOLEAN, true);
        }
        placeholder.setItemMeta(meta);
        this.item = placeholder;
    }

    public static boolean isPlaceholder(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(PLACEHOLDER_KEY, PersistentDataType.BOOLEAN);
    }

    public static boolean isNextLevelPlaceholder(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(NEXT_LEVEL_KEY, PersistentDataType.BOOLEAN);
    }
}
