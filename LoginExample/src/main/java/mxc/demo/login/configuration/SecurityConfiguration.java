package mxc.demo.login.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import mxc.demo.login.domain.UserRole;

/**
 * Make adjustments to Spring's basic security mechanism.
 * 
 * If Spring Security is on the classpath, Spring Boot automatically
 * secures all end-points with basic authentication. Here, we 
 * specify authentication and authorisation.
 * 
 * Note that the CSRF token will be included at runtime by Thymeleaf thanks
 * to the @EnableWebSecurity annotation, which also ensures that we do not
 * have to configure CSRF tokens in this class.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	/**
	 * This service is used to authenticate our users, and we must provide
	 * an implementation. In this class, we register it with the
	 * AuthenticationManagerBuilder.
	 */
    @Autowired
    private UserDetailsService userDetailsService;
    
    // If you want to take the next step and redirect users to the
    // appropriate page based on their role once they are authenticated,
    // you need to supply an AuthenticationSuccessHandler. This will give
    // you a much better solution since there will only need to be a single
    // login widget.
   // @Autowired
   // private SimpleUrlAuthenticationSuccessHandler customLoginSuccessHandler;

	/**
	 * The configure(HttpSecurity) method defines which URL paths should 
	 * be secured and which should not, based on user roles.
	 */
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
			
		// The following mods to httpSecurity say that the path "/" and
		// the path to the login page "/login" do not require authentication, 
		// whereas every other path does.
		// The antmatchers match (reg exps) on the path that goes to the 
		// controllers. For example, ** matches zero or more directories in a path
		// so /admin/** matches the pat /admin, /admin/abc, /admin/abc/xyz
		// and so on.
		httpSecurity
         .authorizeRequests()
         	.antMatchers("/").permitAll()
             .antMatchers("/admin/**").hasRole(UserRole.Uber.name())
             .antMatchers("/pleb/**").hasAnyRole(UserRole.Uber.name(), UserRole.Pleb.name())
             // Any URL that has not already been matched on only requires that the user be authenticated:
            .anyRequest().authenticated()
             .and()
     		// The method formLogin().permitAll() instructs Spring Security 
     		// to allow any access to any URL (i.e. /login and /login?error) associated 
     		// with the specified login form.
         .formLogin()
             .loginPage("/login")
             .defaultSuccessUrl("/")
             .permitAll()   // everyone has access to the login page
       //      .successHandler(customLoginSuccessHandler) // redirections based on role
             .and()
         .logout()
             .permitAll()
             .and()
          .exceptionHandling().accessDeniedPage("/accessdenied")
          ;

		// You'll also see examples like this:
		// .antMatchers("/db/**").access("hasRole('ADMIN') and hasRole('DBA')")
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		
		super.configure(web);
		
		// Spring Boot will, by default, permit access to /css/**, /js/**, /images/**, 
		// and /**/favicon.ico, but @EnableWebSecurity switches that off.
		// We are better off with @EnableWebSecurity, but unfortunately that means
		// we have to explicitly tell Spring Security to bypasses our public resources.
		// I am using webjars (at the moment) so those paths need to be catered for as well.
		web.ignoring().antMatchers("/css/**", "/js/**", "/images/**", "/webjars/**");
	}

	/**
	 * Pass to Spring our custom implementation of its UserDetailsService, which
	 * (a) checks whether a user with a given username exists; and (b) provides
	 * Spring with other information such as the encrypted password, whether
	 * the account is active, the user's roles etc.
	 * 
	 * THere's a "development-only" version of this method commented out below,
	 * which is useful when you just want some dummy data to work with.
	 * 
	 * THe NAME of this method is unimportant. To clear up the surrounding
	 * confusion, see this explanation:
	 * http://stackoverflow.com/questions/35218354/difference-between-registerglobal-configure-configureglobal-configureglo
	 * Also be aware that while this method can be named anything, 
	 * it is important to only configure AuthenticationManagerBuilder in a class 
	 * annotated with either @EnableWebSecurity, @EnableGlobalMethodSecurity, or 
	 * @EnableGlobalAuthentication. Doing otherwise has unpredictable results.
	 */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
        
        // For development purposes, you could instead set up an
        // in-memory user store with some dummy data.
//        auth
//        .inMemoryAuthentication()
//            .withUser("fred").password("passwordfred").roles("USER").and()
//            .withUser("admin").password("password").roles("USER", "ADMIN");
   }
}
