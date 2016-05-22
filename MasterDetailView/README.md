# Master-detail example

## Introduction

This is a small project to demonstrate the use of:

- Spring MVC 4 with [Spring Boot](http://projects.spring.io/spring-boot/)
- [Thymeleaf](http://www.thymeleaf.org/)
- [JQuery Datatables](https://datatables.net/) with server-side pagination, ordering and filtering
- [QueryDSL](http://www.querydsl.com/)

Datatables is a feature-rich plugin for the jQuery Javascript library. Since
it has so many options it can be a little difficult to set up to suit
your personal needs. Therefore I decided to build an example application
for a common use case, the master-detail page.

Spring Boot favours convention over configuration, and makes it easy to 
quickly get a web application up and running. It features an embedded
servlet container (e.g. Tomcat, Jetty) and requires no XML configuration
whatsoever.

Thymeleaf is a server-side Java template engine that has become popular
as a replacement for JSPs.

Querydsl integrates with JPA 2 persistence and provides an elegant alternative to the
JPA 2 Criteria API. This project uses it to extend the repository interface.

The project also utilises:

- Spring Data JPA (although cannot be considered a decent example, since there's only a single entity in the mix)
- H2 in-memory database
- [Webjars](www.webjars.org)

The project was developed using the Eclipse Mars IDE. With the introduction of Querydsl various
m2e quirks reared their ugly heads. These are noted in the [setup section](#settingUp) of this document.
I have not attempted to build and run the project using any other IDE.

## <a name="settingUp"></a>Setting up and Running

Since this is a Maven project, it "should" only be necessary to:

- generate a Maven update and build (also generates metadata classes)
- run mxc.demo.masterdetailpaging.MainApplication as a Java application
- browse to http://localhost:8080

However, if you are an Eclipse user, read on.

Once you configure for QueryDSL, you will have to modify your eclipse.ini file. 
See the comments in the POM for the reasons.

To do this, check the path of your JDK and add something like this to your eclipse.ini:

```
-vm 
C:\Program Files\Java\jdk1.8.0_77\bin\javaw.exe
```

The -vm option must occur before the -vmargs option. Once you have done this, restart Eclipse.

You should now be able to build and run the app as described above.
However, should you be harbouring a burning desire to understand what is going on 
in the POM, or else find that things are not working as advertised, keep reading.

As mentioned earlier, you need to entice Maven to generate your Querydsl metamodel
classes, such as QEmployee from an entity Employee, and so on. To do this:

- add your Querydsl dependencies
- add a plugin to the POM's _build_ section with instructions to generate the metamodel
classes to target/generated-sources/apt (don't commit these generated files, and as 
you can see this project already has the target directory in its .gitignore file)
- add another build plugin, _build-helper-maven-plugin_, to tell Maven that there's another 
source directory in the mix, so that those classes will be compiled

And that should be enough. . . .

But wait -- there's more! Naturally, there's a problem in m2e. See
the comments in this project's POM for what to do next. If you're using IntellJ, with
any luck you won't have to do that for your own projects. In this project, the POM has already been doctored.

If your metadata classes disappear on you again for no apparent reason (e.g. this happened 
at one stage while I was innocently typing in code, despite the fact that
I'm pretty sure I had switched off automatic Maven builds):

- comment out anything that doesn't compile
- force a Maven update
- check to see if the generated source and .class files are there (you should be able to see 
the target/classes/ in your Navigator)
- uncomment out your code and, older but not necessarily wiser, proceed

Note that Project->Clean also removes the generated source.

See also the following:

- https://github.com/eugenp/tutorials/issues/247 
- https://wiki.eclipse.org/Eclipse.ini
- http://stackoverflow.com/questions/24482259/eclipse-issue-with-maven-build-and-jdk-when-generating-qclasses-in-querydsl

## Conclusions

- I cannot recommend [Spring Boot](http://projects.spring.io/spring-boot/) highly enough. 
All Spring projects should be using this. I am indebted to
[this blog post](https://springframework.guru/spring-boot-web-application-part-1-spring-initializr/)
for getting me started with Spring Boot.
- I am new to [Thymeleaf](http://www.thymeleaf.org/), and have only used a small subset of its features
so far. From what I've seen, it seems to be stable and an excellent replacement for JSPs.
- [JQuery Datatables](https://datatables.net/) also appears to be very stable, with a seemingly
inexhaustible supply of features. My main issue with it is that, while there's plenty
of documentation, finding what you need can be quite a challenge.
- [Webjars](www.webjars.org): good for local development, but [see here](http://www.jamesward.com/2014/03/20/webjars-now-on-the-jsdelivr-cdn).

## Remaining tasks

- Update to latest version of Spring Boot.
- Integration tests on the service layer.
- Test using browsers other than Firefox.


