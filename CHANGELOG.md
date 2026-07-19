# Changelog

## 1.1.0 (2026-07-19)

### Features
- 实现背包升级系统（等级配置、消耗计算、预览界面）(fbabbeb, cb31d8b)
- 背包升级改为 Shift+左键触发并显示消耗界面 (a217aa8)
- 添加升级确认界面，右键绿色占位符打开确认操作 (cd166ec)
- 将占位符名称迁移至 i18n 系统并支持运行时刷新 (badbe36)
- 引入 Geyser API 按玩家平台自适应升级点击方式 (516a54e)
- 添加定时自动保存机制解决崩溃丢数据问题 (d0a0a49, 446a1e5)
- 添加 saveall 子命令立即触发全量保存（默认 OP 权限）(970113a)
- 为 set-level 命令添加 Tab 补全支持 (ac0a96b)

### Bug Fixes
- 处理 PR #13 代码评审意见 (79fe04b)

### Refactors
- 优化导入和代码评审内容 (a3fd6da)

### Chores
- 更新 floyd-core 依赖版本至 1.0.6 (74cb850, b15dde9)
- 添加 Claude Code skill 和规则配置 (b947022)
- 完善 gitignore 配置 (6451da4, d050e07)

## 1.0.4 (2026-05-23)

### Features
- 兼容 Minecraft 1.20.6 版本 (b2186b7)
- 添加版本查询命令并优化帮助信息 (c52a21d)
- 移除背包工具物品描述中的斜体格式 (ad0bd00)

### Refactors
- 优化路径构建和插件元数据访问 (fc78768)

### Documentation
- 更新 Minecraft 版本兼容性信息 (870c310)
- 添加插件使用指南英文文档 (279ef7c)

### Chores
- 更新 floyd-core 依赖版本并添加中文繁体翻译 (52babd6)
- 更新版本号为 1.0.4-SNAPSHOT (0f8e935)
