package hrms.service;

import hrms.exception.EmployeeNotFoundException;
import hrms.model.Employee;
import hrms.model.SalaryRecord;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Calculates and stores salary results for a given month.
 * BR7, BR8, BR9, BR10.
 */
public class SalaryService {

    private final EmployeeService  employeeService;
    private final AttendanceService attendanceService;

    // Stores computed salary records: key = empId+"_"+month+"_"+year
    private final Map<String, SalaryRecord> salaryMap = new LinkedHashMap<>();

    public SalaryService(EmployeeService employeeService,
                         AttendanceService attendanceService) {
        this.employeeService   = employeeService;
        this.attendanceService = attendanceService;
    }

    // ── Calculate ─────────────────────────────────────────────

    /**
     * Calculate salary for a single active employee (BR10).
     * Polymorphism: calls emp.calculateSalary() — dispatches to FullTime or PartTime.
     */
    public SalaryRecord calculateForEmployee(String employeeId, int month, int year)
            throws EmployeeNotFoundException {

        Employee emp = employeeService.findById(employeeId);

        if (!"active".equalsIgnoreCase(emp.getStatus()))    // BR10
            throw new IllegalStateException(
                "Cannot calculate salary for inactive employee: " + employeeId);

        int    workDays    = attendanceService.countWorkDays(employeeId, month, year);
        int    absentDays  = attendanceService.countAbsentDays(employeeId, month, year);
        double overtimeHrs = attendanceService.totalOvertimeHours(employeeId, month, year);

        // Polymorphic call — BR7/BR8/BR9 enforced inside each subclass
        double total = emp.calculateSalary(month, year, workDays, absentDays, overtimeHrs);

        // Derive OT pay and deduction for the record display
        double otRate       = emp.getClass().getSimpleName().equals("FullTimeEmployee") ? 80_000 : 50_000;
        double overtimePay  = overtimeHrs * otRate;
        double deduction    = absentDays  * 100_000.0;

        SalaryRecord sr = new SalaryRecord(
            employeeId, emp.getName(), month, year,
            emp.getBasicSalary(), overtimePay, deduction, total);

        salaryMap.put(employeeId + "_" + month + "_" + year, sr);
        return sr;
    }

    /**
     * Calculate salaries for ALL active employees in the given month.
     * Returns list of SalaryRecord (BR10 automatically skips inactive).
     */
    public List<SalaryRecord> calculateForAll(int month, int year) {
        List<SalaryRecord> results = new ArrayList<>();
        for (Employee emp : employeeService.getAllEmployees()) {
            if (!"active".equalsIgnoreCase(emp.getStatus())) continue; // BR10
            try {
                results.add(calculateForEmployee(emp.getId(), month, year));
            } catch (EmployeeNotFoundException ignored) {}
        }
        return results;
    }

    // ── Cascade delete ────────────────────────────────────────

    /**
     * Remove ALL salary records for an employee from memory.
     * Called when an employee is deleted (cascade — Point 6).
     */
    public void deleteByEmployee(String employeeId) {
        List<String> keysToRemove = new ArrayList<>();
        for (String k : salaryMap.keySet()) {
            if (k.startsWith(employeeId + "_")) {
                keysToRemove.add(k);
            }
        }
        for (String k : keysToRemove) {
            salaryMap.remove(k);
        }
    }

    // ── View ──────────────────────────────────────────────────

    public SalaryRecord getSalaryRecord(String employeeId, int month, int year) {
        return salaryMap.get(employeeId + "_" + month + "_" + year);
    }

    public List<SalaryRecord> getAllForMonth(int month, int year) {
        return salaryMap.values().stream()
            .filter(sr -> sr.getMonth() == month && sr.getYear() == year)
            .collect(Collectors.toList());
    }
}
