package hospital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public UserDetailsService userDetails(){
        User.UserBuilder userBuilder=User.withDefaultPasswordEncoder();
        UserDetails admin = userBuilder
                .username("Admin")
                .password("admin123")
                .roles("ADMIN")
                .build();
        UserDetails doctor = userBuilder
                .username("Doctor")
                .password("doctor123")
                .roles("DOCTOR")
                .build();
        UserDetails patient = userBuilder
                .username("Patient")
                .password("patient123")
                .roles("PATIENT")
                .build();
        return new InMemoryUserDetailsManager(admin,doctor,patient);
    }
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()

                .requestMatchers("/hospitals").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")                   // Hospital
                .requestMatchers("/hospitals/new").hasRole("ADMIN")
                .requestMatchers("/hospitals/save").hasRole("ADMIN")
                .requestMatchers("/hospitals/{hospitalId}/delete").hasRole("ADMIN")
                .requestMatchers("/hospitals/edit/{id}").hasAnyRole("ADMIN")

                .requestMatchers("/doctors").hasAnyRole("ADMIN","DOCTOR","PATIENT")                       // Doctors
                .requestMatchers("/{id}/newDoctor").hasAnyRole("ADMIN","DOCTOR")
                .requestMatchers("/{id}/saveDoctor").hasAnyRole("ADMIN","DOCTOR")
                .requestMatchers("/edit/{id}").hasAnyRole("ADMIN","DOCTOR")
                .requestMatchers("/{id}/{doctorId}/update").hasAnyRole("ADMIN","DOCTOR")

                .requestMatchers("/departments").hasAnyRole("ADMIN","DOCTOR","PATIENT")                   // Department
                .requestMatchers("/{id}/newDepartment").hasRole("ADMIN")
                .requestMatchers("/{id}/saveDepartment").hasRole("ADMIN")
                .requestMatchers("/edit/{id}").hasRole("ADMIN")
                .requestMatchers("/{id}/{departmentId}/update").hasRole("ADMIN")
                .requestMatchers("/{id}/{hosId}").hasRole("ADMIN")

                .requestMatchers("/patients").hasAnyRole("ADMIN", "DOCTOR","PATIENT")                     // Patient
                .requestMatchers("/patients/{id}/newPatient").hasAnyRole("ADMIN","DOCTOR")
                .requestMatchers("/patients/savePatient").hasAnyRole("ADMIN","DOCTOR")
                .requestMatchers("/patients/edit/{patientId}").hasAnyRole("ADMIN","DOCTOR")
                .requestMatchers("/patients/{id}/{hosId}").hasAnyRole("ADMIN","DOCTOR")

                .requestMatchers("/appointments").hasAnyRole("ADMIN", "DOCTOR","PATIENT")                 // Appointment
                .requestMatchers("/{id}/newAppointment").hasAnyRole("ADMIN", "DOCTOR","PATIENT")
                .requestMatchers("/{id}/saveAppointment").hasAnyRole("ADMIN", "DOCTOR","PATIENT")
                .requestMatchers("/edit/{id}").hasAnyRole("ADMIN", "DOCTOR","PATIENT")
                .requestMatchers("/{id}/{hosId}").hasAnyRole("ADMIN", "DOCTOR","PATIENT")
                .and()
                .formLogin()
                .defaultSuccessUrl("/hospitals")
                .permitAll();
        return http.build();
    }
}
