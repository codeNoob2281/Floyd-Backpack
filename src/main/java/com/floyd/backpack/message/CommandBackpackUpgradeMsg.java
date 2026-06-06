package com.floyd.backpack.message;

import com.floyd.core.i18n.I18nMessageHolder;
import com.floyd.core.i18n.LocaleMessage;
import org.springframework.stereotype.Component;

@Component
public class CommandBackpackUpgradeMsg implements I18nMessageHolder {

    public static final LocaleMessage UPGRADE_SUCCESS =
            LocaleMessage.of("command.backpack.upgrade.success", "§aBackpack upgraded to Lv{0}! Capacity: {1} slots.");

    public static final LocaleMessage UPGRADE_MAX_LEVEL =
            LocaleMessage.of("command.backpack.upgrade.max-level", "§cYour backpack is already at max level (Lv{0}).");

    public static final LocaleMessage UPGRADE_NO_PERMISSION =
            LocaleMessage.of("command.backpack.upgrade.no-permission", "§cNo permission to upgrade backpack.");

    public static final LocaleMessage UPGRADE_DISABLED =
            LocaleMessage.of("command.backpack.upgrade.disabled", "§cBackpack upgrade is disabled on this server.");

    public static final LocaleMessage UPGRADE_STATUS =
            LocaleMessage.of("command.backpack.upgrade.status", "§6Backpack: Lv{0} | §b{1}/{2} slots used.");

    public static final LocaleMessage SETLEVEL_SUCCESS =
            LocaleMessage.of("command.backpack.setlevel.success", "§aSet {0}''s backpack to Lv{1} ({2} slots).");

    public static final LocaleMessage SETLEVEL_SENDER_ONLY =
            LocaleMessage.of("command.backpack.setlevel.sender-only", "§cThis command can only be executed by a player.");

    public static final LocaleMessage SETLEVEL_PLAYER_NOT_FOUND =
            LocaleMessage.of("command.backpack.setlevel.player-not-found", "§cPlayer not found: {0}.");

    public static final LocaleMessage SETLEVEL_INVALID_LEVEL =
            LocaleMessage.of("command.backpack.setlevel.invalid-level", "§cInvalid level. Must be between 1 and {0}.");

}
