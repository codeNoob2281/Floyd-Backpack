package com.floyd.backpack.injection;

import com.floyd.backpack.event.BackpackEventListener;
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
    BackpackEventListener backpackEventListener() {
        return new BackpackEventListener();
    }
}
