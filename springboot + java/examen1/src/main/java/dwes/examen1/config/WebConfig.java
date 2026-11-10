package dwes.examen1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String ruta = "file:" + Paths.get("uploads/imagenes").toAbsolutePath() + "/";
        registry.addResourceHandler("/img/imagenes/**")
                .addResourceLocations(ruta, "classpath:/static/img/imagenes/");
    }
}