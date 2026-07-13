# 权限节点规范

## 命名规则

- **全小写**，单词间用 `.` 分隔
- 格式：`floydbackpack.<功能>`（前缀固定）
- 例如：`floydbackpack.open`、`floydbackpack.saveall`

## 注册位置（必须同时在三处定义）

### 1. `src/main/resources/plugin.yml` — Bukkit 权限声明

```yaml
permissions:
  floydbackpack.<name>:
    description: "中文描述，说明用途"
    default: op   # true=所有玩家 | op=仅管理员 | false=无人默认拥有
```

> 这是 Bukkit 插件系统的权限来源。缺失会导致权限检查失效。

### 2. `src/main/java/com/floyd/backpack/constant/PermConstant.java` — 常量定义

```java
public static final String XXX = PREFIX + "<name>";
```

> 供子命令 `@SubCommandMapping(permission = ...)` 和代码中的权限点引用。

### 3. `src/main/java/com/floyd/backpack/message/CommandBackpackMsg.java`（如需要）—— 无权限提示文案

```java
public static final LocaleMessage XXX_NO_PERMISSION =
    LocaleMessage.of("command.backpack.xxx.no-permission", "§c无 xxx 权限。");
```

## 新增子命令权限的步骤

1. 在 `plugin.yml` 的 `permissions:` 下追加节点（name / description / default）
2. 在 `PermConstant.java` 添加 `public static final String` 常量
3. 若该权限非公开（default 非 true），在 `CommandBackpackMsg.java` 添加无权限提示文案并在 handler 中校验发送
4. 在对应 `*CmdHandler` 的 `@SubCommandMapping(permission = PermConstant.XXX)` 中引用

## 反例

```yaml
# ❌ 驼峰命名 —— Bukkit 会 normalize 为全小写，与代码常量不匹配
floydbackpack.saveAll:
```

```java
// ❌ 与 plugin.yml 中全小写节点不一致
public static final String SAVE_ALL = PREFIX + "saveAll";
```
