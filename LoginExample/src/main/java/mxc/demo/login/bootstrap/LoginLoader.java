package mxc.demo.login.bootstrap;

import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import mxc.demo.login.domain.Pleb;
import mxc.demo.login.domain.UberUser;
import mxc.demo.login.repositories.UserRepository;


/**
 * Adding some start-up data into our H2 in-memory database.
 */
@Component
public class LoginLoader implements ApplicationListener<ContextRefreshedEvent>{

	// TODO development only!!
	private static final String UBER_USERNAME = "adminUser";
	private static final String UBER_PASSWORD = "admin";
	
	private final Logger logger = Logger.getLogger(LoginLoader.class);
	
	@Autowired
	@NotNull
	private UserRepository userRepo;
		
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		
		Pleb[] plebs = createPlebs();
		
		logger.info("Created " + plebs.length + " plebs, now saving to repository");
		for ( Pleb pleb : plebs ) {
			userRepo.save(pleb);
			logger.info("Saved " + pleb);
		}
		
		// Add a single Admin user.
		{
			String userId = UBER_USERNAME;
			String password = UBER_PASSWORD;
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String encryptedPassword = passwordEncoder.encode(password);
			UberUser admin = new UberUser(userId, encryptedPassword, "Jo", "Smith", "jsmith@someprovider.com");
			userRepo.save(admin);
			logger.info("Saved a uber-user, " + admin);
		}
	}
	
	private Pleb[] createPlebs() {
		
		// All names are made up. Any resemblance to any person, living or dead,
		// etc, etc, coincidental, etc.
		Pleb[] plebs = {
				createPleb("Julie","Banks")
				,createPleb("Matthew","Craig")
				,createPleb("Charles","Smith")
				,createPleb("Ashara","Singh")
				,createPleb("Julie","Nguyen")
				,createPleb("Michael","Lee")
				,createPleb("Daniel","Trident")
				,createPleb("Alison","Combs")
				,createPleb("Ananta","Saer")
				,createPleb("Shane","Williams")
				,createPleb("Jessica","Smith")
				,createPleb("Rebecca","Brown")
			};
						
			return plebs;
	}
	
	private Pleb createPleb(String firstName, String lastName) {
		
		String emailAddress = lastName + firstName.toLowerCase().charAt(0)
				+ "@someProvider.com";
		boolean isExternal = (firstName + lastName).hashCode() % 2 == 0;
		String userId = UserCredentialsGenerator.generateUserName(firstName, lastName);
		String encryptedPassword = UserCredentialsGenerator.generatePassword(firstName, lastName);
		
		return new Pleb(userId, encryptedPassword, firstName, lastName, emailAddress, isExternal);
	}
	
}
