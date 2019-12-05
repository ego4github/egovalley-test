package com.egovalley;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.egovalley.mapper")
@ComponentScan(basePackages = {"com.egovalley"})
@ServletComponentScan(basePackages = {"com.egovalley.web"})
@SuppressWarnings("all")
public class EgoValleyApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(EgoValleyApplication.class, args);
    }

    /**
     * 打包springboot项目
     * @param builder
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }

}
