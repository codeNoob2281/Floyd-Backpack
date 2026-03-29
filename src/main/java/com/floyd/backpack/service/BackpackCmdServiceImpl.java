package com.floyd.backpack.service;

import com.floyd.backpack.entity.Backpack;
import com.floyd.core.FloydPlugin;
import com.floyd.core.permission.RequiredPermission;
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


    @Override
    @RequiredPermission(value = "floydbackpack.open", tipPermValue = true)
    public boolean onOpenBackpackCmd(CommandSender sender) {
        if (checkIsPlayer(sender)) {
            // 打开背包
            openBackpack(sender);
            sender.sendMessage(Component.text("已为您打开背包", NamedTextColor.GREEN));
            return true;
        }
        return false;
    }

    @Override
    @RequiredPermission(value = "floydbackpack.clear", tipPermValue = true)
    public boolean onClearBackpackCmd(@NonNull CommandSender sender, @NotNull String @NonNull [] args) {
        if (!checkIsPlayer(sender)) {
            return false;
        }
        if (args.length == 1) {
            //  二次确认清空背包
            sender.sendMessage(Component.text("是否确认删除背包？请注意这是一个不可逆的操作", NamedTextColor.GOLD));
            sender.sendMessage(Component.text("输入 ", NamedTextColor.GOLD)
                    .append(Component.text("/bp clear confirm", NamedTextColor.RED))
                    .append(Component.text(" 确认", NamedTextColor.GOLD)));
            sender.sendMessage(Component.text("输入 ", NamedTextColor.GOLD)
                    .append(Component.text("/bp clear cancel", NamedTextColor.RED))
                    .append(Component.text(" 取消", NamedTextColor.GOLD)));
            return true;
        } else {
            if ("confirm".equals(args[1])) {
                int clearItemSize = clearBackpack(sender);
                sender.sendMessage(Component.text("已清空背包，共移除", NamedTextColor.GREEN)
                        .append(Component.text(clearItemSize, NamedTextColor.RED))
                        .append(Component.text("件物品", NamedTextColor.GREEN)));
                FloydPlugin.logger().info("清空 [" + sender.getName() + "] 的背包，共移除 [" + clearItemSize + "] 件物品");
                return true;
            } else {
                sender.sendMessage(Component.text("清空背包操作已取消", NamedTextColor.YELLOW));
                return true;
            }
        }
    }

    @Override
    public boolean onErrorCmd(@NonNull CommandSender sender) {
        sender.sendMessage(Component.text("[" + FloydPlugin.instance().getPluginName() + "] 命令使用方法", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("------------------------------------", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/bp open",NamedTextColor.GRAY)
                .append(Component.text(" -> 打开背包",NamedTextColor.AQUA)));
        sender.sendMessage(Component.text("/bp clear",NamedTextColor.GRAY)
                .append(Component.text(" -> 清空背包",NamedTextColor.AQUA)));
        sender.sendMessage(Component.text("------------------------------------", NamedTextColor.GRAY));
        return false;
    }

    private boolean checkIsPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return true;
        }
        sender.sendMessage(Component.text("无法通过控制台执行此命令", NamedTextColor.RED));
        return false;
    }

    private void openBackpack(CommandSender sender) {
        Player player = (Player) sender;
        Backpack backpack = PlayerBackpackManager.getBackpack(player);
        player.openInventory(backpack.getInventory());
    }

    private int clearBackpack(@NotNull CommandSender sender) {
        Player player = (Player) sender;
        Backpack backpack = PlayerBackpackManager.getBackpack(player);
        Inventory inventory = backpack.getInventory();
        List<ItemStack> itemStackList = Arrays.stream(inventory.getStorageContents())
                .filter(Objects::nonNull).toList();
        inventory.clear();
        return itemStackList.size();
    }

}
