# Introduction

This is a small project (and a [work in progress](github.com/mxclarke/repo-red/tree/master/Campus#remaining-work)) to demonstrate the use of:

- [Spring Security 4](http://projects.spring.io/spring-security/) in a web application environment
- Spring MVC 4 with [Spring Boot](http://projects.spring.io/spring-boot/)
- Spring Data JPA
- [Thymeleaf](http://www.thymeleaf.org/)
- responsive UI mechanisms for web apps

Spring Boot favours convention over configuration, and makes it easy to 
quickly get a web application up and running. It features an embedded
servlet container (e.g. Tomcat, Jetty) and requires no XML configuration
whatsoever.

Thymeleaf is a server-side Java template engine that has become popular
as a replacement for JSPs.

The project also utilises:

- an H2 in-memory database
- [Webjars](www.webjars.org) for development purposes only

The project was developed using the Eclipse Mars IDE. I have not attempted to build and run the project using any other IDE.

## Description

The project is a (simplistic) web application for students, lecturers and administrators at a fictional university campus.

- Administrators have full access. The administrator's landing page provides them with a menu-based view for students, lecturers and courses. The administrator's landing page is responsive.
- Students can sign in and view their own details. They can also edit their own details, but do not have the ability to edit certain details such as their name and the amount they have paid towards their courses. They can select and de-select courses in which they wish to be enrolled. A student cannot view another student's details.
- Lecturers, too, can view and edit their own details.

The project is not yet finished. Please refer to the [todo list](github.com/mxclarke/repo-red/tree/master/Campus#remaining-work) below.

Note also that I have simplified certain use cases. For example, the change-password page does not require the user to repeat the password. (Should it? Some people have raised questions as to whether this is a helpful mechanism in the real world.)

Obviously, a real campus administration application would be much more complex than this. For example, courses would have pre-requisites, student quotas, course starting dates, deadlines for enrolment and dropping out, late enrolment fees, concession rates, the ability to set students and lecturers to an archived state rather than deleting them altogether, and so on. The single-role user is also naive, as there should be nothing to stop a lecturer or administrator from also being a student. The requirements as they stand, however, do demonstrate some interesting use cases and problems that frequently occur in customer-based web applications. 
 
# Overview

## Spring Security

This project builds on the work of my [previous project](github.com/mxclarke/repo-red/tree/master/LoginExample) to demonstrate role-based authentication. However, [as discussed](github.com/mxclarke/repo-red/tree/master/LoginExample#it-does-do-it-doesnt-do), if the Campus project were to be set up as it stands and without SSL, it would still not address the matter of the transport protocol. This means that all data, including passwords and other sensitive details, would travel across the wire using the HTTP protocol and in plain text. No web application should be deployed without addressing this concern.

The Campus app includes the following:

- protection against [CSRF](www.owasp.org/index.php/Cross-Site_Request_Forgery_(CSRF)), as handled automatically by Spring Security;
- a custom success handler, such that when a user logs in they are taken to the landing page appropriate for their role;
- an "access denied" page for unsuccessful sign-in attempts;
- [bcrypt](en.wikipedia.org/wiki/Bcrypt) password encoding;
- timeouts, as handled automatically by Spring Security;
- pre-authorisation for end points, preventing, for example, a student from accessing another's view page;
- session management on a last-login-wins basis, such that a session is expired if a user attempts to sign in elsewhere, 
thus increasing security and preventing zombie login sessions;
- validation of data on entry to the end points using custom annotations aggregating existing javax.validation.constraints
annotations, thus consolidating the validation of any field in one place;
- blocking of any data for which the user does not have permissions so that it is not sent through to the service layer.

Please note that password expiration and locking on multiple failed attempts has not been implemented. The latter is particularly important for the prevention of brute force attacks.

Finally, the configuration allows direct access to the H2 console without having to disable CSRF protection.
This should be disabled for a production environment.
           
## UI considerations

TODO

## DTOs/Value Objects

TODO

# Setting up and Running

Since this is a Maven project, it should only be necessary to:

- generate a Maven update and build
- run mxc.demo.campus.CampusApplication as a Java application
- browse to http://localhost:8080

This will take you to the public page. A button enabling users to sign in is available on the banner.
To log in as an administrator, you need to provide the following credentials:

- username = __adminUser__
- password = __admin__

The app also provides a number of student users (lecturers have not yet been properly implemented). The public page at localhost:8080 (home.html) provides some examples. Some students are already enrolled in courses, while others are not.

The complete list of users is logged to the console on startup, so if you wish you can try others. With
the exception of the administrator, usernames and passwords are constructed using the first and last letters of the first and last names, with "pw" prepended for passwords. Examples are given on the public page.

# Testing

An automated test suite (JUnit) is under development as mxc.demo.campus.CampusTestSuite.

The test suite includes integration tests (Selenium Web Driver). In particular, these assess the security concerns of the application.

Further tests will be added, in particular unit tests for the service layer.

# Remaining work

- Admin form for changing a person's password needs to be updated so that the admin doesn't have
to supply the old password.
- Need "home" and "public" icons on main banner for authorised users.
- Fix up the layouts, including on error, starting with the student view/form and password change form.
- Get money serialisation working as formatted strings, but so that Javascript has access to numeric values too.
- Create student.
- Turn delete confirmation popup into a decent modal overlay.
- Ensure all pages are responsive, not just admin landing page.
- Finish lecturers (CRUD).
- Courses: CRUD.
- Javadoc leaves a lot to be desired.
- Student payment system.
- Potenially reinstate session attribute and setting allowed fields in @InitBinder method
in StudentsController, since the alternative means putting the onus on the client to always
supply those fields.
- Consider how to integrate client-side with server-side validation seamlessly.
- Add student enrolment date.
- Get Selenium tests working again, i.e. issues with latest Firefox requiring Selenium update caused dependency incompatibilities.
For Chrome and IE, it seems that you need to download drivers and set these up in the tests. For FIrefox, the quick fix
is that you change Selenium's Maven scope to the default. However, this is hardly ideal and I would like to fix it
properly.
- Get Thymeleaf to strip comments from HTML and CSS. Minification and concatenation of Javascript files and stylesheets, or at least include documentation here as to how it's done.
- Complete documentation.
- Sticky "save" button on longer forms.
- Implement locking on multiple failed sign-in attempts to prevent brute force attacks.
- Shift Javascript into separate files, where appropriate.
- Possibly include icons on buttons, e.g. login, view, edit, etc.
- Fix money: at the moment it is represented by BigDecimal, and should probably change over to javax.money -- or wait for Java 9??
- Use profiles to separate out dev and prod components: https://spring.io/blog/2011/02/14/spring-3-1-m1-introducing-profile/ 
and http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html.

