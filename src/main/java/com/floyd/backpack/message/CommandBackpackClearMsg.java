package com.floyd.backpack.message;

import com.floyd.core.i18n.LocaleMessage;

/**
 * @author floyd
 */
public class CommandBackpackClearMsg {

    public static final LocaleMessage FEATURE_DISABLED =
            LocaleMessage.of("command.backpack.clear.feature-disabled", "§cClear backpack feature is not enabled.");

    public static final LocaleMessage PENDING_OPERATION_EXISTS =
            LocaleMessage.of("command.backpack.clear.pending-operation-exists", "§eA pending confirmation operation already exists, please proceed.");

    public static final LocaleMessage CONFIRM_DELETE =
            LocaleMessage.of("command.backpack.clear.confirm-delete", "§6Are you sure you want to clear your backpack? This operation is irreversible!");

    public static final LocaleMessage CONFIRM_TIMEOUT =
            LocaleMessage.of("command.backpack.clear.confirm-timeout", "§9You have {0}s to confirm this operation.");

    public static final LocaleMessage NO_ACTIVE_OPERATION =
            LocaleMessage.of("command.backpack.clear.no-active-operation", "§eNo active confirmation operation.");

    public static final LocaleMessage OPERATION_CANCELLED =
            LocaleMessage.of("command.backpack.clear.operation-cancelled", "§eClear backpack operation has been cancelled.");

    public static final LocaleMessage CLEARED =
            LocaleMessage.of("command.backpack.clear.cleared", "§aBackpack cleared, removed §c{0} §aitems.");

    public static final LocaleMessage TIP_CONFIRM =
            LocaleMessage.of("command.backpack.clear.tip-confirm", "§6Type §c/bp clear confirm §6to confirm.");

    public static final LocaleMessage TIP_CANCEL =
            LocaleMessage.of("command.backpack.clear.tip-cancel", "§6Type §c/bp clear cancel §6to cancel.");

    public static final LocaleMessage OPERATION_EXPIRED =
            LocaleMessage.of("command.backpack.clear.operation-expired", "§e[Floyd-Backpack] §6Previous operation has expired.");
}
