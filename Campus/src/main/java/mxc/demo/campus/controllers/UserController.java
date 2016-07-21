package mxc.demo.campus.controllers;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mxc.demo.campus.domain.User;
import mxc.demo.campus.domain.UserRole;
import mxc.demo.campus.exception.NoSuchUserException;
import mxc.demo.campus.services.BeanManagerService;
import mxc.demo.campus.services.SecurityService;
import mxc.demo.campus.services.UserService;
import mxc.demo.campus.view.ChangePasswordForm;

/**
 * Controls access to the admin "home" screen.
 *
 * <ul>
 * <li>"/admin" = admin/adminview: admin home, showing views and controls
 *      for students, lecturers and courses</li>
 * <li>/changePassword/{id} = passwordform: retrieves the form for changing
 * 		a user's password</li>
 * </ul>
 */
@Controller
public class UserController {
	
	private static final Logger logger = Logger.getLogger(UserController.class);
	
	private final UserService userService;
	
	private final BeanManagerService beanManagerService;
	
	private final SecurityService securityService;
			
	/**
	 * With more recent versions of Spring you don't actually need the @Autowired
	 * annotation when using constructor DI, but I've left it in to make things clearer.
	 */
	@Autowired
	public UserController(UserService userService, BeanManagerService beanManagerService,
			SecurityService securityService) {
		this.userService = userService;
		this.beanManagerService = beanManagerService;
		this.securityService = securityService;
	}

	/**
	 * Retrieves the form for changing a user's password.
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/changePassword/{id}", 
			method = {RequestMethod.GET, RequestMethod.POST})
	@PreAuthorize("@campusSecurityService.canAccessUser(principal, #id)")
	public String changePasswordForm(@PathVariable Long id, Model model) {
		logger.debug("Accessing the form to change a user's password with id" + id);
		
		User user = userService.getUserById(id);
				
		if ( user == null ) {
			// This should be logged by ExceptionHandlerController
			throw new NoSuchUserException("User with id " + id + " cannot be located");
		} else {
			ChangePasswordForm form = new ChangePasswordForm();
			beanManagerService.copyPropertiesExcept(user, form);
			model.addAttribute("user",form);
			return "/passwordform";
		}
	}
	
	/**
	 * Updates the user's password. We don't have to do a great deal of validation in this
	 * method. All we care about is the usual authentication and authorisation, checking
	 * that the current password is legitimate, and the validation of the new password is
	 * handled by @Valid. The other fields are not used, but will be returned with the
	 * view in the event of error.
	 * @param id
	 * @param passwordForm
	 * @param result
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/updatePassword/{id}",  method = RequestMethod.POST)
	@PreAuthorize("@campusSecurityService.canAccessUser(principal, #id)")
	public String updatePassword(@PathVariable Long id,
			@ModelAttribute("user") @Valid ChangePasswordForm passwordForm, BindingResult result,
			RedirectAttributes redirectAttributes) {
		logger.debug("Updating password for user with id " + id);

		// Check old password and see if that needs to be added to the errors, if any.
		User user = userService.getUserById(id);
		if ( user == null ) {
			// This should be logged by ExceptionHandlerController
			throw new NoSuchUserException("User with id " + id + " cannot be located");
		} else {
			String userName = user.getUserId();
			if ( !securityService.checkPassword(userName, passwordForm.getOldPassword()) ) {
				result.rejectValue("oldPassword", "error.user", "Current password is invalid");
			}
			
			if ( result.hasErrors() ) {
				result.getAllErrors().forEach(logger::debug);
				return "/passwordform";
			} else {
				// Do NOT display the new password in the log.
				logger.debug("Changing user's password");	
				user.setPassword(passwordForm.getNewPassword());		
				user = userService.save(user);
				
				// Add a success message, which the client form may or may not display
				redirectAttributes.addFlashAttribute("updateMessage", "Password successfully modified");
					
				// Return the current user (admin or owner) to the view of the user being modified.
				// TODO later this could be a lecturer or admin so we'll check the person's role.
				if ( user.getRole() == UserRole.Student) {
					return "redirect:/student/" + user.getId();
				}
				logger.warn("Role " + user.getRole() + " not handled");
				
				return "redirect:/";
			}
		}
	}
	
}
