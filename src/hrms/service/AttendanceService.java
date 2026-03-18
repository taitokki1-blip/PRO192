package hrms.service;

import hrms.exception.*;
import hrms.model.Attendance;
import hrms.util.FileUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages attendance records.
 * Key: employeeId+"_"+date  →  Attendance (BR4: one record per employee per day).
 */
public class AttendanceService {

    // Map key: "empId_date" for O(1) duplicate check (BR4)
    private final Map<String, Attendance> attendanceMap = new LinkedHashMap<>();

    private final EmployeeService employeeService;

    public AttendanceService(EmployeeService employeeService) {
        this.employeeService = employeeService;
        // Load from file
        List<Attendance> loaded = FileUtil.loadAttendance();
        for (Attendance a : loaded) {
            attendanceMap.put(key(a.getEmployeeId(), a.getDate()), a);
        }
    }

    private String key(String empId, LocalDate date) {
        return empId + "_" + date.toString();
    }

    // ── Record ────────────────────────────────────────────────

    /**
     * BR3: employee must exist.
     * BR4: no duplicate for same employee+date.
     * BR5: status must be valid.
     * BR11: inputs validated.
     */
    public void recordAttendance(String employeeId, LocalDate date,
                                 String status, double overtimeHours)
            throws EmployeeNotFoundException, DuplicateAttendanceException,
                   InvalidAttendanceStatusException, InvalidInputException {

        if (employeeId == null || employeeId.trim().isEmpty())
            throw new InvalidInputException("Employee ID cannot be blank.");

        if (!employeeService.existsById(employeeId))     // BR3
            throw new EmployeeNotFoundException(employeeId);

        if (!Attendance.isValidStatus(status))            // BR5
            throw new InvalidAttendanceStatusException(status);

        if (overtimeHours < 0)
            throw new InvalidInputException("Overtime hours cannot be negative.");

        String k = key(employeeId, date);
        if (attendanceMap.containsKey(k))                 // BR4
            throw new DuplicateAttendanceException(employeeId, date.toString());

        attendanceMap.put(k, new Attendance(employeeId, date, status, overtimeHours));
        persist();
    }

    // ── Update ────────────────────────────────────────────────

    public void updateAttendance(String employeeId, LocalDate date,
                                 String newStatus, Double newOvertimeHours)
            throws EmployeeNotFoundException, InvalidAttendanceStatusException,
                   InvalidInputException {

        String k = key(employeeId, date);
        Attendance a = attendanceMap.get(k);
        if (a == null)
            throw new InvalidInputException(
                "No attendance record found for " + employeeId + " on " + date);

        if (newStatus != null) {
            if (!Attendance.isValidStatus(newStatus))
                throw new InvalidAttendanceStatusException(newStatus);
            a.setStatus(newStatus);
        }
        if (newOvertimeHours != null) {
            if (newOvertimeHours < 0)
                throw new InvalidInputException("Overtime hours cannot be negative.");
            a.setOvertimeHours(newOvertimeHours);
        }
        persist();
    }

    // ── Queries ───────────────────────────────────────────────

    /** All attendance records for one employee, sorted by date. */
    public List<Attendance> getByEmployee(String employeeId) {
        return attendanceMap.values().stream()
            .filter(a -> a.getEmployeeId().equals(employeeId))
            .sorted(Comparator.comparing(Attendance::getDate))
            .collect(Collectors.toList());
    }

    /** Records for a specific employee in a given month/year. */
    public List<Attendance> getByEmployeeAndMonth(String employeeId, int month, int year) {
        return getByEmployee(employeeId).stream()
            .filter(a -> a.getDate().getMonthValue() == month
                      && a.getDate().getYear() == year)
            .collect(Collectors.toList());
    }

    /** Count Present days in month. */
    public int countWorkDays(String employeeId, int month, int year) {
        return (int) getByEmployeeAndMonth(employeeId, month, year).stream()
            .filter(a -> Attendance.PRESENT.equals(a.getStatus()))
            .count();
    }

    /** Count Absent days in month. */
    public int countAbsentDays(String employeeId, int month, int year) {
        return (int) getByEmployeeAndMonth(employeeId, month, year).stream()
            .filter(a -> Attendance.ABSENT.equals(a.getStatus()))
            .count();
    }

    /** Total overtime hours in month. */
    public double totalOvertimeHours(String employeeId, int month, int year) {
        return getByEmployeeAndMonth(employeeId, month, year).stream()
            .mapToDouble(Attendance::getOvertimeHours)
            .sum();
    }

    // ── Cascade delete ────────────────────────────────────────

    /**
     * Remove ALL attendance records for an employee.
     * Called when an employee is deleted (Point 6).
     */
    public void deleteByEmployee(String employeeId) {
        List<String> keysToRemove = new ArrayList<>();
        for (String k : attendanceMap.keySet()) {
            if (k.startsWith(employeeId + "_")) {
                keysToRemove.add(k);
            }
        }
        for (String k : keysToRemove) {
            attendanceMap.remove(k);
        }
        persist();
    }

    // ── Persistence ───────────────────────────────────────────

    private void persist() {
        FileUtil.saveAttendance(new ArrayList<>(attendanceMap.values()));
    }
}
