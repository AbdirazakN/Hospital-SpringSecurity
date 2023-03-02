package hospital.service.serviceImpl;

import hospital.model.Appointment;
import hospital.model.Doctor;
import hospital.model.Hospital;
import hospital.repository.AppointmentRepository;
import hospital.repository.DoctorRepository;
import hospital.repository.HospitalRepository;
import hospital.service.DoctorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final AppointmentRepository appointmentRepository;


    @Override
    public List<Doctor> getAllDoctors(Long doctorId) {
        return doctorRepository.getAllDoctors(doctorId);
    }

    @Transactional
    @Override
    public void saveDoctor(Doctor doctor, Long hospitalId) {
        try {
            if (doctor != null && hospitalId != null) {
                Hospital hospital = hospitalRepository.findById(hospitalId).orElseThrow();
                for (Doctor hospital1Doctor : hospital.getDoctors()) {
                    if (hospital1Doctor.getEmail().equals(doctor.getEmail())) {
                        throw new Exception("This E-mail already added...");
                    }
                }
                validationFirstLastName(doctor.getFirstName());
                validationFirstLastName(doctor.getLastName());
//                validationEmail(doctor.getEmail());
                validationPosition(doctor.getPosition());
                doctor.setHospital(hospital);
                hospital.plusDoctor();
                doctorRepository.save(doctor);
            } else throw new Exception("Invalid data to save...");
        } catch (Exception e) {
            System.out.println("Failed Saved...");
        }
    }

    @Override
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id).get();
    }

    @Override
    public void deleteDoctorById(Long id) {
        doctorRepository.deleteById(id);
    }

    @Override
    public void updateDoctor(Long doctorId, Doctor doctor) {
        try {
            if (!(doctor == null && doctorId == null)) {
                validationEmail(doctor.getEmail());
                validationFirstLastName(doctor.getFirstName());
                validationFirstLastName(doctor.getLastName());
                validationPosition(doctor.getPosition());
                Doctor doctor1 = doctorRepository.findById(doctorId).get();
                doctor1.setFirstName(doctor.getFirstName());
                doctor1.setLastName(doctor.getLastName());
                doctor1.setEmail(doctor.getEmail());
                doctor1.setPosition(doctor.getPosition());
                doctorRepository.save(doctor1);
            } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Update failed....");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void assignDoctor(Long appointmentId, Long doctorId) throws IOException {
        Doctor doctor = doctorRepository.findById(doctorId).get();
        Appointment appointment = appointmentRepository.findById(appointmentId).get();
        if (appointment.getDoctor() != null) {
            for (Doctor d : appointment.getHospital().getDoctors()) {
                if (d.getId() == doctorId) {
                    throw new IOException("This is already assigned....");
                }
            }
        }
        doctor.addAppointments(appointment);
        appointment.setDoctor(doctor);
        doctorRepository.save(doctor);
        appointmentRepository.save(appointment);
    }

    public void validationEmail(String email) {
        if (email.trim().length() < 13
                || email.length() > 30
                || !email.matches("[0-9A-Za-z.-@]*")
                || !(email.endsWith("@mail.ru")
                || email.endsWith("@gmail.com")
                || email.endsWith("@inbox.ru")
                || email.endsWith("@email.com"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid E-mail!!!");
        }
    }

    public void validationFirstLastName(String name) {
        if (name.length() < 3
                || name.length() > 30
                || !name.matches("[a-zA-Z ]*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid First or Lastname!!!");
        }
    }

    public void validationPosition(String position) {
        if (position.length() < 3
                || position.length() > 20
                || !position.matches("[a-zA-Z ]*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Position!!!");
        }
    }
}
