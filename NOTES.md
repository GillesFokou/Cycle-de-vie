//La substitution

4- Dans app.EssaiFournisseurs
C:\Users\fokou\.jdks\openjdk-27\bin\java.exe "-javaagent:C:\Users\fokou\AppData\Local\Programs\IntelliJ IDEA\lib\idea_rt.jar=65189" -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath "C:\Users\fokou\Documents\esaip\cours ING IR4\Programmation back end\tp07-projet-depart\target\classes;C:\Users\fokou\.m2\repository\org\mariadb\jdbc\mariadb-java-client\3.5.10\mariadb-java-client-3.5.10.jar;C:\Users\fokou\.m2\repository\com\zaxxer\HikariCP\7.1.0\HikariCP-7.1.0.jar;C:\Users\fokou\.m2\repository\org\slf4j\slf4j-api\2.0.20\slf4j-api-2.0.20.jar;C:\Users\fokou\.m2\repository\ch\qos\logback\logback-classic\1.6.4\logback-classic-1.6.4.jar;C:\Users\fokou\.m2\repository\ch\qos\logback\logback-core\1.6.4\logback-core-1.6.4.jar;C:\Users\fokou\.m2\repository\org\hibernate\orm\hibernate-core\7.4.11.Final\hibernate-core-7.4.11.Final.jar;C:\Users\fokou\.m2\repository\jakarta\persistence\jakarta.persistence-api\3.2.0\jakarta.persistence-api-3.2.0.jar;C:\Users\fokou\.m2\repository\jakarta\transaction\jakarta.transaction-api\2.0.1\jakarta.transaction-api-2.0.1.jar;C:\Users\fokou\.m2\repository\org\jboss\logging\jboss-logging\3.6.1.Final\jboss-logging-3.6.1.Final.jar;C:\Users\fokou\.m2\repository\org\hibernate\models\hibernate-models\1.1.2\hibernate-models-1.1.2.jar;C:\Users\fokou\.m2\repository\net\bytebuddy\byte-buddy\1.18.8\byte-buddy-1.18.8.jar;C:\Users\fokou\.m2\repository\jakarta\xml\bind\jakarta.xml.bind-api\4.0.4\jakarta.xml.bind-api-4.0.4.jar;C:\Users\fokou\.m2\repository\jakarta\activation\jakarta.activation-api\2.1.4\jakarta.activation-api-2.1.4.jar;C:\Users\fokou\.m2\repository\org\glassfish\jaxb\jaxb-runtime\4.0.7\jaxb-runtime-4.0.7.jar;C:\Users\fokou\.m2\repository\org\glassfish\jaxb\jaxb-core\4.0.7\jaxb-core-4.0.7.jar;C:\Users\fokou\.m2\repository\org\eclipse\angus\angus-activation\2.0.3\angus-activation-2.0.3.jar;C:\Users\fokou\.m2\repository\org\glassfish\jaxb\txw2\4.0.7\txw2-4.0.7.jar;C:\Users\fokou\.m2\repository\com\sun\istack\istack-commons-runtime\4.1.2\istack-commons-runtime-4.1.2.jar;C:\Users\fokou\.m2\repository\jakarta\inject\jakarta.inject-api\2.0.1\jakarta.inject-api-2.0.1.jar;C:\Users\fokou\.m2\repository\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar;C:\Users\fokou\.m2\repository\org\hibernate\orm\hibernate-hikaricp\7.4.11.Final\hibernate-hikaricp-7.4.11.Final.jar;C:\Users\fokou\.m2\repository\org\flywaydb\flyway-core\13.8.1\flyway-core-13.8.1.jar;C:\Users\fokou\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.22\jackson-annotations-2.22.jar;C:\Users\fokou\.m2\repository\org\flywaydb\flyway-mysql\13.8.1\flyway-mysql-13.8.1.jar" org.sebsy.compta.app.EssaiFournisseurs
INFO  c.z.hikari.HikariDataSource - compta-pool - Starting...
INFO  c.z.hikari.pool.HikariPool - compta-pool - Added connection org.mariadb.jdbc.Connection@6f27a732
INFO  c.z.hikari.HikariDataSource - compta-pool - Start completed.
toutes()           : 4 fournisseurs
parId(2)           : Fournisseur[id=2, nom=FDM SA, ville=Lille]
parId(999)         : Optional.empty
parVille(Nantes)   : [Fournisseur[id=1, nom=Française d'Imports, ville=Nantes]]
creer()            : Fournisseur[id=18, nom=Peinture Express, ville=Nantes]
renommer(cree)     : true
renommer(999)      : false
supprimer(cree)    : true
delai(bon 1)       : Optional[3]
delai(bon 7)       : Optional.empty
INFO  c.z.hikari.HikariDataSource - compta-pool - Shutdown initiated...
INFO  c.z.hikari.HikariDataSource - compta-pool - Shutdown completed.

Process finished with exit code 0
C:\Users\fokou\AppData\Local\Programs\IntelliJ IDEA\plugins\maven-plugin\lib\maven3\bin\mvn.cmd -Didea.version=2026.2.3 -Dmaven.ext.class.path=C:\Users\fokou\AppData\Local\Programs\IntelliJ IDEA\plugins\maven-plugin\lib\intellij.maven.rt\maven-event-listener.jar -Djansi.passthrough=true -Dstyle.color=always -Dmaven.repo.local=C:\Users\fokou\.m2\repository compile -f pom.xml
[INFO] Scanning for projects...
[INFO]
[INFO] ---------------------< org.sebsy:demo-data-access >---------------------
[INFO] Building demo-data-access 1.0.0-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- resources:3.4.0:resources (default-resources) @ demo-data-access ---
[INFO] Copying 1 resource from src\main\resources to target\classes
[INFO]
[INFO] --- compiler:3.16.0:compile (default-compile) @ demo-data-access ---
[INFO] Nothing to compile - all classes are up to date.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.091 s
[INFO] Finished at: 2026-10-27T11:20:51+01:00
[INFO] ------------------------------------------------------------------------

5-Indice — <code>parNom</code> sans exception

  <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter-params</artifactId>
      <version>5.12.2</version>
      <scope>test</scope>
    </dependency>
