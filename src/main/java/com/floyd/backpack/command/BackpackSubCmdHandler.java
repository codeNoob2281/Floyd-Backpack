package com.floyd.backpack.command;

import com.floyd.backpack.BackpackPluginAccessor;
import com.floyd.backpack.constant.PermConstant;
import com.floyd.backpack.entity.Backpack;
import com.floyd.backpack.message.CommandBackpackHelpMsg;
import com.floyd.backpack.message.CommandBackpackMsg;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.core.command.SubCommandHandler;
import com.floyd.core.command.SubCommandMapping;
import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.logging.Logger;
import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
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
    public void onReloadCmd(@NotNull CommandSender sender) {
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
    public void onShowHelpCmd(@NotNull CommandSender sender) {
        sender.sendMessage(CommandBackpackHelpMsg.TITLE.content());
        sender.sendMessage(CommandBackpackHelpMsg.COMMANDS_HEADER.content());
        sender.sendMessage(CommandBackpackHelpMsg.CMD_OPEN_DESC.content());
        sender.sendMessage(CommandBackpackHelpMsg.CMD_CLEAR_DESC.content());
        sender.sendMessage(CommandBackpackHelpMsg.CMD_RELOAD_DESC.content());
        sender.sendMessage(CommandBackpackHelpMsg.CMD_VERSION_DESC.content());
        sender.sendMessage(CommandBackpackHelpMsg.MORE_INFO.content());
    }

    @SubCommandMapping(commands = "version", permission = PermConstant.SHOW_VERSION)
    public void onVersionCmd(@NotNull CommandSender sender) {
        PluginMeta meta = BackpackPluginAccessor.getPlugin().getPluginMeta();
        String author = String.join(", ", meta.getAuthors());
        sender.sendMessage(CommandBackpackMsg.VERSION_INFO.content(meta.getVersion(), author));
    }

    @SubCommandMapping(commands = "save-all", permission = PermConstant.SAVE_ALL)
    public void onSaveAllCmd(@NotNull CommandSender sender) {
        sender.sendMessage(CommandBackpackMsg.SAVE_ALL_START.content());
        PlayerBackpackManager.AutosaveResult result = playerBackpackManager.saveAllBackpack();
        sender.sendMessage(CommandBackpackMsg.SAVE_ALL_SUCCESS.content(
                result.successCount(), result.failCount()));
        if (result.isAllSuccess()) {
            logger.info("SaveAll by [{}] completed, success: {}", sender.getName(), result.successCount());
        } else {
            logger.warn("SaveAll by [{}] completed with failures, success: {}, failed: {}",
                    sender.getName(), result.successCount(), result.failCount());
        }
    }

    @SubCommandMapping(isFallback = true)
    public boolean onErrorCmd(@NotNull CommandSender sender) {
        onShowHelpCmd(sender);
        return false;
    }

    private void openBackpack(CommandSender sender) {
        Player player = (Player) sender;
        Backpack backpack = playerBackpackManager.getBackpack(player);
        player.openInventory(backpack.getInventory());
    }

}
