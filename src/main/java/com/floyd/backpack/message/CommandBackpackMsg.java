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

    public static final LocaleMessage RELOAD_START =
            LocaleMessage.of("command.backpack.reload.start", "§aReloading config...");

    public static final LocaleMessage RELOAD_SUCCESS =
            LocaleMessage.of("command.backpack.reload.success", "§aReload completed in {0}ms.");

    public static final LocaleMessage RELOAD_FAILURE =
            LocaleMessage.of("command.backpack.reload.failure", "§cReload failed, please check the console for errors!");

    public static final LocaleMessage VERSION_INFO =
            LocaleMessage.of("command.backpack.version.info", "§b[Floyd-Backpack] §aVersion: {0} §7| §aAuthor: {1}");
}
