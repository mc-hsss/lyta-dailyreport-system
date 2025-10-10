package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import java.time.LocalDate;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    // 特定の従業員に紐づいた日報情報を検索する
    List<Report> findByEmployee(Employee employee);

    // 特定の従業員の既存の日報の日付データを検索する
    Optional<Report> findByEmployeeAndReportDate(Employee employee, LocalDate reportDate);
    // 更新中の日報以外の表示中日報データについて、日付が被っているものがないか検索する
    Optional<Report> findByEmployeeAndReportDateAndIdNot(Employee employee, LocalDate reportDate, Integer id);

}