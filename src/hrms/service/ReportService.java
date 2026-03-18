package hrms.service;

import hrms.model.Employee;
import hrms.model.SalaryRecord;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates reports:
 * - Low-attendance employees (BR12: absent > 3 days in month)
 * - Highest-paid employees  (BR13: based on total calculated salary)
 */
public class ReportService {

    private static final int LOW_ATTENDANCE_THRESHOLD = 3; // BR12

    private final EmployeeService   employeeService;
    private final AttendanceService attendanceService;
    private final SalaryService     salaryService;

    public ReportService(EmployeeService employeeService,
                         AttendanceService attendanceService,
                         SalaryService salaryService) {
        this.employeeService   = employeeService;
        this.attendanceService = attendanceService;
        this.salaryService     = salaryService;
    }

    /**
     * BR12: Return employees whose absent days in [month/year] > threshold (3).
     */
    public List<String> getLowAttendanceReport(int month, int year) {
        List<String> report = new ArrayList<>();
        for (Employee emp : employeeService.getAllEmployees()) {
            int absent = attendanceService.countAbsentDays(emp.getId(), month, year);
            if (absent > LOW_ATTENDANCE_THRESHOLD) {
                report.add(String.format("%-8s | %-20s | Absent days: %d",
                    emp.getId(), emp.getName(), absent));
            }
        }
        return report;
    }

    /**
     * BR13: Return salary records sorted descending by total salary for [month/year].
     * Must call SalaryService.calculateForAll() first so records exist.
     */
    public List<SalaryRecord> getHighestPaidReport(int month, int year) {
        // Calculate if not done yet
        List<SalaryRecord> records = salaryService.getAllForMonth(month, year);
        if (records.isEmpty()) {
            records = salaryService.calculateForAll(month, year);
        }
        return records.stream()
            .sorted(Comparator.comparingDouble(SalaryRecord::getTotalSalary).reversed())
            .collect(Collectors.toList());
    }
}
