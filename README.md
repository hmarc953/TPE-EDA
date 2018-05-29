# Maven Template

## Requirements

* Java 1.8
* Maven

## How to use

1. Clone this repo
2. Open a terminal and `cd` into the project's root.
3. Create `.jar` by running:

```
$ mvn clean install
```

This should have generated a `.jar` file under `./target/` folder.

4. Execute `.jar` by running:

```
$ java -jar [target/file.jar]
```

## How to adapt it for your project

Place all your code under `src/main/java`. Then, make sure to place the class containing the main method in the appropiate tag inside the `pom.xml` file.
