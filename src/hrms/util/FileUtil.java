package hrms.util;

import hrms.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Handles reading and writing of data files.
 * Format: employees.csv, attendance.csv
 */
public class FileUtil {

    private static final String DATA_DIR       = "data/";
    private static final String EMPLOYEE_FILE  = DATA_DIR + "employees.csv";
    private static final String ATTENDANCE_FILE= DATA_DIR + "attendance.csv";
    private static final String SALARY_FILE = DATA_DIR + "salary_records.csv";
    static {
        // Ensure data directory exists
        new File(DATA_DIR).mkdirs();
    }

    // ══════════════════════════════════════════════════════════
    //  EMPLOYEES
    // ══════════════════════════════════════════════════════════

    /** Save all employees to CSV. */
    public static void saveEmployees(List<Employee> employees) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(EMPLOYEE_FILE))) {
            // header
            pw.println("type,id,name,department,jobTitle,joinDate,basicSalary,status");
            for (Employee e : employees) {
                pw.printf("%s,%s,%s,%s,%s,%s,%.2f,%s%n",
                    e.getEmployeeType(), e.getId(), e.getName(),
                    e.getDepartment(), e.getJobTitle(), e.getJoinDate(),
                    e.getBasicSalary(), e.getStatus());
            }
        } catch (IOException ex) {
            System.out.println("[ERROR] Cannot save employees: " + ex.getMessage());
        }
    }

    /** Load employees from CSV. Returns empty list if file absent. */
    public static List<Employee> loadEmployees() {
        List<Employee> list = new ArrayList<>();
        File f = new File(EMPLOYEE_FILE);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 8) continue;
                String type       = p[0].trim();
                String id         = p[1].trim();
                String name       = p[2].trim();
                String dept       = p[3].trim();
                String title      = p[4].trim();
                LocalDate join    = LocalDate.parse(p[5].trim());
                double salary     = Double.parseDouble(p[6].trim());
                String status     = p[7].trim();

                Employee emp;
                if ("FullTime".equalsIgnoreCase(type)) {
                    emp = new FullTimeEmployee(id, name, dept, title, join, salary);
                } else {
                    emp = new PartTimeEmployee(id, name, dept, title, join, salary);
                }
                emp.setStatus(status);
                list.add(emp);
            }
        } catch (IOException | NumberFormatException ex) {
            System.out.println("[ERROR] Cannot load employees: " + ex.getMessage());
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════
    //  ATTENDANCE
    // ══════════════════════════════════════════════════════════

    /** Save all attendance records to CSV. */
    public static void saveAttendance(List<Attendance> records) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ATTENDANCE_FILE))) {
            pw.println("employeeId,date,status,overtimeHours");
            for (Attendance a : records) {
                pw.printf("%s,%s,%s,%.2f%n",
                    a.getEmployeeId(), a.getDate(),
                    a.getStatus(), a.getOvertimeHours());
            }
        } catch (IOException ex) {
            System.out.println("[ERROR] Cannot save attendance: " + ex.getMessage());
        }
    }

    /** Load attendance records from CSV. */
    public static List<Attendance> loadAttendance() {
        List<Attendance> list = new ArrayList<>();
        File f = new File(ATTENDANCE_FILE);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 4) continue;
                String    empId = p[0].trim();
                LocalDate date  = LocalDate.parse(p[1].trim());
                String    status= p[2].trim();
                double    ot    = Double.parseDouble(p[3].trim());
                list.add(new Attendance(empId, date, status, ot));
            }
        } catch (IOException | NumberFormatException ex) {
            System.out.println("[ERROR] Cannot load attendance: " + ex.getMessage());
        }
        return list;
    }
    
    
    // ══════════════════════════════════════════════════════════
    //  SALARY
    // ══════════════════════════════════════════════════════════
    
    public static void saveSalaryRecords(List<SalaryRecord> records) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SALARY_FILE))) {
            pw.println("employeeId,employeeName,month,year,basicSalary,overtimePay,absenceDeduction,totalSalary");
            for (SalaryRecord sr : records) {
                pw.printf("%s,%s,%d,%d,%.2f,%.2f,%.2f,%.2f%n",
                    sr.getEmployeeId(), sr.getEmployeeName(),
                    sr.getMonth(), sr.getYear(),
                    sr.getBasicSalary(), sr.getOvertimePay(),
                    sr.getAbsenceDeduction(), sr.getTotalSalary());
            }
        } catch (IOException ex) {
            System.out.println("[ERROR] Cannot save salary records: " + ex.getMessage());
        }
    }

    public static List<SalaryRecord> loadSalaryRecords() {
        List<SalaryRecord> list = new ArrayList<>();
        File f = new File(SALARY_FILE);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 8) continue;
                list.add(new SalaryRecord(
                    p[0].trim(), p[1].trim(),
                    Integer.parseInt(p[2].trim()),
                    Integer.parseInt(p[3].trim()),
                    Double.parseDouble(p[4].trim()),
                    Double.parseDouble(p[5].trim()),
                    Double.parseDouble(p[6].trim()),
                    Double.parseDouble(p[7].trim())
                ));
            }
        } catch (IOException | NumberFormatException ex) {
            System.out.println("[ERROR] Cannot load salary records: " + ex.getMessage());
        }
        return list;
    }
}
