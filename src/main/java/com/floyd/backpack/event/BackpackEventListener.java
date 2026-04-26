package com.floyd.backpack.event;

import com.floyd.backpack.service.BackpackCmdService;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.backpack.tools.OpenBackpackTool;
import com.floyd.core.logging.ConsoleLogger;
import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.logging.LogLevel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryAction eventAction = event.getAction();
        boolean isPlaceAction = (eventAction == InventoryAction.PLACE_ALL || eventAction == InventoryAction.PLACE_ONE ||
                eventAction == InventoryAction.PLACE_SOME);
        boolean isSwitchAction = (eventAction == InventoryAction.SWAP_WITH_CURSOR);
        boolean isMoveAction = (eventAction == InventoryAction.MOVE_TO_OTHER_INVENTORY);
        boolean isHotbarAction = (eventAction == InventoryAction.HOTBAR_SWAP);
        Player player = (Player) event.getWhoClicked();
        if ((isPlaceAction || isSwitchAction) && playerBackpackManager.isBackpackInventory(player, event.getClickedInventory())) {
            // 不允许将tool放入到背包中
            if (OpenBackpackTool.matchItemStack(event.getCursor())) {
                event.setCancelled(true);
            }
        } else if (isMoveAction && playerBackpackManager.isBackpackInventory(player, event.getInventory())) {
            // 不允许将tool放入到背包中
            if (OpenBackpackTool.matchItemStack(event.getCurrentItem())) {
                event.setCancelled(true);
            }
        } else if (isHotbarAction && playerBackpackManager.isBackpackInventory(player, event.getClickedInventory())) {
            // 不允许通过快捷键将tool放入到背包中
            ItemStack item = player.getInventory().getItem(event.getHotbarButton());
            if (OpenBackpackTool.matchItemStack(item)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (playerBackpackManager.isBackpackInventory((Player) event.getWhoClicked(), event.getInventory())) {
            // 不允许将tool放入到背包中
            boolean matchTools = event.getNewItems().values().stream()
                    .anyMatch(OpenBackpackTool::matchItemStack);
            if (matchTools) {
                event.setCancelled(true);
            }
        }
    }

    private void givePlayerTools(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (OpenBackpackTool.matchItemStack(item)) {
                return;
            }
        }
        ItemStack itemStack = OpenBackpackTool.getItemStack();
        player.getInventory().addItem(itemStack);
    }
}
