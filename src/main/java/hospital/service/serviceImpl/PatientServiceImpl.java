package hospital.service.serviceImpl;

import hospital.exception.NotFoundException;
import hospital.model.Appointment;
import hospital.model.Hospital;
import hospital.model.Patient;
import hospital.repository.AppointmentRepository;
import hospital.repository.HospitalRepository;
import hospital.repository.PatientRepository;
import hospital.service.PatientService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final HospitalRepository hospitalRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public List<Patient> getAllPatient(Long patientId) {
        return patientRepository.getAllPatient(patientId);
    }

    @Override
    public void savePatient(Patient patient, Long hospitalId) {
        try {
            if (hospitalRepository.findById(hospitalId).get() != null && patient != null) {
                Hospital hospital = hospitalRepository.findById(hospitalId).get();
                for (Patient patient1 : hospital.getPatients()) {
                    if (patient1.getEmail().equals(patient.getEmail())) {
                        throw new ResponseStatusException(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED, "This e-mail already added...");
                    }
                }
                firstLastnameValidation(patient.getFirstName());
                firstLastnameValidation(patient.getLastName());
                emailValidation(patient.getEmail());
                genderValidation(patient.getGender());
                phoneValidation(patient.getPhoneNumber());
                patient.setHospital(hospital);
                hospital.plusPatient();
                patientRepository.save(patient);
            } else throw new NotFoundException(String.format("Hospital with id %d not found", hospitalId));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id).get();
    }

    @Override
    public void deletePatientById(Long id) {
        patientRepository.deleteById(id);
    }

    @Override
    public void updatePatient(Long patientId, Patient patient) {
        try {
            if (patientId != null && patient != null) {
                firstLastnameValidation(patient.getFirstName());
                firstLastnameValidation(patient.getLastName());
                genderValidation(patient.getGender());
                phoneValidation(patient.getPhoneNumber());
                emailValidation(patient.getEmail());

                Patient patient1 = patientRepository.findById(patientId).orElseThrow();
                patient1.setFirstName(patient.getFirstName());
                patient1.setLastName(patient.getLastName());
                patient1.setGender(patient.getGender());
                patient1.setPhoneNumber(patient.getPhoneNumber());
                patient1.setEmail(patient.getEmail());
                patient1.setAppointments(patient.getAppointments());
                patientRepository.save(patient1);
            } else throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Update failed...");
        } catch (ResponseStatusException e) {
            System.out.println(e.getHeaders());
        }
    }

    @Override
    public void assignPatient(Long appointmentId, Long patientId) throws IOException {
        Patient patient = patientRepository.findById(patientId).get();
        Appointment appointment = appointmentRepository.findById(appointmentId).get();
        if (appointment.getPatient() != null) {
            for (Patient p : appointment.getHospital().getPatients()) {
                if (p.getId() == patientId) {
                    throw new IOException("This Patient already added....");
                }
            }
        }
        patient.addAppointment(appointment);
        appointment.setPatient(patient);
        patientRepository.save(patient);
        appointmentRepository.save(appointment);
    }

    public void phoneValidation(String phoneNumber) {
        if (phoneNumber.length() == 13
                && phoneNumber.charAt(0) == '+'
                && phoneNumber.charAt(1) == '9'
                && phoneNumber.charAt(2) == '9'
                && phoneNumber.charAt(3) == '6') {
                int count = 0;
                for (Character i : phoneNumber.toCharArray()) {
                    if (count != 0) {
                        if (!Character.isDigit(i)) {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid number!!!");
                        }
                    }
                    count++;
                }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid number!!!");
        }
    }

    public void emailValidation(String email) {
        if (email.trim().length() < 13
                || email.length() > 30
                || !email.matches("[0-9A-Za-z.-@]*")
                || !(email.endsWith("@mail.ru")
                || email.endsWith("@gmail.com")
                || email.endsWith("@inbox.ru")
                || email.endsWith("@email.com"))) {
            throw new ResponseStatusException(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED, "Invalid E-mail...");
        }
    }

    public void firstLastnameValidation(String name) {
        if (name.trim().length() < 3
                || name.length() > 20
                || !name.matches("[A-Za-z ]*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid First or Lastname!!!");
        }
    }

    public void genderValidation(String gender) {
        if (!(gender.equals("MALE")
                || gender.equals("FEMALE"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Gender!!!");
        }
    }
}
