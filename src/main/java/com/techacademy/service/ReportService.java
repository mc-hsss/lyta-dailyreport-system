package com.techacademy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報全件取得　（管理者用）
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 本人分の日報だけ取得（一般ユーザー用）
    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }

    // 1件検索
    public Report findById(Integer id) {
        Optional<Report> option = reportRepository.findById(id);
        return option.orElse(null);
    }

}
