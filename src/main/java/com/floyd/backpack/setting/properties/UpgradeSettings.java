package com.floyd.backpack.setting.properties;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.StringProperty;
import com.floyd.core.settings.PluginSettingsHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

@Component
public class UpgradeSettings implements PluginSettingsHolder {

    @Comment("Whether backpack upgrade is enabled")
    public static @NotNull BooleanProperty ENABLED = newProperty("upgrade.enabled", true);

    @Comment("Maximum backpack level")
    public static @NotNull IntegerProperty MAX_LEVEL = newProperty("upgrade.max-level", 6);

    @Comment("Maximum slots")
    public static @NotNull IntegerProperty MAX_SLOTS = newProperty("upgrade.max-slots", 54);

    @Comment("Placeholder item material for locked slots")
    public static @NotNull StringProperty PLACEHOLDER_MATERIAL = newProperty("upgrade.placeholder-item.material", "GRAY_STAINED_GLASS_PANE");

    @Comment("Placeholder item display name")
    public static @NotNull StringProperty PLACEHOLDER_NAME = newProperty("upgrade.placeholder-item.name", "§7UNLOCKED");

}
