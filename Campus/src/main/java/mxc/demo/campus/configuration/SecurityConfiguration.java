package mxc.demo.campus.configuration;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import mxc.demo.campus.domain.UserRole;

/**
 * Make adjustments to Spring's basic security mechanism.
 * 
 * If Spring Security is on the classpath, Spring Boot automatically
 * secures all end-points with basic authentication. Here, we 
 * specify authentication and authorisation.
 * 
 * Note that the CSRF token will be included in the login page at runtime by Thymeleaf, thanks
 * to the @EnableWebSecurity annotation, which also ensures that we do not
 * have to configure CSRF tokens in this class.
 * The @EnableGlobalMethodSecurity annotation allows us to use 
 * @PreAuthorize and @PostAUthorize on our service (and controller) methods.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	/**
	 * This service is used to authenticate our users, and we must provide
	 * an implementation. In this class, we register it with the
	 * AuthenticationManagerBuilder.
	 */
    @Autowired
    @NotNull
    private UserDetailsService userDetailsService;
    
    /**
     * This custom AuthenticationSuccessHandler will redirect each user to the 
     * appropriate page based on their role once they login in.
     */
   @Autowired
   @NotNull
   private SimpleUrlAuthenticationSuccessHandler customLoginSuccessHandler;

	/**
	 * The configure(HttpSecurity) method defines which URL paths should 
	 * be secured and which should not, based on user roles.
	 */
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		
		// For your first app, you'll want to allow local Tomcat to run without
		// authorisation so you don't have to supply credentials when you
		// browse to localhost:8080. So you would have the following line 
		// which of course has now been commented out, because we do want
		// to introduce proper authorisation to various pages.
		// httpSecurity.authorizeRequests().antMatchers("/").permitAll();  // typically development only
	
		// Now for the real configuration . . .
		// The following mods to httpSecurity say that the path "/" and the
		// path to the login page "/login" do not require authentication, 
		// whereas every other path does.
		// The antmatchers match (reg exps) on the path that goes to the 
		// controllers. For example, ** matches zero or more directories in a path
		// so /admin/** matches the pat /admin, /admin/abc, /admin/abc/xyz
		// and so on.
		httpSecurity
         .authorizeRequests()
         	.antMatchers("/").permitAll()
         //	.antMatchers("/test/**").permitAll()
         	.antMatchers("/h2-console/**").permitAll() // access to H2 db console
         	.antMatchers("/courses/**").permitAll()
         	.antMatchers("/course/**").permitAll()
             .antMatchers("/admin/**").hasRole(UserRole.Admin.name())
             .antMatchers("/lecturer/**").hasAnyRole(UserRole.Admin.name(), UserRole.Lecturer.name())
             .antMatchers("/student/**").hasAnyRole(UserRole.Admin.name(), UserRole.Student.name())
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
             .successHandler(customLoginSuccessHandler) // redirections based on role
             .and()
         .logout()
             .permitAll()
             .and()
          .exceptionHandling()
          	.accessDeniedPage("/accessdenied")
          	.and()
            // Spring Security requires a HttpSessionListener (HttpSessionEventPublisher) 
            // to be registered, if you wish your app to exhibit reasonable behaviour such
            // as logging the user out (expiring the session) if they attempt to log
            // in elsewhere, increasing security and preventing zombie login sessions.
            // For example, if Sue is logged in at uni, forgets to log out, then attempts
            // to log in at home, she will succeed, but the following configuration ensures
            // that her uni login will have expired and an attempt to refresh her page 
            // on the uni browser will result in the user being redirected to the login page.
            // This works on a last-login-wins basis.
            // Furthermore you can, if you wish, set an expired page to warn the user
            // the "someone" is logged in to their account elsewhere to give them the
            // heads-up should anyone be using their account illegally.
            .sessionManagement()
            	.maximumSessions(1)
            	.expiredUrl("/expired")
            	.sessionRegistry(sessionRegistry())
            ;
		
		// Add the following so that you  can access the
		// H2 console without having to disable CSRF protection.
		RequestMatcher requestMatcher = new RequestMatcher() {
			private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
			//private RegexRequestMatcher apiMatcher = new RegexRequestMatcher("/h2-console/.*", null);
			private final String[] allowedUrls = {"/h2-console"};
			@Override
			public boolean matches(HttpServletRequest request) {
				// First, just mimic the behaviour of the default filter, i.e. we don't apply CSRF
				// protection to any of the allowed methods.
				if (allowedMethods.matcher(request.getMethod()).matches()) {
					return false;
				}
				// Method of request is something else, such as POST. Can it slip through the
				// net anyway? Yes, if it matches our H2 console url.
				String uri = request.getRequestURI();
				for ( String allowedUrl : allowedUrls ) {
					if (uri.startsWith(allowedUrl)) {
						return false;
					}					
				}
				
				// If reached here, should apply CSRF protection to the URI.				
				return true;
			}				
		};
		httpSecurity.csrf().requireCsrfProtectionMatcher(requestMatcher);
		
	    // Disable X-Frame-Options in Spring Security, because the H2 database
        // runs inside a frame. If you don't do this, you'll be able to log in
		// but you'll be greeted with a helpful blank screen.
        httpSecurity.headers().frameOptions().disable();
        
		
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
		// I am using webjars (for local development) so those paths need to be catered for as well.
		web.ignoring().antMatchers("/css/**", "/js/**", "/images/**", "/webjars/**");
	}

	/**
	 * @return a bean to be used for password encryption for the database
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
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
                .passwordEncoder(passwordEncoder());
        
        // For development purposes, you could instead set up an
        // in-memory user store with some dummy data, like this:
//        auth
//        .inMemoryAuthentication()
//            .withUser("fred").password("passwordfred").roles("USER").and()
//            .withUser("admin").password("password").roles("USER", "ADMIN");
   }
    
    // There is an issue in Spring Security in that SessionRegistry is not 
    // exposed as a Bean so there is no way for an ApplicationEvent to be 
    // published to it. This bean provides a workaround, as described here:
    // https://jira.spring.io/browse/SEC-2855
    // See also: https://github.com/spring-projects/spring-boot/issues/1537
    //
    // Note: you can now inject this SessionRegistry bean into a controller or
    // service, and log the current sessions, e.g.
    // final List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    
    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
    }

}
