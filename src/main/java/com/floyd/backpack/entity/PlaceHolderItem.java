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


    public PlaceHolderItem(String materialName, String displayName) {
        Material material = Material.getMaterial(materialName);
        if (material == null) {
            throw new IllegalArgumentException("Invalid placeholder material: " + materialName);
        }
        ItemStack placeholder = new ItemStack(material);
        ItemMeta meta = placeholder.getItemMeta();
        meta.displayName(textComponent(displayName, false, false));
        meta.getPersistentDataContainer().set(PLACEHOLDER_KEY, PersistentDataType.BOOLEAN, true);
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
}
