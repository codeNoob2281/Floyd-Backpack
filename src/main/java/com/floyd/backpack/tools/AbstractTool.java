package com.floyd.backpack.tools;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * @author floyd
 */
public abstract class AbstractTool {

    protected static TextComponent loreText(String text, NamedTextColor color) {
        return Component.text(text, color)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);
    }
}
