package hrms.ui;

import hrms.exception.EmployeeNotFoundException;
import hrms.model.SalaryRecord;
import hrms.service.SalaryService;
import hrms.util.InputUtil;

import java.util.List;

public class SalaryUI {

    private final SalaryService service;

    public SalaryUI(SalaryService service) {
        this.service = service;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n+==============================+");
            System.out.println("|     SALARY MANAGEMENT        |");
            System.out.println("+==============================+");
            System.out.println("|  1. Calculate Salary (One)   |");
            System.out.println("|  2. Calculate Salary (All)   |");
            System.out.println("|  3. View Salary Detail       |");
            System.out.println("|  4. Export Salary Report     |");
            System.out.println("|  0. Back                     |");
            System.out.println("+==============================+");

            int choice = InputUtil.readIntInRange("Choose: ", 0, 4);
            switch (choice) {
                case 1: calculateOne();   break;
                case 2: calculateAll();   break;
                case 3: viewDetail();     break;
                case 4: exportReport();   break;
                case 0: back = true;      break;
            }
        }
    }

    private void calculateOne() {
        System.out.println("\n--- Calculate Salary (Single Employee) ---");
        try {
            String empId = InputUtil.readNonBlank("Employee ID  : ");
            int month    = InputUtil.readIntInRange("Month (1-12) : ", 1, 12);
            int year     = InputUtil.readYear("Year         : ");  // Point 7

            SalaryRecord sr = service.calculateForEmployee(empId, month, year);
            printHeader();
            printRecord(sr);
            System.out.println("[SUCCESS] Salary calculated.");

        } catch (EmployeeNotFoundException e) {
            System.out.println("[FAIL] " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("[FAIL] " + e.getMessage());
        }
    }

    private void calculateAll() {
        System.out.println("\n--- Calculate Salary (All Active Employees) ---");
        int month = InputUtil.readIntInRange("Month (1-12) : ", 1, 12);
        int year  = InputUtil.readYear("Year         : ");  // Point 7

        List<SalaryRecord> records = service.calculateForAll(month, year);
        if (records.isEmpty()) {
            System.out.println("[INFO] No active employees found.");
            return;
        }
        System.out.println("\nSalary report for " + month + "/" + year + ":");
        printHeader();
        for (SalaryRecord sr : records) printRecord(sr);
        System.out.println("[SUCCESS] Calculated salary for " + records.size() + " employee(s).");
    }

    private void viewDetail() {
        System.out.println("\n--- View Salary Detail ---");
        String empId = InputUtil.readNonBlank("Employee ID  : ");
        int month    = InputUtil.readIntInRange("Month (1-12) : ", 1, 12);
        int year     = InputUtil.readYear("Year         : ");  // Point 7

        SalaryRecord sr = service.getSalaryRecord(empId, month, year);
        if (sr == null) {
            System.out.println("[INFO] No salary record found. Please calculate first.");
            return;
        }
        printHeader();
        printRecord(sr);
    }

    private void exportReport() {
        System.out.println("\n--- Export Salary Report ---");
        int month = InputUtil.readIntInRange("Month (1-12) : ", 1, 12);
        int year  = InputUtil.readYear("Year         : ");  

        List<SalaryRecord> records = service.getAllForMonth(month, year);
        if (records.isEmpty()) {
            System.out.println("[INFO] No salary records for " + month + "/" + year
                + ". Please run 'Calculate All' first.");
            return;
        }

        String filename = "data/salary_report_" + year + "_"
            + String.format("%02d", month) + ".csv";
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(filename))){
            pw.println("EmployeeID,Name,Month,Year,BasicSalary,OvertimePay,Deduction,TotalSalary");
            for (SalaryRecord sr : records) {
                pw.printf("%s,%s,%d,%d,%.2f,%.2f,%.2f,%.2f%n",
                    sr.getEmployeeId(), sr.getEmployeeName(),
                    sr.getMonth(), sr.getYear(),
                    sr.getBasicSalary(), sr.getOvertimePay(),
                    sr.getAbsenceDeduction(), sr.getTotalSalary());
            }
            System.out.println("[SUCCESS] Report exported to: " + filename);
        } catch (java.io.IOException e) {
            System.out.println("[FAIL] Cannot write report: " + e.getMessage());
        }
    }

    private void printHeader() {
        System.out.printf("%-8s | %-20s | %7s | %12s | %10s | %10s | %13s%n",
            "ID", "Name", "Month", "BasicSalary", "OT Pay", "Deduction", "Total Salary");
        System.out.println("------------------------------------------------------------------------------------------");
    }

    private void printRecord(SalaryRecord sr) {
        System.out.printf("%-8s | %-20s | %02d/%d | %,12.0f | %,10.0f | %,10.0f | %,13.0f%n",
            sr.getEmployeeId(), sr.getEmployeeName(),
            sr.getMonth(), sr.getYear(),
            sr.getBasicSalary(), sr.getOvertimePay(),
            sr.getAbsenceDeduction(), sr.getTotalSalary());
    }
}
