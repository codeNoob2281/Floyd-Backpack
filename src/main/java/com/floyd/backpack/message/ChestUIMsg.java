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
            LocaleMessage.of("chest-ui.backpack-title", "{0}''s backpack");

    public static final LocaleMessage PLACEHOLDER_LOCKED_SLOT_NAME =
            LocaleMessage.of("chest-ui.placeholder.locked-slot-name", "§7Unlocked");

    public static final LocaleMessage PLACEHOLDER_NEXT_LEVEL_SLOT_NAME =
            LocaleMessage.of("chest-ui.placeholder.next-level-slot-name", "§aNext Level Unlock");

    public static final LocaleMessage PLACEHOLDER_NEXT_LEVEL_SLOT_LORE =
            LocaleMessage.of("chest-ui.placeholder.next-level-slot-lore", "§eShift+Left-click to upgrade");

    public static final LocaleMessage UPGRADE_CONFIRM_TITLE =
            LocaleMessage.of("chest-ui.upgrade-confirm.title", "§6Upgrade Confirmation");

    public static final LocaleMessage UPGRADE_CONFIRM_CURRENT_LV =
            LocaleMessage.of("chest-ui.upgrade-confirm.current-lv", "§eCurrent: Lv{0}");

    public static final LocaleMessage UPGRADE_CONFIRM_CURRENT_LV_LORE =
            LocaleMessage.of("chest-ui.upgrade-confirm.current-lv-lore", "§7{0} slots");

    public static final LocaleMessage UPGRADE_CONFIRM_ARROW =
            LocaleMessage.of("chest-ui.upgrade-confirm.arrow", "§7→");

    public static final LocaleMessage UPGRADE_CONFIRM_NEXT_LV =
            LocaleMessage.of("chest-ui.upgrade-confirm.next-lv", "§aNext: Lv{0}");

    public static final LocaleMessage UPGRADE_CONFIRM_NEXT_LV_LORE =
            LocaleMessage.of("chest-ui.upgrade-confirm.next-lv-lore", "§7{0} slots");

    public static final LocaleMessage UPGRADE_CONFIRM_COST_TITLE =
            LocaleMessage.of("chest-ui.upgrade-confirm.cost-title", "§eCost");

    public static final LocaleMessage UPGRADE_CONFIRM_COST_PLACEHOLDER =
            LocaleMessage.of("chest-ui.upgrade-confirm.cost-placeholder", "§7Coming soon...");

    public static final LocaleMessage UPGRADE_CONFIRM_CONFIRM =
            LocaleMessage.of("chest-ui.upgrade-confirm.confirm", "§a✔ Confirm");

    public static final LocaleMessage UPGRADE_CONFIRM_CANCEL =
            LocaleMessage.of("chest-ui.upgrade-confirm.cancel", "§c✘ Cancel");

    public static final LocaleMessage UPGRADE_CONFIRM_SUCCESS =
            LocaleMessage.of("chest-ui.upgrade-confirm.success", "§aBackpack upgraded to Lv{0}! Capacity: {1} slots.");
}
