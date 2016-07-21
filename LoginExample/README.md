# Login example with Spring Security

## Introduction

This is a small project to demonstrate the use of:

- Basic sign-in capability with [Spring Security 4](http://projects.spring.io/spring-security/)
- Spring MVC 4 with [Spring Boot](http://projects.spring.io/spring-boot/)
- [Thymeleaf](http://www.thymeleaf.org/)
- Automated testing with Selenium Web Driver

Spring Boot favours convention over configuration, and makes it easy to 
quickly get a web application up and running. It features an embedded
servlet container (e.g. Tomcat, Jetty) and requires no XML configuration
whatsoever.

Thymeleaf is a server-side Java template engine that has become popular
as a replacement for JSPs.

The project also utilises:

- Spring Data JPA 
- H2 in-memory database
- [Webjars](http://www.webjars.org)

The project was developed using the Eclipse Mars IDE. I have not attempted to build and run the project using any other IDE.

## Setting up and Running

Since this is a Maven project, it should only be necessary to:

- generate a Maven update and build
- run mxc.demo.login.LoginApplication as a Java application
- browse to http://localhost:8080

If you'd like to log in as an "uber"-user, you need to provide the following credentials:

- username = __adminUser__
- password = __admin__

The app also provides a number of "pleb" users. For example, you can log in as Jessica Smith (not a real person) as follows:

- username = __jash__
- password = __pwjash__

The complete list of users is logged to the console on startup, so if you wish you can try others. With
the exception of the uber-user, IDs and passwords are constructed using the first and last letters of the first and last names, as shown above.

## It does do, it doesn't do

There are two roles: Uber and Pleb. Anonymous users can access the home page at "/", and
the login page. Plebs can also access the pleb view. Uber users can also access the admin
pages, i.e. everything.

If an anonymous user attempts to access any page which requires authorisation, they
will be presented with the login screen. Upon successful login, they will be taken
to the desired page, or back to home if they are not authorised.

If an authenticated user attempts to access a page for which they are not authorised,
they will be taken to an "access denied" page.

If a user explicitly accesses the login page and successfully logs in, they will be
taken to the home page.

An unsuccessful login attempt will return to the login page with an "invalid" message.

When users log out, they are returned to the login page with a message to that effect.

If a user is logged in on one browser, then attempts to login on another, the attempt
will succeed but the original session will have expired and any further attempts to
access the session on the first browser will present the user with the login page.
Furthermore, the successful login on the second browser will take the user to the
"expired" page, to warn them that they are already logged in elsewhere and to change
their password if they suspect someone else is accessing their account.

The app doesn't . . . 

- The first thing you'll notice is that your web app still won't be secure unless you address
the matter of its transport protocol -- if you're using http rather than https, all your
carefully encrypted passwords will travel across the wire in plain text, leaving you vulnerable
to man-in-the-middle attacks. To do this, you'll need to set up SSL. Assuming you are going
to deploy with embedded Tomcat (you can also generate a WAR file and 
[deploy traditionally](http://docs.spring.io/spring-boot/docs/current/reference/html/howto-traditional-deployment.html))
once you [have](https://looksok.wordpress.com/2014/11/16/configure-sslhttps-on-tomcat-with-self-signed-certificate/) 
your [certificate](http://stackoverflow.com/questions/29522114/how-to-add-self-signed-ssl-certificate-to-jhipster-sample-app) 
(see below) you only need to [update application.properties]
(http://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-servlet-containers.html#howto-configure-ssl), and there's an example in Github [here](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-tomcat-ssl).
- In the interest of simplicity, the app does not include a registration mechanism or a
change-password mechanism.
- Ideally, the app should redirect the user to a suitable page based on their role. Such an app would
only need a single login button. I'm planning to do things that way in my next project.
- It is also possible to match on, for example, a user's id, to ensure that User1 cannot see User2's
details. Again, this will be handled in a future project when admin users have the
ability to add, delete and edit (CRUD) users.
- Ideally, the app should secure specific methods. For example, if you have a RESTful
service method to return all your users, you really only want your admins to be able to access it. Spring Security provides this functionality. Future project . . .

It does . . .

- The app demonstrates authentication. Anonymous users can only see the home page and
the login page.
- The app demonstrates role-based authorisation. A "pleb" user cannot see "uber" pages,
for example. If an authenticated non-uber user attempts to see an "uber" page, they
will be denied access.
- The Thymeleaf header displays the authenticated user (if any) and their role, and provides 
a sign-out button. This will appear on every page when a user is logged in.
- The app queries its database for the users, and all passwords are encrypted within the
database.
- CSRF tokens are handled automatically.

__Concerning certificates__: a self-signed certificate will give you https encryption, but a trusted certificate
from a Certificate Authority (CA) will also mean that your public web app won't be flagged as
potentially dangerous by browsers, thus providing you with server validity and your
customers with confidence.

Other suggested enhancements:

- Login sessions could have a timeout associated with them.
- The submit button on the login page could be disabled until the two fields are appropriately filled in.

## Testing

The app includes an automated integration test via Selenium WebDriver.

Remaining issues include:

- Only tested with Firefox so far, version 46.0.1 on Windows 7 Pro. What I'd like is
a way to paramaterise the entire test class/suite with various browsers, while
still retaining the Spring goodness. However, the issue is with runners, in that
there can be only one. A possible solution exists [here](http://stackoverflow.com/questions/28560734/how-to-run-junit-springjunit4classrunner-with-parametrized/28561473#28561473). See also [here](http://blog.codeleak.pl/2015/08/parameterized-integration-tests-with.html). Some
of these solutions parameterise methods only, whereas I hope to parameterise the whole
class. A very neat solution seemed to be on offer [here](https://github.com/NitorCreations/CoreComponents/wiki/WrappingParameterizedRunner), but
its WrappingParameterizedRunner attempts to write to "/tmp/x.class" and in many cases
will throw FileNotFoundException.
- Has been known to "fail" occasionally, despite waiting for staleness of elements, etc. 
I plan to chase that up in the light of comments [in this article](http://www.obeythetestinggoat.com/how-to-get-selenium-to-wait-for-page-load-after-a-click.html).




