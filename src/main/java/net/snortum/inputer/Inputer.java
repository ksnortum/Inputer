package net.snortum.inputer;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Provides an easy way to prompt for and get data from the console.
 * 
 * @author Knute Snortum
 * @version 2017.07.18
 */
public class Inputer {
	private static final String INT_PROMPT = "Enter an integer: ";
	private static final String INT_ERROR_PROMPT = "Invalid integer";
	private static final String STRING_PROMPT = "Enter an string: ";
	private static final String DOUBLE_PROMPT = "Enter a double (decimal): ";
	private static final String DOUBLE_ERROR_PROMPT = "Invalid double (decimal)";
	private static final String VALIDATER_FALSE_PROMPT = "Invalid value";
	private static final String CONTINUE_PROMPT = "Press <enter> to continue: ";
	
	private static final String STRING_DONT_ADD_COLON = 
			  "[:)?]" // match one of the char between the brackets
			+ "\\s*"  // match zero or more white space chars
			+ "$";    // match the end of the string
	private static final Pattern REGEX_DONT_ADD_COLON = Pattern.compile(STRING_DONT_ADD_COLON);
	private static final String STRING_YES_OR_NO = "(?:y|yes|n|no)";
	private static final String STRING_ENDS_WITH_YN = 
			  "\\("             // literal (
			+ "\\s*"            // zero or more white space characters
			+ STRING_YES_OR_NO
			+ "\\s*"
			+ "[,/]?"           // a "," or a "/" or nothing
			+ "\\s*"
			+ STRING_YES_OR_NO
			+ "\\s*"
			+ "\\)"             // literal )
			+ "\\s*"
			+ "$";              // end of string
	private static final Pattern REGEX_ENDS_WITH_YN = Pattern.compile(STRING_ENDS_WITH_YN, Pattern.CASE_INSENSITIVE);
	
	/* Helper predicates for input verification */
	
	/**
	 * <p>Takes low and high integers and returns a predicate (lambda) that tests if an entered integer
	 * is between the two numbers, inclusive.  Used by the client program as an easy way to test for
	 * a range of integers.  For example:</p>
	 * 
	 * <p>{@code int age = in.getInt("Enter your age", Inputer.intRange(0, 130));}</p>
	 * 
	 * @param low the lower range
	 * @param high the higher range
	 * @return a predicate that tests if an integer is within range
	 */
	public static IntPredicate intRange(int low, int high) {
		return i -> i >= low && i <= high;
	}
	
	/**
	 * <p>Takes low and high doubles and returns a predicate (lambda) that tests if an entered double
	 * is between the two numbers, inclusive.  Used by the client program as an easy way to test for
	 * a range of doubles.  For example:</p>
	 * 
	 * <p>{@code double extra = in.getDouble("Enter extra charge", Inputer.doubleRange(1.5, 9.5));}</p>
	 * 
	 * @param low the lower range
	 * @param high the higher range
	 * @return a predicate that tests if a double is within range
	 */
	public static DoublePredicate doubleRange(double low, double high) {
		return d -> d >= low && d <= high;
	}
	
	/**
	 * <p>Takes one or more strings and returns a predicate (lambda) that test if an entered string
	 * matches one of the strings.  Used by the client program to easily test a string against
	 * a list of valid strings.  For instance:</p>
	 * 
	 * <p>{@code String gender = in.getString("Enter gender (m/f/t) ", Inputer.oneOfThese("m", "f", "t"));}</p>
	 * 
	 * @param options one or more strings to test for validity against an entered string
	 * @return a predicate that tests if a string matches any of the options
	 */
	public static Predicate<String> oneOfThese(String... options) {
		return input -> Arrays.stream(options).anyMatch(s -> s.equalsIgnoreCase(input));
	}
	
	/**
	 * <p>Returns a predicate (lambda) that will test if an entered string starts with "Y", "N", "y", or "n".
	 * Used by the client program to test for a "y/n" response.  For example:</p>
	 * 
	 * <p>{@code String agree = in.getString("Do you agree? (y,n) ", Inputer.yesOrNo());}</p>
	 * 
	 * @return the predicate 
	 * @see #getYN(String)
	 */
	public static Predicate<String> yesOrNo() {
		return input -> "YNyn".chars().anyMatch(c -> input.charAt(0) == c);
	}
	
	/* Instance members */
	
	private final Scanner console = new Scanner(System.in);
	private boolean lastCallWasGetEmpty = true;
	
	/* Get String methods */
	
	/**
	 * Prompts for and gets a string, optionally validated.
	 * 
	 * @param prompt the prompt to print
	 * @param validater a string predicate to validate the input with.  Can be null.
	 * @return the entered (and possibly validated) string
	 * @see #getString(String)
	 * @see #getString()
	 * @see #oneOfThese(String...)
	 * @see #yesOrNo()
	 * @see #getYN(String)
	 */
	public String getString(String prompt, Predicate<String> validater) {
		lastCallWasGetEmpty = false;
		boolean inputInvalid = false;
		String result = "";
		
		do {
			printPrompt(prompt);
			result = console.next();
			
			if (validater == null || validater.test(result)) {
				inputInvalid = false;
			} else {
				System.out.println(VALIDATER_FALSE_PROMPT);
    			inputInvalid = true;
			}
		} while (inputInvalid);
		
		return result;
	}
	
	/**
	 * Prompts for and gets a string from the console.  No validation is performed.
	 * 
	 * @param prompt the prompt to print
	 * @return the entered string
	 * @see #getString(String, Predicate)
	 * @see #getString()
	 */
	public String getString(String prompt) {
		return getString(prompt, null);
	}
	
	/**
	 * Prompts for and gets a string from the console.  A generic prompt is used.  No validation is performed.
	 * 
	 * @return the entered string
	 * @see #getString(String, Predicate)
	 * @see #getString(String)
	 */
	public String getString() {
		return getString(STRING_PROMPT, null);
	}
	
	/**
	 * <p>Prompts for and gets a yes or no response.  The response need only start with "y", "n", "Y", or "N".
	 * If the prompt does not already end with a (y,n) or similar text, it will be added.  The first character
	 * of the response, lower-cased, is returned so that it can be tested in a loop.  For example:</p>
	 * 
	 * <pre>
	 * do {
	 *     // get data
	 * } while (in.getYN("Is this correct?") == 'n');
	 * </pre>
	 * 
	 * @param prompt the prompt to print
	 * @return a 'y' or 'n', depending on the response
	 */
	public char getYN(String prompt) {
		if (! REGEX_ENDS_WITH_YN.matcher(prompt).find()) {
			prompt += " (y,n) ";
		}
		
		String result = getString(prompt, yesOrNo());
		
		return result.toLowerCase().charAt(0);
	}
	
	/* Get integer methods */
	
	/**
	 * Prompts for and gets an integer from the console.  Must be a valid integer.  A validater can be 
	 * entered for further validation.
	 * 
	 * @param prompt the prompt to print
	 * @param validater an integer predicate to validate the input.  Can be null.
	 * @return the entered (and possibly validated) integer
	 * @see #getInt(String)
	 * @see #getInt()
	 * @see #intRange(int, int)
	 */
	public int getInt(String prompt, IntPredicate validater) {
		lastCallWasGetEmpty = false;
		boolean inputInvalid = false;
		int result = 0;
		
		do {
			printPrompt(prompt);
			
    		try {
    			result = console.nextInt();
    			
    			if (validater == null || validater.test(result)) {
    				inputInvalid = false;
        		} else {
        			System.out.println(VALIDATER_FALSE_PROMPT);
        			inputInvalid = true;
        		}
    		} catch (InputMismatchException ime) {
    			System.out.println(INT_ERROR_PROMPT);
    			inputInvalid = true;
    			console.next(); // consume bad token
    		}
		} while (inputInvalid);
		
		return result;
	}

	/**
	 * Prompts for and gets an integer from the console.  Must be a valid integer.  No validation
	 * beyond this is performed.
	 * 
	 * @param prompt the prompt to print
	 * @return a valid integer
	 * @see #getInt(String, IntPredicate)
	 * @see #getInt()
	 */
	public int getInt(String prompt) {
		return getInt(prompt, null);
	}
	
	/**
	 * Prompts for a gets an integer from the console.  Must be a valid integer.  No validation
	 * beyond this is performed.  Uses a generic prompt.
	 * 
	 * @return a valid integer
	 * @see #getInt(String, IntPredicate)
	 * @see #getInt(String)
	 */
	public int getInt() {
		return getInt(INT_PROMPT, null);
	}
	
	/* Get double methods */
	
	/**
	 * Prompts for and gets a double from the console.  Must be a valid double.  A validater can be 
	 * entered for further validation.
	 * 
	 * @param prompt the prompt to print
	 * @param validater a double predicate to validate the input.  Can be null.
	 * @return the entered (and possibly validated) double
	 * @see #getDouble(String)
	 * @see #getDouble()
	 * @see #doubleRange(double, double)
	 */
	public double getDouble(String prompt, DoublePredicate validater) {
		lastCallWasGetEmpty = false;
		boolean inputInvalid = false;
		double result = 0.0;
		
		do {
			printPrompt(prompt);
			
			try {
				result = console.nextDouble();
				
    			if (validater == null || validater.test(result)) {
    				inputInvalid = false;
        		} else {
        			System.out.println(VALIDATER_FALSE_PROMPT);
        			inputInvalid = true;
        		}
			} catch (InputMismatchException ime) {
				System.out.println(DOUBLE_ERROR_PROMPT);
    			inputInvalid = true;
    			console.next(); // consume bad token
			}
			
		} while (inputInvalid);
		
		return result;
	}
	
	/**
	 * Prompts for and gets a double from the console.  Must be a valid double.  No further validation
	 * is done.
	 * 
	 * @param prompt the prompt to print
	 * @return a valid double
	 * @see #getDouble(String, DoublePredicate)
	 * @see #getDouble()
	 */
	public double getDouble(String prompt) {
		return getDouble(prompt, null);
	}
	
	/**
	 * Prompts for and gets a double from the console.  Must be a valid double.  No further validation
	 * is done.  Uses a generic message for the prompt.
	 * 
	 * @return a valid double
	 * @see #getDouble(String, DoublePredicate)
	 * @see #getDouble(String)
	 */
	public double getDouble() {
		return getDouble(DOUBLE_PROMPT, null);
	}
	
	/* Pause methods */
	
	/**
	 * Prompts the user, then waits for the &lt;enter&gt; key to be pressed.  Any input before the
	 * &lt;enter&gt; key is pressed is ignored.
	 * 
	 * @param prompt the prompt to print
	 * @see #pause()
	 */
	public void pause(String prompt) {
		printPrompt(prompt);
		
		if (! lastCallWasGetEmpty) {
			console.nextLine();
		}
		
		console.nextLine();
		lastCallWasGetEmpty = true;
	}
	
	/**
	 * Prompts the user, then waits for the &lt;enter&gt; key to be pressed.  Any input before the
	 * &lt;enter&gt; key is pressed is ignored.  A generic message is displayed.
	 * 
	 * @see #pause(String)
	 */
	public void pause() {
		pause(null);
	}
	
	/**
	 * Prints {@code prompt}, checking whether it needs to add a colon to the end.
	 * Changes an empty or null prompt to a generic text. 
	 * 
	 * @param prompt the prompt to display
	 */
	private void printPrompt(String prompt) {
		if (prompt == null || prompt.isEmpty()) {
			prompt = CONTINUE_PROMPT;
		} else if (! REGEX_DONT_ADD_COLON.matcher(prompt).find() ) {
			prompt = prompt + ": ";
		}
		
		System.out.print(prompt);
	}

}
