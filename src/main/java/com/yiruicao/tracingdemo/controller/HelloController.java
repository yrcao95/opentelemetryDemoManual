package com.yiruicao.tracingdemo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class HelloController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public HelloController() {

    }


    @GetMapping("/hello")
    public String hello(HttpServletRequest request, @RequestHeader HttpHeaders httpHeaders) throws InterruptedException {
        try {
            Thread.sleep(1000);
            return "Hello";
        }  finally {
            LOGGER.info("Reached HelloController...");
        }
    }
}
