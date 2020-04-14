package cn.edu.whu.irlab.smart_cite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/4/13 20:34
 * @desc 接收文件配置
 **/
@Configuration
public class MultiPartResolver {



//    @Bean
//    public CommonsMultipartResolver multipartResolver() {
//        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
//        resolver.setDefaultEncoding("UTF-8");
//        resolver.setMaxInMemorySize(-1);
//        return resolver;
//    }
}
