package hospital.service.serviceImpl;

import hospital.exception.InvalidException;
import hospital.exception.NotFoundException;
import hospital.model.Appointment;
import hospital.model.Department;
import hospital.model.Doctor;
import hospital.model.Hospital;
import hospital.repository.AppointmentRepository;
import hospital.repository.DepartmentRepository;
import hospital.repository.DoctorRepository;
import hospital.repository.HospitalRepository;
import hospital.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final HospitalRepository hospitalRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public List<Department> getAllDepartment(Long departmentId) {
        try {
            return departmentRepository.getAllDepartment(departmentId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return departmentRepository.getAllDepartment(departmentId);
    }

    @Override
    public void saveDepartment(Department department, Long hospitalId) throws InvalidException {
        try {
            if (department != null && hospitalId != null) {
                validationName(department.getName());
                Hospital hospital = hospitalRepository.findById(hospitalId).orElseThrow();
                department.setHospital(hospital);
                departmentRepository.save(department);
            } else throw new Exception("Department Save Exception!!!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Department getDepartmentById(Long id) {
        try {
            return departmentRepository.findById(id).get();
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }
        return departmentRepository.findById(id).get();
    }

    @Override
    public void deleteDepartmentById(Long id) {
        try {
            Department department = departmentRepository.findById(id).get();
            for (Doctor doctor : department.getDoctors()) {
                department.getDoctors().remove(doctor);
            }
            for (Appointment appointment : department.getAppointments()) {
                department.getAppointments().remove(appointment);
            }
            departmentRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateDepartment(Long departmentId, Department department) {
        try {
            if (departmentId != null && department != null) {
                validationName(department.getName());
                Department department1 = departmentRepository.findById(departmentId).get();
                department1.setName(department.getName());
                departmentRepository.save(department1);
            } else throw new Exception("Update Exception!!!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void AssignDepartment(Long doctorId, Long departmentId) throws IOException {
        Department department = departmentRepository.findById(departmentId).get();
        Doctor doctor = doctorRepository.findById(doctorId).get();
        if (doctor.getDepartments() != null) {
            for (Department d : doctor.getDepartments()) {
                if (d.getId() == departmentId) {
                    throw new IOException("This Department already added...");
                }
            }
        }
        department.addDoctors(doctor);
        doctor.addDepartments(department);
        departmentRepository.save(department);
        doctorRepository.save(doctor);
    }

    @Override
    public void AssignDepartmentToAppointment(Long appointmentId, Long departmentId) throws IOException {
        Department department = departmentRepository.findById(departmentId).orElseThrow();
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
        if (appointment.getDepartment() != null) {
            for (Department d : appointment.getHospital().getDepartments()) {
                if (d.getId() == departmentId) {
                    throw new IOException("This Department already assigned....");
                }
            }
        }
        department.addAppointment(appointment);
        appointment.setDepartment(department);
        departmentRepository.save(department);
        appointmentRepository.save(appointment);
    }

    public void validationName(String name) {
        if (name.length() < 3
                || name.length() > 20
                || !name.matches("[A-Za-z]*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Department Name!!!");
        }
    }
}
