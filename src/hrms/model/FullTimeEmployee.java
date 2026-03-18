package hrms.model;

import java.time.LocalDate;

public class FullTimeEmployee extends Employee {

    private static final double OT_RATE = 80_000;

    public FullTimeEmployee(String id, String name, String department,
                            String jobTitle, LocalDate joinDate, double basicSalary) {
        super(id, name, department, jobTitle, joinDate, basicSalary);
    }

    @Override
    public double getOtRate() { return OT_RATE; }

    @Override
    public double calculateSalary(int month, int year,
                                  int workDays, int absentDays,
                                  double overtimeHrs) {
        return getBasicSalary() + (overtimeHrs * OT_RATE) - (absentDays * ABSENCE_FEE);
    }

    @Override
    public String getEmployeeType() { return "FullTime"; }
}