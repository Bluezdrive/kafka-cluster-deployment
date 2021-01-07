package de.volkerfaas.kafka.deployment;

import de.volkerfaas.kafka.deployment.controller.model.resolver.SkipablePageRequestHandlerMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import java.util.List;

@EnableWebMvc
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final SortHandlerMethodArgumentResolver sortResolver;

    @Autowired
    public WebMvcConfiguration(SortHandlerMethodArgumentResolver sortResolver) {
        this.sortResolver = sortResolver;
    }

    @Bean
    public SkipablePageRequestHandlerMethodArgumentResolver pageRequestResolver() {
        return new SkipablePageRequestHandlerMethodArgumentResolver(sortResolver);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(pageRequestResolver());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/WEB-INF/resources/static/");
        registry.addResourceHandler("/*.json", "/*.ico", "/*.png").addResourceLocations("classpath:/WEB-INF/resources/");
        registry.addResourceHandler("/index.html").addResourceLocations("classpath:/WEB-INF/resources/index.html");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/index.html");
        registry.addViewController("/login.html").setViewName("forward:/index.html");
    }

    @Bean
    public ViewResolver viewResolver() {
        UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
        viewResolver.setViewClass(InternalResourceView.class);
        return viewResolver;
    }
}
