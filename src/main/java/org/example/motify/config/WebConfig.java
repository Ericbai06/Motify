// package org.example.motify.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.MediaType;
// import org.springframework.http.converter.HttpMessageConverter;
// import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// import java.nio.charset.StandardCharsets;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;

// @Configuration
// public class WebConfig implements WebMvcConfigurer {

//     @Override
//     public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//         // 移除现有的JSON转换器
//         converters.removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);
        
//         // 添加我们自定义的JSON转换器
//         converters.add(0, customJackson2HttpMessageConverter());
//     }

//     @Bean
//     public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
//         MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        
//         List<MediaType> supportedMediaTypes = Arrays.asList(
//             MediaType.APPLICATION_JSON,
//             MediaType.APPLICATION_JSON_UTF8,
//             new MediaType("application", "json", StandardCharsets.UTF_8),
//             new MediaType("application", "*+json", StandardCharsets.UTF_8),
//             MediaType.valueOf("application/json;charset=UTF-8"),
//             MediaType.valueOf("application/*+json;charset=UTF-8")
//         );
        
//         converter.setSupportedMediaTypes(supportedMediaTypes);
//         return converter;
//     }
// }