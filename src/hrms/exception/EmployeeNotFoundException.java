package hrms.exception;

/** Thrown when the employee is not found in the system (BR3). */
public class EmployeeNotFoundException extends Exception {
    public EmployeeNotFoundException(String id) {
        super("Employee not found: " + id);
    }
}
