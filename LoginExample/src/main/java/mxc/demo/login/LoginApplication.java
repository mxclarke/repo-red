package mxc.demo.login;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Runs as a Java application with embedded Tomcat.
 */
@SpringBootApplication
public class LoginApplication {
	
	private static final Logger logger = Logger.getLogger(LoginApplication.class);
	
	public static void main(String[] args) {
		System.out.println("Starting LoginApplication [stdout]");
		logger.info("Starting LoginApplication");
		SpringApplication.run(LoginApplication.class, args);
	}

}
