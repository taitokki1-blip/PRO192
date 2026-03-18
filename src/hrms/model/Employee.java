package hrms.model;

import java.time.LocalDate;

/**
 * Abstract base class for all employee types.
 * Encapsulation: all fields are private, accessed via getters/setters.
 * Inheritance: FullTimeEmployee and PartTimeEmployee extend this class.
 */
public abstract class Employee {

    private final String id;          // BR1: immutable after creation
    private String name;              // BR2: not empty
    private String department;        // BR2: not empty
    private String jobTitle;
    private LocalDate joinDate;
    private double basicSalary;
    private String status;            // "active" or "inactive" — BR10

    public Employee(String id, String name, String department,
                    String jobTitle, LocalDate joinDate, double basicSalary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.jobTitle = jobTitle;
        this.joinDate = joinDate;
        this.basicSalary = basicSalary;
        this.status = "active";
    }

    // ── Getters ────────────────────────────────────────────────
    public String getId()           { return id; }
    public String getName()         { return name; }
    public String getDepartment()   { return department; }
    public String getJobTitle()     { return jobTitle; }
    public LocalDate getJoinDate()  { return joinDate; }
    public double getBasicSalary()  { return basicSalary; }
    public String getStatus()       { return status; }

    // ── Setters (id intentionally omitted — BR1) ──────────────
    public void setName(String name)             { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setJobTitle(String jobTitle)     { this.jobTitle = jobTitle; }
    public void setJoinDate(LocalDate joinDate)  { this.joinDate = joinDate; }
    public void setBasicSalary(double basicSalary){ this.basicSalary = basicSalary; }
    public void setStatus(String status)         { this.status = status; }

    /**
     * Polymorphism: each subclass overrides this with its own OT rate (BR8).
     * @param month       target month (1-12)
     * @param year        target year
     * @param workDays    days with status=Present
     * @param absentDays  days with status=Absent
     * @param overtimeHrs total overtime hours recorded
     * @return calculated monthly salary
     */
    public abstract double calculateSalary(int month, int year,
                                           int workDays, int absentDays,
                                           double overtimeHrs);

    /** Return "FullTime" or "PartTime" — used when saving to file */
    public abstract String getEmployeeType();
    public abstract double getOtRate();
    protected static final double ABSENCE_FEE = 100_000;


    @Override
    public String toString() {
        return String.format("%-8s | %-20s | %-15s | %-15s | %s | %10.0f | %s",
                id, name, department, jobTitle, joinDate, basicSalary, status);
    }
}
