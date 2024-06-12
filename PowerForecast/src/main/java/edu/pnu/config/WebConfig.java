package edu.pnu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	   
	   @Override
	   public void addCorsMappings(CorsRegistry registry) {
		   registry.addMapping("/**")
           .allowedOriginPatterns("*") // 전체 도메인을 허용
           .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
           .allowedHeaders("*")
           .allowCredentials(true)
           .exposedHeaders("Authorization");
//		   registry.addMapping("/**")
//		   	   .allowedHeaders("*")
//			   .allowCredentials(true)
//			   .allowedHeaders(HttpHeaders.AUTHORIZATION)
//			   .allowedMethods(HttpMethod.GET.name(),
//							   HttpMethod.POST.name(),
//							   HttpMethod.PUT.name(),
//							   HttpMethod.DELETE.name())
//			   .allowedOrigins("http://localhost:3000", "http://127.0.0.1:3000")
//			   .exposedHeaders(HttpHeaders.AUTHORIZATION);
	   }

}
