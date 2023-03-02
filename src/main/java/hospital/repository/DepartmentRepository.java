package hospital.repository;

import hospital.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DepartmentRepository extends JpaRepository<Department,Long> {
@Query("select d from Department d where d.hospital.id=:departmentId")
    List<Department> getAllDepartment(Long departmentId);
}
