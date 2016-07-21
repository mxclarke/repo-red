package mxc.demo.campus.view;

import com.fasterxml.jackson.annotation.JsonIgnore;

import mxc.demo.campus.validator.Password;

public class ChangePasswordForm {
	
	private long id;
	
	private String userId;
	
	private String firstName;
	
	private String lastName;
	
	// Put JsonIgnore on this just in case someone accidentally popuates it
	// with the existing password before sending the form to the client.
	@JsonIgnore
	private String oldPassword;

	@Password
	private String newPassword;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the oldPassword
	 */
	public String getOldPassword() {
		return oldPassword;
	}

	/**
	 * @param oldPassword the oldPassword to set
	 */
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	/**
	 * @return the newPassword
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * @param newPassword the newPassword to set
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChangePasswordForm [id=" + id + ", userId=" + userId + ", firstName=" + firstName + ", lastName="
				+ lastName + ", oldPassword=" + oldPassword + ", newPassword=" + newPassword + "]";
	}
}
