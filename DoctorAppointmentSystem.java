import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

class Person {
    String name;
    int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

class Doctor extends Person {
    String specialization;

    public Doctor(String name, int age, String specialization) {
        super(name, age);
        this.specialization = specialization;
    }

    public void displayInfo() {
        System.out.println("Doctor Name: " + name);
        System.out.println("Specialization: " + specialization);
    }
}

class Patient extends Person {
    String problem;

    public Patient(String name, int age, String problem) {
        super(name, age);
        this.problem = problem;
    }

    public void displayInfo() {
        System.out.println("Patient Name: " + name);
        System.out.println("Problem: " + problem);
    }
}

class Appointment {
    Doctor doctor;
    Patient patient;
    String date;

    public Appointment(Doctor doctor, Patient patient, String date) {
        this.doctor = doctor;
        this.patient = patient;
        this.date = date;
    }

    public void displayAppointment() {
        System.out.println("Appointment Details:");
        doctor.displayInfo();
        patient.displayInfo();
        System.out.println("Date: " + date);
    }
}

public class DoctorAppointmentSystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/DoctorAppointmentDB";
    private static final String DB_USER = "Kavishnaa"; // Replace with your MySQL username
    private static final String DB_PASSWORD = "Kavi@1234"; // Replace with your MySQL password

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Connect to the database
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Connected to the database.");

            // Retrieve doctors from the database
            ArrayList<Doctor> doctors = getDoctors(connection);

            // List available doctors
            System.out.println("Available Doctors:");
            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i + 1) + ". " + doctors.get(i).name + " - " + doctors.get(i).specialization);
            }

            // Choose a doctor
            System.out.print("Choose a doctor by entering the number: ");
            int doctorChoice = scanner.nextInt();
            Doctor selectedDoctor = doctors.get(doctorChoice - 1);

            // Enter patient details
            System.out.print("Enter Patient Name: ");
            scanner.nextLine();  // consume newline
            String patientName = scanner.nextLine();

            System.out.print("Enter Patient Age: ");
            int patientAge = scanner.nextInt();

            System.out.print("Enter Patient Problem: ");
            scanner.nextLine();  // consume newline
            String patientProblem = scanner.nextLine();

            Patient patient = new Patient(patientName, patientAge, patientProblem);

            // Enter appointment date
            System.out.print("Enter Appointment Date (dd/mm/yyyy): ");
            String appointmentDate = scanner.nextLine();

            // Insert patient and appointment into the database
            int patientId = addPatient(connection, patient);
            addAppointment(connection, selectedDoctor, patientId, appointmentDate);

            // Display appointment details
            Appointment appointment = new Appointment(selectedDoctor, patient, appointmentDate);
            System.out.println("\nAppointment Scheduled Successfully!\n");
            appointment.displayAppointment();

        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        }

        scanner.close();
    }

    // Method to retrieve doctors from the database
    private static ArrayList<Doctor> getDoctors(Connection connection) throws SQLException {
        ArrayList<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT name, age, specialization FROM doctors";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            String name = rs.getString("name");
            int age = rs.getInt("age");
            String specialization = rs.getString("specialization");
            doctors.add(new Doctor(name, age, specialization));
        }
        return doctors;
    }

    // Method to insert a patient into the database
    private static int addPatient(Connection connection, Patient patient) throws SQLException {
        String sql = "INSERT INTO patients (name, age, problem) VALUES (?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, patient.name);
        pstmt.setInt(2, patient.age);
        pstmt.setString(3, patient.problem);
        pstmt.executeUpdate();

        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1); // Return generated patient_id
        }
        return -1;
    }

    // Method to insert an appointment into the database
    private static void addAppointment(Connection connection, Doctor doctor, int patientId, String date) throws SQLException {
        String sql = "INSERT INTO appointments (doctor_id, patient_id, date) VALUES (?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, getDoctorId(connection, doctor));
        pstmt.setInt(2, patientId);
        pstmt.setString(3, date);
        pstmt.executeUpdate();
    }

    // Method to retrieve doctor_id from the database based on doctor name
    private static int getDoctorId(Connection connection, Doctor doctor) throws SQLException {
        String sql = "SELECT doctor_id FROM doctors WHERE name = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, doctor.name);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("doctor_id");
        }
        return -1;
    }
}
