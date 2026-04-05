package com.floyd.backpack.event;

import com.floyd.backpack.FloydBackpackPlugin;
import com.floyd.backpack.service.PlayerBackpackManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author floyd
 * @date 2026/4/4
 */
public class BackpackEventListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FloydBackpackPlugin.logger().info("检测到玩家退出，开始保存玩家[" + player.getName() + "]的背包数据");
        boolean res = PlayerBackpackManager.flushBackpackToFile(player);
        if (res) {
            FloydBackpackPlugin.logger().info("保存玩家[" + player.getName() + "]的背包数据完成");
        } else {
            FloydBackpackPlugin.logger().warning("保存玩家[" + player.getName() + "]的背包数据失败");
        }
    }
}
