package com.floyd.backpack.event;

import com.floyd.backpack.command.BackpackSubCmdHandler;
import com.floyd.backpack.entity.Backpack;
import com.floyd.backpack.entity.PlaceHolderItem;
import com.floyd.backpack.message.CommandBackpackUpgradeMsg;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.backpack.setting.properties.UpgradeSettings;
import com.floyd.backpack.BackpackPluginAccessor;
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
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.Bukkit;
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

    private static final String PAPI_PLUGIN = "PlaceholderAPI";
    private boolean placeholderRegistered = false;

    /**
     * PAPI 启用后再注册扩展（覆盖 PAPI 晚于本插件启用的情况）。
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
        if (!placeholderRegistered && PAPI_PLUGIN.equals(event.getPlugin().getName())) {
            tryRegisterPlaceholder();
        }
    }

    /**
     * 注册 PlaceholderAPI 变量扩展（幂等）。PAPI 未加载时静默等待（由 onPluginEnable 在 PAPI 启用时触发）。
     */
    public void tryRegisterPlaceholder() {
        if (placeholderRegistered) {
            return;
        }
        if (Bukkit.getPluginManager().getPlugin(PAPI_PLUGIN) != null) {
            new com.floyd.backpack.placeholder.FloydBackpackPlaceholder().register();
            placeholderRegistered = true;
            logger.info("Floyd-Backpack PlaceholderAPI expansion registered.");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 不再自动发放背包工具，背包统一由服务器菜单按钮 / /backpack 打开
        Player player = event.getPlayer();
        logger.info("Player joined, backpack available via menu or /backpack: [{}]", player.getName());
        // 进服引导：欢迎消息 + 首次玩家新手提示（延迟1秒发送避开进服刷屏；不自动开菜单/不自动发物品）
        Bukkit.getScheduler().runTaskLater(BackpackPluginAccessor.getPlugin(), () -> {
            player.sendMessage("§6§l欢迎来到 §a鱼乐服务器 §6§l！");
            player.sendMessage("§7输入 §a/menu §7打开主菜单，§a/bp §7打开个人仓库");
            if (!player.hasPlayedBefore()) {
                player.sendMessage("§b新手提示：主菜单可传送 / 交易 / 银行 / 声望，仓库可花金币逐级扩容");
                player.sendMessage("§b遇到问题可在主菜单 → 辅助功能 / 显示设置 中调整，或联系管理员");
            }
        }, 20L);
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
        // 不再自动发放背包工具
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
            // 锁定占位符（超出容量的灰色格）不可取、不可放
            if (PlaceHolderItem.isPlaceholder(current)) {
                event.setCancelled(true);
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

    /**
     * 背包底栏导航点击：返回 / 翻页 / 升级。
     * 使用 HIGHEST 优先级，确保先于其他 handler 取消底栏点击，避免导航按钮被取走。
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBackpackNavClick(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (!(top.getHolder() instanceof Backpack backpack)) {
            return;
        }
        if (event.getClickedInventory() != top) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot < Backpack.NAV_START) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        if (slot == Backpack.SLOT_RETURN) {
            player.closeInventory();
        } else if (slot == Backpack.SLOT_PREV) {
            backpack.openPage(player, backpack.getCurrentPage() - 1);
        } else if (slot == Backpack.SLOT_NEXT) {
            backpack.openPage(player, backpack.getCurrentPage() + 1);
        } else if (slot == Backpack.SLOT_DEPOSIT_ALL) {
            backpack.depositAll(player);
            player.playSound(Sound.sound().type(org.bukkit.Sound.ENTITY_ITEM_PICKUP).build());
        } else if (slot == Backpack.SLOT_SORT) {
            backpack.sortItems(player);
            player.playSound(Sound.sound().type(org.bukkit.Sound.UI_BUTTON_CLICK).build());
        } else if (slot == Backpack.SLOT_WITHDRAW_ALL) {
            backpack.withdrawAll(player);
            player.playSound(Sound.sound().type(org.bukkit.Sound.ENTITY_ITEM_PICKUP).build());
        } else if (slot == Backpack.SLOT_HELP) {
            sendBackpackHelp(player);
        } else if (slot == Backpack.SLOT_UPGRADE) {
            tryOpenUpgradeConfirm(player);
        }
    }

    /**
     * 仓库操作说明提示（点击底栏「操作说明」按钮时发送）。
     */
    private void sendBackpackHelp(Player player) {
        player.sendMessage("§6§l个人仓库操作说明");
        player.sendMessage("§7• §a全部存入 §7: 把背包物品存入仓库(自动合并同类)");
        player.sendMessage("§7• §e整理 §7: 合并同类堆叠并紧凑排列");
        player.sendMessage("§7• §6全部取出 §7: 把仓库物品取出到背包");
        player.sendMessage("§7• §a升级仓库 §7: 花金币扩容(右下角)");
        player.sendMessage("§7• §c返回 §7: 关闭仓库");
        player.sendMessage("§e容量随等级提升, 当前 Lv" + playerBackpackManager.getBackpack(player).getLevel());
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
            // 不允许拖拽到导航区（底栏）
            boolean hitsNav = event.getNewItems().keySet().stream()
                    .anyMatch(s -> s >= Backpack.NAV_START && s < Backpack.GUI_SIZE);
            if (hitsNav) {
                event.setCancelled(true);
                return;
            }
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
            Backpack backpack = playerBackpackManager.getBackpack(player);
            backpack.syncFromView(event.getInventory());
            backpack.markDirty();
        }
    }

    /**
     * 监听背包内物品拖拽变更事件。与 {@link #onBackpackModify} 配合覆盖所有修改路径。
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBackpackModifyDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player player
                && playerBackpackManager.isBackpackInventory(player, event.getInventory())) {
            Backpack backpack = playerBackpackManager.getBackpack(player);
            backpack.syncFromView(event.getInventory());
            backpack.markDirty();
        }
    }

    /**
     * 检测玩家是否为基岩版玩家（通过 Geyser API）。
     * 未安装 Geyser 时安全返回 false。
     */
    private static boolean isBedrockPlayer(Player player) {
        try {
            Class<?> clazz = Class.forName("org.geysermc.geyser.api.GeyserApi");
            Object api = clazz.getMethod("api").invoke(null);
            return (boolean) clazz.getMethod("isBedrockPlayer", java.util.UUID.class)
                    .invoke(api, player.getUniqueId());
        } catch (Throwable e) {
            return false;
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
        long cost = playerBackpackManager.getUpgradeCost(currentLevel);
        new BackpackUpgradeConfirmGui(player, backpack, currentLevel, nextLevel,
                backpack.getUsableSlots(), nextUsableSlots, cost).open();
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
