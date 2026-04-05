package com.floyd.backpack.injection;

import com.floyd.backpack.command.BackpackCmdExecutor;
import com.floyd.backpack.service.BackpackCmdService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author floyd
 * @date 2026/3/22
 */
@Configuration
public class CommandRegistry {

    @Bean
    BackpackCmdExecutor backpackCmdExecutor(BackpackCmdService backpackCmdService) {
        return new BackpackCmdExecutor(backpackCmdService);
    }
}
