# ExpressionCalculator
A simple expression calculator for the 4 operations and exponentiation.
- Parses to a tree using a recursive descent parser (so it is human readable).
- The tree can then be directly evaluated using a recursive evaluation.
- Also includes a stack-based, step-wise evaluator.

Build using command line
- install a Java 8 compiler and runtime and add it to your path
- install maven package and build system and add it to your path
- git clone this repo
- run tests with mave from root of project folder
```
$ mvn test
```
- build with maven from root of project folder
```
$ mvn package
```
- to run from root of project folder
```
$ java -jar target/expression-calculator-0.9-SNAPSHOT.jar "1 + 2 / 3 * (4 + 5)"
  1 + 2 / 3 * (4 + 5)
  1 + 2 / 3 * (4 + 5) = 7.0
```

IntelliJ
- install IntelliJ IDE
- git clone this repo
- start IntelliJ
- if startup window opens, choose "Open or Import"
- if start window does not open, from file menu choose "new/project from existing sources"
- Navigate to project root folder and choose the `pom.xml` file.
- IntelliJ will create the project if it does not already exist or open the existing project.
- to run tests in IntelliJ, choose `LifeCycle/test` in the Maven panel
- to build in IntelliJ, choose `LifeCycle/package` in the Maven panel