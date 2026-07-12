package com.floyd.backpack.service;

import com.floyd.backpack.BackpackPluginAccessor;
import com.floyd.backpack.entity.Backpack;
import com.floyd.backpack.entity.PlaceHolderItem;
import com.floyd.backpack.message.ChestUIMsg;
import com.floyd.backpack.setting.properties.AutosaveSettings;
import com.floyd.backpack.setting.properties.UpgradeSettings;
import com.floyd.core.common.util.DateUtil;
import com.floyd.core.common.util.FileUtil;
import com.floyd.core.inventory.BukkitItemStackSerializer;
import com.floyd.core.inventory.ItemStackSerializer;
import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.logging.Logger;
import com.floyd.core.settings.PluginSettingsManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * @author floyd
 * @date 2026/3/23
 */
@Component
public class PlayerBackpackManager implements InitializingBean, DisposableBean {

    public static final int INITIAL_BACKPACK_MAP_CAPACITY = 32;

    private static final Logger logger = ConsoleLoggerFactory.get(PlayerBackpackManager.class);

    /**
     * 定时自动保存任务
     */
    private BukkitTask autosaveTask;

    private final Map<String, Backpack> PLAYER_BACKPACK_MAP = new ConcurrentHashMap<>(INITIAL_BACKPACK_MAP_CAPACITY);

    private final ItemStackSerializer ITEM_STACK_SERIALIZER = new BukkitItemStackSerializer();

    @Autowired
    private PluginSettingsManager pluginSettingsManager;

    private Map<Integer, Integer> levelSlotMap = new LinkedHashMap<>();

    @Override
    public void afterPropertiesSet() {
        loadLevelMapping();
        scheduleAutosaveTask();
    }

    @Override
    public void destroy() {
        cancelAutosaveTask();
    }

    public void reloadAutosaveTask() {
        cancelAutosaveTask();
        scheduleAutosaveTask();
    }

    /**
     * 调度定时自动保存任务。仅保存脏（自上次持久化后发生过修改）的背包，以减少 I/O。
     */
    private void scheduleAutosaveTask() {
        Boolean enable = pluginSettingsManager.getProperty(AutosaveSettings.ENABLE);
        if (!enable) {
            logger.info("Autosave is disabled");
            return;
        }
        long intervalMs = pluginSettingsManager.getProperty(AutosaveSettings.INTERVAL);
        long periodTicks = Math.max(1, intervalMs / 50L);
        autosaveTask = Bukkit.getScheduler().runTaskTimer(BackpackPluginAccessor.getPlugin(), () -> {
            AtomicInteger saved = new AtomicInteger(0);
            AtomicInteger failed = new AtomicInteger(0);
            for (Map.Entry<String, Backpack> entry : PLAYER_BACKPACK_MAP.entrySet()) {
                Backpack backpack = entry.getValue();
                if (backpack == null || !backpack.isDirty()) {
                    continue;
                }
                Lock lock = backpack.getLock();
                lock.lock();
                try {
                    // 双重检查：加锁后再次确认，避免重复保存
                    if (!backpack.isDirty()) {
                        continue;
                    }
                    if (writeBackpackDataToFile(backpack)) {
                        backpack.clearDirty();
                        saved.incrementAndGet();
                    } else {
                        failed.incrementAndGet();
                    }
                } finally {
                    lock.unlock();
                }
            }
            int s = saved.get();
            int f = failed.get();
            logger.info("Autosave completed, saved: {}, failed: {}", s, f);
        }, periodTicks, periodTicks);
        logger.info("Autosave task scheduled, interval: {}ms ({} ticks)", intervalMs, periodTicks);
    }

    /**
     * 取消定时自动保存任务
     */
    private void cancelAutosaveTask() {
        if (autosaveTask != null) {
            autosaveTask.cancel();
            autosaveTask = null;
        }
    }

    /**
     * 重新加载等级→容量映射（/bp reload 时调用）
     */
    public void reloadLevelMapping() {
        loadLevelMapping();
    }

    private void loadLevelMapping() {
        Map<Integer, Integer> newMap = new LinkedHashMap<>();
        List<Integer> perLevelSlots = pluginSettingsManager.getProperty(UpgradeSettings.LEVELS);
        for (int i = 0; i < perLevelSlots.size(); i++) {
            newMap.put(i + 1, perLevelSlots.get(i));
        }
        this.levelSlotMap = newMap;
        logger.info("Level mapping loaded: {}", levelSlotMap);
    }

    public int getMaxLevel() {
        return pluginSettingsManager.getProperty(UpgradeSettings.MAX_LEVEL);
    }

    public int getUsableSlots(int level) {
        Integer usableSlots = levelSlotMap.getOrDefault(level, level * 9);
        return Math.min(pluginSettingsManager.getProperty(UpgradeSettings.MAX_SLOTS), usableSlots);
    }

    public @NotNull Backpack getBackpack(Player player) {
        return PLAYER_BACKPACK_MAP.computeIfAbsent(getUuid(player), uuid -> createBackpack(player));
    }

    public void saveAllBackpack() {
        logger.info("Saving all player backpack data");
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        for (String uuid : PLAYER_BACKPACK_MAP.keySet()) {
            Backpack backpack = PLAYER_BACKPACK_MAP.get(uuid);
            if (backpack != null) {
                Lock lock = backpack.getLock();
                lock.lock();
                try {
                    if (writeBackpackDataToFile(backpack)) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
        logger.info("All player backpack data saved, success: {}, failed: {}", successCount.get(), failCount.get());
    }

    public boolean flushBackpackToFile(Player player) {
        String uuid = getUuid(player);
        Backpack backpack = PLAYER_BACKPACK_MAP.get(uuid);
        if (backpack == null) {
            return true;
        }
        Lock lock = backpack.getLock();
        lock.lock();
        try {
            boolean res = writeBackpackDataToFile(backpack);
            if (res) {
                PLAYER_BACKPACK_MAP.remove(uuid);
            }
            return res;
        } finally {
            lock.unlock();
        }
    }

    public boolean isBackpackInventory(Player player, Inventory clickedInventory) {
        if (player == null || clickedInventory == null) {
            return false;
        }
        Backpack backpack = PLAYER_BACKPACK_MAP.get(getUuid(player));
        return backpack != null && backpack.getInventory() == clickedInventory;
    }

    /**
     * 设置背包等级（同时处理升级和降级）
     */
    public void setBackpackLevel(Backpack backpack, int newLevel, int newUsableSlots) {
        Lock lock = backpack.getLock();
        lock.lock();
        try {
            writeBackpackDataToFile(backpack);

            int oldUsableSlots = backpack.getUsableSlots();

            if (newUsableSlots < oldUsableSlots) {
                // 降级：将超出新容量的物品移至溢出映射
                Inventory inventory = backpack.getInventory();
                for (int i = newUsableSlots; i < oldUsableSlots; i++) {
                    ItemStack item = inventory.getItem(i);
                    if (item != null && !PlaceHolderItem.isPlaceholder(item)) {
                        backpack.getOverflowItems().put(i, ITEM_STACK_SERIALIZER.serialize(item));
                        inventory.clear(i);
                    }
                }
            }

            // 更新等级和容量
            backpack.setUpgrade(newLevel, newUsableSlots);
            backpack.setNextLevelUsableSlots(
                    newLevel >= getMaxLevel() ? newUsableSlots : getUsableSlots(newLevel + 1));

            // 写盘 setUpgrade 内部已 markDirty 一次；此处显式再脏一次，防两种写盘间的并发修改丢失
            backpack.markDirty();

            if (newUsableSlots > oldUsableSlots) {
                // 升级：将溢出物品中属于新可用范围的放回 inventory
                Inventory inventory = backpack.getInventory();
                Map<Integer, String> overflow = backpack.getOverflowItems();
                overflow.entrySet().removeIf(entry -> {
                    if (entry.getKey() < newUsableSlots) {
                        ItemStack item = ITEM_STACK_SERIALIZER.deserialize(entry.getValue());
                        if (item != null) {
                            inventory.setItem(entry.getKey(), item);
                        }
                        return true;
                    }
                    return false;
                });
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean writeBackpackDataToFile(Backpack backpack) {
        if (backpack == null) {
            return false;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("_level", backpack.getLevel());

        Inventory inventory = backpack.getInventory();
        int usableSlots = backpack.getUsableSlots();

        // 序列化可见槽位
        for (int i = 0; i < usableSlots; i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack != null && !PlaceHolderItem.isPlaceholder(itemStack)) {
                jsonObject.addProperty(String.valueOf(i), ITEM_STACK_SERIALIZER.serialize(itemStack));
            }
        }

        // 保留溢出物品（隐藏但未丢失的物品）
        backpack.getOverflowItems().forEach((slot, base64) ->
                jsonObject.addProperty(String.valueOf(slot), base64));

        File dataFile = getBackpackDataFile(backpack.getPlayerUuid());
        try {
            FileUtil.writeString(dataFile, jsonObject.toString(), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            logger.error("Failed to save backpack data for player [{}]", backpack.getPlayerName(), e);
            return false;
        }
    }

    private Backpack createBackpack(Player player) {
        int level = 1;
        File backpackDataFile = getBackpackDataFile(getUuid(player));

        // 首次读取：从 JSON 获取 _level
        if (backpackDataFile.exists()) {
            try {
                String json = FileUtil.readString(backpackDataFile, StandardCharsets.UTF_8);
                JsonElement jsonElement = JsonParser.parseString(json);
                if (jsonElement.isJsonObject()) {
                    JsonElement levelElem = jsonElement.getAsJsonObject().get("_level");
                    if (levelElem != null && levelElem.isJsonPrimitive()) {
                        level = levelElem.getAsInt();
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to read level from backpack data for player [{}], using default level 1", player.getName());
            }
        }

        int usableSlots = getUsableSlots(level);
        int nextLevelUsableSlots = level >= getMaxLevel() ? usableSlots : getUsableSlots(level + 1);
        String placeholderMat = pluginSettingsManager.getProperty(UpgradeSettings.PLACEHOLDER_MATERIAL);
        String placeholderName = ChestUIMsg.PLACEHOLDER_LOCKED_SLOT_NAME.content();
        String nextLevelMat = pluginSettingsManager.getProperty(UpgradeSettings.PLACEHOLDER_NEXT_LEVEL_MATERIAL);
        String nextLevelName = ChestUIMsg.PLACEHOLDER_NEXT_LEVEL_SLOT_NAME.content();
        Backpack backpack = new Backpack(player, level, usableSlots,
                placeholderMat, placeholderName, nextLevelMat, nextLevelName, nextLevelUsableSlots);

        // 第二次读取：加载物品数据
        try {
            if (backpackDataFile.exists()) {
                String json = FileUtil.readString(backpackDataFile, StandardCharsets.UTF_8);
                JsonElement jsonElement = JsonParser.parseString(json);
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObj = jsonElement.getAsJsonObject();
                    Map<Integer, ItemStack> loadedItems = new HashMap<>();
                    Map<Integer, String> overflowItems = new LinkedHashMap<>();

                    for (Map.Entry<String, JsonElement> entry : jsonObj.entrySet()) {
                        String key = entry.getKey();
                        if ("_level".equals(key)) {
                            continue;
                        }
                        try {
                            int slot = Integer.parseInt(key);
                            String base64 = entry.getValue().getAsString();
                            if (slot < usableSlots) {
                                loadedItems.put(slot, ITEM_STACK_SERIALIZER.deserialize(base64));
                            } else {
                                overflowItems.put(slot, base64);
                            }
                        } catch (NumberFormatException e) {
                            logger.warn("Invalid slot key in backpack data for player [{}]: {}", player.getName(), key);
                        }
                    }

                    loadedItems.forEach(backpack.getInventory()::setItem);
                    backpack.getOverflowItems().putAll(overflowItems);
                }
            } else {
                if (!backpackDataFile.createNewFile()) {
                    logger.warn("Failed to create backpack data file for player [{}], please check file permissions", player.getName());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load backpack data for player [{}]", player.getName(), e);
            if (!(e instanceof IOException)) {
                File backupFile = new File(backpackDataFile.getAbsolutePath() + ".bak." + DateUtil.format(new Date(), DateUtil.PURE_DATE_TIME_FORMAT));
                try {
                    logger.warn("Backpack data for player [{}] is corrupted, created a new backpack. Backup file location: {}", player.getName(), backupFile.getAbsolutePath());
                    Files.copy(backpackDataFile.toPath(), backupFile.toPath());
                } catch (IOException ioe) {
                    logger.error("Failed to backup backpack data for player [{}]", player.getName(), ioe);
                }
            }
        }
        return backpack;
    }

    private static File getBackpackDataFile(String uuid) {
        return BackpackPluginAccessor.getBackpackDataPath().resolve(uuid + ".json").toFile();
    }

    private static String getUuid(Player player) {
        return player.getUniqueId().toString();
    }
}
