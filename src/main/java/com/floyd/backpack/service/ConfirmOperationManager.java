package com.floyd.backpack.service;

import com.floyd.backpack.FloydBackpackPlugin;
import com.floyd.backpack.setting.properties.CmdClearBackPackSettings;
import com.floyd.backpack.constant.Constants;
import com.floyd.backpack.enums.ConfirmOperationEnum;
import com.floyd.core.logging.ConsoleLogger;
import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.settings.PluginSettingsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author floyd
 * @date 2026/3/29
 */
@org.springframework.stereotype.Component
public class ConfirmOperationManager implements InitializingBean, DisposableBean {

    private static final ConsoleLogger logger = ConsoleLoggerFactory.get(ConfirmOperationManager.class);

    private final Map<ConfirmOperationEnum, Map<String, Long>> lastConfirmOperationTimestampMap = new ConcurrentHashMap<>(8);

    private final Map<ConfirmOperationEnum, Long> confirmOpExpireInterval = new ConcurrentHashMap<>(8);

    /**
     * 淘汰过期key的定时任务
     */
    private BukkitTask expireKeyCheckTask;

    @Autowired
    private PluginSettingsManager settingsManager;

    /**
     * 添加新的二次确认操作
     *
     * @param confirmOperation 确认操作枚举
     * @param playerUuid       玩家UUID
     * @return 是否添加成功
     */
    public boolean addNew(@NotNull ConfirmOperationEnum confirmOperation, @NotNull String playerUuid) {
        Map<String, Long> uuidOperationMap = lastConfirmOperationTimestampMap.computeIfAbsent(confirmOperation, k -> new ConcurrentHashMap<>(32));
        return uuidOperationMap.putIfAbsent(playerUuid, System.currentTimeMillis()) == null;
    }

    /**
     * 移除二次确认操作计时
     *
     * @param confirmOperation 确认操作枚举
     * @param playerUuid       玩家UUID
     * @return
     */
    public boolean remove(@NotNull ConfirmOperationEnum confirmOperation, @NotNull String playerUuid) {
        Map<String, Long> uuidOperationMap = lastConfirmOperationTimestampMap.get(confirmOperation);
        if (uuidOperationMap == null) {
            return false;
        }
        return uuidOperationMap.remove(playerUuid) != null;
    }

    /**
     * 获取二次确认操作的剩余时间，单位ms
     *
     * @param confirmOperation 确认操作枚举
     * @param playerUuid       玩家UUID
     * @return 剩余时间，单位ms，如果超时，则返回0；如果未添加过二次确认操作，则返回null
     */
    public @Nullable Long getTtl(@NotNull ConfirmOperationEnum confirmOperation, @NotNull String playerUuid) {
        Map<String, Long> uuidOperationMap = lastConfirmOperationTimestampMap.get(confirmOperation);
        if (uuidOperationMap == null) {
            return null;
        }
        if (!uuidOperationMap.containsKey(playerUuid)) {
            return null;
        }
        long expireInterval = getExpireInterval(confirmOperation);
        Long lastTimestamp = uuidOperationMap.compute(playerUuid, (uuid, lastT) -> {
            if (lastT == null || (expireInterval - (System.currentTimeMillis() - lastT)) <= 0) {
                return null;
            }
            return lastT;
        });
        return lastTimestamp == null ? 0L : (expireInterval - (System.currentTimeMillis() - lastTimestamp));
    }

    private long getExpireInterval(ConfirmOperationEnum confirmOperation) {
        return confirmOpExpireInterval.getOrDefault(confirmOperation, Constants.DEFAULT_EXPIRE_INTERVAL);
    }

    /**
     * 重新加载配置
     */
    public void reload() {
        cancelTasks();
        scheduleTasks();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduleTasks();
    }

    @Override
    public void destroy() throws Exception {
        cancelTasks();
    }

    /**
     * 定时任务开启
     */
    private void scheduleTasks() {
        Long confirmInterval = settingsManager.getProperty(CmdClearBackPackSettings.CONFIRM_INTERVAL);
        confirmOpExpireInterval.put(ConfirmOperationEnum.CLEAR_BACKPACK, confirmInterval);

        long tickPeriod = (long) (Constants.SERVER_TICK_PER_SECOND * confirmInterval / 1000d);
        expireKeyCheckTask = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(FloydBackpackPlugin.instance(), () -> {
            AtomicInteger removedKeyCount = new AtomicInteger(0);
            lastConfirmOperationTimestampMap.forEach((confirmOperation, uuidOperationMap) -> {
                for (String existUuid : uuidOperationMap.keySet()) {
                    Long ttl = getTtl(confirmOperation, existUuid);
                    if (ttl != null && ttl == 0) {
                        Player player = Bukkit.getPlayer(UUID.fromString(existUuid));
                        if (player != null) {
                            player.sendMessage(Component.text(Constants.MESSAGE_PREFIX, NamedTextColor.YELLOW)
                                    .append(Component.text(" 上一个操作已过期", NamedTextColor.GOLD)));
                        }
                        removedKeyCount.incrementAndGet();
                    }
                }
            });
            int res = removedKeyCount.get();
            if (res > 0) {
                logger.info(res + "个过期的二次确认信息被移除");
            }
        }, tickPeriod, tickPeriod);
    }

    /**
     * 定时任务关闭
     */
    private void cancelTasks() {
        if (expireKeyCheckTask != null) {
            expireKeyCheckTask.cancel();
        }
    }
}
