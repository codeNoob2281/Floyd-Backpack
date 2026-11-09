package com.floyd.backpack.entity;

import com.floyd.backpack.BackpackPluginAccessor;
import com.floyd.backpack.economy.EconomyProvider;
import com.floyd.backpack.message.ChestUIMsg;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.backpack.tools.OpenBackpackTool;
import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import net.milkbowl.vault.economy.Economy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 个人仓库实体。
 * 存储采用全局槽位数组（最大 {@link #TOTAL_CAPACITY} 格），渲染按页分页，
 * 每页 45 个存储格 + 底栏 9 格导航（返回 / 上一页 / 页码 / 下一页 / 升级 / 操作按钮）。
 *
 * @author floyd (modified for multi-page)
 * @since 2026/3/23
 */
public class Backpack implements InventoryHolder {

    private static final Logger logger = ConsoleLoggerFactory.get(Backpack.class);

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    /**
     * 最大总容量（末帝仓库 516 格）
     */
    public static final int TOTAL_CAPACITY = 516;

    /**
     * 每页物品存储格数（剩余 9 格为底栏导航）
     */
    public static final int STORAGE_PER_PAGE = 45;

    /**
     * GUI 总格数 6x9
     */
    public static final int GUI_SIZE = 54;

    /**
     * 底栏起始槽位
     */
    public static final int NAV_START = 45;

    public static final int SLOT_RETURN = 45;
    public static final int SLOT_DEPOSIT_ALL = 46;
    public static final int SLOT_PREV = 47;
    public static final int SLOT_SORT = 48;
    public static final int SLOT_PAGE = 49;
    public static final int SLOT_WITHDRAW_ALL = 50;
    public static final int SLOT_NEXT = 51;
    public static final int SLOT_HELP = 52;
    public static final int SLOT_UPGRADE = 53;

    public static final NamespacedKey NAV_RETURN_KEY = new NamespacedKey("floydbackpack", "nav_return");
    public static final NamespacedKey NAV_DEPOSIT_KEY = new NamespacedKey("floydbackpack", "nav_deposit");
    public static final NamespacedKey NAV_PREV_KEY = new NamespacedKey("floydbackpack", "nav_prev");
    public static final NamespacedKey NAV_SORT_KEY = new NamespacedKey("floydbackpack", "nav_sort");
    public static final NamespacedKey NAV_NEXT_KEY = new NamespacedKey("floydbackpack", "nav_next");
    public static final NamespacedKey NAV_WITHDRAW_KEY = new NamespacedKey("floydbackpack", "nav_withdraw");
    public static final NamespacedKey NAV_HELP_KEY = new NamespacedKey("floydbackpack", "nav_help");
    public static final NamespacedKey NAV_UPGRADE_KEY = new NamespacedKey("floydbackpack", "nav_upgrade");

    /**
     * 全局槽位物品存储（索引即全局 slot，跨页连续）
     */
    private final ItemStack[] items = new ItemStack[TOTAL_CAPACITY];

    @Getter
    private final String playerUuid;

    @Getter
    private final String playerName;

    @Getter
    private int level;

    @Getter
    private int usableSlots;

    /**
     * 背包数据同步锁，解决并发访问问题
     */
    @Getter
    private final Lock lock = new ReentrantLock();

    /**
     * 脏标记：true 表示自上次持久化以来发生过修改，定时任务仅写盘此类背包
     */
    @Getter
    private volatile boolean dirty = false;

    /**
     * 标记背包为已修改。任何写操作完成后调用此方法。
     */
    public void markDirty() {
        this.dirty = true;
    }

    /**
     * 清除脏标记，定时任务成功写盘后调用。
     */
    public void clearDirty() {
        this.dirty = false;
    }

    @Getter
    @Setter
    private String title;

    /**
     * 下一级可用容量（等级变化时调用）
     */
    @Setter
    private int nextLevelUsableSlots;

    /**
     * 升级信息（打开时实时计算，用于底栏升级按钮 lore 展示）
     */
    private long upgradeCost = -1;
    private int nextLevelLore = -1;
    private double balance = -1;
    private boolean economyReady = false;

    /**
     * 溢出物品映射（slot → base64），存储因降级而隐藏但未丢失的物品
     */
    @Getter
    private final Map<Integer, String> overflowItems = new LinkedHashMap<>();

    private int currentPage = 0;

    private Inventory currentView;

    private final String placeholderMaterial;

    private final String placeholderName;

    public Backpack(@NotNull Player player, int level, int usableSlots,
                    String placeholderMaterial, String placeholderName,
                    String nextLevelMaterial, String nextLevelName, int nextLevelUsableSlots) {
        this(player.getUniqueId().toString(), player.getName(), level, usableSlots,
                placeholderMaterial, placeholderName,
                nextLevelMaterial, nextLevelName, nextLevelUsableSlots);
    }

    public Backpack(String playerUuid, String playerName, int level, int usableSlots,
                    String placeholderMaterial, String placeholderName,
                    String nextLevelMaterial, String nextLevelName, int nextLevelUsableSlots) {
        usableSlots = fixedUsableSlots(usableSlots);
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.level = level;
        this.usableSlots = usableSlots;
        this.placeholderMaterial = placeholderMaterial;
        this.placeholderName = placeholderName;
        this.nextLevelUsableSlots = nextLevelUsableSlots;
    }

    /**
     * 更新背包等级和容量。
     */
    public void setUpgrade(int newLevel, int newUsableSlots) {
        newUsableSlots = fixedUsableSlots(newUsableSlots);
        this.level = newLevel;
        this.usableSlots = newUsableSlots;
        markDirty();
    }

    private int fixedUsableSlots(int usableSlotsToCheck) {
        if (usableSlotsToCheck < 1 || usableSlotsToCheck > TOTAL_CAPACITY) {
            int fitUsableSlots = Math.max(1, Math.min(TOTAL_CAPACITY, usableSlotsToCheck));
            logger.warn("UsableSlots value should between 1 and {} but got {}, using default value {}",
                    TOTAL_CAPACITY, usableSlotsToCheck, fitUsableSlots);
            return fitUsableSlots;
        }
        return usableSlotsToCheck;
    }

    /**
     * 读取全局槽位物品
     */
    public ItemStack getStoredItem(int globalSlot) {
        if (globalSlot < 0 || globalSlot >= TOTAL_CAPACITY) {
            return null;
        }
        return items[globalSlot];
    }

    /**
     * 写入全局槽位物品（null 表示清空）
     */
    public void setStoredItem(int globalSlot, ItemStack item) {
        if (globalSlot < 0 || globalSlot >= TOTAL_CAPACITY) {
            return;
        }
        items[globalSlot] = item;
        markDirty();
    }

    /**
     * 已使用格数（非空物品）
     */
    public int getUsedSlots() {
        int used = 0;
        for (int g = 0; g < usableSlots; g++) {
            ItemStack it = items[g];
            if (it != null && it.getType() != Material.AIR) {
                used++;
            }
        }
        return used;
    }

    /**
     * 总页数
     */
    public int getPageCount() {
        return Math.max(1, (int) Math.ceil((double) usableSlots / STORAGE_PER_PAGE));
    }

    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * 打开指定页（翻页 / 重新打开均走此入口）
     */
    public void openPage(Player player, int page) {
        // 先把当前视图（上一页/关闭前的改动）同步回存储数组，避免物品丢失
        syncFromView(this.currentView);
        refreshUpgradeInfo(player);
        int pageCount = getPageCount();
        if (page < 0) {
            page = 0;
        }
        if (page >= pageCount) {
            page = pageCount - 1;
        }
        this.currentPage = page;
        this.currentView = buildPage(page);
        // 延迟 1 tick 打开，避免在 InventoryClickEvent 内同步打开导致“打开即关闭”
        org.bukkit.plugin.Plugin plugin = BackpackPluginAccessor.getPlugin();
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.openInventory(currentView), 1L);
    }

    /**
     * 重新渲染当前页并打开（用于存取/整理后刷新，不再二次 sync 以免覆盖刚写入的 items）。
     */
    private void reopen(Player player) {
        this.currentView = buildPage(this.currentPage);
        org.bukkit.plugin.Plugin plugin = BackpackPluginAccessor.getPlugin();
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.openInventory(currentView), 1L);
    }

    /**
     * 打开时实时计算升级费用 / 下一级容量 / 玩家余额，供底栏升级按钮 lore 展示。
     */
    private void refreshUpgradeInfo(Player player) {
        try {
            PlayerBackpackManager mgr = BackpackPluginAccessor.getPlayerBackpackManager();
            int maxLevel = mgr.getMaxLevel();
            if (level < maxLevel) {
                this.upgradeCost = mgr.getUpgradeCost(level);
                this.nextLevelLore = mgr.getUsableSlots(level + 1);
            } else {
                this.upgradeCost = -1;
                this.nextLevelLore = -1;
            }
            Economy eco = EconomyProvider.get();
            if (eco != null) {
                this.economyReady = true;
                this.balance = eco.getBalance(player);
            } else {
                this.economyReady = false;
            }
        } catch (Throwable t) {
            this.upgradeCost = -1;
            this.nextLevelLore = -1;
            this.economyReady = false;
        }
    }

    /**
     * 将当前页视图中的物品同步回全局存储数组。
     */
    public void syncFromView(Inventory view) {
        if (view == null) {
            return;
        }
        int start = this.currentPage * STORAGE_PER_PAGE;
        boolean changed = false;
        for (int i = 0; i < STORAGE_PER_PAGE; i++) {
            int global = start + i;
            if (global < usableSlots) {
                ItemStack it = view.getItem(i);
                // 锁定格的占位符只是 UI 展示，绝不能写回存储数组。
                // 升级后容量变大时，旧视图里新解锁的格子仍残留占位符，若写回会导致新格子继续显示为锁定，
                // 直到重新进服（占位符不入库、等级已落盘）才恢复——即「升级后格子没变多」的根因。
                if (PlaceHolderItem.isPlaceholder(it)) {
                    continue;
                }
                if (!sameItem(items[global], it)) {
                    items[global] = it;
                    changed = true;
                }
            }
        }
        if (changed) {
            markDirty();
        }
    }

    private static boolean sameItem(ItemStack a, ItemStack b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }

    private Inventory buildPage(int page) {
        Inventory inv = Bukkit.createInventory(this, GUI_SIZE, ChestUIMsg.BACKPACK_TITLE.content(playerName));
        int start = page * STORAGE_PER_PAGE;
        for (int i = 0; i < STORAGE_PER_PAGE; i++) {
            int global = start + i;
            if (global >= usableSlots) {
                // 超出当前容量的锁定格（不可存放）
                inv.setItem(i, new PlaceHolderItem(placeholderMaterial, placeholderName).getItemStack());
            } else {
                inv.setItem(i, items[global]);
            }
        }
        setNavItems(inv, page);
        return inv;
    }

    private void setNavItems(Inventory inv, int page) {
        int pageCount = getPageCount();
        // 左下：返回（关闭背包）
        inv.setItem(SLOT_RETURN, navItem(Material.BARRIER, ChestUIMsg.NAV_RETURN_NAME.content(), NAV_RETURN_KEY,
                List.of(ChestUIMsg.NAV_RETURN_LORE.content())));
        // 全部存入
        inv.setItem(SLOT_DEPOSIT_ALL, navItem(Material.CHEST, ChestUIMsg.NAV_DEPOSIT_NAME.content(), NAV_DEPOSIT_KEY,
                List.of(ChestUIMsg.NAV_DEPOSIT_LORE1.content(), ChestUIMsg.NAV_DEPOSIT_LORE2.content())));
        // 上一页
        if (page > 0) {
            inv.setItem(SLOT_PREV, navItem(Material.ARROW, ChestUIMsg.NAV_PREV_NAME.content(), NAV_PREV_KEY, null));
        } else {
            inv.setItem(SLOT_PREV, navItem(Material.GRAY_DYE, ChestUIMsg.NAV_PREV_FIRST.content(), null, null));
        }
        // 整理
        inv.setItem(SLOT_SORT, navItem(Material.REDSTONE, ChestUIMsg.NAV_SORT_NAME.content(), NAV_SORT_KEY,
                List.of(ChestUIMsg.NAV_SORT_LORE.content())));
        // 中间：页码 + 容量信息
        inv.setItem(SLOT_PAGE, pageInfoItem(page, pageCount));
        // 全部取出
        inv.setItem(SLOT_WITHDRAW_ALL, navItem(Material.CHEST, ChestUIMsg.NAV_WITHDRAW_NAME.content(), NAV_WITHDRAW_KEY,
                List.of(ChestUIMsg.NAV_WITHDRAW_LORE1.content(), ChestUIMsg.NAV_WITHDRAW_LORE2.content())));
        // 下一页
        if (page < pageCount - 1) {
            inv.setItem(SLOT_NEXT, navItem(Material.ARROW, ChestUIMsg.NAV_NEXT_NAME.content(), NAV_NEXT_KEY, null));
        } else {
            inv.setItem(SLOT_NEXT, navItem(Material.GRAY_DYE, ChestUIMsg.NAV_NEXT_LAST.content(), null, null));
        }
        // 帮助
        inv.setItem(SLOT_HELP, navItem(Material.BOOK, ChestUIMsg.NAV_HELP_NAME.content(), NAV_HELP_KEY,
                List.of(ChestUIMsg.NAV_HELP_LORE.content())));
        // 右下：升级仓库（lore 实时显示费用与余额）
        inv.setItem(SLOT_UPGRADE, buildUpgradeItem());
    }

    private ItemStack buildUpgradeItem() {
        List<String> lore = new ArrayList<>();
        lore.add(ChestUIMsg.NAV_UPGRADE_LORE_CLICK.content());
        if (upgradeCost >= 0 && nextLevelLore >= 0) {
            int nextLv = level + 1;
            lore.add(ChestUIMsg.NAV_UPGRADE_NEXT_LEVEL.content(nextLv, nextLevelLore));
            lore.add(ChestUIMsg.NAV_UPGRADE_COST.content(formatMoney(upgradeCost)));
            if (economyReady) {
                if (balance >= upgradeCost) {
                    lore.add(ChestUIMsg.NAV_UPGRADE_BALANCE_ENOUGH.content());
                } else {
                    lore.add(ChestUIMsg.NAV_UPGRADE_BALANCE_INSUFFICIENT.content(formatMoney((long) balance)));
                }
            } else {
                lore.add(ChestUIMsg.NAV_UPGRADE_BALANCE_UNKNOWN.content());
            }
        } else {
            lore.add(ChestUIMsg.NAV_UPGRADE_MAX_LEVEL.content());
        }
        return navItem(Material.EXPERIENCE_BOTTLE, ChestUIMsg.NAV_UPGRADE_NAME.content(), NAV_UPGRADE_KEY, lore);
    }

    private static String formatMoney(long value) {
        return String.format("%,d", value);
    }

    private ItemStack navItem(Material material, String name, NamespacedKey key, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(LEGACY.deserialize(name).decoration(TextDecoration.ITALIC, false));
        if (lore != null && !lore.isEmpty()) {
            meta.lore(lore.stream()
                    .map(line -> (net.kyori.adventure.text.Component) LEGACY.deserialize(line)
                            .decoration(TextDecoration.ITALIC, false))
                    .toList());
        }
        if (key != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        }
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack pageInfoItem(int page, int pageCount) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(LEGACY.deserialize(ChestUIMsg.NAV_PAGE_NAME.content())
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
                LEGACY.deserialize(ChestUIMsg.NAV_PAGE_LINE.content(page + 1, pageCount)).decoration(TextDecoration.ITALIC, false),
                LEGACY.deserialize(ChestUIMsg.NAV_PAGE_CAPACITY.content(usableSlots)).decoration(TextDecoration.ITALIC, false),
                LEGACY.deserialize(ChestUIMsg.NAV_PAGE_USED.content(getUsedSlots())).decoration(TextDecoration.ITALIC, false),
                LEGACY.deserialize(ChestUIMsg.NAV_PAGE_LEVEL.content(level)).decoration(TextDecoration.ITALIC, false)
        ));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * 把玩家背包（主背包，不含 armor/offhand）中的物品存入仓库。
     * 优先合并到同类堆叠，再放入空槽；仓库放不下则剩余留在玩家背包。
     */
    public void depositAll(Player player) {
        syncFromView(this.currentView);
        lock.lock();
        try {
            PlayerInventory pinv = player.getInventory();
            ItemStack[] contents = pinv.getStorageContents();
            for (int i = 0; i < contents.length; i++) {
                ItemStack it = contents[i];
                if (it == null || it.getType() == Material.AIR) {
                    continue;
                }
                if (OpenBackpackTool.matchItemStack(it)) {
                    continue;
                }
                if (PlaceHolderItem.isPlaceholder(it)) {
                    continue;
                }
                boolean placed = tryMergeOrStore(it);
                contents[i] = placed ? null : it;
            }
            pinv.setStorageContents(contents);
            markDirty();
        } finally {
            lock.unlock();
        }
        reopen(player);
    }

    /**
     * 整理：合并同类堆叠并紧凑排列（空格前移），不改变物品数量。
     */
    public void sortItems(Player player) {
        syncFromView(this.currentView);
        lock.lock();
        try {
            List<ItemStack> all = new ArrayList<>();
            for (int g = 0; g < usableSlots; g++) {
                ItemStack it = items[g];
                if (it != null && it.getType() != Material.AIR) {
                    all.add(it.clone());
                }
            }
            Arrays.fill(items, 0, usableSlots, null);
            int idx = 0;
            for (ItemStack src : all) {
                int remaining = src.getAmount();
                for (int j = 0; j < idx; j++) {
                    ItemStack t = items[j];
                    if (t.isSimilar(src) && t.getAmount() < t.getMaxStackSize()) {
                        int add = Math.min(t.getMaxStackSize() - t.getAmount(), remaining);
                        t.setAmount(t.getAmount() + add);
                        remaining -= add;
                        if (remaining <= 0) {
                            break;
                        }
                    }
                }
                if (remaining > 0 && idx < usableSlots) {
                    ItemStack n = src.clone();
                    n.setAmount(remaining);
                    items[idx++] = n;
                }
            }
            markDirty();
        } finally {
            lock.unlock();
        }
        reopen(player);
    }

    /**
     * 把仓库全部物品取出到玩家背包，背包放不下的部分掉落在玩家脚下。
     */
    public void withdrawAll(Player player) {
        syncFromView(this.currentView);
        lock.lock();
        try {
            PlayerInventory pinv = player.getInventory();
            for (int g = 0; g < usableSlots; g++) {
                ItemStack it = items[g];
                if (it == null || it.getType() == Material.AIR) {
                    continue;
                }
                java.util.Map<Integer, ItemStack> leftover = pinv.addItem(it.clone());
                if (leftover.isEmpty()) {
                    items[g] = null;
                } else {
                    ItemStack rem = leftover.values().iterator().next();
                    items[g] = (rem != null && rem.getAmount() > 0) ? rem : null;
                }
            }
            markDirty();
        } finally {
            lock.unlock();
        }
        reopen(player);
    }

    private boolean tryMergeOrStore(ItemStack src) {
        for (int g = 0; g < usableSlots; g++) {
            ItemStack existing = items[g];
            if (existing != null && existing.getType() != Material.AIR
                    && existing.isSimilar(src) && existing.getAmount() < existing.getMaxStackSize()) {
                int add = Math.min(existing.getMaxStackSize() - existing.getAmount(), src.getAmount());
                existing.setAmount(existing.getAmount() + add);
                src.setAmount(src.getAmount() - add);
                if (src.getAmount() <= 0) {
                    return true;
                }
            }
        }
        for (int g = 0; g < usableSlots; g++) {
            if (items[g] == null) {
                items[g] = src.clone();
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (currentView == null) {
            this.currentView = buildPage(this.currentPage);
        }
        return currentView;
    }
}
