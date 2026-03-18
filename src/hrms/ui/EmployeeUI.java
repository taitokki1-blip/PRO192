package hrms.ui;

import hrms.exception.*;
import hrms.model.Employee;
import hrms.service.AttendanceService;
import hrms.service.EmployeeService;
import hrms.service.SalaryService;
import hrms.util.InputUtil;

import java.time.LocalDate;
import java.util.List;

public class EmployeeUI {

    private final EmployeeService   employeeService;
    private final AttendanceService attendanceService;
    private final SalaryService     salaryService;

    // SalaryService needed for cascade delete (Point 6)
    public EmployeeUI(EmployeeService employeeService,
                      AttendanceService attendanceService,
                      SalaryService salaryService) {
        this.employeeService   = employeeService;
        this.attendanceService = attendanceService;
        this.salaryService     = salaryService;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n+==============================+");
            System.out.println("|     MANAGE EMPLOYEES         |");
            System.out.println("+==============================+");
            System.out.println("|  1. Add Employee             |");
            System.out.println("|  2. Update Employee          |");
            System.out.println("|  3. Delete Employee          |");
            System.out.println("|  4. View All Employees       |");
            System.out.println("|  5. Search Employees         |");
            System.out.println("|  0. Back                     |");
            System.out.println("+==============================+");

            int choice = InputUtil.readIntInRange("Choose: ", 0, 5);
            switch (choice) {
                case 1: addEmployee();     break;
                case 2: updateEmployee();  break;
                case 3: deleteEmployee();  break;
                case 4: viewAll();         break;
                case 5: searchEmployees(); break;
                case 0: back = true;       break;
            }
        }
    }

    // ── Add ───────────────────────────────────────────────────

    private void addEmployee() {
        System.out.println("\n--- Add New Employee ---");
        try {
            // Point 1: choose type by number 1/2
            String type = InputUtil.readEmployeeType();

            String id = InputUtil.readAlphanumeric("Employee ID           : ");

            // Point 2: name/dept/title = letters only
            String name  = InputUtil.readAlphaField("Full Name             : ");
            String dept  = InputUtil.readAlphaField("Department            : ");
            String title = InputUtil.readAlphaField("Job Title             : ");

            // Point 2: join date cannot be future
            LocalDate join = InputUtil.readDateNotFuture("Join Date (yyyy-MM-dd): ");

            double salary = InputUtil.readSalary("Basic Salary (VND)    : ");

            employeeService.addEmployee(type, id, name, dept, title, join, salary);
            System.out.println("[SUCCESS] Employee " + id + " added successfully.");

        } catch (DuplicateEmployeeIdException | InvalidInputException e) {
            System.out.println("[FAIL] " + e.getMessage());
        }
    }

    // ── Update ────────────────────────────────────────────────

    private void updateEmployee() {
        System.out.println("\n--- Update Employee ---");
        viewAll();
        String id = InputUtil.readAlphanumeric("Enter Employee ID to update: ");
        try {
            Employee emp = employeeService.findById(id);
            System.out.println("Current info:");
            printHeader();
            System.out.println(emp);
            System.out.println("(Press Enter to keep current value)");

            // Point 2: alpha-only, Enter = keep
            String name  = InputUtil.readOptionalAlpha(
                "New Name       [" + emp.getName()       + "]: ");
            String dept  = InputUtil.readOptionalAlpha(
                "New Department [" + emp.getDepartment() + "]: ");
            String title = InputUtil.readOptionalAlpha(
                "New Job Title  [" + emp.getJobTitle()   + "]: ");

            // Point 3: salary loops on invalid, Enter = keep
            Double salary = InputUtil.readOptionalSalary(
                "New Salary     [" + (long) emp.getBasicSalary() + "]: ");

            // Point 3: status by 0/1, Enter = keep
            String status = InputUtil.readOptionalStatus(emp.getStatus());

            employeeService.updateEmployee(id, name, dept, title, salary, status);
            System.out.println("[SUCCESS] Employee " + id + " updated.");

        } catch (EmployeeNotFoundException | InvalidInputException e) {
            System.out.println("[FAIL] " + e.getMessage());
        }
    }

    // ── Delete ────────────────────────────────────────────────

    private void deleteEmployee() {
        System.out.println("\n--- Delete Employee ---");
        viewAll();
        String id = InputUtil.readAlphanumeric("Enter Employee ID to delete: ");
        try {
            Employee emp = employeeService.findById(id);
            System.out.println("Employee to delete: " + emp.getName()
                + " [" + emp.getDepartment() + "]");

            // Point 5: only y/n accepted, loops otherwise
            boolean confirmed = InputUtil.readConfirmYN("Are you sure?");
            if (confirmed) {
                // Point 6: cascade delete — attendance + salary + employee
                attendanceService.deleteByEmployee(id);
                salaryService.deleteByEmployee(id);
                employeeService.deleteEmployee(id);
                System.out.println("[SUCCESS] Employee " + id
                    + " and all related attendance/salary records deleted.");
            } else {
                System.out.println("[INFO] Delete cancelled.");
            }
        } catch (EmployeeNotFoundException e) {
            System.out.println("[FAIL] " + e.getMessage());
        }
    }

    // ── View All ──────────────────────────────────────────────

    private void viewAll() {
        List<Employee> list = employeeService.getAllEmployees();
        if (list.isEmpty()) {
            System.out.println("[INFO] No employees found.");
            return;
        }
        System.out.println("\n--- All Employees (" + list.size() + ") ---");
        printHeader();
        System.out.println("----------------------------------------------------------------------------------------------------");
        for (Employee e : list) System.out.println(e);
    }

    // ── Search ────────────────────────────────────────────────

    private void searchEmployees() {
        System.out.println("  1. By Name");
        System.out.println("  2. By Department");
        System.out.println("  3. By Job Title");

    int field = InputUtil.readIntInRange("Choose search field (1-3): ", 1, 3);

    String keyword = InputUtil.readNonBlank("Enter keyword: ");

        List<Employee> results = employeeService.searchByField(field, keyword);
        if (results.isEmpty()) {
            System.out.println("[INFO] No employees found matching '" + keyword + "'.");
            return;
        }
        System.out.println("Found " + results.size() + " result(s):");
        printHeader();
        System.out.println("----------------------------------------------------------------------------------------------------");
        for (Employee e : results) System.out.println(e);
    }

    // ── Helpers ───────────────────────────────────────────────

    private void printHeader() {
        System.out.printf("%-8s | %-20s | %-15s | %-15s | %-12s | %10s | %s%n",
            "ID", "Name", "Department", "Job Title", "Join Date", "Salary", "Status");
    }
}
