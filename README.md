# XCS Sample

This repository contains the code base of a sample project that will be used in
the XCS subject inside the DGSS itinerary.

## Deployment Environment

The environment is based on Maven 3, MySQL 5.5, WildFly 8.2.1 and Eclipse Mars 
for JEE.

### Java JDK 8
Download and install Java JDK 8, preferably the Oracle version (the commands 
`java` and `javac` must be available).

### Maven
Install Maven 3 in your system, if it was not installed (the `mvn` command must 
be available)

### Git
First, install git in your system if it was not installed (the `git` command 
must be available). We will work with Git to get updates of these sample.
Concretely, we will work with a Git repository inside
[our Gitlab server](http://sing.ei.uvigo.es/dt/gitlab).

    Git url: `http://sing.ei.uvigo.es/dt/gitlab/dgss/xcs-sample.git`

### Eclipse
You can use any other IDE, such as IntelliJ IDEA or NetBeans, as long as they 
are compatible with Maven projects.

Before continue, you have **to patch Eclipse Mars**, concretely the m2e-wtp 
plugin. Go to `Help -> Install New Software`. Work with repository located 
at `http://download.eclipse.org/m2e-wtp/snapshots/mars/` and then select and 
install "Maven Integration for WTP". Restart Eclipse.

Open Eclipse Mars JEE and import your Maven project with 
`File -> Import -> Maven -> Existing Maven Projects`

Select your source code folder (where the `pom.xml` should be placed)

Eclipse should then import 2 projects (`xcs-sample` and `domain`)

## Sample 1: Testing entities
Using JUnit and Hamcrest, we will see how to test JPA entities or any other
Java class. This libraries are the base for every test done in the application.

## Sample 2: Testing EJBs
Using Arquillian and Arquillian Persistence, the EJBs are tested. We wouldn't do
unit testing in this layer, as we don't want to mock the `EntityManager`.

In this layer we will use some workarounds to set the desired role and principal
in the tests.

## Sample 3: Testing with test doubles
Using EasyMock, we will mock the EJBs to test the REST classes isolated from the
underlying layer.

## Sample 4: Testing JAX-RS
Using Arquillian REST Client, we will test the REST API accessing it as real
HTTP clients.

## Sample 5: Testing JSF
Using Arquillian Drone, Arquillian Graphene and Selenium, we will test the JSF
web interface accessing it as real Web clients.

## Sample 6: Additional Testing Tools
Coming soon...
