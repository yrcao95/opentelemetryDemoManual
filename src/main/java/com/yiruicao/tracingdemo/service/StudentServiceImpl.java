package com.yiruicao.tracingdemo.service;

import com.yiruicao.tracingdemo.DAO.StudentDAO;
import com.yiruicao.tracingdemo.POJO.Student;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class StudentServiceImpl implements StudentService{
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    StudentDAO studentDAO;
    Tracer tracer;
    ExecutorService executorService;

    @Autowired
    public StudentServiceImpl(StudentDAO studentDAO, Tracer tracer, ExecutorService executorService) {
        this.studentDAO = studentDAO;
        this.tracer = tracer;
        this.executorService = executorService;
    }

    @Override
    @WithSpan
    public void addStudentService(@SpanAttribute("student") Student student) {
//        Span span = tracer.spanBuilder("StudentService.addStudentService").startSpan();
//        span.setAttribute("name", student.getName());
//        span.setAttribute("age", student.getAge());
        LOGGER.info("Entering addStudentService...");
        try
//                (Scope scope = span.makeCurrent())
        {
            studentDAO.addStudentDAO(student);
        } finally {
            LOGGER.info("Exiting addStudentService...");
//            span.end();
        }
    }

    @Override
    @WithSpan
    public void addTwoStudentService(@SpanAttribute("student") Student student) {
//        Span span = tracer.spanBuilder("StudentService.addTwoStudentService").startSpan();
//        span.setAttribute("name", student.getName());
//        span.setAttribute("age", student.getAge());
        try
//                (Scope scope = span.makeCurrent())
        {
//            SpanContext spanContext = span.getSpanContext();
            CompletableFuture<Void> f1 = getWithLatencyFuture(student);
            CompletableFuture<Void> f2 = getWithoutLatencyFuture(student);
            f1.runAfterBoth(f2, () -> LOGGER.info("Both tasks finished..."));
        } finally {
            LOGGER.info("addTwoStudentService exited...");
//            span.end();
        }
    }

    @WithSpan
    private CompletableFuture<Void> getWithLatencyFuture(@SpanAttribute("student") Student student) {
        return CompletableFuture.runAsync(() -> {
            try {
                studentDAO.addStudentDAOWithLatency(student);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, Context.taskWrapping(executorService));
    }

    @WithSpan
    private CompletableFuture<Void> getWithoutLatencyFuture(@SpanAttribute("student") Student student) {
        return CompletableFuture.runAsync(() -> {
            try {
                studentDAO.addStudentDAO(student);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Context.taskWrapping(executorService));
    }
}
