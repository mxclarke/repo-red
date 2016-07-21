package mxc.demo.campus.services;

import java.util.Collection;

import mxc.demo.campus.domain.Lecturer;
import mxc.demo.campus.view.LecturerView;

/**
 * Service for retrieving and modfifying users who are lecturers.
 */
public interface LecturerService {

	Collection<LecturerView> getLecturers();

	Lecturer getLecturerById(long id);
	
	Lecturer save(Lecturer lecturer);
}
