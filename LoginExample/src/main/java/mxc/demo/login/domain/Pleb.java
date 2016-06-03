package mxc.demo.login.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * A lowly user entity.
 */
@Entity
@DiscriminatorValue("P")
public class Pleb extends User {

	/**
	 * An additional attribute for this type of user.
	 */
	private boolean external = false;

	public Pleb(String userId, String password, String firstName, String lastName, String emailAddress, boolean external) {
		super(userId, password, firstName, lastName, emailAddress);
		this.external = external;
		this.setRole(UserRole.Pleb);
	}

	public Pleb() {
	}

	public boolean isExternal() {
		return external;
	}

	public void setExternal(boolean external) {
		this.external = external;
	}

	@Override
	public String toString() {
		return "Student [" + super.toString() + ", external=" + external 
				+ ", toString()=]";
	}

}
