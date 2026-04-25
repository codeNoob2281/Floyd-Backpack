package com.floyd.backpack.event;

import com.floyd.backpack.FloydBackpackPlugin;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.core.logging.ConsoleLogger;
import com.floyd.core.logging.ConsoleLoggerFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author floyd
 * @date 2026/4/4
 */
public class BackpackEventListener implements Listener {

    private static final ConsoleLogger logger = ConsoleLoggerFactory.get(BackpackEventListener.class);

    private final PlayerBackpackManager playerBackpackManager;

    public BackpackEventListener(PlayerBackpackManager playerBackpackManager) {
        this.playerBackpackManager = playerBackpackManager;
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
}
