package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {

// 特定の従業員に紐づいた日報情報を検索する
    List<Report> findByEmployee(Employee employee);

}