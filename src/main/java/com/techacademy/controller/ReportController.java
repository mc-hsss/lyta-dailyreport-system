package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    @Autowired
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

     // 日報詳細画面
    @GetMapping("/{id}/")
    public String detail(@PathVariable("id") int id, Model model) {
        Report report = reportService.findById(id);
        model.addAttribute("report", report);
        model.addAttribute("employee", report.getEmployee());

        return "reports/detail";
    }

     // 日報新規登録画面の表示
     @GetMapping(value = "/add")
        public String create(@AuthenticationPrincipal UserDetail loginUser, Model model) {

         model.addAttribute("report", new Report());      // 入力用の空のReport
         model.addAttribute("employee", loginUser.getEmployee()); // 表示用
         return "reports/new";
     }

     // 日報新規登録処理
     @PostMapping("/add")
     public String add(@AuthenticationPrincipal UserDetail loginUser,@Validated Report report, BindingResult res, Model model) {

         // 入力チェック
         if (res.hasErrors()) {
             model.addAttribute("employee",loginUser.getEmployee());
             return "reports/new";
         }

         // 登録処理を呼び出し
         Employee employee = loginUser.getEmployee(); // 日報の記述者
         ErrorKinds result = reportService.save(report, employee);

         // 日付重複チェックエラーの場合
         if (result == ErrorKinds.DATECHECK_ERROR) {
             model.addAttribute("dateErrorMsg", "既に登録されている日付です");
             model.addAttribute("employee", employee);
             return "reports/new";
         }


         return "redirect:/reports";
     }
    // 日報削除処理
     @PostMapping(value = "/{id}/delete")
     public String delete(@PathVariable("id") int id, @AuthenticationPrincipal UserDetail userDetail, Model model) {

         ErrorKinds result = reportService.delete(id, userDetail);

         if (ErrorMessage.contains(result)) {
             model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
             model.addAttribute("report", reportService.findById(id));
             return detail(id, model);
         }

         return "redirect:/reports";
     }

}
