package com.floyd.backpack.service.impl;

import com.floyd.backpack.FloydBackpackPlugin;
import com.floyd.backpack.setting.properties.CmdClearBackPackSettings;
import com.floyd.backpack.constant.PermConstant;
import com.floyd.backpack.entity.Backpack;
import com.floyd.backpack.enums.ConfirmOperationEnum;
import com.floyd.backpack.service.BackpackCmdService;
import com.floyd.backpack.service.ConfirmOperationManager;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.core.FloydPlugin;
import com.floyd.core.logging.ConsoleLogger;
import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.permission.RequiredPermission;
import com.floyd.core.settings.PluginSettingsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author floyd
 * @date 2026/3/28
 */
@Service
public class BackpackCmdServiceImpl implements BackpackCmdService {

    ConsoleLogger logger = ConsoleLoggerFactory.get(BackpackCmdServiceImpl.class);

    private final ConfirmOperationManager confirmOperationManager;

    private final PluginSettingsManager pluginSettingsManager;

    private final PlayerBackpackManager playerBackpackManager;

    public BackpackCmdServiceImpl(ConfirmOperationManager confirmOperationManager,
                                  PluginSettingsManager pluginSettingsManager,
                                  PlayerBackpackManager playerBackpackManager) {
        this.confirmOperationManager = confirmOperationManager;
        this.pluginSettingsManager = pluginSettingsManager;
        this.playerBackpackManager = playerBackpackManager;
    }


    @Override
    @RequiredPermission(value = PermConstant.OPEN_BACKPACK, tipPermValue = true)
    public boolean onOpenBackpackCmd(CommandSender sender) {
        if (checkIsPlayer(sender)) {
            // 打开背包
            openBackpack(sender);
            return true;
        } else {
            sender.sendMessage(Component.text("无法通过控制台执行此命令", NamedTextColor.RED));
        }
        return false;
    }

    @Override
    @RequiredPermission(value = PermConstant.CLEAR_BACKPACK, tipPermValue = true)
    public boolean onClearBackpackCmd(@NonNull CommandSender sender, @NotNull String @NonNull [] args) {
        boolean isPlayer = checkIsPlayer(sender);
        Boolean enable = pluginSettingsManager.getProperty(CmdClearBackPackSettings.ENABLE);
        if (!enable) {
            sender.sendMessage(Component.text("当前未启用清空背包功能", NamedTextColor.RED));
            if (!isPlayer) {
                FloydBackpackPlugin.instance().getLogger().warning("请在插件目录的config.yml中修改command.backpack.clear.enable为true以开启背包清理功能");
            }
            return false;
        }
        if (!isPlayer) {
            sender.sendMessage(Component.text("无法通过控制台执行此命令", NamedTextColor.RED));
            return false;
        }
        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();

        Boolean needConfirm = pluginSettingsManager.getProperty(CmdClearBackPackSettings.NEED_CONFIRM);
        // 无需二次确认，直接清空背包
        if (!needConfirm) {
            execClearBackpack(sender);
            return true;
        }

        if (args.length == 1) {
            //  提示必须进行二次确认操作
            Long ttl = confirmOperationManager.getTtl(ConfirmOperationEnum.CLEAR_BACKPACK, uuid);
            if (ttl != null && ttl > 0) {
                FloydBackpackPlugin.instance().getLogger().info(player.getName() + "仍存在活跃的二次确认操作，剩余时间" + ttl + "ms");
                sender.sendMessage(Component.text("当前仍存在一个待确认的操作，请继续执行", NamedTextColor.YELLOW));
                sendClearConfirmTipMsg(sender);
            } else {
                confirmOperationManager.addNew(ConfirmOperationEnum.CLEAR_BACKPACK, uuid);
                sender.sendMessage(Component.text("是否确认删除背包？请注意这是一个不可逆的操作", NamedTextColor.GOLD));
                sendClearConfirmTipMsg(sender);
                Long confirmInterval = pluginSettingsManager.getProperty(CmdClearBackPackSettings.CONFIRM_INTERVAL);
                long seconds = Math.round((double) confirmInterval / 1000L);
                sender.sendMessage(Component.text("你有" + seconds + "s的时间处理该操作", NamedTextColor.BLUE));
            }
            return true;
        } else {
            // 处理二次确认结果
            Long ttl = confirmOperationManager.getTtl(ConfirmOperationEnum.CLEAR_BACKPACK, uuid);
            if (ttl == null || ttl == 0) {
                sender.sendMessage(Component.text("当前无活跃的二次确认操作", NamedTextColor.YELLOW));
                return true;
            }
            if ("confirm".equals(args[1])) {
                confirmOperationManager.remove(ConfirmOperationEnum.CLEAR_BACKPACK, uuid);
                execClearBackpack(sender);
                return true;
            } else {
                confirmOperationManager.remove(ConfirmOperationEnum.CLEAR_BACKPACK, uuid);
                sender.sendMessage(Component.text("清空背包操作已取消", NamedTextColor.YELLOW));
                return true;
            }
        }
    }

    private void execClearBackpack(@NonNull CommandSender sender) {
        int clearItemSize = clearBackpack(sender);
        sender.sendMessage(Component.text("已清空背包，共移除", NamedTextColor.GREEN)
                .append(Component.text(clearItemSize, NamedTextColor.RED))
                .append(Component.text("件物品", NamedTextColor.GREEN)));
        logger.info("清空 [" + sender.getName() + "] 的背包，共移除 [" + clearItemSize + "] 件物品");
    }

    private static void sendClearConfirmTipMsg(@NonNull CommandSender sender) {
        sender.sendMessage(Component.text("输入 ", NamedTextColor.GOLD)
                .append(Component.text("/bp clear confirm", NamedTextColor.RED))
                .append(Component.text(" 确认", NamedTextColor.GOLD)));
        sender.sendMessage(Component.text("输入 ", NamedTextColor.GOLD)
                .append(Component.text("/bp clear cancel", NamedTextColor.RED))
                .append(Component.text(" 取消", NamedTextColor.GOLD)));
    }

    @Override
    public boolean onErrorCmd(@NonNull CommandSender sender) {
        sender.sendMessage(Component.text("[" + FloydPlugin.instance().getPluginName() + "] 命令使用方法", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("------------------------------------", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/bp open", NamedTextColor.GRAY)
                .append(Component.text(" -> 打开背包", NamedTextColor.AQUA)));
        sender.sendMessage(Component.text("/bp clear", NamedTextColor.GRAY)
                .append(Component.text(" -> 清空背包", NamedTextColor.AQUA)));
        sender.sendMessage(Component.text("------------------------------------", NamedTextColor.GRAY));
        return false;
    }

    private boolean checkIsPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    private void openBackpack(CommandSender sender) {
        Player player = (Player) sender;
        Backpack backpack = playerBackpackManager.getBackpack(player);
        player.openInventory(backpack.getInventory());
    }

    private int clearBackpack(@NotNull CommandSender sender) {
        Player player = (Player) sender;
        Backpack backpack = playerBackpackManager.getBackpack(player);
        Inventory inventory = backpack.getInventory();
        List<ItemStack> itemStackList = Arrays.stream(inventory.getStorageContents())
                .filter(Objects::nonNull).toList();
        inventory.clear();
        return itemStackList.size();
    }

}
