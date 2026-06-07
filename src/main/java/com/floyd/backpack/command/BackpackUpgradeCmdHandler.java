package com.floyd.backpack.command;

import com.floyd.backpack.command.completer.NamedParameterCompleter;
import com.floyd.backpack.constant.PermConstant;
import com.floyd.backpack.entity.Backpack;
import com.floyd.backpack.message.CommandBackpackMsg;
import com.floyd.backpack.message.CommandBackpackUpgradeMsg;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.backpack.setting.properties.UpgradeSettings;
import com.floyd.core.command.SubCommandHandler;
import com.floyd.core.command.SubCommandMapping;
import com.floyd.core.command.param.ParameterCompleterFactory;
import com.floyd.core.command.param.SubCommandParam;
import com.floyd.core.settings.PluginSettingsManager;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SubCommandHandler(rootCommand = "backpack")
public class BackpackUpgradeCmdHandler extends AbstractCmdHandler {

    private final PluginSettingsManager pluginSettingsManager;

    protected BackpackUpgradeCmdHandler(PlayerBackpackManager playerBackpackManager,
                                        PluginSettingsManager pluginSettingsManager) {
        super(playerBackpackManager);
        this.pluginSettingsManager = pluginSettingsManager;
    }

    @SubCommandMapping(commands = "upgrade", permission = PermConstant.UPGRADE_BACKPACK)
    public void onUpgradeCmd(@NotNull CommandSender sender) {
        Boolean enabled = pluginSettingsManager.getProperty(UpgradeSettings.ENABLED);
        if (!enabled) {
            sender.sendMessage(CommandBackpackUpgradeMsg.UPGRADE_DISABLED.content());
            return;
        }
        if (!checkIsPlayer(sender)) {
            sender.sendMessage(CommandBackpackMsg.CONSOLE_NOT_ALLOWED.content());
            return;
        }
        Player player = (Player) sender;
        Backpack backpack = playerBackpackManager.getBackpack(player);

        int currentLevel = backpack.getLevel();
        int maxLevel = playerBackpackManager.getMaxLevel();

        if (currentLevel >= maxLevel) {
            sender.sendMessage(CommandBackpackUpgradeMsg.UPGRADE_MAX_LEVEL.content(maxLevel));
            return;
        }

        int newLevel = currentLevel + 1;
        int newUsableSlots = playerBackpackManager.getUsableSlots(newLevel);

        if (newUsableSlots <= 0) {
            sender.sendMessage(CommandBackpackUpgradeMsg.UPGRADE_MAX_LEVEL.content(maxLevel));
            return;
        }

        // 若背包当前打开，先关闭
        if (player.getOpenInventory().getTopInventory() == backpack.getInventory()) {
            player.closeInventory();
        }

        playerBackpackManager.setBackpackLevel(backpack, newLevel, newUsableSlots);

        // 重新打开背包
        player.openInventory(backpack.getInventory());

        sender.sendMessage(CommandBackpackUpgradeMsg.UPGRADE_SUCCESS.content(newLevel, newUsableSlots));
        player.playSound(Sound.sound()
                .type(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP)
                .build());
    }

    @SubCommandMapping(commands = "set-level", permission = PermConstant.ADMIN_BACKPACK)
    public void onSetLevelCmd(@NotNull CommandSender sender,
                              @SubCommandParam(index = 0, description = "target player",
                                      completer = ParameterCompleterFactory.ONLINE_PLAYERS) Player targetPlayer,
                              @SubCommandParam(index = 1, description = "target level",
                                      completer = NamedParameterCompleter.BACKPACK_LEVEL) Integer targetLevel) {
        int maxLevel = playerBackpackManager.getMaxLevel();
        if (targetLevel < 1 || targetLevel > maxLevel) {
            sender.sendMessage(CommandBackpackUpgradeMsg.SETLEVEL_INVALID_LEVEL.content(maxLevel));
            return;
        }

        Backpack backpack = playerBackpackManager.getBackpack(targetPlayer);
        int newUsableSlots = playerBackpackManager.getUsableSlots(targetLevel);

        if (newUsableSlots <= 0) {
            sender.sendMessage(CommandBackpackUpgradeMsg.SETLEVEL_INVALID_LEVEL.content(maxLevel));
            return;
        }

        // 若目标玩家背包当前打开，先关闭
        if (targetPlayer.getOpenInventory().getTopInventory() == backpack.getInventory()) {
            targetPlayer.closeInventory();
        }

        playerBackpackManager.setBackpackLevel(backpack, targetLevel, newUsableSlots);

        sender.sendMessage(CommandBackpackUpgradeMsg.SETLEVEL_SUCCESS.content(
                targetPlayer.getName(), targetLevel, newUsableSlots));
        targetPlayer.sendMessage(CommandBackpackUpgradeMsg.UPGRADE_SUCCESS.content(targetLevel, newUsableSlots));
    }
}
