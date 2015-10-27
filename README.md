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

### How to run tests with Arquillian?
This project is configured to use two Maven profiles:
* `wildfly-embedded-h2`: This profile uses Wildfly in embedded mode with the H2
`ExampleDS` database that is included by default in this Java EE server (it has
the JNDI name `java:jboss/datasources/ExampleDS`).
* `wildfly-embedded-mysql`: Same as before, but it uses a MySQL datasource with
the JNDI name `java:jboss/datasources/xcs`.

In both profiles, the Wildfly server is downloaded automatically using the
`maven-dependency-plugin`, that extracts it in the `target/wildfly-<version>`
folder (`target/wildfly-8.2.1.Final` currently). In the MySQL profile, the MySQL
driver is also downloaded using this plugin and added to the
`target/wildfly-<version>/standalone/deployments` folder, to make it available
in the Wildfly server.

For each profile, Maven is configured to use the files stored in
`src/test/resources-<profile name>` as resources when running tests, in addition
to the stored in the `src/test/resources` folder, as usual. Inside this folder,
the projects using Arquillian must include a `standalone.xml` file, that will be
replace the default `standalone.xml` file of the Wildfly server. This is
specially useful to configure the security constraints. The MySQL resources
folder must also include a `mysql-ds.xml` file, with the MySQL datasource
configuration that will be added to the Wildfly server.

Therefore, when running Maven tests (e.g. `mvn test`), they will run without any
external requirement.

#### Arquillian tests in Eclipse
To run Arquillian tests in Eclipse (or in any non-Maven enviroment) a further
step is needed. You must configure the following system properties:
* `java.util.logging.manager`: The logger to be used by the standard Java
logger. Commonly, the value `org.jboss.logmanager.LogManager` is used.
* `wildfly.version`: The version of the Wildfly server stored in `target`.
The current version is `8.2.1.Final`.

In Eclipse, this system properties can be added to the run configuration in the
`VM arguments` field of the `Arguments` tab. For example, the following
configuration will work for the current configuration:
```
-Dwildfly.version=8.2.1.Final
-Djava.util.logging.manager=org.jboss.logmanager.LogManager
```
This configuration will run with the **h2** database. If you wish to run the
tests with the **MySQL** database, you have to add to additional system
configuration:
* `mysql.version`: The version of the MySQL driver (currently, `5.1.21`). This
version is used by the `mysql-ds.xml` configuration files.
* `arquillian.launch`: This system property is used to change the profile used
by Arquillian. It should be `wildfly-embedded-mysql` to use the MySQL profile.

Therefore, the `VM arguments` configuration for running the tests in Eclipse
using the MySQL database is:
```
-Dwildfly.version=8.2.1.Final
-Djava.util.logging.manager=org.jboss.logmanager.LogManager
-Dmysql.version=5.1.21
-Darquillian.launch=wildfly-embedded-mysql
```

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
