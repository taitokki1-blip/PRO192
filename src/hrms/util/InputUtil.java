package hrms.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Helper utilities for reading and validating console input (BR11).
 * Compatible with JDK 1.8 (no isBlank(), no repeat(), no switch arrows).
 */
public class InputUtil {

    private static final Scanner sc = new Scanner(System.in);

    // ── Basic string ──────────────────────────────────────────

    /** Read a non-blank string (any characters). */
    public static String readNonBlank(String prompt) {
        while (true) {
            System.out.print(prompt);
            String val = sc.nextLine().trim();
            if (!val.isEmpty()) return val;
            System.out.println("  [!] Input cannot be empty. Please try again.");
        }
    }
    
    
    public static String readAlphanumeric(String prompt) {
        while (true) {
            System.out.print(prompt);
            String val = sc.nextLine().trim();
            if (val.isEmpty()) {
                System.out.println("  [!] Input cannot be empty. Please try again.");
            } else if (!val.matches("[a-zA-Z0-9]+")) {
                System.out.println("  [!] Only letters and digits are allowed (no spaces or special characters).");
            } else {
                return val;
            }
        }
    }

    /**
     * Read a name/department/jobTitle field:
     * - Cannot be blank
     * - Only letters, spaces, hyphens allowed (no numbers/special chars)
     */
    public static String readAlphaField(String prompt) {
        while (true) {
            System.out.print(prompt);
            String val = sc.nextLine().trim();
            if (val.isEmpty()) {
                System.out.println("  [!] Input cannot be empty. Please try again.");
            } else if (!val.matches("[a-zA-Z\\s\\-]+")) {
                System.out.println("  [!] Only letters, spaces, and hyphens are allowed.");
            } else {
                return val;
            }
        }
    }

    /** Read an optional alpha field. Enter = keep current. Returns null if blank. */
    public static String readOptionalAlpha(String prompt) {
        while (true) {
            System.out.print(prompt);
            String val = sc.nextLine().trim();
            if (val.isEmpty()) return null;                          // keep current
            if (!val.matches("[a-zA-Z\\s\\-]+")) {
                System.out.println("  [!] Only letters, spaces, and hyphens are allowed.");
            } else {
                return val;
            }
        }
    }

    /** Read an optional string (any characters). Enter = keep current (returns null). */
    public static String readOptional(String prompt) {
        System.out.print(prompt);
        String val = sc.nextLine().trim();
        return val.isEmpty() ? null : val;
    }

    // ── Numbers ───────────────────────────────────────────────

    /** Read a salary: must be >= 0, loops until valid. */
    public static double readSalary(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double val = Double.parseDouble(sc.nextLine().trim());
                if (val > 0) return val;
                System.out.println("  [!] Salary must be > 0. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number. Please enter a numeric value.");
            }
        }
    }

    /** Read an optional salary. Enter = keep current (returns null). */
    public static Double readOptionalSalary(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = sc.nextLine().trim();
            if (raw.isEmpty()) return null;
            try {
                double val = Double.parseDouble(raw);
                if (val > 0) return val;
                System.out.println("  [!] Salary must be > 0. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number. Please try again.");
            }
        }
    }

    /** Read a non-negative double (for overtime hours). */
    public static double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double val = Double.parseDouble(sc.nextLine().trim());
                if (val < 0) {
                System.out.println("  [!] Overtime hours cannot be negative.");
                } else if (val > 10) {
                System.out.println("  [!] Overtime hours cannot exceed 10 hours/day.");
                } else {
                return val;
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number. Please enter a numeric value.");
            }
        }
    }

    /** Read an integer within [min, max]. */
    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(sc.nextLine().trim());
                if (val >= min && val <= max) return val;
                System.out.println("  [!] Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number.");
            }
        }
    }

    /**
     * Read a year: must be between 2000 and current year (inclusive).
     * Point 7: year range 2000 → now.
     */
    public static int readYear(String prompt) {
        int currentYear = LocalDate.now().getYear();
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(sc.nextLine().trim());
                if (val >= 2000 && val <= currentYear) return val;
                System.out.println("  [!] Year must be between 2000 and " + currentYear + ".");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid year.");
            }
        }
    }

    // ── Dates ─────────────────────────────────────────────────

    
    public static LocalDate readDateNotFuture(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                LocalDate date = LocalDate.parse(sc.nextLine().trim());
                if (date.isAfter(LocalDate.now())) {
                    System.out.println("  [!] Date cannot be in the future.");
                } else {
                    return date;
                }
            } catch (DateTimeParseException e) {
                System.out.println("  [!] Invalid date. Use format yyyy-MM-dd (e.g. 2024-03-15).");
            }
        }
    }

    /** Read any valid date (no future restriction — e.g. for querying). */
    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(sc.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("  [!] Invalid date. Use format yyyy-MM-dd.");
            }
        }
    }

    // ── Special fields ────────────────────────────────────────

    /**
     * Employee type: enter 1 = FullTime, 2 = PartTime.
     * Point 1: choose by number instead of typing full word.
     */
    public static String readEmployeeType() {
        System.out.println("  1. Full-Time  |  2. Part-Time");
        while (true) {
            System.out.print("  Choose type (1/2): ");
            String raw = sc.nextLine().trim();
            if ("1".equals(raw)) return "FullTime";
            if ("2".equals(raw)) return "PartTime";
            System.out.println("  [!] Please enter 1 (Full-Time) or 2 (Part-Time).");
        }
    }

    /**
     * Read valid attendance status: Present / Absent / Leave.
     * Case-insensitive, loops until valid.
     */
    public static String readAttendanceStatus(String prompt) {
        while (true) {
            System.out.print(prompt);
            String val = sc.nextLine().trim();
            if ("Present".equalsIgnoreCase(val) ||
                "Absent".equalsIgnoreCase(val)  ||
                "Leave".equalsIgnoreCase(val)) {
                return val.substring(0, 1).toUpperCase() + val.substring(1).toLowerCase();
            }
            System.out.println("  [!] Status must be Present, Absent, or Leave.");
        }
    }

    /**
     * Read employee status for update: 0 = inactive, 1 = active.
     * Point 3: choose by number instead of typing.
     * Returns null if user presses Enter (keep current).
     */
    public static String readOptionalStatus(String currentStatus) {
        System.out.println("  Current status: " + currentStatus);
        System.out.println("  0. Inactive  |  1. Active  |  Enter = keep current");
        while (true) {
            System.out.print("  Choose (0/1): ");
            String raw = sc.nextLine().trim();
            if (raw.isEmpty()) return null;
            if ("0".equals(raw)) return "inactive";
            if ("1".equals(raw)) return "active";
            System.out.println("  [!] Please enter 0 (Inactive) or 1 (Active), or press Enter to keep.");
        }
    }

    /**
     * Confirm deletion: only y or n accepted.
     * Point 5: loops if anything other than y/n is entered.
     */
    public static boolean readConfirmYN(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String val = sc.nextLine().trim();
            if ("y".equalsIgnoreCase(val)) return true;
            if ("n".equalsIgnoreCase(val)) return false;
            System.out.println("  [!] Please enter y or n only.");
        }
    }
}
