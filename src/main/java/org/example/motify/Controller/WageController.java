package org.example.motify.Controller;

import org.example.motify.Entity.Repairman;
import org.example.motify.Entity.Wage;
import org.example.motify.Repository.RepairmanRepository;
import org.example.motify.Repository.WageRepository;
import org.example.motify.Service.WageService;
import org.example.motify.config.SchedulingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wages")
public class WageController {

    @Autowired
    private WageRepository wageRepository;

    @Autowired
    private SchedulingConfig schedulingConfig;

    @Autowired
    private WageService wageService;

    @Autowired
    private RepairmanRepository repairmanRepository;

    /**
     * 获取指定月份的工资记录
     * 
     * @param year  年份
     * @param month 月份(1-12)
     * @return 工资记录列表
     */
    @GetMapping("/{year}/{month}")
    public ResponseEntity<?> getMonthlyWages(
            @PathVariable int year,
            @PathVariable int month) {
        List<Wage> wages = wageRepository.findByYearAndMonth(year, month);
        return ResponseEntity.ok(createSuccessResponse(wages));
    }

    /**
     * 获取指定维修人员的工资记录
     * 
     * @param repairmanId 维修人员ID
     * @return 工资记录列表
     */
    @GetMapping("/repairman/{repairmanId}")
    public ResponseEntity<?> getRepairmanWages(@PathVariable Long repairmanId) {
        List<Wage> wages = wageRepository.findByRepairmanIdOrderByYearDescMonthDesc(repairmanId);
        return ResponseEntity.ok(createSuccessResponse(wages));
    }

    /**
     * 手动触发月度工资结算
     * 
     * @param payload 包含年份和月份的请求体
     * @return 成功消息
     */
    @PostMapping("/calculate")
    public ResponseEntity<?> calculateMonthlyWages(@RequestBody Map<String, Integer> payload) {
        int year = payload.getOrDefault("year", LocalDate.now().minusMonths(1).getYear());
        int month = payload.getOrDefault("month", LocalDate.now().minusMonths(1).getMonthValue());

        schedulingConfig.manualWageSettlement(year, month);

        Map<String, Object> message = new HashMap<>();
        message.put("message", String.format("%d年%d月的工资已计算完成", year, month));

        return ResponseEntity.ok(createSuccessResponse(message));
    }

    // =============== 维修人员工资查询接口 ===============

    /**
     * 获取当前维修人员的所有工资记录
     */
    @GetMapping("/repairman/{repairmanId}/history")
    public ResponseEntity<?> getRepairmanWageHistory(@PathVariable Long repairmanId) {
        List<Wage> wages = wageService.getRepairmanWages(repairmanId);
        return ResponseEntity.ok(createSuccessResponse(wages));
    }

    /**
     * 获取指定月份的工资记录
     */
    @GetMapping("/my/{year}/{month}")
    public ResponseEntity<?> getMyMonthlyWage(
            @PathVariable int year,
            @PathVariable int month,
            @RequestParam Long repairmanId) {

        List<Wage> wages = wageRepository.findByRepairmanIdAndYearAndMonth(repairmanId, year, month);
        if (wages.isEmpty()) {
            return ResponseEntity.ok(createSuccessResponse(Collections.emptyMap(),
                    String.format("%d年%d月没有工资记录", year, month)));
        }

        return ResponseEntity.ok(createSuccessResponse(wages.getFirst()));
    }

    /**
     * 获取年度工资统计
     */
    @GetMapping("/my/yearly-stats")
    public ResponseEntity<?> getMyYearlyWageStats(
            @RequestParam(required = false) Integer year,
            @RequestParam Long repairmanId) {

        if (year == null) {
            year = LocalDate.now().getYear();
        }

        // 获取指定年份的所有工资记录
        List<Wage> yearWages = wageRepository.findByRepairmanIdAndYear(repairmanId, year);

        if (yearWages.isEmpty()) {
            return ResponseEntity.ok(createSuccessResponse(Collections.emptyMap(),
                    String.format("%d年没有工资记录", year)));
        }

        // 计算统计数据
        double totalIncome = yearWages.stream().mapToDouble(Wage::getTotalIncome).sum();
        double totalHours = yearWages.stream().mapToDouble(Wage::getTotalWorkHours).sum();
        double averageMonthlyIncome = totalIncome / yearWages.size();
        double averageHourlyRate = totalIncome / totalHours;

        // 查找收入最高的月份
        Wage highestMonth = yearWages.stream()
                .max(Comparator.comparing(Wage::getTotalIncome))
                .orElseThrow();

        // 构建响应数据
        Map<String, Object> stats = new HashMap<>();
        stats.put("year", year);
        stats.put("totalIncome", totalIncome);
        stats.put("totalWorkHours", totalHours);
        stats.put("averageMonthlyIncome", averageMonthlyIncome);
        stats.put("averageHourlyRate", averageHourlyRate);
        stats.put("highestMonth", highestMonth.getMonth());
        stats.put("highestMonthIncome", highestMonth.getTotalIncome());
        stats.put("workingMonths", yearWages.size());
        stats.put("monthlyDetails", yearWages.stream()
                .sorted(Comparator.comparing(Wage::getMonth))
                .collect(Collectors.toList()));

        return ResponseEntity.ok(createSuccessResponse(stats));
    }

    /**
     * 获取工资统计摘要
     */
    @GetMapping("/my/summary")
    public ResponseEntity<?> getMyWageSummary(@RequestParam Long repairmanId) {
        List<Wage> allWages = wageService.getRepairmanWages(repairmanId);

        if (allWages.isEmpty()) {
            return ResponseEntity.ok(createSuccessResponse(Collections.emptyMap(), "没有工资记录"));
        }

        // 计算基本统计数据
        double totalIncome = allWages.stream().mapToDouble(Wage::getTotalIncome).sum();
        double totalHours = allWages.stream().mapToDouble(Wage::getTotalWorkHours).sum();
        int totalMonths = allWages.size();
        double averageMonthlyIncome = totalIncome / totalMonths;

        // 计算过去三个月的收入
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        List<Wage> recentWages = new ArrayList<>();

        // 收集过去三个月的工资记录
        for (int i = 0; i < 3; i++) {
            int targetMonth = currentMonth - i;
            int targetYear = currentYear;

            if (targetMonth <= 0) {
                targetMonth += 12;
                targetYear -= 1;
            }

            final int month = targetMonth;
            final int year = targetYear;

            allWages.stream()
                    .filter(w -> w.getYear() == year && w.getMonth() == month)
                    .findFirst()
                    .ifPresent(recentWages::add);
        }

        double recent3MonthsIncome = recentWages.stream()
                .mapToDouble(Wage::getTotalIncome)
                .sum();

        // 获取最高和最低收入月份
        Wage highestMonth = allWages.stream()
                .max(Comparator.comparing(Wage::getTotalIncome))
                .orElseThrow();

        Wage lowestMonth = allWages.stream()
                .min(Comparator.comparing(Wage::getTotalIncome))
                .orElseThrow();

        // 构建响应数据
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalIncome", totalIncome);
        summary.put("totalWorkHours", totalHours);
        summary.put("totalMonths", totalMonths);
        summary.put("averageMonthlyIncome", averageMonthlyIncome);
        summary.put("recent3MonthsIncome", recent3MonthsIncome);
        summary.put("highestMonth", String.format("%d年%d月", highestMonth.getYear(), highestMonth.getMonth()));
        summary.put("highestMonthIncome", highestMonth.getTotalIncome());
        summary.put("lowestMonth", String.format("%d年%d月", lowestMonth.getYear(), lowestMonth.getMonth()));
        summary.put("lowestMonthIncome", lowestMonth.getTotalIncome());

        // 计算年度收入趋势
        Map<Integer, Double> yearlyTrend = allWages.stream()
                .collect(Collectors.groupingBy(
                        Wage::getYear,
                        Collectors.summingDouble(Wage::getTotalIncome)));

        summary.put("yearlyTrend", yearlyTrend);

        return ResponseEntity.ok(createSuccessResponse(summary));
    }

    /**
     * 创建统一的成功响应
     */
    private Map<String, Object> createSuccessResponse(Object data) {
        return createSuccessResponse(data, "success");
    }

    /**
     * 创建统一的成功响应（带自定义消息）
     */
    private Map<String, Object> createSuccessResponse(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", message);
        response.put("data", data);
        return response;
    }
}