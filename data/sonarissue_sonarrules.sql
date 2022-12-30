-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: localhost    Database: sonarissue
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `sonarrules`
--

LOCK TABLES `sonarrules` WRITE;
/*!40000 ALTER TABLE `sonarrules` DISABLE KEYS */;
INSERT INTO `sonarrules` VALUES ('common-java:Duplicat','1 duplicated blocks of code must be removed.','MAJOR','java','CODE_SMELL'),('java:S100','Rename this method name to match the regular expression \'^[a-z][a-zA-Z0-9]*$\'.','MINOR','java','CODE_SMELL'),('java:S101','Rename this class name to match the regular expression \'^[A-Z][a-zA-Z0-9]*$\'.','MINOR','java','CODE_SMELL'),('java:S106','Replace this use of System.out or System.err by a logger.','MAJOR','java','CODE_SMELL'),('java:S1066','Merge this if statement with the enclosing one.','MAJOR','java','CODE_SMELL'),('java:S1104','Make ac a static final constant or non-public and provide accessors if needed.','MINOR','java','CODE_SMELL'),('java:S1116','Remove this empty statement.','MINOR','java','CODE_SMELL'),('java:S1118','Add a private constructor to hide the implicit public one.','MAJOR','java','CODE_SMELL'),('java:S112','Define and throw a dedicated exception instead of using a generic one.','MAJOR','java','CODE_SMELL'),('java:S1124','Reorder the modifiers to comply with the Java Language Specification.','MINOR','java','CODE_SMELL'),('java:S1126','Replace this if-then-else statement by a single return statement.','MINOR','java','CODE_SMELL'),('java:S1135','Complete the task associated to this TODO comment.','INFO','java','CODE_SMELL'),('java:S116','Rename this field \"user_id\" to match the regular expression \'^[a-z][a-zA-Z0-9]*$\'.','MINOR','java','CODE_SMELL'),('java:S117','Rename this local variable to match the regular expression \'^[a-z][a-zA-Z0-9]*$\'.','MINOR','java','CODE_SMELL'),('java:S1186','Add a nested comment explaining why this method is empty, throw an UnsupportedOperationException or complete the implementation.','CRITICAL','java','CODE_SMELL'),('java:S1192','Define a constant instead of duplicating this literal \"user_id\" 10 times.','CRITICAL','java','CODE_SMELL'),('java:S1197','Move the array designators [] to the type.','MINOR','java','CODE_SMELL'),('java:S120','Rename this package name to match the regular expression \'^[a-z_]+(\\.[a-z_][a-z0-9_]*)*$\'.','MINOR','java','CODE_SMELL'),('java:S1220','Move this file to a named package.','MINOR','java','CODE_SMELL'),('java:S1301','Replace this \"switch\" statement by \"if\" statements to increase readability.','MINOR','java','CODE_SMELL'),('java:S131','Add a default case to this switch.','CRITICAL','java','CODE_SMELL'),('java:S1319','The type of \"csv_array\" should be an interface such as \"List\" rather than the implementation \"ArrayList\".','MINOR','java','CODE_SMELL'),('java:S135','Reduce the total number of break and continue statements in this loop to use at most one.','MINOR','java','CODE_SMELL'),('java:S1444','Make this \"public static ac\" field final','MINOR','java','CODE_SMELL'),('java:S1481','Remove this unused \"t\" local variable.','MINOR','java','CODE_SMELL'),('java:S1488','Immediately return this expression instead of assigning it to the temporary variable \"tmp\".','MINOR','java','CODE_SMELL'),('java:S1602','Remove useless curly braces around statement and then remove useless return keyword (sonar.java.source not set. Assuming 8 or greater.)','MINOR','java','CODE_SMELL'),('java:S1845','Rename method \"deleteCourseServer\" to prevent any misunderstanding/clash with field \"deleteCourseServer\".','BLOCKER','java','CODE_SMELL'),('java:S1854','Remove this useless assignment to local variable \"t\".','MAJOR','java','CODE_SMELL'),('java:S2093','Change this \"try\" to a try-with-resources. (sonar.java.source not set. Assuming 7 or greater.)','CRITICAL','java','CODE_SMELL'),('java:S2095','Use try-with-resources or close this \"ReadableByteChannel\" in a \"finally\" clause.','BLOCKER','java','BUG'),('java:S3346','Move this \"assert\" side effect to another statement.','MAJOR','java','BUG'),('java:S3776','Refactor this method to reduce its Cognitive Complexity from 18 to the 15 allowed.','CRITICAL','java','CODE_SMELL'),('java:S5843','Simplify this regular expression to reduce its complexity from 22 to the 20 allowed.','MAJOR','java','CODE_SMELL'),('java:S5993','Change the visibility of this constructor to \"protected\".','MAJOR','java','CODE_SMELL'),('java:S5998','Refactor this repetition that can lead to a stack overflow for large inputs.','MAJOR','java','BUG'),('java:S6353','Use concise character class syntax \'\\\\d\' instead of \'[0-9]\'.','MINOR','java','CODE_SMELL');
/*!40000 ALTER TABLE `sonarrules` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-12-30 15:30:42
