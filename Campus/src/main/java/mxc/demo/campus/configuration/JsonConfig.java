package mxc.demo.campus.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

@Configuration
public class JsonConfig {

	   @Autowired
	   void configureObjectMapper( final ObjectMapper jacksonMapper ) {
	    //objectMapper.disable( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS );
		   jacksonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		   jacksonMapper.registerModule(new MrBeanModule());
	   }
}
