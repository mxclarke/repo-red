package mxc.demo.masterdetailpaging.bootstrap;

import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import mxc.demo.masterdetailpaging.domain.Student;
import mxc.demo.masterdetailpaging.repositories.StudentRepository;

/**
 * Adding some start-up data into our H2 in-memory database.
 * @author Moira
 */
@Component
public class StudentLoader implements ApplicationListener<ContextRefreshedEvent>{

	private Logger logger = Logger.getLogger(StudentLoader.class);
	
	@Autowired
	@NotNull
	private StudentRepository repo;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		Student[] students = {
			new Student("132A","Julie","Banks",Boolean.FALSE)
			, new Student("444X","Matthew","Craig",Boolean.TRUE)
			, new Student("234B","Charles","Smith",Boolean.FALSE)
			, new Student("765T", "Melissa", "West", Boolean.TRUE)
			, new Student("654L","Ashara","Singh", Boolean.FALSE)
			, new Student("123L","Julie","Nguyen", Boolean.FALSE)
			, new Student("898K","Michael","Lee", Boolean.FALSE)
			, new Student("443K","Daniel","Trident", Boolean.FALSE)
			, new Student("666J","Philip","Summers", Boolean.FALSE)
			, new Student("668J","Alison","Combs", Boolean.FALSE)
			, new Student("432G","Felicity","Myers", Boolean.FALSE)
			, new Student("0909G","Ananta","Saer", Boolean.FALSE)
			, new Student("678F","Kylie","Mason", Boolean.FALSE)
			, new Student("654F","Richard","Hope", Boolean.FALSE)
			, new Student("223D","Timothy","Hope", Boolean.TRUE)
			, new Student("333E","Shane","Williams", Boolean.TRUE)
			, new Student("444","Simon","Myers", Boolean.TRUE)
			, new Student("555","David","Peterson", Boolean.TRUE)
			, new Student("666","Damien","Summers", Boolean.TRUE)
			, new Student("777","Jessica","Smith", Boolean.TRUE)
			, new Student("888","Rachel","Brown", Boolean.TRUE)
			, new Student("999","Rebecca","Brown", Boolean.TRUE)
			, new Student("223","Annette","Green", Boolean.TRUE)
			, new Student("334","Xavier","Paris", Boolean.TRUE)
			, new Student("445","Yvonne","Winters", Boolean.TRUE)
			, new Student("556","Colin","Winters", Boolean.TRUE)
			, new Student("667","Fabian","East", Boolean.TRUE)
			, new Student("778","Gregory","London", Boolean.TRUE)
			, new Student("889","Jeremy","Keswick", Boolean.TRUE)
			, new Student("991","Kevin","Anderson", Boolean.TRUE)
			, new Student("112","Luke","Burns", Boolean.TRUE)
			, new Student("121","Sylvia","Burns", Boolean.TRUE)
			, new Student("232","Theresa","Leader", Boolean.TRUE)
			, new Student("343","Kieran","Lyle", Boolean.TRUE)
			//, new Student("","","", Boolean.TRUE)
		};
		
		logger.info("Created " + students.length + " students, now saving to repository");
		for ( Student student : students ) {
			repo.save(student);
		}
	
	}

}
