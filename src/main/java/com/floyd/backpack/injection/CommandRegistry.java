package com.floyd.backpack.injection;

import com.floyd.backpack.command.BackpackCmdExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author floyd
 * @date 2026/3/22
 */
@Configuration
public class CommandRegistry {

    @Bean
    BackpackCmdExecutor backpackCmdExecutor() {
        return new BackpackCmdExecutor();
    }
}
