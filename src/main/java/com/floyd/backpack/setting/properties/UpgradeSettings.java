package com.floyd.backpack.setting.properties;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.*;
import ch.jalu.configme.properties.types.PrimitivePropertyType;
import ch.jalu.configme.properties.types.PropertyType;
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
    public static @NotNull StringProperty PLACEHOLDER_NAME = newProperty("upgrade.placeholder-item.name", "§Unlocked");

    @Comment("Placeholder material for slots unlockable at next level")
    public static @NotNull StringProperty PLACEHOLDER_NEXT_LEVEL_MATERIAL = newProperty("upgrade.placeholder-item.next-level-material", "LIME_STAINED_GLASS_PANE");

    @Comment("Placeholder display name for slots unlockable at next level")
    public static @NotNull StringProperty PLACEHOLDER_NEXT_LEVEL_NAME = newProperty("upgrade.placeholder-item.next-level-name", "§aNext Level Unlock");

    @Comment("The number of backpack slots corresponding to each level, with index 0 representing level 1")
    public static @NotNull ListProperty<Integer> LEVELS = new ListProperty<>("upgrade.levels", PrimitivePropertyType.INTEGER, 9, 18, 27, 36, 45, 54);
}
