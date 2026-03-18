package hrms.exception;

/** Thrown when an attendance record already exists for a given employee+date (BR4). */
public class DuplicateAttendanceException extends Exception {
    public DuplicateAttendanceException(String employeeId, String date) {
        super("Attendance already recorded for employee " + employeeId + " on " + date);
    }
}
