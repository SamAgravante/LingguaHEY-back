package edu.cit.lingguahey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LingguaheyApplication {

	public static void main(String[] args) {
		SpringApplication.run(LingguaheyApplication.class, args);
	}

	/*@Bean
	public CommandLineRunner commandLineRunner(AuthenticationService authenticationService) {
		return args -> {
			var admin = RegisterRequest.builder()
				.firstName("Admin")
				.middleName("D")
				.lastName("Admin")
				.email("admin@mail.com")
				.password("1388582293")
				.subscriptionStatus(true)
				.role(ADMIN)
				.build();
			System.out.println("Admin token: " + authenticationService.register(admin).getToken());
		};
	}*/

}
