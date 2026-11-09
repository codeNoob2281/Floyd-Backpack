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
}
