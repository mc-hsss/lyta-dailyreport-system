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

    // 日報削除
    @Transactional
    public ErrorKinds delete(Integer id, UserDetail userDetail) {

        // 自分を削除しようとした場合はエラーメッセージを表示
        if (id.equals(userDetail.getEmployee().getCode())) {
            return ErrorKinds.LOGINCHECK_ERROR;
        }
        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 1件検索
    public Report findById(Integer id) {
        Optional<Report> option = reportRepository.findById(id);
        return option.orElse(null);
    }
    // 日報更新
    @Transactional
    public ErrorKinds update(Report report) {

        //元の従業員情報を取得
        Report dbReport = findById(report.getId());

     // 更新中の日報以外の表示中日報について、日付が被っていないかチェック
        Optional<Report> existing = reportRepository.findByEmployeeAndReportDateAndIdNot(
                report.getEmployee(),
                report.getReportDate(),
                report.getId()   // 更新中の日報自身を除外
        );

        if (existing.isPresent()) {
            return ErrorKinds.DATECHECK_ERROR;
        }

        //更新対象の項目を上書き
        dbReport.setReportDate(report.getReportDate());
        dbReport.setTitle(report.getTitle());
        dbReport.setContent(report.getContent());


        // 更新日時を設定
        dbReport.setUpdatedAt(LocalDateTime.now());

        //保存
        reportRepository.save(dbReport);

        return ErrorKinds.SUCCESS;
    }

}
