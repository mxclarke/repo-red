# Introduction

This is a small project (and a [work in progress](https://github.com/mxclarke/repo-red/tree/master/Campus#remaining-work)) to demonstrate the use of:

- [Spring Security 4](http://projects.spring.io/spring-security/) in a web application environment
- Spring MVC 4 with [Spring Boot](http://projects.spring.io/spring-boot/)
- Spring Data JPA
- [Thymeleaf](http://www.thymeleaf.org/)
- responsive UI mechanisms for web apps
- HTML5/Javascript/CSS front end

Spring Boot favours convention over configuration, and makes it easy to 
quickly get a web application up and running. It features an embedded
servlet container (e.g. Tomcat, Jetty) and requires no XML configuration
whatsoever.

Thymeleaf is a server-side Java template engine that has become popular
as a replacement for JSPs.

The project also utilises:

- an H2 in-memory database
- [Webjars](http://www.webjars.org) for development purposes only

The project was developed using the Eclipse Mars IDE. I have not attempted to build and run the project using any other IDE.

## Description

The project is a (simplistic) web application for students, lecturers and administrators at a fictional university campus.

- Users can be students, lecturers or administrators.
- There are a number of courses. 
-- Each course may have a supervising lecturer, while a lecturer can supervise zero-to-many courses (bi-directional relationship).
- Each course can have zero-to-many students enrolled in it, and a student can be enrolled in zero-to-many courses (bi-directional relationship).
- Administrators have full access. The administrator's landing page provides them with a menu-based view for students, lecturers and courses. The administrator's landing page is responsive.
- Students can sign in and view their own details. They can also edit their own details, but do not have the ability to edit certain details such as their name and the amount they have paid towards their courses. They can select and de-select courses in which they wish to be enrolled. A student cannot view another student's details.
- Lecturers, too, can view and edit their own details.

The project is not yet finished. I have focused in the first instance on student editing and the administrator's landing page. Please refer to the [todo list](https://github.com/mxclarke/repo-red/tree/master/Campus#remaining-work) below.

Note also that I have simplified certain use cases. For example, the change-password page does not require the user to repeat the password. (Should it? Some people have raised questions as to whether this is a helpful mechanism in the real world.)

Obviously, a real campus administration application would be much more complex than this. For example, courses would have pre-requisites, student quotas, course starting dates, deadlines for enrolment and dropping out, late enrolment fees, concession rates, the ability to set students and lecturers to an archived state rather than deleting them altogether, and so on. The concept of a single-role user is also simplistic, as there should be nothing to stop a lecturer or administrator from also being a student (although there is also nothing to stop a person from having more than one user account). The requirements as they stand, however, do demonstrate some interesting use cases and problems that frequently occur in customer-based web applications. 
 
# Overview

## Spring Security

This project builds on the work of my [previous project](https://github.com/mxclarke/repo-red/tree/master/LoginExample) to demonstrate role-based authentication. However, [as discussed](https://github.com/mxclarke/repo-red/tree/master/LoginExample#it-does-do-it-doesnt-do), if the Campus project were to be set up as it stands and without SSL, it would still not address the matter of the transport protocol. This means that all data, including passwords and other sensitive details, would travel across the wire using the HTTP protocol and in plain text. No web application should be deployed without addressing this concern.

The Campus app includes the following:

- protection against [CSRF](http://www.owasp.org/index.php/Cross-Site_Request_Forgery_(CSRF)), as handled automatically by Spring Security;
- a custom success handler, such that when a user logs in they are taken to the landing page appropriate for their role;
- an "access denied" page for unsuccessful sign-in attempts;
- [bcrypt](https://en.wikipedia.org/wiki/Bcrypt) password encoding;
- timeouts, as handled automatically by Spring Security;
- pre-authorisation for end points, preventing, for example, a student from accessing another's view page;
- session management on a last-login-wins basis, such that a session is expired if a user attempts to sign in elsewhere, 
thus increasing security and preventing zombie login sessions;
- validation of data on entry to the end points using custom annotations aggregating existing javax.validation.constraints
annotations, thus consolidating the validation of any field in one place;
- filtering of outbound data;
- allowing the JPA provider to process query arguments so that SQL injection attacks are prevented;
- blocking of any incoming data for which the user does not have permissions so that it is not sent through to the service layer.

The last point is particularly important when considering the possibility of malicious proxy apps or malicious users modifying user-names and IDs to gain access to other people's accounts.

Please note that password expiration and locking on multiple failed attempts has not been implemented. The latter is particularly important for the prevention of brute force attacks.

Finally, the configuration allows direct access to the H2 console without having to disable CSRF protection.
This should be disabled for a production environment.
           
## UI considerations

- The application employs tables for displaying some of the data (e.g. lists of users, lists of courses), this being one of the more difficult use cases for front-end web applications. The issues include: a) how the table should be displayed in responsive scenarios for screens of varying sizes; b) server-side pagination for large datasets.

  I have used JQuery Datatables in the past, and found them to be feature-rich and robust. I have used them in this app with Bootstrap styling for the table of students and table of courses on the admin's landing page. In this application I have also experimented with Footables, which looked very promising, and have used it for the list of lecturers on the admin's landing page. Footables, however, has a possible disadvantage in that it doesn't currently provide an easy solution for server-side paging. If your use cases are such that you can be sure your datasets are always going to be a modest size, this won't be a problem -- otherwise, JQuery Datatables are the better choice.

  Obviously, in a real-world app, you would need to choose one for the sake of consistency.

- The _Campus_ app will be responsive. For this reason, the students table on the admin's landing page has a single _View_ button, rather than one each of _View_, _Modify_ and _Delete_ (I have yet to do the conversion for the other tables). Operations such as editing, changing passwords and deletion are then available from the student view page.

  However, how this is handled in a real-world app would depend on the results of business analysis. I'd expect that students and lecturers are likely to be using tablets and phones, but do the admins actually need responsive screens? If they do, should the _View_ button be replaced with a drop-down combo box for the view/edit/delete operations, rather than having to go to a new screen to access them? If not, should the _View_ button be replaced by a more intuitive icon, or a more descriptive word such as _Manage_? Are there situations in which admins need to use multi-row deletion/archiving of users? Will there be any need for admins to edit inline? Are the filtering options on the table of students appropriate for the admins' needs?

  All these questions and more need to be considered before designing any front-end application.

- Another issue is where and how users and admins should change passwords. In the end, I opted for simplicity, and there is now a _Change password_ button on the student view page, which takes users to a separate page.

  I experimented with a slide-out toggle (_Change password_, _Dont' change password_) on the editing form that produced a mini-editor, thus requiring one less trip to the server. However, I found that this only increased complexity and was less intuitive. I wouldn't rule the idea out altogether; there might exist third-party software to accomplish this in a better way. 

  Another option is for the _Change password_ button to produce a modal overlay. (For the most part, I have avoided popups in the application, but am not a fundamentalist in this respect.) This would be fine if accessed from the view page, but presents a problem if the button were to be moved to the editing form. For example, what happens if the user then backs out of the editing operation? Has the user also cancelled any change of password? Either way, will it be obvious to the user? I think the answer to the last question is "no", and in this case the operation should not be merged with editing user details.

- Client-side validation is another consideration. I am trying to develop a strategy for this. Clearly, you want validation of incoming data to back-end end points to occur immediately, and to return any errors to the client. Since there's no law that says there'll be only one client page per MVC end point, and the client could in fact be outside your control, this is a reasonable approach.  However, it also makes sense to validate directly on the client (using HTML5 or Javascript), so that the user sees errors immediately and without having to wait for a round-trip from the server.

  The question remains, however, as to how to consolidate both sets of validation, which should be one and the same. Some time ago I used a banking app whose front-end validation was clearly out of synch with what the database actually accepts (this was discovered while attempting to change my password). This unfortunate example demonstrates that, once the DRY principle is violated, it is all too easy to modify, say, a regex pattern in one place and forget to do so in another.

  I don't know whether a solution already exists for this problem, but I am planning to research the matter.

- _Cancel_ buttons have not been included on forms, since [it is expected](https://www.nngroup.com/articles/reset-and-cancel-buttons/) that people are used to using the browsers's _Back_ button. 

- A possible enhancement to the _Campus_ app is to [make _Save_ buttons on editing forms sticky](http://uxmovement.com/mobile/why-users-miss-form-buttons-placed-in-the-action-bar/), so that they are always in view as the user scrolls up and down the form. The caveat here is to ensure the button stands out from the rest of the form, so that the user doesn't think they've finished an incomplete form because they didn't notice the scrollbar. Separating editing forms out into small pieces and multiple pages, so that the _Save_ button is always visible without having to scroll is another option.


## DTOs/Value Objects

Should you use Data Transfer Objects? While there has been a movement away from DTOs in recent years, this topic is nevertheless [fraught with peril](http://stackoverflow.com/questions/1440952/why-are-data-transfer-objects-an-anti-pattern).

Personally, I don't see DTOs as an "anti-pattern", but they can certainly be overused. They come with a fairly high cost, and therefore their use needs to be justified. Claims that you should never "pollute" the service or view layers with domain-level entities can be questioned -- after all, in Spring these are just POJOs. Replication of these classes merely for the sake of keeping software tiers pure is a violation of the DRY principle.

However, justifications for the use of DTOs can include:

- It is vital to prevent sensitive (and/or unnecessary) data from being exported from your services and MVC end points, which also reduces the amount of traffic. Sometimes a DTO is the best way to achieve this. Other times, those attributes you don't want to export can be controlled with Jackson annotations (for example, @JsonIgnore should always be placed on a password field). This assumes, of course, that all users will have access to the same fields, which isn't true in the _Campus_ application. This is a decision you need to make on a case-by-case basis.
- Sometimes the data represented in a web page or being exported from a Restful service does not map nicely to your domain objects. For example, the _Campus_ student editing form does not map readily to a Student entity. While a Student entity has a list of Course entities in which the student is enrolled, the student editing page requires a list of _all_ courses, with a boolean flag against each, so that the user can select or deselect courses in which to be enrolled. Clearly, in this case, a DTO should be used.
- Some web pages have a lot of dynamic data, such that data is being requested from the page via Ajax calls. In the _Campus_ application, this only occurs when user tables request paged data. However, if we were to build a more complex page with many such requests, we could encounter a problem in that browsers often limit the number of requests that a page can make. In such cases, it would be helpful to bundle the data to reduce the number of requests.
- Where you have bi-directional relationships and your template engine does not recognise JSON annotations such as @JsonIgnore, @JsonManagedReference and @JsonBackReference, cyclic dependencies can and will result in infinite recursion. DTOs can be used to prevent this.
- DTOs decouple your API from your implementation. Architects of large-scale applications with many developers in the mix need to keep this in mind. Should the custodians of objects in the domain layer have to consider what goes on in the presentation layer every time they need to implement a change?

If DTOs are sometimes, often or always necessary, there are ways to reduce their burden. For example, projections can be used in RESTful web services. There are moves afoot to enable the use of projections in Spring MVC too. This is an approach I'm keen on, but while I've had success in exporting projections, I then ran into difficulties when importing them as model attributes. The @JsonView approach [has issues](http://stackoverflow.com/questions/23260464/how-to-serialize-using-jsonview-with-nested-objects) and will, again, strike trouble when presented to the template engine. Finally, I've seen suggestions that DTOs should simply be written as classes with public fields.

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
- ~~Do a search for console.log(...) as this will crash any attempts to run in IE.~~
- Check for XSS attacks. Thymeleaf is supposed to escape all HTML by default, but a) I want to test this; 
b) there might be a vulnerability in the admin view where Javascript is being used to generate the
table footer (search expressions aren't saved but I want to check whether they're relayed back to the user and
the potential impact).
- Fix up the layouts, including on error, starting with the student view/form and password change form.
- Get money serialisation working as formatted strings, but so that Javascript has access to numeric values too.
- Create student.
- Turn delete confirmation popup into a decent modal overlay.
- Ensure all pages are responsive, not just admin landing page.
- Finish lecturers (CRUD).
- Courses: CRUD.
- Improvemets to Javadoc.
- Student payment system.
- Potenially reinstate session attribute and setting allowed fields in @InitBinder method
in StudentsController, since the alternative means putting the onus on the client to always
supply those fields (e.g. as hidden inputs).
- Consider how to integrate client-side with server-side validation seamlessly.
- Add student enrolment date.
- Get Selenium tests working again, i.e. issues with latest Firefox requiring Selenium update caused dependency incompatibilities.
For Chrome and IE, it seems that you need to download drivers and set these up in the tests. For FIrefox, the quick fix
is that you change Selenium's Maven scope to the default. However, this is hardly ideal and I would like to fix it
properly.
- Get Thymeleaf to strip comments from HTML and CSS. Minification and concatenation of Javascript files and stylesheets, or at least include documentation here as to how it's done.
- Complete documentation.
- Sticky "save" button on longer forms, or else divide editing forms into smaller steps.
- Implement locking on multiple failed sign-in attempts to prevent brute force attacks.
- Shift Javascript into separate files, where appropriate.
- Possibly include icons on buttons, e.g. login, view, edit, etc.
- Fix money: at the moment it is represented by BigDecimal, and should probably change over to javax.money -- or wait for Java 9??
- Use profiles to separate out dev and prod components: https://spring.io/blog/2011/02/14/spring-3-1-m1-introducing-profile/ 
and http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html.

