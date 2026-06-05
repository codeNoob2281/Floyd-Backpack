---
name: minecraft-plugin-debug
description: Build, deploy and debug Minecraft plugins in local development server via IDEA.
---

# Minecraft Plugin Debug Workflow

## Overview

Complete workflow for building, deploying, and debugging Minecraft plugins in a local development server using IntelliJ IDEA. Covers Maven build, artifact deployment, and IDEA remote debugging configuration.

## When to Use

- Developing or testing Minecraft plugins locally
- Need to debug plugin code with breakpoints
- After making code changes and want to test on a running server

## Prerequisites

| # | Item | Notes |
|---|------|-------|
| 1 | JDK | Version matching project's `java.version` in pom.xml |
| 2 | IntelliJ IDEA | With Java/JDK support |
| 3 | Local Minecraft server | Purpur/Paper/Spigot server directory |
| 4 | Maven | For building the plugin |

## Configuration Detection

### Step 0 — Detect Project Settings

When this skill is invoked, first detect the project configuration from `pom.xml`:

```bash
# Read java.version from pom.xml
JAVA_VERSION=$(grep -oP '(?<=<java.version>)[^<]+' pom.xml)

# Get project artifact info
ARTIFACT_ID=$(grep -oP '(?<=<artifactId>)[^<]+' pom.xml | head -1)
VERSION=$(grep -oP '(?<=<version>)[^<]+' pom.xml | head -1)
```

### Step 1 — Find JDK Path

Search for JDK matching the required version:

```bash
# Windows: Search common JDK locations
# - C:\Program Files\Zulu\zulu-<version>
# - C:\Program Files\Java\jdk-<version>
# - C:\Program Files\Eclipse Adoptium\jdk-<version>
```

### Step 2 — Load Server Configuration

Check if configuration file exists at `.claude/project-env.json`:

```bash
if [ -f ".claude/project-env.json" ]; then
    # Read server path and jar from JSON
    MC_SERVER_PATH=$(cat .claude/project-env.json | grep -oP '"path"\s*:\s*"\K[^"]+')
    MC_SERVER_JAR=$(cat .claude/project-env.json | grep -oP '"jar"\s*:\s*"\K[^"]+')
else
    # Copy from example and ask user to configure
    cp .claude/project-env.json.example .claude/project-env.json
    echo "请配置 .claude/project-env.json 中的服务器路径"
fi
```

**If not configured**, tell the user to:

1. Copy `.claude/project-env.json.example` to `.claude/project-env.json`
2. Update the `server.path` and `server.jar` values

## Debug Workflow

### Step 1 — Build Plugin

```bash
cd $PROJECT_DIR
JAVA_HOME="/path/to/jdk-$JAVA_VERSION" mvn clean package
```

**Important**: JDK version must match `java.version` in pom.xml. Lombok compatibility:
| Lombok | Max JDK |
|--------|---------|
| 1.18.30 | 21 |
| 1.18.36 | 23 |
| 1.18.38+ | 25 |

### Step 2 — Deploy to Server

```bash
cp $PROJECT_DIR/target/${ARTIFACT_ID}-*.jar $MC_SERVER_PATH/plugins/
```

### Step 3 — Configure IDEA Run Configuration

Guide the user to configure IDEA:

1. Open `Run` → `Edit Configurations...`
2. Click `+` → Select `JAR Application`
3. Configure:

| Field | Value |
|-------|-------|
| **Name** | `Minecraft Dev Server` |
| **Path to JAR** | `$MC_SERVER_PATH/$MC_SERVER_JAR` |
| **Working directory** | `$MC_SERVER_PATH` |
| **JRE** | JDK $JAVA_VERSION |
| **Program arguments** | `nogui` |
| **VM options** | See below |

**VM Options**:
```
-Xms4G -Xmx6G -XX:+UseG1GC
```

4. Click `Apply` → `OK`

### Step 4 — Start Debug Session

1. Set breakpoints in plugin source code (click left gutter of editor)
2. Click **Debug button** (bug icon) or press `Shift + F9`
3. Wait for server to fully start
4. In Minecraft client, connect to `localhost:25565`
5. Trigger plugin functionality → debugger will pause at breakpoints

## Quick Reference

### One-liner Build & Deploy

```bash
cd "$PROJECT_DIR" && JAVA_HOME="/path/to/jdk" mvn clean package && cp target/*.jar "$MC_SERVER_PATH/plugins/"
```

### Common Debug Scenarios

| Scenario | Where to Set Breakpoint |
|----------|------------------------|
| Player opens backpack | `BackpackEventListener.onPlayerInteract()` |
| Command execution | `*CmdHandler` classes |
| Item save/load | `*Manager.save()` or `*Manager.load()` |
| Event handling | `*EventListener` classes |

## Troubleshooting

### Build Fails with "cannot find symbol" on Lombok methods

**Cause**: JDK version mismatch with Lombok version.

**Fix**: Use compatible JDK:
```bash
# Check current JDK
java -version

# Use specific JDK
JAVA_HOME="/path/to/compatible/jdk" mvn clean package
```

### Debugger Not Stopping at Breakpoints

1. Ensure breakpoint has a checkmark (not grayed out)
2. Verify the deployed JAR matches the source code (rebuild & redeploy)
3. Check IDEA is using the same JDK version as the server

### Server Won't Start in IDEA

1. Verify JAR path exists in run configuration
2. Verify working directory is set correctly
3. Check JRE version matches server requirements
