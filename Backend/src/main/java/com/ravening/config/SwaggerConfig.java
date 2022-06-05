package com.ravening.config;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@EnableSwagger2
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket postsApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("recipe-books-api")
                .apiInfo(apiInfo()).select().paths(postPaths()).build();
    }

    private Predicate<String> postPaths() {
        return or(regex("/api/recipes.*"), regex("/api/*"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Recipe List API")
                .description("API reference for managing your favourite recipes")
                .termsOfServiceUrl("https://github.com/ravening")
                .contact(new Contact("Rakesh", "", "rakeshv@apache.org"))
                .licenseUrl("Apache License").version("1.0").build();
    }

}
