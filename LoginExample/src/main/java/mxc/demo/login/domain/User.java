package mxc.demo.login.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;

import org.hibernate.validator.constraints.NotBlank;

/**
 * An abstract class defining any user of the system.
 *
 * You need your hashcode()/equals() for paging/sorting because the returned
 * data is manipulated as a Collection. Note that I'm not using Lombok.
 */
@Entity
@Inheritance
@DiscriminatorColumn(name="USER_TYPE")
public abstract class User {
	
	@Id
	@GeneratedValue
	private long id;
	
	// We need an immutable and unique key for equals and hashcode.
	// This cannot be the generated ID (see below) and cannot be
	// name, email address etc, since these are not guaranteed to be
	// unique and cannot be final as they are subject to change.
	@NotBlank
	@Column(unique=true)
	private String userId;
	
	@NotBlank
	@Column(unique=true)
	private String password;
	
	@NotBlank
	private String firstName;
	@NotBlank
	private String lastName;
	
	// Users in this app only have one role. In a different application, they might
	// have a set of roles.
	@Enumerated(EnumType.STRING)
	private UserRole role;
	
	@NotBlank
	private String emailAddress;

	public User(String userId, String password, String firstName, String lastName, String emailAddress) {
		this.userId = userId;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		// Initially set role to the lowest common denominator
		this.role = UserRole.Pleb;
	}
	
	protected User() {
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}
	
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public UserRole getRole() {
		return role;
	}

	public final void setRole(UserRole role) {
		this.role = role;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Override
	public String toString() {
		return "id=" + id + ", userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", emailAddress=" + emailAddress;
	}

	// You need to establish equality based on the business key of the object,
	// which unfortunately cannot be the auto-generated key: 
	// you can't use the primary key because multiple unsaved objects all have 
	// a null PK and would be "equal". An object won't be equal to itself before and 
	// after being persisted, and hash-based collections won't work properly 
	// if the hash code changes during the collection's lifespan.
	// https://docs.jboss.org/hibernate/stable/core.old/reference/en/html/persistent-classes-equalshashcode.html
	// When considering uniqueness, even fullname + DOB is not guaranteed to be unique.
	// Names, email addresses, etc, are subject to change.
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

}
