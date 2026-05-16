package com.floyd.backpack.command;

import com.floyd.backpack.constant.PermConstant;
import com.floyd.backpack.entity.Backpack;
import com.floyd.backpack.enums.ConfirmOperationEnum;
import com.floyd.backpack.message.CommandBackpackClearMsg;
import com.floyd.backpack.message.CommandBackpackMsg;
import com.floyd.backpack.service.ConfirmOperationManager;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.backpack.setting.properties.CmdClearBackPackSettings;
import com.floyd.core.command.SubCommandHandler;
import com.floyd.core.command.SubCommandMapping;
import com.floyd.core.logging.Logger;
import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.settings.PluginSettingsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author floyd
 */
@SubCommandHandler(rootCommand = "backpack")
public class BackpackClearCmdHandler extends AbstractCmdHandler {

    private static final Logger logger = ConsoleLoggerFactory.get(BackpackClearCmdHandler.class);

    private final ConfirmOperationManager confirmOperationManager;

    private final PluginSettingsManager pluginSettingsManager;

    protected BackpackClearCmdHandler(PlayerBackpackManager playerBackpackManager,
                                      ConfirmOperationManager confirmOperationManager,
                                      PluginSettingsManager pluginSettingsManager) {
        super(playerBackpackManager);
        this.confirmOperationManager = confirmOperationManager;
        this.pluginSettingsManager = pluginSettingsManager;
    }

    @SubCommandMapping(commands = "clear", permission = PermConstant.CLEAR_BACKPACK)
    public boolean onClearBackpackCmd(@NonNull CommandSender sender) {
        boolean isPlayer = checkIsPlayer(sender);
        Boolean enable = pluginSettingsManager.getProperty(CmdClearBackPackSettings.ENABLE);
        if (!enable) {
            sender.sendMessage(CommandBackpackClearMsg.FEATURE_DISABLED.content());
            if (!isPlayer) {
                logger.warn("Please set command.backpack.clear.enable to true in config.yml to enable the clear backpack feature");
            }
            return false;
        }
        if (!isPlayer) {
            sender.sendMessage(CommandBackpackMsg.CONSOLE_NOT_ALLOWED.content());
            return false;
        }
        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();

        Boolean needConfirm = pluginSettingsManager.getProperty(CmdClearBackPackSettings.NEED_CONFIRM);
        if (!needConfirm) {
            execClearBackpack(sender);
            return true;
        }

        Long ttl = confirmOperationManager.getTtl(ConfirmOperationEnum.CLEAR_BACKPACK, uuid);
        if (ttl != null && ttl > 0) {
            logger.info("Player {} still has an active confirmation operation, remaining time: {}ms", player.getName(), ttl);
            sender.sendMessage(CommandBackpackClearMsg.PENDING_OPERATION_EXISTS.content());
            sendClearConfirmTipMsg(sender);
        } else {
            confirmOperationManager.addNew(ConfirmOperationEnum.CLEAR_BACKPACK, uuid);
            sender.sendMessage(CommandBackpackClearMsg.CONFIRM_DELETE.content());
            sendClearConfirmTipMsg(sender);
            Long confirmInterval = pluginSettingsManager.getProperty(CmdClearBackPackSettings.CONFIRM_INTERVAL);
            long seconds = Math.round((double) confirmInterval / 1000L);
            sender.sendMessage(CommandBackpackClearMsg.CONFIRM_TIMEOUT.content(seconds));
        }
        return true;
    }

    @SubCommandMapping(commands = {"clear", "confirm"}, permission = PermConstant.CLEAR_BACKPACK)
    public boolean onClearConfirmCmd(@NonNull CommandSender sender) {
        if (!checkIsPlayer(sender)) {
            sender.sendMessage(CommandBackpackMsg.CONSOLE_NOT_ALLOWED.content());
            return false;
        }
        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();
        Long ttl = confirmOperationManager.getTtl(ConfirmOperationEnum.CLEAR_BACKPACK, uuid);
        if (ttl == null || ttl == 0) {
            sender.sendMessage(CommandBackpackClearMsg.NO_ACTIVE_OPERATION.content());
            return true;
        }
        confirmOperationManager.remove(ConfirmOperationEnum.CLEAR_BACKPACK, uuid);
        execClearBackpack(sender);
        return true;
    }

    @SubCommandMapping(commands = {"clear", "cancel"}, permission = PermConstant.CLEAR_BACKPACK)
    public boolean onClearCancelCmd(@NonNull CommandSender sender) {
        if (!checkIsPlayer(sender)) {
            sender.sendMessage(CommandBackpackMsg.CONSOLE_NOT_ALLOWED.content());
            return false;
        }
        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();
        Long ttl = confirmOperationManager.getTtl(ConfirmOperationEnum.CLEAR_BACKPACK, uuid);
        if (ttl == null || ttl == 0) {
            sender.sendMessage(CommandBackpackClearMsg.NO_ACTIVE_OPERATION.content());
            return true;
        }
        confirmOperationManager.remove(ConfirmOperationEnum.CLEAR_BACKPACK, uuid);
        sender.sendMessage(CommandBackpackClearMsg.OPERATION_CANCELLED.content());
        return true;
    }


    private void execClearBackpack(@NonNull CommandSender sender) {
        int clearItemSize = clearBackpack(sender);
        sender.sendMessage(CommandBackpackClearMsg.CLEARED.content(clearItemSize));
        logger.info("Cleared backpack for [{}], removed [{}] items", sender.getName(), clearItemSize);
    }

    private static void sendClearConfirmTipMsg(@NonNull CommandSender sender) {
        sender.sendMessage(CommandBackpackClearMsg.TIP_CONFIRM.content());
        sender.sendMessage(CommandBackpackClearMsg.TIP_CANCEL.content());
    }

    private int clearBackpack(@NotNull CommandSender sender) {
        Player player = (Player) sender;
        Backpack backpack = playerBackpackManager.getBackpack(player);
        Inventory inventory = backpack.getInventory();
        long clearItemCount = Arrays.stream(inventory.getStorageContents())
                .filter(Objects::nonNull).count();
        inventory.clear();
        return (int) clearItemCount;
    }

}
