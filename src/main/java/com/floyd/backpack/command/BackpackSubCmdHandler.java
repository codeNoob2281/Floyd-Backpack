package com.floyd.backpack.command;

import com.floyd.backpack.BackpackPluginAccessor;
import com.floyd.backpack.constant.Constants;
import com.floyd.backpack.constant.PermConstant;
import com.floyd.backpack.entity.Backpack;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.core.command.SubCommandHandler;
import com.floyd.core.command.SubCommandMapping;
import com.floyd.core.logging.ConsoleLogger;
import com.floyd.core.logging.ConsoleLoggerFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.springframework.util.StopWatch;

/**
 * @author floyd
 * @date 2026/3/28
 */
@SubCommandHandler(rootCommand = "backpack")
public class BackpackSubCmdHandler extends AbstractCmdHandler {

    private static final ConsoleLogger logger = ConsoleLoggerFactory.get(BackpackSubCmdHandler.class);

    protected BackpackSubCmdHandler(PlayerBackpackManager playerBackpackManager) {
        super(playerBackpackManager);
    }

    @SubCommandMapping(commands = {}, permission = PermConstant.OPEN_BACKPACK)
    public boolean onRootCmd(CommandSender sender) {
        return onOpenBackpackCmd(sender);
    }

    @SubCommandMapping(commands = "open", permission = PermConstant.OPEN_BACKPACK)
    public boolean onOpenBackpackCmd(CommandSender sender) {
        if (checkIsPlayer(sender)) {
            // 打开背包
            openBackpack(sender);
            return true;
        } else {
            sender.sendMessage(Component.text("无法通过控制台执行此命令", NamedTextColor.RED));
        }
        return false;
    }

    @SubCommandMapping(commands = "reload", permission = PermConstant.RELOAD_CONFIG)
    public void onReloadCmd(@NonNull CommandSender sender, @NonNull @NotNull String @NonNull [] args) {
        sender.sendMessage(Component.text("正在重新加载配置...", NamedTextColor.GREEN));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        boolean isReloadSuccess = BackpackPluginAccessor.reload();
        stopWatch.stop();
        if (isReloadSuccess) {
            sender.sendMessage(Component.text("重新加载配置完成，耗时" + stopWatch.getTotalTimeMillis() + "ms", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("重新加载配置失败，请前往控制台查看异常！", NamedTextColor.RED));
        }
    }

    @SubCommandMapping(commands = "help", permission = PermConstant.SHOW_HELP)
    public void onShowHelpCmd(@NonNull CommandSender sender, @NonNull @NotNull String @NonNull [] args) {
        sender.sendMessage(Component.text(Constants.MESSAGE_PREFIX + " 帮助菜单", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("------------------------------------", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/bp open", NamedTextColor.GRAY)
                .append(Component.text(" -> 打开背包", NamedTextColor.AQUA)));
        sender.sendMessage(Component.text("/bp clear", NamedTextColor.GRAY)
                .append(Component.text(" -> 清空背包", NamedTextColor.AQUA)));
        sender.sendMessage(Component.text("/bp reload", NamedTextColor.GRAY)
                .append(Component.text(" -> 重载背包", NamedTextColor.AQUA)));
        sender.sendMessage(Component.text("------------------------------------", NamedTextColor.GRAY));
    }

    @SubCommandMapping(isFallback = true)
    public boolean onErrorCmd(@NonNull CommandSender sender, @NotNull String @NonNull [] args) {
        onShowHelpCmd(sender, args);
        return false;
    }

    private void openBackpack(CommandSender sender) {
        Player player = (Player) sender;
        Backpack backpack = playerBackpackManager.getBackpack(player);
        player.openInventory(backpack.getInventory());
    }

}
