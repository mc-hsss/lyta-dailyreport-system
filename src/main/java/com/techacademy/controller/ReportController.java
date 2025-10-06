package com.techacademy.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;
    private final EmployeeService employeeService;

    public ReportController(ReportService reportService, EmployeeService employeeService) {
        this.reportService = reportService;
        this.employeeService = employeeService;
    }

    // 日報一覧画面
    @GetMapping
    public String listReports(@AuthenticationPrincipal UserDetail loginUser, Model model) {

        // ログインユーザーの情報を取得
        Employee employee = loginUser.getEmployee();

        List<Report> reports;

        // 管理者なら全件取得、一般ユーザーなら本人分のみ取得
        if (employeeService.isAdmin(employee)) {
            reports = reportService.findAll();
        } else {
            reports = reportService.findByEmployee(employee);
        }

        // 取得データをModelに格納
        model.addAttribute("listSize", reports.size());
        model.addAttribute("reportList", reports);

        return "reports/list";
    }

}