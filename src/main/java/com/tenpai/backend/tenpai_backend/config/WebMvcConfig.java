package com.tenpai.backend.tenpai_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /avatars/** to local file system avatars/ directory
        String avatarPath = "file:" + System.getProperty("user.dir") + File.separator + "avatars" + File.separator;
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations(avatarPath);
    }
}
