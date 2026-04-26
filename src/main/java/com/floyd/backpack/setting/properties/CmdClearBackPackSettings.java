package com.floyd.backpack.setting.properties;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.LongProperty;
import com.floyd.backpack.constant.Constants;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static ch.jalu.configme.properties.PropertyInitializer.*;

/**
 * 背包清除命令配置
 *
 * @author floyd
 * @date 2026/3/29
 */
@Component
public class CmdClearBackPackSettings implements SettingsHolder {

    /**
     * 是否启用
     */
    public static @NotNull BooleanProperty ENABLE = newProperty("command.backpack.clear.enable", true);

    /**
     * 是否需要二次确认
     */
    public static @NotNull BooleanProperty NEED_CONFIRM = newProperty("command.backpack.clear.need-confirm", true);

    /**
     * 二次确认间隔时间，单位ms
     */
    public static @NotNull LongProperty CONFIRM_INTERVAL = newProperty("command.backpack.clear.confirm-interval", Constants.DEFAULT_EXPIRE_INTERVAL);
}
