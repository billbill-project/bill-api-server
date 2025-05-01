package site.billbill.apiserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import site.billbill.apiserver.config.interceptor.BillApiInterceptor;
import site.billbill.apiserver.config.interceptor.InternalAuthInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final BillApiInterceptor billApiInterceptor;
    private final InternalAuthInterceptor internalAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(internalAuthInterceptor)
                .addPathPatterns("/api/v1/push/chat");

        registry.addInterceptor(billApiInterceptor)
                .excludePathPatterns("/docs/**", "/swagger-ui/**", "/v3/api-docs/**", "/error", "/api/v1/auth/**","/v3/api-docs/**",
                        "/api/v1/images/user", "/api/v1/users/password", "/api/v1/push/chat")
                .addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .maxAge(3600L)
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
