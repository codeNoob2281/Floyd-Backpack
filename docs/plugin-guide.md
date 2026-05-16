# Floyd-Backpack 插件使用指南

## 概述

Floyd-Backpack 是一个 PaperMC 服务器背包插件，为每位玩家提供独立的 54 格存储空间。插件基于 Floyd-Core 框架开发，支持数据持久化、国际化、背包工具快捷打开和二次确认防误操作。

## 功能

- **54 格背包**: 每位玩家拥有独立的双箱容量存储空间，数据按 UUID 隔离
- **背包工具**: 玩家加入或重生时自动获取工具物品（末影之眼），右键点击即可打开背包；为防止误操作，工具物品无法存入背包
- **数据持久化**: 玩家退出时自动保存，服务器关闭时批量持久化到 JSON 文件
- **清空功能**: 一键清空背包，支持二次确认机制和可配置的超时时间
- **国际化**: 内置英文和简体中文，通过配置文件切换，背包标题随语言动态更新
- **热重载**: 支持不重启服务器重新加载配置
- **并发安全**: 背包数据使用 `ReentrantLock` 保证线程安全

## 命令

| 命令 | 别名 | 权限 | 描述 |
|------|------|------|------|
| `/backpack` | `/bp` | `floydbackpack.open` | 打开个人背包（默认操作） |
| `/backpack open` | - | `floydbackpack.open` | 打开个人背包 |
| `/backpack clear` | - | `floydbackpack.clear` | 清空背包（需二次确认） |
| `/backpack clear confirm` | - | `floydbackpack.clear` | 确认清空背包 |
| `/backpack clear cancel` | - | `floydbackpack.clear` | 取消清空 |
| `/backpack reload` | - | `floydbackpack.reload` | 重新加载配置 |
| `/backpack help` | - | `floydbackpack.help` | 显示帮助信息 |

> 控制台无法执行背包相关命令。

### 使用示例

```bash
# 打开背包
/bp

# 或完整命令
/backpack

# 清空背包（触发二次确认）
/bp clear

# 确认清空（不可逆操作）
/bp clear confirm

# 取消清空
/bp clear cancel

# 重新加载配置
/bp reload

# 查看帮助
/bp help
```

### 清空操作流程

1. 输入 `/bp clear`，系统提示确认信息并显示倒计时
2. 输入 `/bp clear confirm` 确认清空（不可逆）
3. 输入 `/bp clear cancel` 取消操作
4. 若超时未确认，操作自动失效

## 权限

| 权限节点 | 默认值 | 描述 |
|---------|--------|------|
| `floydbackpack.open` | `true`（所有人） | 允许玩家打开背包 |
| `floydbackpack.clear` | `op`（管理员） | 允许玩家清空背包 |
| `floydbackpack.reload` | `op`（管理员） | 允许重新加载配置 |
| `floydbackpack.help` | `true`（所有人） | 显示帮助信息 |

## 背包工具

玩家加入服务器或重生时，若物品栏中没有工具物品，会自动获得一个 **末影之眼** 作为背包工具。

- **获取方式**: 玩家加入/重生时自动发放
- **使用方式**: 手持工具物品右键点击，即可打开背包
- **锁定保护**: 工具物品**无法**通过点击、拖拽、Shift 移动或快捷键放入背包界面，防止误存
- **识别机制**: 基于 `PersistentDataContainer` 标签识别，支持多语言环境；同时保留基于物品类型和附魔的向后兼容识别
- **替代方式**: 也可使用 `/bp` 命令打开背包

## 配置

配置文件位于 `plugins/FloydBackpack/config.yml`。

### 完整配置项

```yaml
logging:
  file:
    enable: true       # 是否启用文件日志
  level: INFO          # 日志级别

command:
  backpack:
    clear:
      enable: true         # 是否启用背包清空命令
      need-confirm: true   # 清空前是否需要二次确认
      confirm-interval: 30000  # 二次确认超时时间（毫秒）

i18n:
  locale: en           # 语言设置，可选 en / zh_cn
```

### 配置项说明

| 路径 | 类型 | 默认值 | 描述 |
|------|------|--------|------|
| `logging.file.enable` | boolean | `true` | 是否将日志输出到文件 |
| `logging.level` | string | `INFO` | 日志输出级别 |
| `command.backpack.clear.enable` | boolean | `true` | 是否启用 `/bp clear` 命令 |
| `command.backpack.clear.need-confirm` | boolean | `true` | 清空背包前是否需要二次确认 |
| `command.backpack.clear.confirm-interval` | long | `30000` | 二次确认的超时时间（毫秒） |
| `i18n.locale` | string | `en` | 语言区域设置 |

### 热重载

修改配置文件后，无需重启服务器，执行以下命令即可生效：

```bash
/bp reload
```

## 国际化

插件内置两套语言文件：

| 语言 | 文件名 | 配置值 |
|------|--------|--------|
| 英文 | `language/en.yml` | `en` |
| 简体中文 | `language/zh_cn.yml` | `zh_cn` |

切换语言后执行 `/bp reload` 即可生效。背包界面的标题会随语言设置动态更新，已打开的老界面会在下次操作时重建为新语言标题，且物品内容不丢失。

### 自定义语言文件

如需添加未内置的语言（如日语），可以手动创建语言文件：

1. 在 `plugins/FloydBackpack/language/` 目录下新建 YAML 文件（若目录不存在则手动创建）
2. 参照内置语言文件的结构，翻译所有消息键。例如创建日语文件 `language/ja.yml`：

```yaml
command:
  backpack:
    console-not-allowed: "§cコンソールからこのコマンドを実行できません。"
    reload:
      start: "§a設定を再読み込み中..."
      success: "§a再読み込みが完了しました ({0}ms)。"
      failure: "§c再読み込みに失敗しました。コンソールを確認してください。"
    help:
      line1: "§b[Floyd-Backpack] §a§lヘルプ"
      line2: "§6§n>> コマンド"
      line3: "§3/bp open §e- §7バックパックを開く"
      line4: "§3/bp clear §e- §7バックパックを空にする"
      line5: "§3/bp reload §e- §7設定を再読み込み"
      line6: "§e詳細: §f§nhttps://github.com/codeNoob2281/Floyd-Backpack"
    clear:
      feature-disabled: "§cバックパックを空にする機能は無効です。"
      pending-operation-exists: "§e保留中の確認操作があります。続行してください。"
      confirm-delete: "§6バックパックを空にしてもよろしいですか？この操作は元に戻せません。"
      confirm-timeout: "§9残り {0} 秒以内に確認してください。"
      no-active-operation: "§e有効な確認操作がありません。"
      operation-cancelled: "§eバックパックを空にする操作はキャンセルされました。"
      cleared: "§aバックパックを空にしました。§c{0}§a 個のアイテムを削除。"
      tip-confirm: "§6確認するには §c/bp clear confirm §6を入力"
      tip-cancel: "§6キャンセルするには §c/bp clear cancel §6を入力"
      operation-expired: "§e[Floyd-Backpack] §6前回の操作は期限切れです。"
chest-ui:
  backpack-title: "§6{0}のバックパック"
backpack-tool:
  item-name: "§b[Floyd-Backpack]§6 右クリックでバックパックを開く"
  lore-line1: "§aこのアイテムを持って右クリックでバックパックを開く"
  lore-line2: "§aまたは /bp コマンドを使用"
```

3. 修改 `config.yml` 中的语言配置：

```yaml
i18n:
  locale: ja
```

4. 执行 `/bp reload` 使新语言生效

> 语言文件必须与内置文件保持相同的 YAML 结构和键名，否则插件可能无法正确加载。若插件无法自动读取外部语言文件，可将文件置于源码 `src/main/resources/language/` 目录后重新构建。

## 数据存储

玩家背包数据存储位置：

```
plugins/FloydBackpack/backpack/<玩家UUID>.json
```

- 每位玩家的背包数据以 UUID 命名独立保存
- **保存时机**: 玩家退出服务器时自动保存；服务器关闭时批量保存所有在线玩家数据
- **加载方式**: 首次打开背包时从磁盘加载，后续操作使用内存缓存
- **容错机制**: 若 JSON 数据损坏，自动备份为 `.bak.<时间戳>` 文件，并为玩家创建新的空背包
- **并发安全**: 每个背包实例持有 `ReentrantLock`，保证多线程环境下的数据安全

## 使用须知

1. **清空背包是不可逆操作**，开启二次确认的情况下务必确认后再执行
2. **控制台无法执行任何背包命令**
3. Floyd-Core 已作为 Maven 依赖内嵌在插件中，无需额外安装
4. 背包工具物品无法存入背包界面，这是为了防止误存导致无法打开背包
5. 切换语言后需执行 `/bp reload` 使新语言设置生效
6. 建议定期备份 `plugins/FloydBackpack/backpack/` 目录下的数据文件
