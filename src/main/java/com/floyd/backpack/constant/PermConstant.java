package com.floyd.backpack.constant;

/**
 * 权限常量
 *
 * @author floyd
 * @date 2026/3/30
 */
public class PermConstant {

    private static final String PREFIX = "floydbackpack.";

    /**
     * 打开背包权限
     */
    public static final String OPEN_BACKPACK = PREFIX + "open";

    /**
     * 清空背包权限
     */
    public static final String CLEAR_BACKPACK = PREFIX + "clear";

    /**
     * 重载配置
     */
    public static final String RELOAD_CONFIG = PREFIX + "reload";

    /**
     * 展示帮助信息
     */
    public static final String SHOW_HELP = PREFIX + "help";

    /**
     * 展示版本信息
     */
    public static final String SHOW_VERSION = PREFIX + "version";

    /**
     * 背包升级权限
     */
    public static final String UPGRADE_BACKPACK = PREFIX + "upgrade";

    /**
     * 管理员权限（setlevel 等）
     */
    public static final String ADMIN_BACKPACK = PREFIX + "admin";

    /**
     * 立即触发全量保存权限
     */
    public static final String SAVE_ALL = PREFIX + "saveall";

}
