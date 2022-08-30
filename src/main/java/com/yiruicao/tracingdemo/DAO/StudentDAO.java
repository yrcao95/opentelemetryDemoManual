package com.yiruicao.tracingdemo.DAO;

import com.yiruicao.tracingdemo.POJO.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface StudentDAO {
    void addStudentDAO(Student student);
    void addStudentDAOWithLatency(Student student) throws InterruptedException;
    Integer getAge(String name);
}



