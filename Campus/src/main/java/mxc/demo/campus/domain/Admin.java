package mxc.demo.campus.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Domain entity for a user who has administrator privileges.
 */
@Entity
@DiscriminatorValue("A")
public class Admin extends User {

	public Admin(String userId, String password, String firstName, String lastName, String emailAddress) {
		super(userId, password, firstName, lastName, emailAddress, UserRole.Admin);
	}
	
	protected Admin() {
		setRole(UserRole.Admin);
	}

	@Override
	public String toString() {
		return "Admin [" + super.toString() + "]";
	}
}
