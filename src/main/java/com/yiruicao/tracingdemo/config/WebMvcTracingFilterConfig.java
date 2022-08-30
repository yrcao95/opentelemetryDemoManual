package com.yiruicao.tracingdemo.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.spring.webmvc.SpringWebMvcTelemetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class WebMvcTracingFilterConfig {
    private OpenTelemetry openTelemetry;

    @Autowired
    public WebMvcTracingFilterConfig(OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
    }

    @Bean
    public Filter webMvcTracingFilter() {
        Filter filter = SpringWebMvcTelemetry.create(openTelemetry).newServletFilter();
        return filter;
    }
}
