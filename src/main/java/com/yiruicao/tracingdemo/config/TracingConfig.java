package com.yiruicao.tracingdemo.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class TracingConfig {
    private static final String ENDPOINT = "http://localhost:14250";

    @Bean
    public JaegerGrpcSpanExporter createJaegerExporter() {
        JaegerGrpcSpanExporter jaegerExporter =
                JaegerGrpcSpanExporter.builder()
                        .setEndpoint(ENDPOINT)
                        .setTimeout(30, TimeUnit.SECONDS)
                        .build();
        return jaegerExporter;
    }

    @Bean
    public SdkTracerProvider createSdkTracerProvider() {
        Resource serviceNameResource =
                Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, "tracingDemoOpenTelemetry"));
        SdkTracerProvider tracerProvider =
                SdkTracerProvider.builder()
                        .addSpanProcessor(SimpleSpanProcessor.create(createJaegerExporter()))
                        .setResource(Resource.getDefault().merge(serviceNameResource))
                        .build();
        return tracerProvider;
    }

    @Bean
    public OpenTelemetry createOpenTelemetry() {
        TextMapPropagator textMapPropagator = W3CTraceContextPropagator.getInstance();
        OpenTelemetrySdk openTelemetry =
                OpenTelemetrySdk.builder()
                        .setTracerProvider(createSdkTracerProvider())
                        .setPropagators(ContextPropagators.create(textMapPropagator))
                        .build();
        return openTelemetry;
    }

    @Bean
    public Tracer createTracer() {
        return createOpenTelemetry().getTracer("tracingDemoScope");
    }
}
