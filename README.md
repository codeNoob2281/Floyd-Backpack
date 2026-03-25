# Floyd-Backpack

![Java Version](https://img.shields.io/badge/Java-21-blue)
![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.11-green)
![Build Status](https://img.shields.io/badge/build-passing-brightgreen)

**Floyd-Backpack** 是一个基于 PaperMC 的 Minecraft 背包插件，为玩家提供额外的存储空间管理功能。

## ✨ 特性

- 🎒 **54 格背包** - 提供大型储物空间（双箱容量）
- 💾 **数据持久化** - 自动保存玩家背包数据
- 🧹 **清空功能** - 支持一键清空背包（带二次确认）
- 🎨 **自定义界面** - 显示玩家专属背包界面
- ⚡ **高性能** - 基于 Floyd-Core 框架开发

## 📋 依赖

- **Java**: 21 或更高版本
- **Minecraft**: 1.21+
- **服务端**: PaperMC 或兼容的服务端
- **前置插件**: 
  - Floyd-Core (必需)
  - Lombok (编译时)

## 🚀 安装

### 方式一：手动安装

1. 下载 `floyd-backpack-1.0.0-SNAPSHOT.jar` 文件
2. 将 jar 文件放入服务端的 `plugins` 目录
3. 重启服务器

### 方式二：源码构建

```bash
# 克隆项目
git clone https://github.com/codeNoob2281/Floyd-Backpack.git

# 进入项目目录
cd Floyd-Backpack

# 使用 Maven 构建
mvn clean package
```

构建完成后，jar 文件位于 `target/` 目录

## ⌨️ 命令

| 命令 | 别名 | 权限 | 描述 |
|------|------|------|------|
| `/backpack` | `/bp` | 无 | 打开个人背包 |
| `/bp open` | - | 无 | 打开背包（默认子命令） |
| `/bp clear` | - | 无 | 清空背包（需二次确认） |

### 命令示例

```bash
# 打开背包
/bp

# 或使用完整命令
/backpack

# 清空背包（会触发二次确认）
/bp clear

# 确认清空（不可逆操作）
/bp clear confirm

# 取消清空
/bp clear cancel
```

## ⚙️ 配置

配置文件位置：`plugins/FloydBackpack/config.yml`

```yaml
logging:
  file:
    enable: false  # 是否启用文件日志
```

## 📁 数据存储

玩家背包数据存储在：
```
plugins/FloydBackpack/backpack/
```

每个玩家的背包数据会以 UUID 命名单独保存，确保数据持久化。

## 🛠️ 开发

### 技术栈

- **语言**: Java 21
- **构建工具**: Maven
- **核心框架**: Floyd-Core
- **API**: PaperMC API 1.21.11
- **工具库**: Lombok, Adventure API

### 项目结构

```
Floyd-Backpack/
├── src/main/
│   ├── java/com/floyd/backpack/
│   │   ├── command/           # 命令处理器
│   │   ├── entity/            # 实体类（背包）
│   │   ├── event/             # 事件监听
│   │   ├── injection/         # 依赖注入
│   │   ├── service/           # 业务逻辑层
│   │   └── FloydBackpackPlugin.java  # 主类
│   └── resources/
│       ├── config.yml         # 配置文件
│       └── plugin.yml         # 插件描述
└── pom.xml                    # Maven 配置
```

### 编译环境

```xml
<java.version>21</java.version>
<maven.compiler.source>21</maven.compiler.source>
<maven.compiler.target>21</maven.compiler.target>
```

## 📝 使用说明

1. **打开背包**: 在游戏中输入 `/bp` 即可打开个人背包
2. **存储物品**: 将物品拖入背包界面即可存储
3. **取出物品**: 从背包界面取出物品到玩家物品栏
4. **清空背包**: 使用 `/bp clear` 并确认后清空所有物品

## ⚠️ 注意事项

- 清空背包是**不可逆操作**，请谨慎使用
- 背包数据会在服务器关闭时自动保存
- 确保已安装 Floyd-Core 前置插件
- 控制台无法执行背包相关命令

## 🐛 问题反馈

如遇到问题，请提交至：[GitHub Issues](https://github.com/codeNoob2281/Floyd-Backpack/issues)

## 📄 许可证

本项目采用 Apache License 2.0 开源许可证。查看 [LICENSE](LICENSE) 文件了解详情。

## 👨‍💻 作者

- **floyd** - [codeNoob2281](https://github.com/codeNoob2281)

## 🔄 更新日志

### v1.0.0-SNAPSHOT (2026-03-25)

- ✨ 初始版本发布
- 🎒 实现 54 格背包系统
- 💾 实现背包数据持久化
- 🧹 添加背包清空功能（带二次确认）
- 🎨 集成 Adventure API 消息系统
- ⚡ 基于 Floyd-Core 框架优化性能

---

**Enjoy coding!** 🎉
