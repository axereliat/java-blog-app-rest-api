package com.blogapp.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class BeanConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map config = new HashMap();
        config.put("cloud_name", "dr8ovbzd2");
        config.put("api_key", "562999287916593");
        config.put("api_secret", "B7oPT9o7Dk0W2E1fOe3e7PS2ZIY");

        return new Cloudinary(config);
    }
}
