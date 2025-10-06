package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    //ログイン中の社員を社員コードで検索する
    Employee findByCode(String code);
}