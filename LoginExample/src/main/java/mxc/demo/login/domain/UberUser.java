package mxc.demo.login.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * The admin user(s).
 */
@Entity
@DiscriminatorValue("U")
public class UberUser extends User {

	public UberUser(String userId, String password, String firstName, String lastName, String emailAddress) {
		super(userId, password, firstName, lastName, emailAddress);
		this.setRole(UserRole.Uber);
	}
	
	public UberUser() {
	}

	@Override
	public String toString() {
		return "Admin [" + super.toString() + "]";
	}
}
