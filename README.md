# Inputer

**Inputer** is a simple, easy-to-use class for prompting for and validating input from the console.

## Usage

See `InputerPlay.java` for usage examples.  The basic syntax is:

    Inputer in = new Inputer();
    String name = in.getString("Enter your name");

Inputer will add ": " to a prompt that it thinks needs it.  So the above prompt wold actually be:

    Enter your name:

Validation is done with predicates.  For instance, this would get a positive integer:

    int number = in.getInt("Enter a positive integer", i -> i > 0);

Some predicates are provided for you.  For instance, the following will return "m", "f", or "t":

    String gender = in.getString("Enter gender (m/f/t) ", Inputer.oneOfThese("m", "f", "t"));

A specialized version of the above is used to easily a yes/no response.  This can be used in loops:

    do {
        // Get data here
    } while (in.getYN("Is this correct?") == 'n');

## Javadoc

A `pom.xml` file has been provided so that the Javadocs can be build using [Maven](https://maven.apache.org/).  Issue the following at the command line:

    mvn site javadoc:javadoc

# TODO

* Does not get long, BigInteger, or BigDecimal