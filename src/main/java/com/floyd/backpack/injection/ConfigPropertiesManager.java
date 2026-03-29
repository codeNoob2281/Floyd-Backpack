package com.floyd.backpack.injection;

import com.floyd.backpack.FloydBackpackPlugin;
import com.floyd.backpack.config.CmdClearBackPackConfig;
import com.floyd.backpack.constant.Constants;
import org.bukkit.configuration.file.FileConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author floyd
 * @date 2026/3/29
 */
@Configuration
public class ConfigPropertiesManager {

    @Bean
    public CmdClearBackPackConfig cmdClearBackPackConfig() {
        CmdClearBackPackConfig config = new CmdClearBackPackConfig();
        FileConfiguration configuration = FloydBackpackPlugin.instance().getConfig();
        config.setEnable(configuration.getBoolean("command.backpack.clear.enable", true));
        config.setNeedConfirm(configuration.getBoolean("command.backpack.clear.need-confirm", true));
        config.setConfirmInterval(configuration.getLong("command.backpack.clear.confirm-interval", Constants.DEFAULT_EXPIRE_INTERVAL));
        return config;
    }

}
