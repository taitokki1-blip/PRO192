package hrms.ui;

import hrms.service.*;
import hrms.util.InputUtil;

public class MainMenu {

    private final EmployeeUI   employeeUI;
    private final AttendanceUI attendanceUI;
    private final SalaryUI     salaryUI;
    private final ReportUI     reportUI;

    public MainMenu() {
        EmployeeService   empSvc  = new EmployeeService();
        AttendanceService attSvc  = new AttendanceService(empSvc);
        SalaryService     salSvc  = new SalaryService(empSvc, attSvc);
        ReportService     repSvc  = new ReportService(empSvc, attSvc, salSvc);

        // Point 6: EmployeeUI receives AttendanceService + SalaryService for cascade delete
        this.employeeUI   = new EmployeeUI(empSvc, attSvc, salSvc);
        this.attendanceUI = new AttendanceUI(attSvc);
        this.salaryUI     = new SalaryUI(salSvc);
        this.reportUI     = new ReportUI(repSvc);
    }

    public void run() {
        printBanner();
        boolean running = true;
        while (running) {
            System.out.println("\n+==================================+");
            System.out.println("|   HUMAN RESOURCE MANAGEMENT      |");
            System.out.println("|          SYSTEM (HRMS)           |");
            System.out.println("+==================================+");
            System.out.println("|  1. Manage Employees             |");
            System.out.println("|  2. Manage Attendance            |");
            System.out.println("|  3. Salary Management            |");
            System.out.println("|  4. Reports                      |");
            System.out.println("|  0. Exit                         |");
            System.out.println("+==================================+");

            int choice = InputUtil.readIntInRange("Choose: ", 0, 4);
            switch (choice) {
                case 1: employeeUI.show();   break;
                case 2: attendanceUI.show(); break;
                case 3: salaryUI.show();     break;
                case 4: reportUI.show();     break;
                case 0:
                    System.out.println("\nGoodbye! Have a great day.");
                    running = false;
                    break;
            }
        }
    }

    private void printBanner() {
        System.out.println("+==========================================+");
        System.out.println("|       HRMS - HR Management System       |");
        System.out.println("|         FPT University Project           |");
        System.out.println("+==========================================+");
        System.out.println("  Data is automatically loaded from /data/");
    }
}
