package mxc.demo.campus;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CampusApplication {
	
	private static final Logger logger = Logger.getLogger(CampusApplication.class);
	
	public static void main(String[] args) {
		logger.info("Starting CampusApplication");
	
		SpringApplication.run(CampusApplication.class, args);
	}

}
