package hrms.service;

import hrms.exception.DuplicateEmployeeIdException;
import hrms.exception.EmployeeNotFoundException;
import hrms.exception.InvalidInputException;
import hrms.model.Employee;
import hrms.model.FullTimeEmployee;
import hrms.model.PartTimeEmployee;
import hrms.util.FileUtil;

import java.time.LocalDate;
import java.util.*;

/**
 * Manages employees in memory and persists via FileUtil.
 * Uses a Map<String, Employee> for O(1) lookup by ID.
 */
public class EmployeeService {

    // Collections: Map for fast ID lookup, List for ordered iteration
    private final Map<String, Employee> employeeMap = new LinkedHashMap<>();

    public EmployeeService() {
        // Load persisted data on startup
        List<Employee> loaded = FileUtil.loadEmployees();
        for (Employee e : loaded) {
            employeeMap.put(e.getId(), e);
        }
    }

    // ── Add ───────────────────────────────────────────────────

    /**
     * Adds a new employee.
     * BR1: unique ID. BR2: name & department not empty. BR11: validation.
     */
    public void addEmployee(String type, String id, String name, String department,
                            String jobTitle, LocalDate joinDate, double basicSalary)
            throws DuplicateEmployeeIdException, InvalidInputException {

        // BR11 validation — JDK 8: use trim().isEmpty() instead of isBlank()
        if (id == null || id.trim().isEmpty())
            throw new InvalidInputException("Employee ID cannot be blank.");
        if (name == null || name.trim().isEmpty())           // BR2
            throw new InvalidInputException("Employee name cannot be blank.");
        if (department == null || department.trim().isEmpty()) // BR2
            throw new InvalidInputException("Department cannot be blank.");
        if (basicSalary <= 0)
            throw new InvalidInputException("Basic salary must be > 0.");

        if (employeeMap.containsKey(id))               // BR1
            throw new DuplicateEmployeeIdException(id);

        Employee emp;
        if ("FullTime".equalsIgnoreCase(type)) {
            emp = new FullTimeEmployee(id, name, department, jobTitle, joinDate, basicSalary);
        } else {
            emp = new PartTimeEmployee(id, name, department, jobTitle, joinDate, basicSalary);
        }

        employeeMap.put(id, emp);
        persist();
    }

    // ── Update ────────────────────────────────────────────────

    /**
     * Updates mutable fields. ID cannot be changed (BR1).
     * Pass null to keep existing value.
     */
    public void updateEmployee(String id, String name, String department,
                               String jobTitle, Double basicSalary, String status)
            throws EmployeeNotFoundException, InvalidInputException {

        Employee emp = findById(id);

        
        if (name != null) emp.setName(name);

        if (department != null) emp.setDepartment(department);

        if (jobTitle != null) emp.setJobTitle(jobTitle);

        if (basicSalary != null) {
            emp.setBasicSalary(basicSalary);
        }

        if (status != null) {
            emp.setStatus(status); 
        }

        persist();
    }

    // ── Delete ────────────────────────────────────────────────

    /**
     * Remove employee record only.
     * Cascade deletion of attendance + salary is handled in EmployeeUI
     * by calling AttendanceService.deleteByEmployee() before this.
     */
    public void deleteEmployee(String id) throws EmployeeNotFoundException {
        if (!employeeMap.containsKey(id)) throw new EmployeeNotFoundException(id);
        employeeMap.remove(id);
        persist();
    }

    // ── Queries ───────────────────────────────────────────────

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employeeMap.values());
    }

    public Employee findById(String id) throws EmployeeNotFoundException {
        Employee emp = employeeMap.get(id);
        if (emp == null) throw new EmployeeNotFoundException(id);
        return emp;
    }

    /** Search by Name (1), Department (2), JobTitle (3) */
    public List<Employee> searchByField(int field, String keyword) {
        final String kw = keyword.toLowerCase();
        List<Employee> result = new ArrayList<>();
        for (Employee e : employeeMap.values()) {
            boolean match = false;
            switch (field) {
                case 1: match = e.getName().toLowerCase().contains(kw); break;
                case 2: match = e.getDepartment().toLowerCase().contains(kw); break;
                case 3: match = e.getJobTitle().toLowerCase().contains(kw); break;
                default: break;
            }
            if (match) result.add(e);
        }
        return result;
    }

    public boolean existsById(String id) {
        return employeeMap.containsKey(id);
    }

    // ── Persistence ───────────────────────────────────────────

    private void persist() {
        FileUtil.saveEmployees(new ArrayList<>(employeeMap.values()));
    }
}
