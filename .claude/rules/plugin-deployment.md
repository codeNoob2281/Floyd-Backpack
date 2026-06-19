# Plugin Deployment Rule

## Trigger

### User-initiated

When the user says any of the following after completing code changes:
- "完成开发" / "开发完成"
- "部署插件" / "部署到服务器"
- "测试插件" / "在服务器上测试"
- "打包并部署"

### Auto-trigger

After Claude completes any of the following tasks:
- Implementing a new feature
- Fixing a bug
- Refactoring code that affects plugin behavior

## Action

Invoke the `minecraft-plugin-debug` skill to build, deploy, and guide debug setup.

## Examples

```
用户: 代码改完了，帮我部署到测试服务器
Claude: [调用 minecraft-plugin-debug skill]
```

```
用户: 帮我修复背包升级的bug
Claude: [修复bug] → [自动调用 minecraft-plugin-debug skill]
```
