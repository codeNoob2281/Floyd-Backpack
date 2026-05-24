package com.floyd.backpack.event;

import com.floyd.backpack.command.BackpackSubCmdHandler;
import com.floyd.backpack.entity.Backpack;
import com.floyd.backpack.entity.PlaceHolderItem;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.backpack.tools.OpenBackpackTool;
import com.floyd.core.logging.Logger;
import com.floyd.core.logging.ConsoleLoggerFactory;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
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

    private static final Logger logger = ConsoleLoggerFactory.get(BackpackEventListener.class);

    private final PlayerBackpackManager playerBackpackManager;

    private final BackpackSubCmdHandler backpackSubCmdHandler;

    public BackpackEventListener(PlayerBackpackManager playerBackpackManager,
                                 BackpackSubCmdHandler backpackSubCmdHandler) {
        this.playerBackpackManager = playerBackpackManager;
        this.backpackSubCmdHandler = backpackSubCmdHandler;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        givePlayerTools(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        logger.info("Player quit detected, saving backpack data for player [{}]", player.getName());
        boolean res = playerBackpackManager.flushBackpackToFile(player);
        if (res) {
            logger.info("Backpack data saved for player [{}]", player.getName());
        } else {
            logger.warn("Failed to save backpack data for player [{}]", player.getName());
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        givePlayerTools(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (OpenBackpackTool.matchEvent(event)) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            backpackSubCmdHandler.onOpenBackpackCmd(player);
            player.playSound(Sound.sound()
                    .type(org.bukkit.Sound.BLOCK_CHEST_OPEN)
                    .build());
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

        // 拦截占位符物品交互
        if (playerBackpackManager.isBackpackInventory(player, event.getClickedInventory())) {
            if (PlaceHolderItem.isPlaceholder(event.getCurrentItem())
                    || PlaceHolderItem.isPlaceholder(event.getCursor())) {
                event.setCancelled(true);
                return;
            }
        }
        if (isMoveAction || isHotbarAction) {
            ItemStack targetItem = isMoveAction ? event.getCurrentItem()
                    : player.getInventory().getItem(event.getHotbarButton());
            if (PlaceHolderItem.isPlaceholder(targetItem)) {
                event.setCancelled(true);
                return;
            }
        }

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
                return;
            }
            // 不允许拖拽涉及占位符物品
            boolean involvesPlaceholder = event.getNewItems().values().stream()
                    .anyMatch(PlaceHolderItem::isPlaceholder)
                    || event.getInventory().getStorageContents().length > 0
                    && event.getNewItems().keySet().stream().anyMatch(slot -> {
                ItemStack existing = event.getInventory().getItem(slot);
                return PlaceHolderItem.isPlaceholder(existing);
            });
            if (involvesPlaceholder) {
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
        OpenBackpackTool openBackpackTool = new OpenBackpackTool();
        player.getInventory().addItem(openBackpackTool.getItemStack());
    }
}
