package com.floyd.backpack.command;

import com.floyd.backpack.service.PlayerBackpackManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author floyd
 */
public abstract class AbstractCmdHandler {

    protected final PlayerBackpackManager playerBackpackManager;

    protected AbstractCmdHandler(PlayerBackpackManager playerBackpackManager) {
        this.playerBackpackManager = playerBackpackManager;
    }

    protected boolean checkIsPlayer(CommandSender sender) {
        return sender instanceof Player;
    }
}
