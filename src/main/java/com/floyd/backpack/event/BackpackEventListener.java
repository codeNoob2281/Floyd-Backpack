package com.floyd.backpack.event;

import com.floyd.backpack.command.BackpackSubCmdHandler;
import com.floyd.backpack.entity.Backpack;
import com.floyd.backpack.entity.PlaceHolderItem;
import com.floyd.backpack.message.CommandBackpackUpgradeMsg;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.backpack.setting.properties.UpgradeSettings;
import com.floyd.backpack.tools.OpenBackpackTool;
import com.floyd.backpack.ui.BackpackUpgradeConfirmGui;
import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.logging.Logger;
import com.floyd.core.settings.PluginSettingsManager;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author floyd
 * @date 2026/4/4
 */
public class BackpackEventListener implements Listener {

    private static final Logger logger = ConsoleLoggerFactory.get(BackpackEventListener.class);

    private final PlayerBackpackManager playerBackpackManager;

    private final BackpackSubCmdHandler backpackSubCmdHandler;

    private final PluginSettingsManager pluginSettingsManager;

    public BackpackEventListener(PlayerBackpackManager playerBackpackManager,
                                 BackpackSubCmdHandler backpackSubCmdHandler,
                                 PluginSettingsManager pluginSettingsManager) {
        this.playerBackpackManager = playerBackpackManager;
        this.backpackSubCmdHandler = backpackSubCmdHandler;
        this.pluginSettingsManager = pluginSettingsManager;
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
    public void onConfirmGuiClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof BackpackUpgradeConfirmGui confirmGui)) {
            return;
        }
        event.setCancelled(true);
        Inventory clickedInv = event.getClickedInventory();
        if (clickedInv != null && clickedInv.getType() == InventoryType.CHEST
                && clickedInv.getHolder() == confirmGui) {
            ItemStack current = event.getCurrentItem();
            if (BackpackUpgradeConfirmGui.isConfirmButton(current)) {
                confirmGui.onConfirm(playerBackpackManager);
            } else if (BackpackUpgradeConfirmGui.isCancelButton(current)) {
                confirmGui.onCancel();
            }
        }
    }

    @EventHandler
    public void onPlaceholderClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryAction action = event.getAction();
        boolean isMoveAction = action == InventoryAction.MOVE_TO_OTHER_INVENTORY;
        boolean isHotbarAction = action == InventoryAction.HOTBAR_SWAP;

        if (playerBackpackManager.isBackpackInventory(player, event.getClickedInventory())) {
            ItemStack current = event.getCurrentItem();
            if (PlaceHolderItem.isPlaceholder(current)) {
                event.setCancelled(true);
                if (PlaceHolderItem.isNextLevelPlaceholder(current) && event.getClick() == ClickType.SHIFT_LEFT) {
                    tryOpenUpgradeConfirm(player);
                }
                return;
            }
            if (PlaceHolderItem.isPlaceholder(event.getCursor())) {
                event.setCancelled(true);
                return;
            }
        }
        if (isMoveAction || isHotbarAction) {
            ItemStack targetItem = isMoveAction ? event.getCurrentItem()
                    : player.getInventory().getItem(event.getHotbarButton());
            if (PlaceHolderItem.isPlaceholder(targetItem)) {
                event.setCancelled(true);
            }
        }
    }

    private static boolean isPlayerClick(InventoryAction action) {
        return action == InventoryAction.PICKUP_ALL
                || action == InventoryAction.PICKUP_HALF
                || action == InventoryAction.PICKUP_ONE;
    }

    @EventHandler
    public void onToolProtectClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryAction action = event.getAction();
        boolean isPlaceAction = action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE
                || action == InventoryAction.PLACE_SOME;
        boolean isSwitchAction = action == InventoryAction.SWAP_WITH_CURSOR;
        boolean isMoveAction = action == InventoryAction.MOVE_TO_OTHER_INVENTORY;
        boolean isHotbarAction = action == InventoryAction.HOTBAR_SWAP;

        if ((isPlaceAction || isSwitchAction) && playerBackpackManager.isBackpackInventory(player, event.getClickedInventory())) {
            if (OpenBackpackTool.matchItemStack(event.getCursor())) {
                event.setCancelled(true);
            }
        } else if (isMoveAction && playerBackpackManager.isBackpackInventory(player, event.getInventory())) {
            if (OpenBackpackTool.matchItemStack(event.getCurrentItem())) {
                event.setCancelled(true);
            }
        } else if (isHotbarAction && playerBackpackManager.isBackpackInventory(player, event.getClickedInventory())) {
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
                if (slot < 0 || slot >= event.getInventory().getSize()) {
                    return false;
                }
                ItemStack existing = event.getInventory().getItem(slot);
                return PlaceHolderItem.isPlaceholder(existing);
            });
            if (involvesPlaceholder) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * 监听背包内物品变更事件（点击）。
     * 使用 MONITOR 优先级 + ignoreCancelled = true，确保仅在发生未被取消的物品变更时标记脏数据。
     * 相比监听 onInventoryClose，此方式：
     * 1. 避免仅打开查看未做修改时的无意义写盘
     * 2. 确保长时间打开背包期间发生的修改能被定时任务捕获保存
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBackpackModify(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player
                && playerBackpackManager.isBackpackInventory(player, event.getInventory())) {
            playerBackpackManager.getBackpack(player).markDirty();
        }
    }

    /**
     * 监听背包内物品拖拽变更事件。与 {@link #onBackpackModify} 配合覆盖所有修改路径。
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBackpackModifyDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player player
                && playerBackpackManager.isBackpackInventory(player, event.getInventory())) {
            playerBackpackManager.getBackpack(player).markDirty();
        }
    }

    private void tryOpenUpgradeConfirm(Player player) {
        Boolean enabled = pluginSettingsManager.getProperty(UpgradeSettings.ENABLED);
        if (!enabled) {
            player.sendMessage(CommandBackpackUpgradeMsg.UPGRADE_DISABLED.content());
            return;
        }

        Backpack backpack = playerBackpackManager.getBackpack(player);
        int currentLevel = backpack.getLevel();
        int maxLevel = playerBackpackManager.getMaxLevel();

        if (currentLevel >= maxLevel) {
            player.sendMessage(CommandBackpackUpgradeMsg.UPGRADE_MAX_LEVEL.content(maxLevel));
            return;
        }

        int nextLevel = currentLevel + 1;
        int nextUsableSlots = playerBackpackManager.getUsableSlots(nextLevel);

        if (nextUsableSlots <= 0) {
            player.sendMessage(CommandBackpackUpgradeMsg.UPGRADE_MAX_LEVEL.content(maxLevel));
            return;
        }

        // 打开升级确认 GUI
        new BackpackUpgradeConfirmGui(player, backpack, currentLevel, nextLevel,
                backpack.getUsableSlots(), nextUsableSlots).open();
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
