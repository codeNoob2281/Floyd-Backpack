package com.floyd.backpack.ui;

import com.floyd.backpack.entity.Backpack;
import com.floyd.backpack.message.ChestUIMsg;
import com.floyd.backpack.service.PlayerBackpackManager;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BackpackUpgradeConfirmGui implements InventoryHolder {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    public static final NamespacedKey CONFIRM_KEY = new NamespacedKey("floydbackpack", "upgrade_confirm");
    public static final NamespacedKey CANCEL_KEY = new NamespacedKey("floydbackpack", "upgrade_cancel");

    private static final int GUI_SIZE = 27;

    private static final int SLOT_CURRENT_LEVEL = 12;
    private static final int SLOT_ARROW = 13;
    private static final int SLOT_NEXT_LEVEL = 14;
    private static final int SLOT_COST_INFO = 4;
    private static final int SLOT_CANCEL = 20;
    private static final int SLOT_CONFIRM = 24;

    private final Inventory inventory;
    private final Player player;
    private final Backpack backpack;
    private final int currentLevel;
    private final int nextLevel;
    private final int nextUsableSlots;
    private final int oldUsableSlots;

    public BackpackUpgradeConfirmGui(Player player, Backpack backpack,
                                     int currentLevel, int nextLevel,
                                     int oldUsableSlots, int nextUsableSlots) {
        this.player = player;
        this.backpack = backpack;
        this.currentLevel = currentLevel;
        this.nextLevel = nextLevel;
        this.oldUsableSlots = oldUsableSlots;
        this.nextUsableSlots = nextUsableSlots;

        String title = ChestUIMsg.UPGRADE_CONFIRM_TITLE.content();
        this.inventory = Bukkit.createInventory(this, GUI_SIZE, title);

        initItems();
    }

    public void open() {
        player.openInventory(inventory);
    }

    public void onConfirm(PlayerBackpackManager manager) {
        if (player.getOpenInventory().getTopInventory() == this.inventory) {
            player.closeInventory();
        }
        if (backpack.getLevel() != currentLevel) {
            return;
        }
        manager.setBackpackLevel(backpack, nextLevel, nextUsableSlots);
        player.openInventory(backpack.getInventory());

        player.sendMessage(ChestUIMsg.UPGRADE_CONFIRM_SUCCESS.content(nextLevel, nextUsableSlots));
        player.playSound(Sound.sound()
                .type(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP)
                .build());
    }

    public void onCancel() {
        player.closeInventory();
    }

    private void initItems() {
        ItemStack filler = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.displayName(Component.empty());
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < GUI_SIZE; i++) {
            inventory.setItem(i, filler);
        }

        setInfoItem(SLOT_CURRENT_LEVEL, Material.BOOK,
                ChestUIMsg.UPGRADE_CONFIRM_CURRENT_LV.content(currentLevel),
                ChestUIMsg.UPGRADE_CONFIRM_CURRENT_LV_LORE.content(oldUsableSlots));

        setInfoItem(SLOT_ARROW, Material.ARROW,
                ChestUIMsg.UPGRADE_CONFIRM_ARROW.content());

        setInfoItem(SLOT_NEXT_LEVEL, Material.ENCHANTED_BOOK,
                ChestUIMsg.UPGRADE_CONFIRM_NEXT_LV.content(nextLevel),
                ChestUIMsg.UPGRADE_CONFIRM_NEXT_LV_LORE.content(nextUsableSlots));

        setInfoItem(SLOT_COST_INFO, Material.EMERALD,
                ChestUIMsg.UPGRADE_CONFIRM_COST_TITLE.content(),
                List.of(ChestUIMsg.UPGRADE_CONFIRM_COST_PLACEHOLDER.content()));

        setButtonItem(SLOT_CANCEL, Material.RED_WOOL,
                ChestUIMsg.UPGRADE_CONFIRM_CANCEL.content(),
                CANCEL_KEY);

        setButtonItem(SLOT_CONFIRM, Material.LIME_WOOL,
                ChestUIMsg.UPGRADE_CONFIRM_CONFIRM.content(),
                CONFIRM_KEY);
    }

    private void setInfoItem(int slot, Material material, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(LEGACY_SERIALIZER.deserialize(displayName)
                .decoration(TextDecoration.ITALIC, false));
        if (lore != null && !lore.isEmpty()) {
            meta.lore(lore.stream()
                    .map(line -> (Component) LEGACY_SERIALIZER.deserialize(line)
                            .decoration(TextDecoration.ITALIC, false))
                    .toList());
        }
        item.setItemMeta(meta);
        inventory.setItem(slot, item);
    }

    private void setInfoItem(int slot, Material material, String displayName, String loreLine) {
        setInfoItem(slot, material, displayName, List.of(loreLine));
    }

    private void setInfoItem(int slot, Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(LEGACY_SERIALIZER.deserialize(displayName)
                .decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(meta);
        inventory.setItem(slot, item);
    }

    private void setButtonItem(int slot, Material material, String displayName, NamespacedKey key) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(LEGACY_SERIALIZER.deserialize(displayName)
                .decoration(TextDecoration.ITALIC, false));
        meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        inventory.setItem(slot, item);
    }

    public static boolean isConfirmButton(ItemStack item) {
        return hasKey(item, CONFIRM_KEY);
    }

    public static boolean isCancelButton(ItemStack item) {
        return hasKey(item, CANCEL_KEY);
    }

    private static boolean hasKey(ItemStack item, NamespacedKey key) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
