# Inputer

**Inputer** is a simple, easy-to-use, well-documented class for prompting for and validating input from the command line.

## Usage

See `InputerPlay.java` for usage examples.  The basic syntax is:

    String name = Inputer.getString("Enter your name");

Inputer will add ": " to a prompt that it thinks needs it.  So the above prompt would actually be:

    Enter your name:

Validation is done with predicates.  For instance, this would get a positive integer:

    int number = Inputer.getInt("Enter a positive integer", i -> i > 0);

Some predicates are provided for you.  For instance, the following will return "m", "f", or "t":

    String gender = Inputer.getString("Enter gender (m/f/t) ", Inputer.oneOfThese("m", "f", "t"));

A specialized version of the above is used to easily a yes/no response.  This can be used in loops:

    do {
        // Get data here
    } while (Inputer.getYN("Is this correct?") == 'n');

## TODO

* Make class more DRY
* Does not work with BigInteger or BigDecimal