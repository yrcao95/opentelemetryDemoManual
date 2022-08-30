package com.yiruicao.tracingdemo.controller;

import com.yiruicao.tracingdemo.DAO.StudentDAO;
import com.yiruicao.tracingdemo.POJO.Student;
import com.yiruicao.tracingdemo.service.StudentService;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.With;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class StudentController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    StudentService studentService;
    StudentDAO studentDAO;
    WebClient webClient;
    OpenTelemetry openTelemetry;
    Tracer tracer;

    @Autowired
    public StudentController(StudentService studentService, StudentDAO studentDAO, WebClient webClient, OpenTelemetry openTelemetry, Tracer tracer) {
        this.studentService = studentService;
        this.studentDAO = studentDAO;
        this.webClient = webClient;
        this.openTelemetry = openTelemetry;
        this.tracer = tracer;
    }

    @PostMapping("/student/save")
    @WithSpan
    public String saveStudent(@RequestBody @SpanAttribute("student") Student student, @RequestHeader HttpHeaders httpHeaders) {
        LOGGER.info("Reached save student method...");
        Span span = tracer.spanBuilder("Controller.saveStudent").startSpan();
        span.setAttribute("name", student.getName());
        span.setAttribute("age", student.getAge());
        try
                (Scope scope = span.makeCurrent())
        {
            studentService.addStudentService(student);
            return "Success!";
        } finally {
            LOGGER.info("Save student exited...");
            span.end();
        }
    }

    @PostMapping("/student/saveAsync")
    public String saveStudentAsync(@RequestBody @SpanAttribute("student") Student student, @RequestHeader HttpHeaders httpHeaders) {
        LOGGER.info("Reached save student async method...");
        Span span = tracer.spanBuilder("Controller.saveStudentAsync").startSpan();
        span.setAttribute("name", student.getName());
        span.setAttribute("age", student.getAge());
        try
                (Scope scope = span.makeCurrent())
        {
            studentService.addTwoStudentService(student);
            return "Success!";
        } finally {
            LOGGER.info("Save student Async exited...");
            span.end();
        }
    }

    @GetMapping("/student/get")
    @WithSpan
    public String getStudentAgeDirectly(@RequestParam @SpanAttribute("name") String name, @RequestHeader HttpHeaders httpHeaders) {
        LOGGER.info("Reached get student age directly...");
//        Span span = tracer.spanBuilder("Controller.getStudentAgeDirectly").setParent(Context.current()).startSpan();
//        span.setAttribute("name", name);
        try
//                (Scope scope = span.makeCurrent())
        {
            return Integer.toString(studentDAO.getAge(name));
        } finally {
            LOGGER.info("get student age exited...");
//            span.end();
        }
    }

    @GetMapping("/student/get/indirectly")
    public String getStudentIndirectly(@RequestParam String name) throws MalformedURLException {
        LOGGER.info("Reached get student age indirectly...");
        Span span = tracer.spanBuilder("Controller.getStudentAgeIndirectly").setSpanKind(SpanKind.CLIENT).startSpan();
        span.setAttribute("name", name);
        try
                (Scope scope = span.makeCurrent())
        {
            WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = webClient.method(HttpMethod.GET);
            WebClient.RequestBodySpec bodySpec = uriSpec.uri(uriBuilder -> uriBuilder
                    .path("/student/get")
                    .queryParam("name", name)
                    .build());
            Mono<String> integerMono = bodySpec.retrieve().bodyToMono(String.class);
            String integer = integerMono.block();
            return integer;
        } finally {
            LOGGER.info("Leaving get student age indirectly...");
            span.end();
        }
    }
}
