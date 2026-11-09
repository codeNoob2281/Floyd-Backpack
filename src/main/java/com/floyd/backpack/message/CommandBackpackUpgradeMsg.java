package com.floyd.backpack.message;

import com.floyd.core.i18n.I18nMessageHolder;
import com.floyd.core.i18n.LocaleMessage;
import org.springframework.stereotype.Component;

@Component
public class CommandBackpackUpgradeMsg implements I18nMessageHolder {

    public static final LocaleMessage UPGRADE_SUCCESS =
            LocaleMessage.of("command.backpack.upgrade.success", "§a背包已升级至 Lv{0}！容量: {1} 格。");

    public static final LocaleMessage UPGRADE_MAX_LEVEL =
            LocaleMessage.of("command.backpack.upgrade.max-level", "§c背包已达最大等级 (Lv{0})。");

    public static final LocaleMessage UPGRADE_NO_PERMISSION =
            LocaleMessage.of("command.backpack.upgrade.no-permission", "§c无背包升级权限。");

    public static final LocaleMessage UPGRADE_DISABLED =
            LocaleMessage.of("command.backpack.upgrade.disabled", "§c本服务器已禁用背包升级。");

    public static final LocaleMessage UPGRADE_ECONOMY_UNAVAILABLE =
            LocaleMessage.of("command.backpack.upgrade.economy-unavailable", "§c经济系统未就绪，无法升级，请联系管理员。");

    public static final LocaleMessage UPGRADE_INSUFFICIENT_FUNDS =
            LocaleMessage.of("command.backpack.upgrade.insufficient-funds", "§c余额不足，升级需要 {0}。");

    public static final LocaleMessage UPGRADE_DEDUCT_FAILED =
            LocaleMessage.of("command.backpack.upgrade.deduct-failed", "§c扣费失败，升级已取消。");

    public static final LocaleMessage UPGRADE_COST_DEDUCTED =
            LocaleMessage.of("command.backpack.upgrade.cost-deducted", "§e已扣除 {0}。");

    public static final LocaleMessage UPGRADE_STATUS =
            LocaleMessage.of("command.backpack.upgrade.status", "§6背包: Lv{0} | §b已用 {1}/{2} 格。");

    public static final LocaleMessage SETLEVEL_SUCCESS =
            LocaleMessage.of("command.backpack.setlevel.success", "§a已将 {0} 的背包设为 Lv{1} ({2} 格)。");

    public static final LocaleMessage SETLEVEL_SENDER_ONLY =
            LocaleMessage.of("command.backpack.setlevel.sender-only", "§c该命令只能由玩家执行。");

    public static final LocaleMessage SETLEVEL_PLAYER_NOT_FOUND =
            LocaleMessage.of("command.backpack.setlevel.player-not-found", "§c未找到玩家: {0}。");

    public static final LocaleMessage SETLEVEL_INVALID_LEVEL =
            LocaleMessage.of("command.backpack.setlevel.invalid-level", "§c无效等级。必须介于 1 和 {0} 之间。");

}
