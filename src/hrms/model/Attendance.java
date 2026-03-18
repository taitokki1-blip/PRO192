package hrms.model;

import java.time.LocalDate;

/**
 * Records one attendance entry per employee per day.
 * BR4: unique (employeeId + date).
 * BR5: status ∈ {Present, Absent, Leave}.
 */
public class Attendance {

    public static final String PRESENT = "Present";
    public static final String ABSENT  = "Absent";
    public static final String LEAVE   = "Leave";

    private String     employeeId;
    private LocalDate  date;
    private String     status;        // BR5
    private double     overtimeHours; // hours of OT on this day

    public Attendance(String employeeId, LocalDate date,
                      String status, double overtimeHours) {
        this.employeeId    = employeeId;
        this.date          = date;
        this.status        = status;
        this.overtimeHours = overtimeHours;
    }

    // ── Getters ────────────────────────────────────────────────
    public String    getEmployeeId()    { return employeeId; }
    public LocalDate getDate()          { return date; }
    public String    getStatus()        { return status; }
    public double    getOvertimeHours() { return overtimeHours; }

    // ── Setters ────────────────────────────────────────────────
    public void setStatus(String status)             { this.status = status; }
    public void setOvertimeHours(double overtimeHours){ this.overtimeHours = overtimeHours; }

    /** Validate that status is one of the allowed values (BR5). */
    public static boolean isValidStatus(String status) {
        return PRESENT.equalsIgnoreCase(status)
            || ABSENT.equalsIgnoreCase(status)
            || LEAVE.equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-12s | %-8s | %.1f hrs OT",
                employeeId, date, status, overtimeHours);
    }
}
