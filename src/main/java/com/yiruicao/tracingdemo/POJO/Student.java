package com.yiruicao.tracingdemo.POJO;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Student implements Serializable {
    private String name;
    private Integer age;

    public String toString() {
        return "Name: " + name + ", age: " + age;
    }
}
