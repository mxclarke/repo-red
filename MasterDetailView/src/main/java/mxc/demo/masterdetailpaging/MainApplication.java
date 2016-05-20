package mxc.demo.masterdetailpaging;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A Spring Boot app can be run as a Java application.
 */
@SpringBootApplication
public class MainApplication {
	
	private static final Logger logger = Logger.getLogger(MainApplication.class);

	public static void main(String[] args) {
		System.out.println("Starting MasterDetailPagingApplication [stdout]");
		logger.info("Starting MasterDetailPagingApplication");
		SpringApplication.run(MainApplication.class, args);
	}
}
