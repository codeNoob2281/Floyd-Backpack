package com.floyd.backpack.service;

import com.floyd.backpack.FloydBackpackPlugin;
import com.floyd.backpack.entity.Backpack;
import com.floyd.core.FloydPlugin;
import com.floyd.core.PluginBizException;
import com.floyd.core.inventory.io.BukkitItemStackSerializer;
import com.floyd.core.inventory.io.ItemStackSerializer;
import com.floyd.core.logging.ConsoleLogger;
import com.floyd.core.util.FileUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * @author floyd
 * @date 2026/3/23
 */
public class PlayerBackpackManager {

    private static final Map<String, Backpack> PLAYER_BACKPACK_MAP = new ConcurrentHashMap<>(32);

    private static final ItemStackSerializer ITEM_STACK_SERIALIZER = new BukkitItemStackSerializer();

    public static @NotNull Backpack getBackpack(Player player) {
        return PLAYER_BACKPACK_MAP.compute(getUuid(player), (uuid, backPack) -> {
            if (backPack == null) {
                backPack = createBackpack(player);
            }
            return backPack;
        });
    }

    public static void saveAllBackpack() {
        ConsoleLogger logger = FloydPlugin.logger();
        logger.info("开始保存所有玩家的背包数据");
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        PLAYER_BACKPACK_MAP.forEach((uuid, backpack) -> {
            if (writeBackpackDataToFile(backpack)) {
                successCount.incrementAndGet();
            } else {
                failCount.incrementAndGet();
            }
        });
        logger.info("保存所有玩家的背包数据完成，成功数：" + successCount.get() + "，失败数:" + failCount.get());
    }

    public static boolean saveBackpack(Player player) {
        String uuid = getUuid(player);
        Backpack backpack = PLAYER_BACKPACK_MAP.get(uuid);
        return writeBackpackDataToFile(backpack);
    }

    private static boolean writeBackpackDataToFile(Backpack backpack) {
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
        FileUtil.writeString(dataFile, jsonObject.toString(), StandardCharsets.UTF_8);
        return true;
    }

    private static Backpack createBackpack(Player player) {
        Backpack backpack = new Backpack(player);
        // 读取持久化的背包物品数据
        File backpackDataFile = getBackpackDataFile(getUuid(player));
        if (backpackDataFile.exists()) {
            JsonElement jsonElement = JsonParser.parseString(FileUtil.readString(backpackDataFile, StandardCharsets.UTF_8));
            if (jsonElement.isJsonNull()) {
                return backpack;
            }
            JsonObject jsonObj = jsonElement.getAsJsonObject();
            Map<Integer, ItemStack> itemStackMap = new HashMap<>(jsonObj.size());
            jsonObj.entrySet().forEach(entry -> {
                itemStackMap.put(Integer.parseInt(entry.getKey()), ITEM_STACK_SERIALIZER.deserialize(entry.getValue().getAsString()));
            });
            itemStackMap.forEach(backpack.getInventory()::setItem);
        } else {
            try {
                backpackDataFile.createNewFile();
            } catch (IOException e) {
                throw new PluginBizException("创建文件失败：" + backpackDataFile.getAbsolutePath(), e);
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
