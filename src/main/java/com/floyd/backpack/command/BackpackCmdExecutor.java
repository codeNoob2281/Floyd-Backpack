package com.floyd.backpack.command;

import com.floyd.backpack.FloydBackpackPlugin;
import com.floyd.backpack.constant.PermConstant;
import com.floyd.backpack.service.BackpackCmdService;
import com.floyd.core.command.TrieCommandCompleter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.StopWatch;

import java.util.List;

/**
 * @author floyd
 * @date 2026/3/22
 */
public class BackpackCmdExecutor implements CommandExecutor, TabCompleter {

    private final BackpackCmdService backpackCmdService;

    private final TrieCommandCompleter commandCompleter;

    public BackpackCmdExecutor(BackpackCmdService backpackCmdService) {
        this.backpackCmdService = backpackCmdService;
        this.commandCompleter = new TrieCommandCompleter();
        initCmdCompleter();
    }

    private void initCmdCompleter() {
        commandCompleter.addCommand("backpack");
        commandCompleter.addCommand("backpack open", PermConstant.OPEN_BACKPACK);
        commandCompleter.addCommand("backpack clear", PermConstant.CLEAR_BACKPACK);
        commandCompleter.addCommand("backpack clear confirm", PermConstant.CLEAR_BACKPACK);
        commandCompleter.addCommand("backpack clear cancel", PermConstant.CLEAR_BACKPACK);
        commandCompleter.addCommand("backpack reload", PermConstant.RELOAD_CONFIG);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0 || "open".equals(args[0])) {
            return backpackCmdService.onOpenBackpackCmd(sender);
        } else if ("clear".equals(args[0])) {
            return backpackCmdService.onClearBackpackCmd(sender, args);
        } else if ("reload".equals(args[0])) {
            sender.sendMessage(Component.text("正在重新加载配置...", NamedTextColor.GREEN));
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            boolean isReloadSuccess = FloydBackpackPlugin.reload();
            stopWatch.stop();
            if (isReloadSuccess) {
                sender.sendMessage(Component.text("重新加载配置完成，耗时" + stopWatch.getTotalTimeMillis() + "ms", NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("重新加载配置失败，请前往控制台查看异常！", NamedTextColor.RED));
            }
            return true;
        } else {
            return backpackCmdService.onErrorCmd(sender);
        }
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                                                @NotNull String @NotNull [] args) {
        if (sender instanceof Player) {
            return commandCompleter.nextArgs((Player) sender, command.getName(), args);
        } else {
            return commandCompleter.nextArgs(command.getName(), args);
        }
    }
}
