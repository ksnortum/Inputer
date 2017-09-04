package net.snortum.inputer;

import java.util.function.Predicate;

/**
 * Provides examples for how to use {@link Inputer}, or to do testing.
 * 
 * @author Knute Snortum
 * @version 2017.08.30
 */
public class InputerPlay {
	
	private String[] states = {"WA", "OR", "CA"};
	private Predicate<String> usaZipValidater = z -> z.matches("\\d{5}(-\\d{4})?");

	public static void main(String[] args) {
		new InputerPlay().run();
	}
	
	private void run() {
		simpleExample();
		complexExample();
		// testErrorConditions();
	}
	
	private void simpleExample() {
		Inputer in = new Inputer();
		
		// Get text
		String text = in.getString();
		
		// Add a prompt
		String name = in.getString("Enter your name");
		
		// Add validation 
		String zipcode = in.getString("Enter ZIP Code", s -> s.length() <= 10);
		
		// Use one of Inputer's built-in validaters
		int age = in.getInt("Enter your age", Inputer.intRange(0, 130));
		
		// Add a default value that is passed back if the user pressed <enter> only
		int number = in.getInt("Enter the meaning of the universe", d -> d > 0, 42);
		
		// Wait for input
		in.pause();
		
		System.out.printf("%nText: %s, Name: %s, ZIP: %s%n", text, name, zipcode);
		System.out.printf("Age: %d, The answer to everything: %d%n", age, number);
	}

	private void complexExample() {
		Inputer in = new Inputer();
		String name = null;
		String address = null;
		String address2 = "";
		String city = null;
		String state = null;
		String zipcode = null;
		
		System.out.println();

		do {
			
			// No validation, first entry cannot be empty, subsequent entry default to last entry.
			// name is set to null and passed as the default, signifying that an initial entry must be made.
			name = in.getString("Enter your name", null, name); 
			address = in.getString("Enter address 1", null, address);
			
			// Because address2 is empty (not null), the user is allowed to <enter> past the field
			address2 = in.getString("Enter address 2", null, address2); 
			city = in.getString("Enter city", null, city);
			
			// Validates against an array of states.
			// Normally a complete set of US states would be used.
			state = in.getString("Enter state (west coast only)", Inputer.oneOfThese(states), state);
			
			// Uses an external validater, the regex does a better job of validating
			zipcode = in.getString("Enter ZIP Code", usaZipValidater, zipcode);
			
		// Try entering 'n' and <enter> through your old answers
		} while (in.getYN("Is this information correct?") == 'n');

		// Yes/no question that returns a String (not a char) and defaults to "y"
		String agree = in.getString("Do you agree with out terms and conditions? (y,n)", Inputer.yesOrNo(), "y");

		System.out.println();
		System.out.println(name);
		System.out.println(address);
		
		if (! address2.isEmpty()) {
			System.out.println(address2);
		}
		
		System.out.printf("%s, %s  %s%n", city, state, zipcode);
		System.out.println("y".equalsIgnoreCase(agree) ? "User agreed" : "User didn't agree");
	}

	@SuppressWarnings("unused")
	private void testErrorConditions() {
		Inputer in = new Inputer();
		String nothing = in.getString("Get nothing? ", Inputer.oneOfThese());
		in.getString("Type YES to continue", Inputer.oneOfThese("YES"));
		int badRange = in.getInt("Bad range", Inputer.intRange(100, 0));
		double badRange2 = in.getDouble("Bad range", Inputer.doubleRange(100, 0));
	}

}
