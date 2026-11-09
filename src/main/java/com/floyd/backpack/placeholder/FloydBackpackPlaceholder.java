package com.floyd.backpack.placeholder;

import com.floyd.backpack.BackpackPluginAccessor;
import com.floyd.backpack.entity.Backpack;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * PlaceholderAPI 扩展，暴露个人仓库实时数据给菜单 / 记分板等使用。
 * 变量：
 *   %floydbackpack_used%  已使用格数
 *   %floydbackpack_max%   当前总容量
 *   %floydbackpack_free%  剩余空余格数
 *   %floydbackpack_level% 当前等级
 */
public class FloydBackpackPlaceholder extends PlaceholderExpansion {

    private final String identifier = "floydbackpack";

    @Override
    public @NotNull String getIdentifier() {
        return identifier;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Floyd";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.2.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || !player.isOnline() || player.getPlayer() == null) {
            return "";
        }
        try {
            Backpack backpack = BackpackPluginAccessor.getPlayerBackpackManager()
                    .getBackpack(player.getPlayer());
            switch (params.toLowerCase()) {
                case "used":
                    return String.valueOf(backpack.getUsedSlots());
                case "max":
                    return String.valueOf(backpack.getUsableSlots());
                case "free":
                    return String.valueOf(backpack.getUsableSlots() - backpack.getUsedSlots());
                case "level":
                    return String.valueOf(backpack.getLevel());
                default:
                    return null;
            }
        } catch (Throwable t) {
            return "";
        }
    }
}
