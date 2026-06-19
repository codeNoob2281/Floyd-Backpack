package com.floyd.backpack.ui;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.ItemStack;

/**
 * @author floyd
 */
public class ChestButton {

    protected static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    protected ItemStack item;

    public ChestButton() {
    }

    public ChestButton(ItemStack item) {
        this.item = item;
    }

    /**
     * 获取ItemStack
     */
    public ItemStack getItemStack() {
        return this.item;
    }

    /**
     * 创建物品的lore描述
     *
     * @param text     文本，支持颜色符号
     * @param isItalic 是否斜体
     * @param isBold   是否加粗
     * @return
     */
    protected static TextComponent textComponent(String text, boolean isItalic, boolean isBold) {
        return LEGACY_SERIALIZER.deserialize(text)
                .decoration(TextDecoration.ITALIC, isItalic)
                .decoration(TextDecoration.BOLD, isBold);
    }
}
