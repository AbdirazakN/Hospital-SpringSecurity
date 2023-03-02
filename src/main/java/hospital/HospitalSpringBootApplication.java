package hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HospitalSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalSpringBootApplication.class, args);
    }

    // Spring Boot MVC проектке Security in memory кошунуздар.

    //3 роль болсун: ADMIN, DOCTOR, PATIENT.
    //Admin - бардык endpoint торго доступ болсун.
    //Doctor - Пациент тузуп, update жана delete кыла алат, appointment тузуп update жана delete кыла алат, department ти коро алат.
    //Patient - бардык страницаларды жон гана коро алат, appointment тузуп, update жана delete кыла алат.

}
