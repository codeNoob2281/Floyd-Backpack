package com.floyd.backpack.service;

import com.floyd.backpack.FloydBackpackPlugin;
import com.floyd.backpack.entity.Backpack;
import com.floyd.core.inventory.io.BukkitItemStackSerializer;
import com.floyd.core.inventory.io.ItemStackSerializer;
import com.floyd.core.logging.ConsoleLogger;
import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.util.DateUtil;
import com.floyd.core.util.FileUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * @author floyd
 * @date 2026/3/23
 */
@Component
public class PlayerBackpackManager {

    private static final ConsoleLogger logger = ConsoleLoggerFactory.get(PlayerBackpackManager.class);

    private final Map<String, Backpack> PLAYER_BACKPACK_MAP = new ConcurrentHashMap<>(32);

    private final ItemStackSerializer ITEM_STACK_SERIALIZER = new BukkitItemStackSerializer();

    public @NotNull Backpack getBackpack(Player player) {
        return PLAYER_BACKPACK_MAP.computeIfAbsent(getUuid(player), uuid -> createBackpack(player));
    }

    public void saveAllBackpack() {
        logger.info("开始保存所有玩家的背包数据");
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        PLAYER_BACKPACK_MAP.forEach((uuid, backpack) -> {
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
        });
        logger.info("保存所有玩家的背包数据完成，成功数：" + successCount.get() + "，失败数:" + failCount.get());
    }

    /**
     *
     * 将玩家的背包数据保存到文件
     *
     * @param player 玩家
     * @return 保存结果
     */
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

    private boolean writeBackpackDataToFile(Backpack backpack) {
        if (backpack == null) {
            return false;
        }
        JsonObject jsonObject = new JsonObject();
        Inventory inventory = backpack.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack != null) {
                jsonObject.addProperty(String.valueOf(i), ITEM_STACK_SERIALIZER.serialize(itemStack));
            }
        }
        File dataFile = getBackpackDataFile(backpack.getPlayerUuid());
        try {
            FileUtil.writeString(dataFile, jsonObject.toString(), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            logger.error("保存玩家[" + backpack.getPlayerName() + "]的背包数据失败", e);
            return false;
        }
    }

    private Backpack createBackpack(Player player) {
        Backpack backpack = new Backpack(player);
        // 读取持久化的背包物品数据
        File backpackDataFile = getBackpackDataFile(getUuid(player));
        try {
            if (backpackDataFile.exists()) {
                JsonElement jsonElement = JsonParser.parseString(FileUtil.readString(backpackDataFile, StandardCharsets.UTF_8));
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObj = jsonElement.getAsJsonObject();
                    Map<Integer, ItemStack> itemStackMap = new HashMap<>(jsonObj.size());
                    jsonObj.entrySet().forEach(entry -> {
                        itemStackMap.put(Integer.parseInt(entry.getKey()), ITEM_STACK_SERIALIZER.deserialize(entry.getValue().getAsString()));
                    });
                    itemStackMap.forEach(backpack.getInventory()::setItem);
                }
            } else {
                boolean res = backpackDataFile.createNewFile();
                if (!res) {
                    logger.warn("创建玩家[" + player.getName() + "]的背包数据文件失败，请检查文件权限");
                }
            }
        } catch (Exception e) {
            logger.error("加载玩家[" + player.getName() + "]的背包数据失败", e);
            if (e instanceof IOException) {
                return backpack;
            }
            // 非io异常，备份损坏的文件
            File backupFile = new File(backpackDataFile.getAbsolutePath() + ".bak." + DateUtil.format(new Date(), DateUtil.PURE_DATE_TIME_FORMAT));
            try {
                logger.warn("由于玩家[" + player.getName() + "]的背包已损坏，已为该玩家创建新背包，旧背包文件备份位置：" + backupFile.getAbsolutePath());
                Files.copy(backpackDataFile.toPath(), backupFile.toPath());
            } catch (IOException ioe) {
                logger.error("备份玩家[" + player.getName() + "]的背包数据失败", ioe);
            }
        }
        return backpack;
    }

    private static File getBackpackDataFile(String uuid) {
        FloydBackpackPlugin plugin = (FloydBackpackPlugin) FloydBackpackPlugin.instance();
        return plugin.getBackpackDataPath().resolve(uuid + ".json").toFile();
    }

    private static String getUuid(Player player) {
        return player.getUniqueId().toString();
    }
}
