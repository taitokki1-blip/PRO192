package hrms.ui;

import hrms.model.SalaryRecord;
import hrms.service.ReportService;
import hrms.util.InputUtil;

import java.util.List;

public class ReportUI {

    private final ReportService service;

    public ReportUI(ReportService service) {
        this.service = service;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n+==============================+");
            System.out.println("|         REPORTS              |");
            System.out.println("+==============================+");
            System.out.println("|  1. Low Attendance Report    |");
            System.out.println("|  2. Highest Paid Report      |");
            System.out.println("|  0. Back                     |");
            System.out.println("+==============================+");

            int choice = InputUtil.readIntInRange("Choose: ", 0, 2);
            switch (choice) {
                case 1: lowAttendance(); break;
                case 2: highestPaid();   break;
                case 0: back = true;     break;
            }
        }
    }

    private void lowAttendance() {
        System.out.println("\n--- Low Attendance Report (Absent > 3 days) ---");
        int month = InputUtil.readIntInRange("Month (1-12): ", 1, 12);
        int year  = InputUtil.readYear("Year        : ");  // Point 7

        List<String> report = service.getLowAttendanceReport(month, year);
        if (report.isEmpty()) {
            System.out.println("[INFO] No employees with low attendance in "
                + month + "/" + year + ".");
            return;
        }
        System.out.printf("%nLow-attendance employees for %02d/%d:%n", month, year);
        System.out.printf("%-8s | %-20s | %s%n", "ID", "Name", "Absent Days");
        System.out.println("--------------------------------------------------");
        for (String line : report) System.out.println(line);
        System.out.println("\nTotal: " + report.size() + " employee(s) flagged.");
    }

    private void highestPaid() {
        System.out.println("\n--- Highest Paid Report ---");
        int month = InputUtil.readIntInRange("Month (1-12): ", 1, 12);
        int year  = InputUtil.readYear("Year        : ");  // Point 7

        List<SalaryRecord> records = service.getHighestPaidReport(month, year);
        if (records.isEmpty()) {
            System.out.println("[INFO] No salary data for " + month + "/" + year
                + ". Please calculate salaries first.");
            return;
        }
        System.out.printf("%nHighest-paid employees for %02d/%d:%n", month, year);
        System.out.printf("%-4s | %-8s | %-20s | %13s%n",
            "Rank", "ID", "Name", "Total Salary");
        System.out.println("-------------------------------------------------------");
        int rank = 1;
        for (SalaryRecord sr : records) {
            System.out.printf("%-4d | %-8s | %-20s | %,13.0f VND%n",
                rank++, sr.getEmployeeId(), sr.getEmployeeName(), sr.getTotalSalary());
        }
    }
}
