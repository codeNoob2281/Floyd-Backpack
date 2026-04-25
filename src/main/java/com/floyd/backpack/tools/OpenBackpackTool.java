package com.floyd.backpack.tools;

import com.floyd.backpack.FloydBackpackPlugin;
import com.floyd.backpack.constant.Constants;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * 背包打开工具类
 *
 * @author floyd
 */
public class OpenBackpackTool extends AbstractTool {

    private static final ItemStack ITEM_STACK_TEMPLATE = initItemStack();

    /**
     * 获取ItemStack
     *
     * @return
     */
    public static ItemStack getItemStack() {
        return initItemStack();
    }

    /**
     * 匹配交互事件
     *
     * @param event
     * @return
     */
    public static boolean matchEvent(PlayerInteractEvent event) {
        if (event == null || !event.getAction().isRightClick()) {
            return false;
        }
        ItemStack itemStackInHand = event.getItem();
        return itemStackInHand != null && ITEM_STACK_TEMPLATE.isSimilar(itemStackInHand);
    }

    private static ItemStack initItemStack() {
        ItemStack is = new ItemStack(Material.ENDER_EYE, 1);
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.addEnchant(Enchantment.UNBREAKING, 10, true);
        itemMeta.itemName(Component.text(Constants.MESSAGE_PREFIX, NamedTextColor.AQUA)
                .append(Component.text(" 右键打开背包", NamedTextColor.GOLD)));
        itemMeta.lore(List.of(
                loreText("手持该物品，右键打开背包", NamedTextColor.GREEN),
                loreText("也可以输入指令/bp打开", NamedTextColor.GREEN)
        ));
        is.setItemMeta(itemMeta);
        return is;
    }

}
