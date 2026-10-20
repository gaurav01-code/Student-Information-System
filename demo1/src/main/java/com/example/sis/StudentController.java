
package com.example.sis;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private static final String DB_URL = "jdbc:sqlite:students.db";

    public record StudentInput(
            String rollNumber,
            String studentName,
            String email,
            String department,
            double marks
    ) {}

    public record Student(
            int studentId,
            String rollNumber,
            String studentName,
            String email,
            String department,
            double marks
    ) {}

    public record MessageResult(String message) {}

    public record InsertResult(int studentId, String message) {}

    // 1. VIEW ALL STUDENTS
    @GetMapping
    public List<Student> getAllStudents() {

        String sql = """
                SELECT StudentID, RollNumber, StudentName,
                       Email, Department, Marks
                FROM Students
                ORDER BY StudentID
                """;

        List<Student> students = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("StudentID"),
                        rs.getString("RollNumber"),
                        rs.getString("StudentName"),
                        rs.getString("Email"),
                        rs.getString("Department"),
                        rs.getDouble("Marks")
                ));
            }

            return students;

        } catch (SQLException e) {
            e.printStackTrace();

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to fetch student records."
            );
        }
    }

    // 2. INSERT STUDENT
    @PostMapping
    public InsertResult insertStudent(@RequestBody StudentInput s) {

        validateStudent(s);

        String sql = """
                INSERT INTO Students
                    (RollNumber, StudentName, Email, Department, Marks)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = con.prepareStatement(sql)) {

            setStudentFields(ps, s);
            ps.executeUpdate();

            try (Statement st = con.createStatement();
                 ResultSet rs =
                         st.executeQuery("SELECT last_insert_rowid()")) {

                rs.next();

                return new InsertResult(
                        rs.getInt(1),
                        "Student inserted successfully."
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();

            throw databaseError(e, "insertion");
        }
    }

    // 3. UPDATE STUDENT
    @PutMapping("/{studentId}")
    public MessageResult updateStudent(
            @PathVariable("studentId") int studentId,
            @RequestBody StudentInput s) {

        validateStudent(s);

        String sql = """
                UPDATE Students
                SET RollNumber = ?,
                    StudentName = ?,
                    Email = ?,
                    Department = ?,
                    Marks = ?
                WHERE StudentID = ?
                """;

        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = con.prepareStatement(sql)) {

            setStudentFields(ps, s);
            ps.setInt(6, studentId);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 0) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student not found."
                );
            }

            return new MessageResult(
                    "Student updated successfully."
            );

        } catch (SQLException e) {
            e.printStackTrace();

            throw databaseError(e, "update");
        }
    }

    // 4. DELETE STUDENT
    @DeleteMapping("/{studentId}")
    public MessageResult deleteStudent(
            @PathVariable("studentId") int studentId) {

        String sql =
                "DELETE FROM Students WHERE StudentID = ?";

        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            int rowsDeleted = ps.executeUpdate();

            if (rowsDeleted == 0) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student not found."
                );
            }

            return new MessageResult(
                    "Student deleted successfully."
            );

        } catch (SQLException e) {
            e.printStackTrace();

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Database deletion failed."
            );
        }
    }

    // VALIDATE STUDENT INPUT
    private static void validateStudent(StudentInput s) {

        if (s == null
                || isBlank(s.rollNumber())
                || isBlank(s.studentName())
                || isBlank(s.email())
                || isBlank(s.department())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "All fields are required."
            );
        }

        if (!Double.isFinite(s.marks())
                || s.marks() < 0
                || s.marks() > 100) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Marks must be between 0 and 100."
            );
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    // REUSE THE SAME FIELDS FOR INSERT AND UPDATE
    private static void setStudentFields(
            PreparedStatement ps,
            StudentInput s) throws SQLException {

        ps.setString(1, s.rollNumber().trim());
        ps.setString(2, s.studentName().trim());
        ps.setString(3, s.email().trim());
        ps.setString(4, s.department().trim());
        ps.setDouble(5, s.marks());
    }

    // DATABASE ERROR HANDLING
    private static ResponseStatusException databaseError(
            SQLException e, String operation) {

        if (e.getMessage() != null
                && e.getMessage().toLowerCase().contains("unique")) {

            return new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Roll number or email already exists."
            );
        }

        return new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Database " + operation + " failed."
        );
    }
}
