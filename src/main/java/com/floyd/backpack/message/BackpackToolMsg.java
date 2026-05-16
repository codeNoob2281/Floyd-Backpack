package com.floyd.backpack.message;

import com.floyd.core.i18n.I18nMessageHolder;
import com.floyd.core.i18n.LocaleMessage;
import org.springframework.stereotype.Component;

@Component
public class BackpackToolMsg implements I18nMessageHolder {

    public static final LocaleMessage ITEM_NAME =
            LocaleMessage.of("backpack-tool.item-name", "Right-click to open backpack");

    public static final LocaleMessage LORE_LINE1 =
            LocaleMessage.of("backpack-tool.lore-line1", "Hold this item and right-click to open backpack");

    public static final LocaleMessage LORE_LINE2 =
            LocaleMessage.of("backpack-tool.lore-line2", "Or use the /bp command to open");
}
