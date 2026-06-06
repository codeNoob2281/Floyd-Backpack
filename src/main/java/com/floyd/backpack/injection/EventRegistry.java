package com.floyd.backpack.injection;

import com.floyd.backpack.command.BackpackSubCmdHandler;
import com.floyd.backpack.event.BackpackEventListener;
import com.floyd.backpack.service.PlayerBackpackManager;
import com.floyd.core.settings.PluginSettingsManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author floyd
 * @date 2026/4/4
 */
@Configuration
public class EventRegistry {

    @Bean
    BackpackEventListener backpackEventListener(PlayerBackpackManager playerBackpackManager,
                                                BackpackSubCmdHandler backpackSubCmdHandler,
                                                PluginSettingsManager pluginSettingsManager) {
        return new BackpackEventListener(playerBackpackManager, backpackSubCmdHandler, pluginSettingsManager);
    }
}
