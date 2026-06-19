package com.floyd.backpack.tools;

import com.floyd.backpack.message.BackpackToolMsg;
import com.floyd.backpack.ui.ChestButton;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * 背包打开工具类
 *
 * @author floyd
 */
public class OpenBackpackTool extends ChestButton {

    private static final NamespacedKey BACKPACK_TOOL_KEY = new NamespacedKey("floydbackpack", "backpack_tool");

    public OpenBackpackTool() {
        ItemStack is = new ItemStack(Material.ENDER_EYE, 1);
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.addEnchant(Enchantment.UNBREAKING, 10, true);
        itemMeta.itemName(LEGACY_SERIALIZER.deserialize(BackpackToolMsg.ITEM_NAME.content()));
        itemMeta.lore(List.of(
                LEGACY_SERIALIZER.deserialize(BackpackToolMsg.LORE_LINE1.content()).decoration(TextDecoration.ITALIC, false),
                LEGACY_SERIALIZER.deserialize(BackpackToolMsg.LORE_LINE2.content()).decoration(TextDecoration.ITALIC, false)
        ));
        itemMeta.getPersistentDataContainer().set(BACKPACK_TOOL_KEY, PersistentDataType.BOOLEAN, true);
        is.setItemMeta(itemMeta);
        this.item = is;
    }

    /**
     * 匹配交互事件
     */
    public static boolean matchEvent(PlayerInteractEvent event) {
        if (event == null || !event.getAction().isRightClick()) {
            return false;
        }
        ItemStack itemStackInHand = event.getItem();
        return matchItemStack(itemStackInHand);
    }

    public static boolean matchItemStack(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        // Check PDC tag (current format, supports all locales)
        if (meta.getPersistentDataContainer().has(BACKPACK_TOOL_KEY, PersistentDataType.BOOLEAN)) {
            return true;
        }
        // Backward compatibility: match legacy items by material and enchantment
        return itemStack.getType() == Material.ENDER_EYE
                && meta.hasEnchant(Enchantment.UNBREAKING)
                && meta.getEnchantLevel(Enchantment.UNBREAKING) == 10;
    }

}
