package hrms.ui;

import hrms.exception.*;
import hrms.model.Attendance;
import hrms.service.AttendanceService;
import hrms.util.InputUtil;

import java.time.LocalDate;
import java.util.List;

public class AttendanceUI {

    private final AttendanceService service;

    public AttendanceUI(AttendanceService service) {
        this.service = service;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n+==============================+");
            System.out.println("|     MANAGE ATTENDANCE        |");
            System.out.println("+==============================+");
            System.out.println("|  1. Record Attendance        |");
            System.out.println("|  2. Update Attendance        |");
            System.out.println("|  3. View Attendance History  |");
            System.out.println("|  4. Monthly Summary          |");
            System.out.println("|  0. Back                     |");
            System.out.println("+==============================+");

            int choice = InputUtil.readIntInRange("Choose: ", 0, 4);
            switch (choice) {
                case 1: recordAttendance(); break;
                case 2: updateAttendance(); break;
                case 3: viewHistory();      break;
                case 4: monthlySummary();   break;
                case 0: back = true;        break;
            }
        }
    }

    // ── Record ────────────────────────────────────────────────

    private void recordAttendance() {
        System.out.println("\n--- Record Attendance ---");
        
        try {
            String    empId  = InputUtil.readAlphanumeric("Employee ID                  : ");
            // Point 2: attendance date also cannot be future
            LocalDate date   = InputUtil.readDateNotFuture("Date (yyyy-MM-dd)            : ");
            String    status = InputUtil.readAttendanceStatus("Status (Present/Absent/Leave): ");
            double    ot     = InputUtil.readPositiveDouble("Overtime Hours               : ");

            service.recordAttendance(empId, date, status, ot);
            System.out.println("[SUCCESS] Attendance recorded for " + empId + " on " + date + ".");

        } catch (EmployeeNotFoundException | DuplicateAttendanceException
                 | InvalidAttendanceStatusException | InvalidInputException e) {
            System.out.println("[FAIL] " + e.getMessage());
        }
    }

    // ── Update ────────────────────────────────────────────────

    private void updateAttendance() {
    System.out.println("\n--- Update Attendance ---");
    try {
        String    empId = InputUtil.readAlphanumeric("Employee ID      : ");
        LocalDate date  = InputUtil.readDate("Date (yyyy-MM-dd): ");

        Attendance current = service.getRecord(empId, date);
        if (current == null) {
            System.out.println("[FAIL] No attendance record found for "
                + empId + " on " + date + ".");
            return;
        }
        System.out.println("-----------------------------------------------------------------");
        System.out.println("  (Press Enter to keep current value)");
        System.out.println("Current: Status = " + current.getStatus());

        String status = null;
        System.out.println("  1. Present  |  2. Absent  |  3. Leave  |  Enter = keep current");
    while (true) {
        System.out.print("  New Status: ");
        String rawStatus = InputUtil.readOptional(""); // đọc input
        if (rawStatus == null || rawStatus.isEmpty()) {
            break; // giữ nguyên
        }
        if ("1".equals(rawStatus)) { status = "Present"; break; }
        if ("2".equals(rawStatus)) { status = "Absent";  break; }
        if ("3".equals(rawStatus)) { status = "Leave";   break; }
        System.out.println("  [!] Please enter 1, 2, 3, or press Enter to keep.");
    }
    
        System.out.println("OT Hours =" + current.getOvertimeHours());
        Double ot = null;
        String rawOt = InputUtil.readOptional(
            "  New OT Hours: ");
        if (rawOt != null && !rawOt.isEmpty()) {
            double parsedOt = Double.parseDouble(rawOt);
            if (parsedOt < 0)
                throw new InvalidInputException("Overtime hours cannot be negative.");
            if (parsedOt > 10)
                throw new InvalidInputException("Overtime hours cannot exceed 10.");
            ot = parsedOt;
        }

        service.updateAttendance(empId, date, status, ot);
        System.out.println("[SUCCESS] Attendance updated.");

    } catch (EmployeeNotFoundException | InvalidAttendanceStatusException
             | InvalidInputException e) {
        System.out.println("[FAIL] " + e.getMessage());
    } catch (NumberFormatException e) {
        System.out.println("[FAIL] Invalid overtime hours. Please enter a number.");
    }
}

    // ── View History ──────────────────────────────────────────

    private void viewHistory() {
        System.out.println("\n--- Attendance History ---");
        String empId = InputUtil.readAlphanumeric("Employee ID: ");
        List<Attendance> records = service.getByEmployee(empId);
        if (records.isEmpty()) {
            System.out.println("[INFO] No attendance records found for " + empId + ".");
            return;
        }
        System.out.printf("%-8s | %-12s | %-8s | %s%n", "EmpID", "Date", "Status", "OT Hrs");
        System.out.println("-------------------------------------------------------");
        for (Attendance a : records) System.out.println(a);
    }

    // ── Monthly Summary ───────────────────────────────────────

    private void monthlySummary() {
        System.out.println("\n--- Monthly Summary ---");
        String empId = InputUtil.readAlphanumeric("Employee ID  : ");
        int month    = InputUtil.readIntInRange("Month (1-12) : ", 1, 12);
        // Point 7: year 2000 → current year
        int year     = InputUtil.readYear("Year         : ");

        int    work   = service.countWorkDays(empId, month, year);
        int    absent = service.countAbsentDays(empId, month, year);
        double ot     = service.totalOvertimeHours(empId, month, year);
        List<Attendance> list = service.getByEmployeeAndMonth(empId, month, year);
        int leave = 0;
        for (Attendance a : list) {
            if (Attendance.LEAVE.equals(a.getStatus())) leave++;
        }

        System.out.printf("%n  Employee  : %s%n", empId);
        System.out.printf("  Period    : %02d/%d%n", month, year);
        System.out.printf("  Present   : %d day(s)%n", work);
        System.out.printf("  Absent    : %d day(s)%n", absent);
        System.out.printf("  Leave     : %d day(s)%n", leave);
        System.out.printf("  Overtime  : %.1f hour(s)%n", ot);
    }
}
