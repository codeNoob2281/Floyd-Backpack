# Floyd-Backpack

![Java Version](https://img.shields.io/badge/Java-21-blue)
![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.11-green)
![Build Status](https://img.shields.io/badge/build-passing-brightgreen)

**Floyd-Backpack** 是一个基于 PaperMC 的 Minecraft 背包插件，为每位玩家提供独立的 54 格背包空间（双箱容量），支持数据持久化、多语言和背包工具物品快捷打开。

> 详细的插件使用、命令、权限和配置说明请参阅 [插件使用指南](docs/plugin-guide.md)。

## 特性

- **54 格背包** - 每位玩家拥有独立的双箱容量存储空间
- **背包工具** - 玩家加入/重生时自动获取工具物品，右键即可打开背包
- **数据持久化** - 玩家退出时自动保存，服务器关闭时批量持久化
- **清空功能** - 支持一键清空背包（带二次确认防误操作）
- **国际化** - 支持英文和简体中文，可动态切换
- **高性能** - 基于 Spring DI + Floyd-Core 框架，支持并发安全访问
- **自定义界面** - 标题随语言设置动态变化

## 依赖

- **Java**: 21 或更高版本
- **Minecraft**: 1.21+
- **服务端**: PaperMC 或兼容的服务端

## 安装

### 手动安装

1. 从 [Releases](https://github.com/codeNoob2281/Floyd-Backpack/releases) 下载最新版本
2. 将 jar 文件放入服务端的 `plugins` 目录
3. 重启服务器或使用 `/reload`

### 源码构建

```bash
# 克隆项目
git clone https://github.com/codeNoob2281/Floyd-Backpack.git

# 进入项目目录
cd Floyd-Backpack

# 使用 Maven 构建
mvn clean package
```

构建完成后，jar 文件位于 `target/` 目录。

## 项目结构

```
Floyd-Backpack/
├── src/main/
│   ├── java/com/floyd/backpack/
│   │   ├── command/           # 命令处理器
│   │   ├── constant/          # 常量和权限定义
│   │   ├── entity/            # 实体类（背包）
│   │   ├── enums/             # 枚举
│   │   ├── event/             # 事件监听
│   │   ├── injection/         # 依赖注入配置
│   │   ├── message/           # 国际化消息
│   │   ├── service/           # 业务逻辑层
│   │   ├── setting/           # 配置属性
│   │   └── tools/             # 工具物品
│   └── resources/
│       ├── config.yml         # 配置文件
│       ├── plugin.yml         # 插件描述
│       └── language/          # 语言文件
├── docs/
│   └── plugin-guide.md        # 插件使用指南
└── pom.xml
```

## 技术栈

- **语言**: Java 21
- **构建工具**: Maven
- **核心框架**: Floyd-Core（Maven 依赖，内嵌于插件中）
- **API**: PaperMC API 1.21.11
- **工具库**: Lombok, Adventure API

## 贡献

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交改动 (`git commit -m 'Add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 提交 Pull Request

## 许可证

本项目采用 GPL 3.0 开源许可证。查看 [LICENSE](LICENSE) 文件了解详情。

## 作者

- **floyd** - [codeNoob2281](https://github.com/codeNoob2281)

## 更新日志

### v1.0.3
- 新增国际化支持（英文/简体中文）
- 日志打印内容优化
- 子命令处理重构
- 修复物品拖拽时的工具物品拦截逻辑

### v1.0.2
- 新增背包工具物品，支持右键快捷打开背包
- 新增配置文件热重载命令
- 优化并发数据访问安全

### v1.0.0

- 初始版本发布
- 实现 54 格背包系统
- 实现背包数据持久化
- 添加背包清空功能（带二次确认）
- 集成 Adventure API 消息系统
- 基于 Floyd-Core 框架优化性能
