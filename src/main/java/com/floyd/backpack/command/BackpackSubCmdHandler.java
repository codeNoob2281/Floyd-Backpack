package com.floyd.backpack.command;

import com.floyd.backpack.BackpackPluginAccessor;
import com.floyd.backpack.constant.Constants;
import com.floyd.backpack.constant.PermConstant;
import com.floyd.backpack.entity.Backpack;
import com.floyd.backpack.message.CommandBackpackHelpMsg;
import com.floyd.backpack.message.CommandBackpackMsg;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.core.command.SubCommandHandler;
import com.floyd.core.command.SubCommandMapping;
import com.floyd.core.i18n.I18nMessageProvider;
import com.floyd.core.logging.Logger;
import com.floyd.core.logging.ConsoleLoggerFactory;
import net.kyori.adventure.text.Component;
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

    private static final Logger logger = ConsoleLoggerFactory.get(BackpackSubCmdHandler.class);

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
            sender.sendMessage(CommandBackpackMsg.CONSOLE_NOT_ALLOWED.content());
        }
        return false;
    }

    @SubCommandMapping(commands = "reload", permission = PermConstant.RELOAD_CONFIG)
    public void onReloadCmd(@NonNull CommandSender sender, @NonNull @NotNull String @NonNull [] args) {
        sender.sendMessage(CommandBackpackMsg.RELOAD_START.content());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        boolean isReloadSuccess = BackpackPluginAccessor.reload();
        stopWatch.stop();
        if (isReloadSuccess) {
            sender.sendMessage(CommandBackpackMsg.RELOAD_SUCCESS.content(stopWatch.getTotalTimeMillis()));
        } else {
            sender.sendMessage(CommandBackpackMsg.RELOAD_FAILURE.content());
        }
    }

    @SubCommandMapping(commands = "help", permission = PermConstant.SHOW_HELP)
    public void onShowHelpCmd(@NonNull CommandSender sender, @NonNull @NotNull String @NonNull [] args) {
        sender.sendMessage(CommandBackpackHelpMsg.LINE1.content());
        sender.sendMessage(CommandBackpackHelpMsg.LINE2.content());
        sender.sendMessage(CommandBackpackHelpMsg.LINE3.content());
        sender.sendMessage(CommandBackpackHelpMsg.LINE4.content());
        sender.sendMessage(CommandBackpackHelpMsg.LINE5.content());
        sender.sendMessage(CommandBackpackHelpMsg.LINE6.content());
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
