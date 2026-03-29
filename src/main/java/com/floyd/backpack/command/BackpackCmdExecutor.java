package com.floyd.backpack.command;

import com.floyd.backpack.entity.Backpack;
import com.floyd.backpack.service.BackpackCmdService;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.core.FloydPlugin;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author floyd
 * @date 2026/3/22
 */
public class BackpackCmdExecutor implements CommandExecutor, TabCompleter {

    @Autowired
    private BackpackCmdService backpackCmdService;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0 || "open".equals(args[0])) {
            return backpackCmdService.onOpenBackpackCmd(sender);
        } else if ("clear".equals(args[0])) {
            return backpackCmdService.onClearBackpackCmd(sender, args);
        } else {
            return backpackCmdService.onErrorCmd(sender);
        }
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                                                @NotNull String @NotNull [] args) {
        List<String> argList = Arrays.stream(args)
                .filter(arg -> !arg.isEmpty())
                .toList();
        if (argList.isEmpty()) {
            return List.of("open", "clear");
        } else if ("clear".equals(args[0])) {
            return List.of("confirm", "cancel");
        }
        return List.of();
    }
}
