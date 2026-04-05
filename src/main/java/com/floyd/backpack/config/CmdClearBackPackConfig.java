package com.floyd.backpack.config;

import com.floyd.backpack.constant.Constants;
import lombok.Data;

/**
 *  背包清除命令配置
 *
 * @author floyd
 * @date 2026/3/29
 */
@Data
public class CmdClearBackPackConfig {

    /**
     * 是否启用
     */
    private boolean enable = true;

    /**
     * 是否需要二次确认
     */
    private boolean needConfirm = true;

    /**
     * 二次确认间隔时间，单位ms
     */
    private Long confirmInterval = Constants.DEFAULT_EXPIRE_INTERVAL;
}
