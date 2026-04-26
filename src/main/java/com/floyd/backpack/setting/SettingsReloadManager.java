package com.floyd.backpack.setting;

import com.floyd.core.settings.PluginSettingsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author floyd
 */
@Component
public class SettingsReloadManager {

    @Autowired
    PluginSettingsManager pluginSettingsManager;


    /**
     * 重新加载配置
     */
    public void reload() {
        pluginSettingsManager.reload();
    }
}
