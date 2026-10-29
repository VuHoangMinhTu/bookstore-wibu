package nlu.com.app.configuration;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    private final String[] allowedOrigins;

    public CorsConfig(Environment environment) {

        List<String> origins = Binder.get(environment)
                .bind("app.cors.origins", Bindable.listOf(String.class))
                .orElseThrow(() ->
                        new IllegalStateException("Missing app.cors.origins")
                );

        this.allowedOrigins = origins.toArray(String[]::new);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns(allowedOrigins)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "WS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    public String[] getAllowedOrigins() {
        return allowedOrigins.clone();
    }
}
