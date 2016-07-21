package mxc.demo.campus.services;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mxc.demo.campus.domain.Lecturer;
import mxc.demo.campus.repositories.LecturerRepository;
import mxc.demo.campus.view.LecturerView;

@Service
public class LecturerServiceImpl extends AbstractPagingService<Lecturer, Long> 
	implements LecturerService {

	private final LecturerRepository repo;
	
	private final BeanManagerService beanManagerService;
	
	@Autowired
	public LecturerServiceImpl(
			LecturerRepository repo, 
			BeanManagerService beanManagerService,
	    		@Qualifier("lecturers") FilterBuilder lecturerFilterBuilder) {
	    	super(lecturerFilterBuilder);
	    	this.repo = repo;
	    	this.beanManagerService = beanManagerService;
	    }

	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.LecturerService#save(mxc.demo.campus.domain.Lecturer)
	 */
	@Override
	@Transactional
	public Lecturer save(Lecturer lecturer) {
		return repo.save(lecturer);
		/*
		Lecturer lecturer = repo.findOne(lecturerDTO.getId());
		if ( lecturer == null ) {
			// Creating a new lecturer
			lecturer = new Lecturer();
			
		}
		// Update
		beanCopier.copyAllProperties(lecturerDTO, lecturer);

		return repo.save(lecturer);
		*/
	}

	private LecturerView getLecturer(final Lecturer lecturer) {
		
		LecturerView lecturerView = new LecturerView();
		beanManagerService.copyPropertiesExcept(lecturer, lecturerView);
		return lecturerView;
	}
	
	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.LecturerService#getLecturers()
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_Admin')")
	@Transactional(readOnly = true)
	public Collection<LecturerView>  getLecturers() {
		Iterable<Lecturer> persistedLecturers = repo.findAll();
		
		List<LecturerView> viewLecturers =
				StreamSupport.stream(persistedLecturers.spliterator(), false)
				.map(pl -> getLecturer(pl))
				.collect(Collectors.toList());
		
		return viewLecturers;
	}

	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.LecturerService#getLecturerById(long)
	 */
	@Override
	@Transactional(readOnly = true)
	public Lecturer getLecturerById(long id) {
		return repo.findOne(id);
	}
}
