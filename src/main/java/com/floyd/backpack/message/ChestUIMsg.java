package com.floyd.backpack.message;

import com.floyd.core.i18n.I18nMessageHolder;
import com.floyd.core.i18n.LocaleMessage;
import org.springframework.stereotype.Component;

/**
 *
 * @author floyd
 */
@Component
public class ChestUIMsg implements I18nMessageHolder {

    public static final LocaleMessage BACKPACK_TITLE =
            LocaleMessage.of("chest-ui.backpack-title", "个人仓库");

    public static final LocaleMessage PLACEHOLDER_LOCKED_SLOT_NAME =
            LocaleMessage.of("chest-ui.placeholder.locked-slot-name", "§7未解锁");

    public static final LocaleMessage PLACEHOLDER_NEXT_LEVEL_SLOT_NAME =
            LocaleMessage.of("chest-ui.placeholder.next-level-slot-name", "§a下一级解锁");

    public static final LocaleMessage PLACEHOLDER_NEXT_LEVEL_SLOT_LORE =
            LocaleMessage.of("chest-ui.placeholder.next-level-slot-lore", "§eShift+左键进行升级");

    public static final LocaleMessage UPGRADE_CONFIRM_TITLE =
            LocaleMessage.of("chest-ui.upgrade-confirm.title", "§6升级确认");

    public static final LocaleMessage UPGRADE_CONFIRM_CURRENT_LV =
            LocaleMessage.of("chest-ui.upgrade-confirm.current-lv", "§e当前: Lv{0}");

    public static final LocaleMessage UPGRADE_CONFIRM_CURRENT_LV_LORE =
            LocaleMessage.of("chest-ui.upgrade-confirm.current-lv-lore", "§7{0} 格");

    public static final LocaleMessage UPGRADE_CONFIRM_ARROW =
            LocaleMessage.of("chest-ui.upgrade-confirm.arrow", "§7→");

    public static final LocaleMessage UPGRADE_CONFIRM_NEXT_LV =
            LocaleMessage.of("chest-ui.upgrade-confirm.next-lv", "§a升级后: Lv{0}");

    public static final LocaleMessage UPGRADE_CONFIRM_NEXT_LV_LORE =
            LocaleMessage.of("chest-ui.upgrade-confirm.next-lv-lore", "§7{0} 格");

    public static final LocaleMessage UPGRADE_CONFIRM_COST_TITLE =
            LocaleMessage.of("chest-ui.upgrade-confirm.cost-title", "§e消耗");

    public static final LocaleMessage UPGRADE_CONFIRM_COST_VALUE =
            LocaleMessage.of("chest-ui.upgrade-confirm.cost-value", "§6{0} 金币");

    public static final LocaleMessage UPGRADE_CONFIRM_COST_PLACEHOLDER =
            LocaleMessage.of("chest-ui.upgrade-confirm.cost-placeholder", "§7暂未开放...");

    public static final LocaleMessage UPGRADE_CONFIRM_CONFIRM =
            LocaleMessage.of("chest-ui.upgrade-confirm.confirm", "§a✔ 确认升级");

    public static final LocaleMessage UPGRADE_CONFIRM_CANCEL =
            LocaleMessage.of("chest-ui.upgrade-confirm.cancel", "§c✘ 取消");

    public static final LocaleMessage UPGRADE_CONFIRM_SUCCESS =
            LocaleMessage.of("chest-ui.upgrade-confirm.success", "§a背包已升级至 Lv{0}！容量: {1} 格。");

    // ── 底栏导航（每页 45 存储格 + 9 导航格）──────────────────────────
    public static final LocaleMessage NAV_RETURN_NAME =
            LocaleMessage.of("chest-ui.nav.return-name", "§c返回");
    public static final LocaleMessage NAV_RETURN_LORE =
            LocaleMessage.of("chest-ui.nav.return-lore", "§7关闭个人仓库");

    public static final LocaleMessage NAV_DEPOSIT_NAME =
            LocaleMessage.of("chest-ui.nav.deposit-name", "§a全部存入");
    public static final LocaleMessage NAV_DEPOSIT_LORE1 =
            LocaleMessage.of("chest-ui.nav.deposit-lore1", "§7把背包里的物品存入仓库");
    public static final LocaleMessage NAV_DEPOSIT_LORE2 =
            LocaleMessage.of("chest-ui.nav.deposit-lore2", "§7(自动合并到同类堆叠)");

    public static final LocaleMessage NAV_PREV_NAME =
            LocaleMessage.of("chest-ui.nav.prev-name", "§e上一页");
    public static final LocaleMessage NAV_PREV_FIRST =
            LocaleMessage.of("chest-ui.nav.prev-first", "§7已是第一页");

    public static final LocaleMessage NAV_SORT_NAME =
            LocaleMessage.of("chest-ui.nav.sort-name", "§e整理");
    public static final LocaleMessage NAV_SORT_LORE =
            LocaleMessage.of("chest-ui.nav.sort-lore", "§7合并同类堆叠并紧凑排列");

    public static final LocaleMessage NAV_PAGE_NAME =
            LocaleMessage.of("chest-ui.nav.page-name", "§a个人仓库");
    public static final LocaleMessage NAV_PAGE_LINE =
            LocaleMessage.of("chest-ui.nav.page-line", "§e第 {0} / {1} 页");
    public static final LocaleMessage NAV_PAGE_CAPACITY =
            LocaleMessage.of("chest-ui.nav.page-capacity", "§7仓库容量: {0} 格");
    public static final LocaleMessage NAV_PAGE_USED =
            LocaleMessage.of("chest-ui.nav.page-used", "§7已使用: {0} 格");
    public static final LocaleMessage NAV_PAGE_LEVEL =
            LocaleMessage.of("chest-ui.nav.page-level", "§7当前等级: Lv{0}");

    public static final LocaleMessage NAV_WITHDRAW_NAME =
            LocaleMessage.of("chest-ui.nav.withdraw-name", "§6全部取出");
    public static final LocaleMessage NAV_WITHDRAW_LORE1 =
            LocaleMessage.of("chest-ui.nav.withdraw-lore1", "§7把仓库物品取出到背包");
    public static final LocaleMessage NAV_WITHDRAW_LORE2 =
            LocaleMessage.of("chest-ui.nav.withdraw-lore2", "§7背包放不下则掉落在地上");

    public static final LocaleMessage NAV_NEXT_NAME =
            LocaleMessage.of("chest-ui.nav.next-name", "§e下一页");
    public static final LocaleMessage NAV_NEXT_LAST =
            LocaleMessage.of("chest-ui.nav.next-last", "§7已是最后一页");

    public static final LocaleMessage NAV_HELP_NAME =
            LocaleMessage.of("chest-ui.nav.help-name", "§b操作说明");
    public static final LocaleMessage NAV_HELP_LORE =
            LocaleMessage.of("chest-ui.nav.help-lore", "§7点击查看操作提示");

    public static final LocaleMessage NAV_UPGRADE_NAME =
            LocaleMessage.of("chest-ui.nav.upgrade-name", "§a升级仓库");
    public static final LocaleMessage NAV_UPGRADE_LORE_CLICK =
            LocaleMessage.of("chest-ui.nav.upgrade-lore-click", "§7点击升级仓库容量");
    public static final LocaleMessage NAV_UPGRADE_NEXT_LEVEL =
            LocaleMessage.of("chest-ui.nav.upgrade-next-level", "§7下一级: §aLv{0} §7· §e{1} 格");
    public static final LocaleMessage NAV_UPGRADE_COST =
            LocaleMessage.of("chest-ui.nav.upgrade-cost", "§7费用: §6{0} 金币");
    public static final LocaleMessage NAV_UPGRADE_BALANCE_ENOUGH =
            LocaleMessage.of("chest-ui.nav.upgrade-balance-enough", "§a✔ 余额充足，可升级");
    public static final LocaleMessage NAV_UPGRADE_BALANCE_INSUFFICIENT =
            LocaleMessage.of("chest-ui.nav.upgrade-balance-insufficient", "§c✘ 余额不足 (当前 {0})");
    public static final LocaleMessage NAV_UPGRADE_BALANCE_UNKNOWN =
            LocaleMessage.of("chest-ui.nav.upgrade-balance-unknown", "§7当前余额未知");
    public static final LocaleMessage NAV_UPGRADE_MAX_LEVEL =
            LocaleMessage.of("chest-ui.nav.upgrade-max-level", "§7已达最高等级");

    // 底栏「操作说明」弹窗
    public static final LocaleMessage NAV_HELP_HEADER =
            LocaleMessage.of("chest-ui.nav.help-header", "§6§l个人仓库操作说明");
    public static final LocaleMessage NAV_HELP_DEPOSIT =
            LocaleMessage.of("chest-ui.nav.help-deposit", "§7• §a全部存入 §7: 把背包物品存入仓库(自动合并同类)");
    public static final LocaleMessage NAV_HELP_SORT =
            LocaleMessage.of("chest-ui.nav.help-sort", "§7• §e整理 §7: 合并同类堆叠并紧凑排列");
    public static final LocaleMessage NAV_HELP_WITHDRAW =
            LocaleMessage.of("chest-ui.nav.help-withdraw", "§7• §6全部取出 §7: 把仓库物品取出到背包");
    public static final LocaleMessage NAV_HELP_UPGRADE =
            LocaleMessage.of("chest-ui.nav.help-upgrade", "§7• §a升级仓库 §7: 花金币扩容(右下角)");
    public static final LocaleMessage NAV_HELP_RETURN =
            LocaleMessage.of("chest-ui.nav.help-return", "§7• §c返回 §7: 关闭仓库");
    public static final LocaleMessage NAV_HELP_CAPACITY =
            LocaleMessage.of("chest-ui.nav.help-capacity", "§e容量随等级提升, 当前 Lv{0}");
}
