//package com.d202.assemble.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {
//
//    @Value("${resource.path}")
//    private String resourcePath;
//
//    @Value("${upload.path}")
//    private String uploadPath;
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler(uploadPath)
//                .addResourceLocations(resourcePath);
//    }
//}
