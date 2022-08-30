package com.yiruicao.tracingdemo.DAO;

import com.yiruicao.tracingdemo.POJO.Student;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.UUID;

@Repository
public class StudentDAOImpl implements StudentDAO{
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    RedisTemplate<String, Object> redisTemplate;
    Tracer tracer;

    @Autowired
    public StudentDAOImpl(@Qualifier("redisTemplate") RedisTemplate redisTemplate, Tracer tracer) {
        this.redisTemplate = redisTemplate;
        this.tracer = tracer;
    }

    @Override
    @WithSpan
    public void addStudentDAO(@SpanAttribute("student") Student student) {
        LOGGER.info("Entering addStudentDAO...");
//        Span span = tracer.spanBuilder("StudentDAO.addStudent").startSpan();
//        span.setAttribute("name", student.getName());
//        span.setAttribute("age", student.getAge());
        try
//                (Scope scope = span.makeCurrent())
        {
            redisTemplate.opsForValue().set(student.getName(), Integer.toString(student.getAge()), Duration.ofMinutes(1));
        } finally {
            LOGGER.info("addStudentDAO exited...");
//            span.end();
        }
    }

    @Override
    @WithSpan
    public void addStudentDAOWithLatency(@SpanAttribute("student") Student student) throws InterruptedException {
        LOGGER.info("Entering addStudentDAOWithLatency...");
//        Span span = tracer.spanBuilder("StudentDAO.addStudentWithLatency").startSpan();
//        span.setAttribute("name", student.getName());
//        span.setAttribute("age", student.getAge());
        try
//                (Scope scope = span.makeCurrent())
        {
            Thread.sleep(3000);
            redisTemplate.opsForValue().set(student.getName() + "Latency", Integer.toString(student.getAge()), Duration.ofMinutes(1));
        } finally {
            LOGGER.info("addStudentDAOWithLatency exited...");
//            span.end();
        }
    }

    @Override
    public Integer getAge(@SpanAttribute String name) {
        LOGGER.info("Entering getAge...");
//        Span span = tracer.spanBuilder("StudentDAO.getAge").startSpan();
//        span.setAttribute("name", name);
        try
//                (Scope scope = span.makeCurrent())
        {
            Object ageObject = redisTemplate.opsForValue().get(name);
            return Integer.parseInt(ageObject.toString());
        } finally {
            LOGGER.info("Exiting getAge...");
//            span.end();
        }
    }
}
