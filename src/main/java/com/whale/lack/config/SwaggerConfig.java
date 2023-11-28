package com.whale.lack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * 自定义swagger的配置
 * 访问路径：http://localhost:8080/api/doc.html#/home
 */
@Configuration
@EnableSwagger2WebMvc // Swagger的开关，表示已经启用Swagger
@Profile({"dev","test"}) //指定在哪个环境下加载此配置,上线环境不让暴露
public class SwaggerConfig {
    @Bean(value = "defaultApi2") //生成一个swagger的配置，框架扫描到这个配置，注入到swagger的对象中，就可以初始化一个文档
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //标注控制器的位置
                .apis(RequestHandlerSelectors.basePackage("com.whale.lack.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /*
     * api信息
     * @return
     */

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("lack-伙伴匹配")
                .contact(new Contact("whale", "www.xxx.com", "xxx@qq.com"))
                .description("这是用Swagger动态生成的用户中心接口文档")
                .termsOfServiceUrl("NO terms of service")
                .version("1.0")
                .build();
    }
}
