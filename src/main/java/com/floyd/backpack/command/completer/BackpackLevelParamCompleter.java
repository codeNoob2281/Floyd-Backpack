package com.floyd.backpack.command.completer;

import com.floyd.backpack.setting.properties.UpgradeSettings;
import com.floyd.core.command.param.ParameterCompleter;
import com.floyd.core.settings.PluginSettingsManager;
import org.bukkit.command.CommandSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author floyd
 */
@Component(NamedParameterCompleter.BACKPACK_LEVEL)
public class BackpackLevelParamCompleter implements ParameterCompleter {

    @Autowired
    private PluginSettingsManager pluginSettingsManager;

    @Override
    public List<String> complete(CommandSender commandSender, String partial) {
        Integer maxLevel = pluginSettingsManager.getProperty(UpgradeSettings.MAX_LEVEL);
        // 1 => maxLevel
        return IntStream.rangeClosed(1, maxLevel)
                .mapToObj(String::valueOf)
                .filter(s -> s.startsWith(partial))
                .collect(Collectors.toList());
    }
}
