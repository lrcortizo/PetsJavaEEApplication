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

    Git URL: `http://sing.ei.uvigo.es/dt/gitlab/dgss/xcs-sample.git`

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

Eclipse should then import a parent project (`xcs-sample`) and 6 child projects
(`tests`, `domain`, `service`, `rest`, `jsf` and `ear`).

### MySQL
In order to run the tests with the `wildfly-embedded-mysql` profile (more about
this in the **Sample 2** section) and to run the application, we need a MySQL
server.

The server can be installed as usual, but it must contain the `xcs` database and
the user `xcs` identified by `xcs` should have all privileges on this database.
You can do this by executing the following commands:
```sql
CREATE DATABASE xcs;
GRANT ALL PRIVILEGES ON xcs TO xcs@localhost IDENTIFIED BY 'xcs';
FLUSH PRIVILEGES;
```

If you want to add some data to this database to run the application, you can
also execute:
```sql
DROP TABLE IF EXISTS `User`;
CREATE TABLE `User` (
  `role` varchar(5) NOT NULL,
  `login` varchar(100) NOT NULL,
  `password` varchar(32) NOT NULL,
  PRIMARY KEY (`login`)
);

DROP TABLE IF EXISTS `Pet`;
CREATE TABLE `Pet` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `animal` varchar(4) NOT NULL,
  `birth` datetime NOT NULL,
  `name` varchar(100) NOT NULL,
  `owner` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_6mfctqh1tpytabbk1u4bk1pym` (`owner`),
  CONSTRAINT `FK_6mfctqh1tpytabbk1u4bk1pym` FOREIGN KEY (`owner`) REFERENCES `User` (`login`)
);

-- All the passwords are "<login>pass".
INSERT INTO `User`
   VALUES ('ADMIN','jose','A3F6F4B40B24E2FD61F08923ED452F34'),
          ('OWNER','pepe','B43B4D046860B2BD945BCA2597BF9F07'),
          ('OWNER','juan','B4FBB95580592697DC71488A1F19277E'),
          ('OWNER','ana','22BEEAE33E9B2657F9610621502CD7A4'),
          ('OWNER','lorena','05009E420932C21E5A68F5EF1AADD530');

INSERT INTO `Pet` (animal, birth, name, owner)
   VALUES ('CAT','2000-01-01 01:01:01','Pepecat','pepe'),
          ('CAT','2000-01-01 01:01:01','Max','juan'),
          ('DOG','2000-01-01 01:01:01','Juandog','juan'),
          ('CAT','2000-01-01 01:01:01','Anacat','ana'),
          ('DOG','2000-01-01 01:01:01','Max','ana'),
          ('BIRD','2000-01-01 01:01:01','Anabird','ana');
```

### Wildfly 8
Before we can run the project, we need to configure a WildFly server to include
the datasource used by the application and the security configuration.

#### Datasource
There are several ways to add a datasource to a WildFly server. We are going to
use the simplest way: add the datasource as a deployment. To do so, you have to
add a XML file to the `standalone/deployments` folder of the WildFly server with
the following content:
```xml
<datasources xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns="http://www.ironjacamar.org/doc/schema"
  xsi:schemaLocation="http://www.ironjacamar.org/doc/schema http://www.ironjacamar.org/doc/schema/datasources_1_1.xsd">

  <datasource jndi-name="java:jboss/datasources/xcs" pool-name="MySQLPool">

      <connection-url>jdbc:mysql://localhost:3306/xcs</connection-url>
      <driver>mysql-connector-java-5.1.21.jar</driver>
      <pool>
          <max-pool-size>30</max-pool-size>
      </pool>
      <security>
          <user-name>xcs</user-name>
          <password>xcs</password>
      </security>
  </datasource>
</datasources>
```

In addition, you also have to add to the same folder the MySQL driver that can
be downloaded from [here](http://central.maven.org/maven2/mysql/mysql-connector-java/5.1.21/mysql-connector-java-5.1.21.jar).

#### Security configuration
All the WildFly security configuration is done in the
`standalone/configuration/standalone.xml` file of the server.

Inside the `<security-reamls>` element you have to add:
```xml
<security-realm name="RemotingRealm">
    <authentication>
        <jaas name="AppRealmLoopThrough"/>
    </authentication>
</security-realm>
```

And inside the `<security-domains>` element you have to add:
```xml
<security-domain name="AppRealmLoopThrough" cache-type="default">
    <authentication>
        <login-module code="Client" flag="required">
            <module-option name="multi-threaded" value="true"/>
        </login-module>
    </authentication>
</security-domain>
<security-domain name="xcs-sample-security-domain">
    <authentication>
        <login-module code="Database" flag="required">
            <module-option name="dsJndiName" value="java:jboss/datasources/xcs"/>
            <module-option name="principalsQuery" value="SELECT password FROM User WHERE login=?"/>
            <module-option name="rolesQuery" value="SELECT role, 'Roles' FROM User WHERE login=?"/>
            <module-option name="hashAlgorithm" value="MD5"/>
            <module-option name="hashEncoding" value="hex"/>
            <module-option name="ignorePasswordCase" value="true"/>
        </login-module>
    </authentication>
</security-domain>
```

#### Deploying the application
When the `package` goal is run in the `xcs-sample` project, an EAR file is
generated inside the `target` folder of the `ear` project.

The EAR file contains all the elements of the project (JARs and WARs) and,
therefore, you only have to deploy this file in the WildFly container to deploy
the entire application. To do so, you can copy this file to the
`standalone/deployments` folder of WidlFly.

Once this is done, you can run the WildFly server executing the
`bin/standalone.sh` script. The application should be running in
`http://localhost:8080/`

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

Tests can be run using the same configuration as explained in *Sample 2*.

When executed, the REST resources can be found in:
* Owners: `http://localhost:8080/rest/api/owner`
* Pets: `http://localhost:8080/rest/api/pet`

## Sample 5: Testing JSF
Using Arquillian Drone, Arquillian Graphene and Selenium, we will test the JSF
web interface accessing it as real Web clients.

Tests can be run using the same configuration as explained in *Sample 2*.

When executed, the REST resources can be found in
`http://localhost:8080/jsf/faces/index.html`.

## Sample 6: Additional Testing Tools
Coming soon...
