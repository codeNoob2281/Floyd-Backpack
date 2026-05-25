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
}
