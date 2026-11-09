package com.floyd.backpack.setting.properties;

import ch.jalu.configme.Comment;
import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.ListProperty;
import ch.jalu.configme.properties.StringProperty;
import ch.jalu.configme.properties.types.PrimitivePropertyType;
import com.floyd.core.settings.PluginSettingsHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

@Component
public class UpgradeSettings implements PluginSettingsHolder {

    @Comment("Whether backpack upgrade is enabled")
    public static @NotNull BooleanProperty ENABLED = newProperty("upgrade.enabled", true);

    @Comment("Maximum backpack level")
    public static @NotNull IntegerProperty MAX_LEVEL = newProperty("upgrade.max-level", 10);

    @Comment("Maximum slots (hard cap, must be >= largest level capacity)")
    public static @NotNull IntegerProperty MAX_SLOTS = newProperty("upgrade.max-slots", 1024);

    @Comment("Placeholder item material for locked slots")
    public static @NotNull StringProperty PLACEHOLDER_MATERIAL = newProperty("upgrade.placeholder-item.material", "GRAY_STAINED_GLASS_PANE");

    @Comment("Placeholder material for slots unlockable at next level")
    public static @NotNull StringProperty PLACEHOLDER_NEXT_LEVEL_MATERIAL = newProperty("upgrade.placeholder-item.next-level-material", "LIME_STAINED_GLASS_PANE");

    @Comment("The number of backpack slots corresponding to each level, with index 0 representing level 1")
    public static @NotNull ListProperty<Integer> LEVELS = new ListProperty<>("upgrade.levels", PrimitivePropertyType.INTEGER, 15, 25, 35, 53, 65, 73, 128, 156, 256, 516);

    @Comment("Cost (economy) to upgrade FROM level (index+1) TO level (index+2). Index 0 = cost to upgrade from L1 to L2, index 8 = cost L9 to L10. Length must be MAX_LEVEL - 1")
    public static @NotNull ListProperty<Long> UPGRADE_COSTS = new ListProperty<>("upgrade.costs", PrimitivePropertyType.LONG,
            50000L, 160000L, 460000L, 510000L, 580000L, 675000L, 712000L, 1840000L, 2920000L);
}
