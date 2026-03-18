package hrms.exception;

/** Thrown when an attendance status is not Present/Absent/Leave (BR5). */
public class InvalidAttendanceStatusException extends Exception {
    public InvalidAttendanceStatusException(String status) {
        super("Invalid attendance status '" + status + "'. Allowed: Present, Absent, Leave");
    }
}
