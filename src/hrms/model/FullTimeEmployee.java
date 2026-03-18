package hrms.model;

import java.time.LocalDate;

/**
 * Full-time employee.
 * BR8: OT rate = 80,000 VND/hour.
 * BR7: Salary = basicSalary + (overtimeHrs * 80000) - (absentDays * 100000)
 */
public class FullTimeEmployee extends Employee {

    private static final double OT_RATE     = 80_000;   // BR8
    private static final double ABSENCE_FEE = 100_000;  // BR9

    public FullTimeEmployee(String id, String name, String department,
                            String jobTitle, LocalDate joinDate, double basicSalary) {
        super(id, name, department, jobTitle, joinDate, basicSalary);
    }

    @Override
    public double calculateSalary(int month, int year,
                                  int workDays, int absentDays,
                                  double overtimeHrs) {
        double overtimePay      = overtimeHrs * OT_RATE;
        double absenceDeduction = absentDays  * ABSENCE_FEE;
        return getBasicSalary() + overtimePay - absenceDeduction;
    }

    @Override
    public String getEmployeeType() { return "FullTime"; }
}
