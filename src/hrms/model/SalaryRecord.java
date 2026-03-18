package hrms.model;

/**
 * Stores a computed monthly salary result for one employee.
 */
public class SalaryRecord {

    private String employeeId;
    private String employeeName;
    private int    month;
    private int    year;
    private double basicSalary;
    private double overtimePay;
    private double absenceDeduction;
    private double totalSalary;

    public SalaryRecord(String employeeId, String employeeName,
                        int month, int year,
                        double basicSalary, double overtimePay,
                        double absenceDeduction, double totalSalary) {
        this.employeeId       = employeeId;
        this.employeeName     = employeeName;
        this.month            = month;
        this.year             = year;
        this.basicSalary      = basicSalary;
        this.overtimePay      = overtimePay;
        this.absenceDeduction = absenceDeduction;
        this.totalSalary      = totalSalary;
    }

    public String getEmployeeId()       { return employeeId; }
    public String getEmployeeName()     { return employeeName; }
    public int    getMonth()            { return month; }
    public int    getYear()             { return year; }
    public double getBasicSalary()      { return basicSalary; }
    public double getOvertimePay()      { return overtimePay; }
    public double getAbsenceDeduction() { return absenceDeduction; }
    public double getTotalSalary()      { return totalSalary; }

    @Override
    public String toString() {
        return String.format(
            "%-8s | %-20s | %02d/%d | Basic: %,10.0f | OT: %,8.0f | Deduct: %,8.0f | Total: %,12.0f",
            employeeId, employeeName, month, year,
            basicSalary, overtimePay, absenceDeduction, totalSalary);
    }
}
