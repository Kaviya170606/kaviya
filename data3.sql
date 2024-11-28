
SELECT 
    appointments.appointment_id,
    doctors.name AS doctor_name,
    doctors.specialization,
    patients.name AS patient_name,
    patients.problem,
    appointments.date
FROM 
    appointments
JOIN 
    doctors ON appointments.doctor_id = doctors.doctor_id
JOIN 
    patients ON appointments.patient_id = patients.patient_id;