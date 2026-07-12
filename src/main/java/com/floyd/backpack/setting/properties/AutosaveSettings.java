package com.floyd.backpack.setting.properties;

import ch.jalu.configme.Comment;
import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.LongProperty;
import com.floyd.core.settings.PluginSettingsHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

@Component
public class AutosaveSettings implements PluginSettingsHolder {

    /**
     * 是否启用定时自动保存机制
     */
    @Comment("Enable periodic auto-save of backpack data (prevents data loss on server crash)")
    public static @NotNull BooleanProperty ENABLE = newProperty("autosave.enable", true);

    /**
     * 自动保存间隔，单位 ms（默认 5 分钟）
     */
    @Comment("Auto-save interval in milliseconds (default: 300000 = 5 minutes)")
    public static @NotNull LongProperty INTERVAL = newProperty("autosave.interval", 300000L);
}
