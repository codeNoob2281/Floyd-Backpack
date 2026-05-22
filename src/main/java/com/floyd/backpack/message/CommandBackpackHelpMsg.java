package com.floyd.backpack.message;

import com.floyd.core.i18n.I18nMessageHolder;
import com.floyd.core.i18n.LocaleMessage;
import org.springframework.stereotype.Component;

/**
 * @author floyd
 */
@Component
public class CommandBackpackHelpMsg implements I18nMessageHolder {

    public static final LocaleMessage TITLE =
            LocaleMessage.of("command.backpack.help.title", "§b[Floyd-Backpack] §a§lHelp Info");

    public static final LocaleMessage COMMANDS_HEADER =
            LocaleMessage.of("command.backpack.help.commands-header", "§6§n>> Commands");

    public static final LocaleMessage CMD_OPEN_DESC =
            LocaleMessage.of("command.backpack.help.cmd-open", "§3/bp open §e- §7Open Backpack");

    public static final LocaleMessage CMD_CLEAR_DESC =
            LocaleMessage.of("command.backpack.help.cmd-clear", "§3/bp clear §e- §7Clear backpack");

    public static final LocaleMessage CMD_RELOAD_DESC =
            LocaleMessage.of("command.backpack.help.cmd-reload", "§3/bp reload §e- §7Reload config");

    public static final LocaleMessage CMD_VERSION_DESC =
            LocaleMessage.of("command.backpack.help.cmd-version", "§3/bp version §e- §7Show version info");

    public static final LocaleMessage MORE_INFO =
            LocaleMessage.of("command.backpack.help.more-info", "§eVisit §f§nhttps://github.com/codeNoob2281/Floyd-Backpack§e for more info.");
}
