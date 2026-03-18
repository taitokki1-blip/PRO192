package hrms.exception;

/** Thrown when trying to add an employee whose ID already exists (BR1). */
public class DuplicateEmployeeIdException extends Exception {
    public DuplicateEmployeeIdException(String id) {
        super("Employee ID already exists: " + id);
    }
}
