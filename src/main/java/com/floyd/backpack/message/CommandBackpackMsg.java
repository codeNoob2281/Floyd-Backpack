package com.floyd.backpack.message;

import com.floyd.core.i18n.I18nMessageHolder;
import com.floyd.core.i18n.LocaleMessage;
import org.springframework.stereotype.Component;

/**
 * @author floyd
 */
@Component
public class CommandBackpackMsg implements I18nMessageHolder {

    public static final LocaleMessage CONSOLE_NOT_ALLOWED =
            LocaleMessage.of("command.backpack.console-not-allowed", "§cCannot execute this command from console.");

    public static final LocaleMessage SAVE_ALL_START =
            LocaleMessage.of("command.backpack.saveall.start", "§aSaving all backpack data...");

    public static final LocaleMessage SAVE_ALL_SUCCESS =
            LocaleMessage.of("command.backpack.saveall.success", "§aAll backpack data saved, success: §c{0}§a, failed: §c{1}§a.");

    public static final LocaleMessage SAVE_ALL_NO_OP_PERMISSION =
            LocaleMessage.of("command.backpack.saveall.no-permission", "§cNo permission to save all backpack data.");

    public static final LocaleMessage RELOAD_START =
            LocaleMessage.of("command.backpack.reload.start", "§aReloading config...");

    public static final LocaleMessage RELOAD_SUCCESS =
            LocaleMessage.of("command.backpack.reload.success", "§aReload completed in {0}ms.");

    public static final LocaleMessage RELOAD_FAILURE =
            LocaleMessage.of("command.backpack.reload.failure", "§cReload failed, please check the console for errors!");

    public static final LocaleMessage VERSION_INFO =
            LocaleMessage.of("command.backpack.version.info", "§b[Floyd-Backpack] §aVersion: {0} §7| §aAuthor: {1}");
}
