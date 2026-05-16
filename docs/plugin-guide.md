# Floyd-Backpack Plugin

## Overview

Floyd-Backpack is a powerful Minecraft plugin designed for PaperMC servers that provides players with additional storage management capabilities. This plugin allows each player to have their own personal 54-slot backpack (equivalent to double chest capacity) for storing items securely. Built on the Floyd-Core framework, it ensures high performance and reliable data persistence.

## Features

- 🎒 **54-Slot Backpack**: Provides ample storage space for players
- 💾 **Data Persistence**: Automatically saves player backpack data
- 🧹 **Clear Function**: One-click backpack clearing with confirmation system
- 🎨 **Custom Interface**: Personalized backpack interface for each player
- ⚡ **High Performance**: Developed using Floyd-Core framework for optimal efficiency

## Commands

| Command | Alias | Permission | Description |
|---------|-------|------------|-------------|
| `/backpack` | `/bp` | None | Open personal backpack |
| `/bp open` | - | None | Open backpack (default subcommand) |
| `/bp clear` | - | `floydbackpack.clear` | Clear backpack (requires confirmation) |

### Command Examples

```bash
# Open backpack
/bp

# Or use full command
/backpack

# Clear backpack (triggers confirmation)
/bp clear

# Confirm clearing (irreversible action)
/bp clear confirm

# Cancel clearing
/bp clear cancel
```

## Permissions

| Permission | Default | Description |
|-----------|---------|-------------|
| `floydbackpack.open` | true | Allows players to open their backpack |
| `floydbackpack.clear` | op | Allows players to clear their backpack |
| `floydbackpack.reload` | op | Allows reloading configuration |
| `floydbackpack.help` | true | Displays help information |

## Configuration

Configuration file location: `plugins/FloydBackpack/config.yml`

```yaml
logging:
  file:
    enable: false  # Enable file logging
  level: INFO
command:
  backpack:
    clear:
      enable: true # Enable backpack clearing operation
      need-confirm: true # Require secondary confirmation
      confirm-interval: 30000 # Confirmation interval in milliseconds
```

## Data Storage

Player backpack data is stored in:
```
plugins/FloydBackpack/backpack/
```

Each player's backpack data is saved individually with their UUID as the filename, ensuring persistent storage across server restarts.

## Usage Instructions

1. **Open Backpack**: Type `/bp` in-game to open your personal backpack
2. **Store Items**: Drag items into the backpack interface to store them
3. **Retrieve Items**: Take items from the backpack interface to your inventory
4. **Clear Backpack**: Use `/bp clear` and confirm to empty all items

## Important Notes

- Clearing a backpack is an **irreversible operation** - use with caution
- Backpack data is automatically saved when the server shuts down
- Ensure Floyd-Core dependency plugin is installed
- Console cannot execute backpack-related commands