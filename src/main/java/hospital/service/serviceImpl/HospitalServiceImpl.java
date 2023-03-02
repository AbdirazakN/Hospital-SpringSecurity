package hospital.service.serviceImpl;

import hospital.model.Hospital;
import hospital.repository.HospitalRepository;
import hospital.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {
    private final HospitalRepository hospitalRepository;

    @Override
    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    @Override
    public void saveHospital(Hospital hospital) {
        try {
            if (validationName(hospital.getName())
                    && validationAddress(hospital.getAddress())
                    && hospital != null) {
                hospitalRepository.save(hospital);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not saved...");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Hospital getHospitalById(Long id) {
        return hospitalRepository.findById(id).get();
    }

    @Override
    public void deleteHospitalById(Long id) {
        hospitalRepository.deleteById(id);
    }

    @Override
    public void updateHospital(Hospital hospital) {
        try {
            if (validationName(hospital.getName())
                    && validationAddress(hospital.getAddress())
                    && hospital != null) {
                hospitalRepository.save(hospital);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Updated...");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Hospital> search(String keyWord) {
        if (keyWord != null && !keyWord.trim().isEmpty()) {
            return hospitalRepository.search("%" + keyWord + "%");
        } else {
            return hospitalRepository.findAll();
        }
    }

    public boolean validationName(String name) {
        try {
            if (name.equals(null)
                    || name.length() < 3
                    || name.length() > 20
                    || !name.matches("[A-Za-z]*")) {
                throw new Exception("Hospital name Exception...");
            } else {
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean validationAddress(String address) {
        try {
            if (address.equals(null)
                    || address.length() < 3
                    || address.length() > 20
                    || !address.matches("[A-Za-z0-9 .]*")) {
                throw new Exception("Hospital address Exception...");
            } else {
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
