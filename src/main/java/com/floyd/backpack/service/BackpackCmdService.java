package com.floyd.backpack.service;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

/**
 * @author floyd
 * @date 2026/3/28
 */
public interface BackpackCmdService {

    /**
     * 打开背包
     *
     * @param sender
     * @return
     */
    boolean onOpenBackpackCmd(CommandSender sender);

    /**
     * 清空背包
     *
     * @param sender
     * @param args
     * @return
     */
    boolean onClearBackpackCmd(@NonNull CommandSender sender, @NotNull String @NonNull [] args);

    /**
     * 重载配置
     *
     * @param sender
     * @param args
     * @return
     */
    boolean onReloadCmd(@NonNull CommandSender sender, @NotNull String @NonNull [] args);

    /**
     * 发送help消息
     *
     * @param sender
     * @param args
     * @return
     */
    boolean onShowHelpCmd(@NonNull CommandSender sender, @NotNull String @NonNull [] args);

    /**
     * 错误命令处理
     *
     * @param sender
     * @return
     */
    boolean onErrorCmd(@NonNull CommandSender sender, @NotNull String @NonNull [] args);
}
