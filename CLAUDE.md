# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build

```bash
# Build JDK must match <java.version> in pom.xml (21)
mvn clean package
```

Output jar: `target/Floyd-Backpack-<version>.jar`. Maven shade plugin bundles all dependencies (Floyd-Core).

No tests currently exist in this project.

## Architecture

### Plugin lifecycle (Floyd-Core framework)

`FloydBackpackPlugin` extends `FloydPlugin` (from floyd-core), which wraps Paper's `JavaPlugin` with Spring DI.

- `initialize()` — called on plugin enable. Registers Bukkit event listeners, creates data dirs.
- `cleanup()` — called on plugin disable. Saves all backpack data to disk.
- `reload()` — custom reload: `SettingsReloadManager.reload()` → `ConfirmOperationManager.reload()` → logger reload.
- `getCustomConfigClasses()` — returns the `@Configuration` inner class that enables `@ComponentScan` over `com.floyd.backpack` and `com.floyd.core`.

### Command system (Floyd-Core `@SubCommandHandler`)

Commands are NOT registered in `plugin.yml` programmatically. Instead:

1. `plugin.yml` declares only the root command `/backpack` (alias `/bp`).
2. Handler classes use `@SubCommandHandler(rootCommand = "backpack")` at class level.
3. Individual methods use `@SubCommandMapping(commands = "...", permission = "...")`.
4. Floyd-Core's Spring component scan discovers handlers and auto-registers subcommand routing.
5. All handlers extend `AbstractCmdHandler` (holds `PlayerBackpackManager` reference). `BackpackSubCmdHandler` and `BackpackClearCmdHandler` are two separate handlers sharing the same root command namespace.
6. The fallback method (`isFallback = true`) catches unknown subcommands and shows help.

To add a new subcommand: create a class extending `AbstractCmdHandler`, annotate with `@SubCommandHandler(rootCommand = "backpack")`, add methods with `@SubCommandMapping`. No other registration needed — Spring scan picks it up automatically.

### Message / i18n system (Floyd-Core)

- Message classes implement `I18nMessageHolder` (marker interface) and define `public static final LocaleMessage` fields via `LocaleMessage.of(key, defaultEnglish)`.
- Floyd-Core's `I18nMessageProvider` scans all `I18nMessageHolder` implementations at startup and maps keys → locale-specific values from `language/<locale>.yml` files.
- `LocaleMessage.content(args...)` resolves the message for the current locale (set in `config.yml` → `i18n.locale`).

### Backpack entity & inventory lifecycle

`Backpack` implements `InventoryHolder`. Key behaviors:

- **Lazy inventory creation**: `getInventory()` calls `createNewInventoryIfTitleChange()`, which checks if the title matches the current locale. If mismatched (e.g., locale changed), it creates a new `Inventory`, copies old contents, and updates the title.
- **Size validation**: Constructor enforces `size > 0 && size % 9 == 0`. Currently always uses `DEFAULT_SIZE = 54`.
- **Concurrency**: `ReentrantLock lock` per backpack instance guards file I/O. The `inventory` field is `volatile`, with `synchronized` block for DCL in title-change check.

### Persistence (`PlayerBackpackManager`)

- In-memory: `ConcurrentHashMap<String, Backpack>` keyed by UUID.
- Disk: `plugins/FloydBackpack/backpack/<UUID>.json` — a flat JSON `{"<slotIndex>": "<base64>"}` serialized via `BukkitItemStackSerializer`.
- Load: `getBackpack()` → `computeIfAbsent` triggers `createBackpack()`, which reads JSON and populates inventory.
- Save: `flushBackpackToFile()` (on player quit) and `saveAllBackpack()` (on server shutdown). Both acquire the backpack's lock.
- Corruption handling: JSON parse errors trigger backup to `.bak.<timestamp>` and return an empty backpack.

### Config system (ConfigMe via Floyd-Core)

- ConfigMe properties are defined in `setting/properties/` as `public static final` fields (e.g., `BooleanProperty`, `LongProperty`).
- `PluginSettingsManager` (from Floyd-Core) manages the `PropertiesHolder` and YAML file.
- `SettingsReloadManager.reload()` wraps `pluginSettingsManager.reload()`.
- Simple string/int/bool properties use ConfigMe. For dynamic YAML maps (like future `levels:` sections), read the raw YAML directly from the config file.

### Event listener (`BackpackEventListener`)

Spring-managed bean (created in `EventRegistry`), registered manually in `FloydBackpackPlugin.initialize()` via `Bukkit.getPluginManager().registerEvents()`.

Handles: `PlayerJoin`/`PlayerRespawn` (give tool), `PlayerQuit` (flush data), `PlayerInteract` (right-click tool → open backpack), `InventoryClick`/`InventoryDrag` (prevent tool items from entering backpack).

### Tool item identification (`OpenBackpackTool`)

Uses `PersistentDataContainer` key `floydbackpack:backpack_tool` (Boolean) as primary identification. Falls back to matching `ENDER_EYE` + `UNBREAKING` 10 enchantment for legacy items without PDC tags.

### Dependency injection (`EventRegistry`)

`@Configuration` class with `@Bean` factory methods. Currently only declares `BackpackEventListener`. Command handlers are auto-detected by Floyd-Core's subcommand scanner — they do NOT need `@Bean` definitions here.

## Key patterns for new features

- **New subcommand**: extend `AbstractCmdHandler` → add `@SubCommandHandler` + `@SubCommandMapping` methods.
- **New messages**: create a class implementing `I18nMessageHolder`, add `LocaleMessage` static fields, add corresponding keys to `language/*.yml`.
- **New config properties**: add ConfigMe `Property` fields in `setting/properties/`, add corresponding YAML to `config.yml`.
- **New event handling**: add `@EventHandler` methods in `BackpackEventListener` (or create a new listener and register it in `EventRegistry` + `FloydBackpackPlugin.initialize()`).
- **PDC-based item tagging**: follow the `OpenBackpackTool` pattern — use `NamespacedKey("floydbackpack", "<key>")` + `PersistentDataType`.
