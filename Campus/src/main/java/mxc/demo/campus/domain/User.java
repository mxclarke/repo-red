package mxc.demo.campus.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnore;

import mxc.demo.campus.validator.CampusEmail;
import mxc.demo.campus.validator.Password;
import mxc.demo.campus.validator.PasswordHash;
import mxc.demo.campus.validator.PersonName;
import mxc.demo.campus.validator.UserId;

/**
 * Superclass for user entities.
 * 
 * Note that our strategy here is to keep users in a single table. This has a performance
 * advantage, while the disadvantage is that there will end up being a great many unused
 * fields, so storage could be an issue.
 */
@Entity
@Inheritance
@DiscriminatorColumn(name="USER_TYPE")
public abstract class User {
		
	@Id
	@GeneratedValue
	private long id;
	
	/**
	 * The user's login/sign-in username.
	 * Assuming we might want to collect users in maps or sets, we will need
	 * to implement equals and hashcode. In that case, we need an immutable
	 * and unique key. This cannot be the primary key generated for persistence
	 * (see below) and cannot be name, email address etc, since these are subject to
	 * change (and might not be unique).
	 */
	@UserId
	@Column(unique=true, updatable=false)
	private String userId;
	
	/**
	 * The encrypted password.
	 * The BCrypt algorithm generates a String of length 60, so we need to make 
	 * sure that the password will be stored in a column that can accommodate it. 
	 * In this case I have set it to 100 (future-proofing, but probably overkill). 
	 * Either the @Size or @Column annotation will do the job.
	 */
	@PasswordHash
	@Column(nullable=false, length=100)
	@JsonIgnore
	private String password;
	
	@PersonName
	private String firstName;
	
	@PersonName
	private String lastName;
	
	/**
	 * Users in this university have a single role. In a real-world application,
	 * it's likely they could have more than one role.
	 */
	@Enumerated(EnumType.STRING)
	private UserRole role;
	
	@CampusEmail
	private String emailAddress;

	protected User(String userId, String password, String firstName, String lastName, String emailAddress, UserRole role) {
		this.userId = userId;
		this.setPassword(password);
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.role = role;
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
	
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	// TODO I can't see a problem with hashing the password here rather than in the
	// service, but I could be wrong. Investigate further.
	public void setPassword(@Password String password) {
		if ( password == null ) {
			this.password = null;
		} else {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			this.password = passwordEncoder.encode(password);
		}
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
	// which unfortunately cannot be the auto-generated key -- you can't use the primary 
	// key because multiple unsaved objects all have a null PK and would be "equal". 
	// An object won't be equal to itself before and after being persisted, and hash-based 
	// collections won't work properly if the hash code changes during the collection's lifespan.
	// When considering uniqueness, even something like fullname + DOB is not guaranteed to be unique.
	// Names, email addresses, etc, are all subject to change, and therefore cannot
	// be made final. Therefore I am using the "userId" as the unique and immutable key.
	// Even if the person's name changes, this should not.
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
