package mxc.demo.campus.bootstrap;

/**
 * THis is a development-only class for producing usernames,
 * passwords and so on for a set of dummy users.
 * 
 * A real password generator (to supply
 * passwords to users on registration, to be changed at first login)
 * would supply something random and more robust.
 * 
 * Note: passwords are encrypted by the domain object.
 */
public class UserCredentialsGenerator {

	/**
	 * Generates a user's userId based on their name.
	 * 
	 * Example: John SMith's username would be jnsh
	 * 
	 * @param firstName must not be empty
	 * @param lastName must not be empty
	 * @return a lower-case username in the form
	 * first-letter-of-firstname + last-letter-of-first-name
	 * + first-letter-of-last-name + last-letter-of-last-name
	 */
	public static String generateUserName(String firstName, String lastName) {
		
		StringBuilder sb = new StringBuilder();
		sb.append(firstName.toLowerCase().charAt(0));
		sb.append(firstName.toLowerCase().charAt(firstName.length()-1));
		sb.append(lastName.toLowerCase().charAt(0));
		sb.append(lastName.toLowerCase().charAt(lastName.length()-1));
		
		return sb.toString();
	}
	
	/**
	 * Generates a non-random password for the user, based on
	 * their username, and returns an encrypted version of that
	 * password using BCryptPasswordEncoder.
	 * 
	 * Example: John SMith's password would be pwjnsh
	 * 
	 * @param firstName must not be empty
	 * @param lastName must not be empty
	 * @return a lowercase password hash for the user from 
	 * "pw" + username
	 */
	public static String generatePassword(String firstName, String lastName) {
		
		String password = "pw" + generateUserName(firstName,lastName);
		return password;
	}
}
