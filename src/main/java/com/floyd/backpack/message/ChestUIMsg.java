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
}
