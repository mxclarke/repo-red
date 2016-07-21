package mxc.demo.campus.domain;

public enum UserRole {
	Student, Lecturer, Admin;
	
	/**
	 * Required for Spring security
	 * 
	 * @return the role name prepended with 'ROLE_'
	 */
	public String getWithRolePrefix() {
		return "ROLE_" + name();
	}
}
