CREATE DATABASE DoctorAppointmentDB;

USE DoctorAppointmentDB;

-- Create a table for storing doctor information
CREATE TABLE doctors (
    doctor_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    specialization VARCHAR(50) NOT NULL
);

-- Create a table for storing patient information
CREATE TABLE patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    problem VARCHAR(100) NOT NULL
);

-- Create a table for storing appointment information
CREATE TABLE appointments (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    doctor_id INT,
    patient_id INT,
    date VARCHAR(20),
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
);