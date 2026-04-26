package com.floyd.backpack.event;

import com.floyd.backpack.service.BackpackCmdService;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.backpack.tools.OpenBackpackTool;
import com.floyd.core.logging.ConsoleLogger;
import com.floyd.core.logging.ConsoleLoggerFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author floyd
 * @date 2026/4/4
 */
public class BackpackEventListener implements Listener {

    private static final ConsoleLogger logger = ConsoleLoggerFactory.get(BackpackEventListener.class);

    private final PlayerBackpackManager playerBackpackManager;

    private final BackpackCmdService backpackCmdService;

    public BackpackEventListener(PlayerBackpackManager playerBackpackManager,
                                 BackpackCmdService backpackCmdService) {
        this.playerBackpackManager = playerBackpackManager;
        this.backpackCmdService = backpackCmdService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        givePlayerTools(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        logger.info("检测到玩家退出，开始保存玩家[" + player.getName() + "]的背包数据");
        boolean res = playerBackpackManager.flushBackpackToFile(player);
        if (res) {
            logger.info("保存玩家[" + player.getName() + "]的背包数据完成");
        } else {
            logger.warn("保存玩家[" + player.getName() + "]的背包数据失败");
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        givePlayerTools(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (OpenBackpackTool.matchEvent(event)) {
            backpackCmdService.onOpenBackpackCmd(event.getPlayer());
            event.setCancelled(true);
        }
    }

    private void givePlayerTools(Player player) {
        ItemStack itemStack = OpenBackpackTool.getItemStack();
        player.getInventory().addItem(itemStack);
    }
}
