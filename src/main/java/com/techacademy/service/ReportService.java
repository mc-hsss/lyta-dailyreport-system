package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
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

    // 日報を新規登録、保存
    @Transactional
    public ErrorKinds save(Report report,Employee loginUser) {

        // 同一社員の同一日付の日報がすでに登録されていないかチェック
        Optional<Report> existingReport =
                reportRepository.findByEmployeeAndReportDate(loginUser, report.getReportDate());

        if (existingReport.isPresent()) {
            // すでに登録済みの場合はエラーを返す
            return ErrorKinds.DATECHECK_ERROR;
        }


        // 新規登録処理（削除フラグ、登録日時、更新日時を登録）
        report.setEmployee(loginUser);
        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }


    // 1件検索
    public Report findById(Integer id) {
        Optional<Report> option = reportRepository.findById(id);
        return option.orElse(null);
    }

}
