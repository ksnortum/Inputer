package net.snortum.inputer;

import java.util.Arrays;
import java.util.Scanner;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Provides an easy way to prompt for, get, and validate data from the command line.
 *
 * @author Knute Snortum
 * @version 2020.03.29
 */
public class Inputer {
	private static final Scanner KEYBOARD = new Scanner(System.in);

	private static final String INT_PROMPT = "Enter an integer: ";
	private static final String INT_ERROR_PROMPT = "Invalid integer";
	private static final String LONG_PROMPT = "Enter a long: ";
	private static final String LONG_ERROR_PROMPT = "Invalid long";
	private static final String STRING_PROMPT = "Enter an string: ";
	private static final String DOUBLE_PROMPT = "Enter a double (decimal): ";
	private static final String DOUBLE_ERROR_PROMPT = "Invalid double (decimal)";
	private static final String VALIDATER_FALSE_PROMPT = "Invalid value";
	private static final String CONTINUE_PROMPT = "Press <enter> to continue: ";

	private static final String STRING_DONT_ADD_COLON =
			  "[:?]"  // match one of the chars between the brackets
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
	private static final Pattern REGEX_DONT_ADD_OPTIONAL = Pattern.compile("optional", Pattern.CASE_INSENSITIVE);

	/* Do not instantiate */
	private Inputer() {
	}

	/* Get String methods */

	/**
	 * Prompts for and gets a string, optionally validated, with an optional default value
	 *
	 * @param prompt the prompt to print
	 * @param validater a string predicate to validate the input with.  Can be null.
	 * @param defalt the default value if &lt;enter&gt; is pressed.  If an empty string is valid, <b>defalt</b>
	 *               should be empty.  If there is no default, pass <code>null</code>.  Any non-null
	 *               value is considered valid, regardless of the value of <b>validater</b>.
	 * @return the entered (and possibly validated) string
	 * @see #getString(String, Predicate)
	 * @see #getString(String)
	 * @see #getString()
	 * @see #oneOfThese(String...)
	 * @see #yesOrNo()
	 * @see #getYN(String)
	 */
	public static String getString(String prompt, Predicate<String> validater, String defalt) {
		boolean inputInvalid = false;
		String result = "";

		do {
			printPrompt(prompt, defalt);
			result = KEYBOARD.nextLine();

			// User pressed <enter>
			if (result.isEmpty()) {

				// Take the default value
				if (defalt != null) {
					result = defalt;
					inputInvalid = false;
				} else {

					// Empty string is invalid if default value is null
					System.out.println(VALIDATER_FALSE_PROMPT);
        			inputInvalid = true;
				}
			} else {

				// Validate
				if (validater == null || validater.test(result)) {
					inputInvalid = false;
				} else {
					System.out.println(VALIDATER_FALSE_PROMPT);
					inputInvalid = true;
				}
			}

		} while (inputInvalid);

		return result;
	}

	/**
	 * Prompts for and gets a string, optionally validated, with no default value
	 *
	 * @param prompt the prompt to print
	 * @param validater a string predicate to validate the input with.  Can be null.
	 * @return the entered (and possibly validated) string
	 * @see #getString(String, Predicate, String)
	 * @see #getString(String)
	 * @see #getString()
	 */
	public static String getString(String prompt, Predicate<String> validater) {
		return getString(prompt, validater, null);
	}

	/**
	 * Prompts for and gets a string from the keyboard.  No validation is performed.  No default value.
	 *
	 * @param prompt the prompt to print
	 * @return the entered string
	 * @see #getString(String, Predicate, String)
	 * @see #getString(String, Predicate)
	 * @see #getString()
	 */
	public static String getString(String prompt) {
		return getString(prompt, null, null);
	}

	/**
	 * Prompts for and gets a string from the keyboard.  A standard prompt is used.  No validation is performed.
	 *
	 * @return the entered string
	 * @see #getString(String, Predicate, String)
	 * @see #getString(String, Predicate)
	 * @see #getString(String)
	 */
	public static String getString() {
		return getString(STRING_PROMPT, null, null);
	}

	/**
	 * <p>Prompts for and gets a yes or no response.  The response need only start with "y", "n", "Y", or "N".
	 * If the prompt does not already end with a (y,n) or similar text, it will be added.  The first character
	 * of the response, lower-cased, is returned so that it can be tested in a loop.  For example:</p>
	 *
	 * <pre>
	 * do {
	 *     // get data
	 * } while (Inputer.getYN("Is this correct?") == 'n');
	 * </pre>
	 *
	 * @param prompt the prompt to print
	 * @return a 'y' or 'n', depending on the response
	 */
	public static char getYN(String prompt) {
		if (! REGEX_ENDS_WITH_YN.matcher(prompt).find()) {
			prompt += " (y,n)";
		}

		String result = getString(prompt, yesOrNo(), null);

		return result.toLowerCase().charAt(0);
	}

	/* Get integer methods */

	/**
	 * Prompts for and gets an integer from the keyboard.  Must be a valid integer.  A validater can be
	 * entered for further validation.  Allows a default value to be entered.
	 *
	 * @param prompt the prompt to print
	 * @param validater an integer predicate to validate the input.  Can be null.
	 * @param defalt the default value if &lt;enter&gt; is pressed.  If there is no default, pass
	 *               <code>null</code>.  Any non-null value is considered valid, regardless of the
	 *               value of <b>validater</b>.
	 * @return the entered (and possibly validated) integer
	 * @see #getInt(String, IntPredicate)
	 * @see #getInt(String)
	 * @see #getInt()
	 * @see #intRange(int, int)
	 */
	public static int getInt(String prompt, IntPredicate validater, Integer defalt) {
		boolean inputInvalid = false;
		int result = 0;

		do {
			printPrompt(prompt, String.valueOf(defalt));
			String input = KEYBOARD.nextLine();

			// User pressed <enter>
			if (input.isEmpty()) {

				// Take the default value
				if (defalt != null) {
					result = defalt;
					inputInvalid = false;
				} else {

					// Pressing <enter> is invalid if default value is null
					System.out.println(VALIDATER_FALSE_PROMPT);
					inputInvalid = true;
				}
			} else {
				try {
					result = Integer.parseInt(input);

					// Validate
					if (validater == null || validater.test(result)) {
						inputInvalid = false;
					} else {
						System.out.println(VALIDATER_FALSE_PROMPT);
						inputInvalid = true;
					}
				} catch (NumberFormatException nfe) {
					System.out.println(INT_ERROR_PROMPT);
					inputInvalid = true;
				}
			}

		} while (inputInvalid);

		return result;
	}

	/**
	 * Prompts for and gets an integer from the keyboard.  Must be a valid integer.  A validater can be
	 * entered for further validation.  No default value.
	 *
	 * @param prompt the prompt to print
	 * @param validater an integer predicate to validate the input.  Can be null.
	 * @return the entered (and possibly validated) integer
	 * @see #getInt(String, IntPredicate, Integer)
	 * @see #getInt(String)
	 * @see #getInt()
	 * @see #intRange(int, int)
	 */
	public static int getInt(String prompt, IntPredicate validater) {
		return getInt(prompt, validater, null);
	}

	/**
	 * Prompts for and gets an integer from the keyboard.  Must be a valid integer.  No validation
	 * beyond this is performed. No default value.
	 *
	 * @param prompt the prompt to print
	 * @return a valid integer
	 * @see #getInt(String, IntPredicate, Integer)
	 * @see #getInt(String, IntPredicate)
	 * @see #getInt()
	 */
	public static int getInt(String prompt) {
		return getInt(prompt, null, null);
	}

	/**
	 * Prompts for and gets an integer from the keyboard.  Must be a valid integer.  No validation
	 * beyond this is performed.  No default value.  Uses a standard prompt.
	 *
	 * @return a valid integer
	 * @see #getInt(String, IntPredicate, Integer)
	 * @see #getInt(String, IntPredicate)
	 * @see #getInt(String)
	 */
	public static int getInt() {
		return getInt(INT_PROMPT, null, null);
	}

	/* Get long methods */

	/**
	 * Prompts for and gets a long from the keyboard.  Must be a valid long.  A validater can be
	 * entered for further validation.  Allows a default value to be entered.
	 *
	 * @param prompt the prompt to print
	 * @param validater a long predicate to validate the input.  Can be null.
	 * @param defalt the default value if &lt;enter&gt; is pressed.  If there is no default, pass
	 *               <code>null</code>.  Any non-null value is considered valid, regardless of the
	 *               value of <b>validater</b>.
	 * @return the entered (and possibly validated) long
	 * @see #getLong(String, LongPredicate)
	 * @see #getLong(String)
	 * @see #getLong()
	 * @see #longRange(long, long)
	 */
	public static long getLong(String prompt, LongPredicate validater, Long defalt) {
		boolean inputInvalid = false;
		long result = 0;

		do {
			printPrompt(prompt, String.valueOf(defalt));
			String input = KEYBOARD.nextLine();

			// User pressed <enter>
			if (input.isEmpty()) {

				// Take the default value
				if (defalt != null) {
					result = defalt;
					inputInvalid = false;
				} else {

					// Pressing <enter> is invalid if default value is null
					System.out.println(VALIDATER_FALSE_PROMPT);
					inputInvalid = true;
				}
			} else {
				try {
					result = Long.parseLong(input);

					// Validate
					if (validater == null || validater.test(result)) {
						inputInvalid = false;
					} else {
						System.out.println(VALIDATER_FALSE_PROMPT);
						inputInvalid = true;
					}
				} catch (NumberFormatException nfe) {
					System.out.println(LONG_ERROR_PROMPT);
					inputInvalid = true;
				}
			}

		} while (inputInvalid);

		return result;
	}

	/**
	 * Prompts for and gets a long from the keyboard.  Must be a valid long.  A validater can be
	 * entered for further validation.  No default value.
	 *
	 * @param prompt the prompt to print
	 * @param validater a long predicate to validate the input.  Can be null.
	 * @return the entered (and possibly validated) long
	 * @see #getLong(String, LongPredicate, Long)
	 * @see #getLong(String)
	 * @see #getLong()
	 * @see #longRange(long, long)
	 */
	public static long getLong(String prompt, LongPredicate validater) {
		return getLong(prompt, validater, null);
	}

	/**
	 * Prompts for and gets a long from the keyboard.  Must be a valid long.  No validation
	 * beyond this is performed.  No default value.
	 *
	 * @param prompt the prompt to print
	 * @return a valid long
	 * @see #getLong(String, LongPredicate, Long)
	 * @see #getLong(String, LongPredicate)
	 * @see #getLong()
	 */
	public static long getLong(String prompt) {
		return getLong(prompt, null, null);
	}

	/**
	 * Prompts for and gets a long from the keyboard.  Must be a valid long.  No validation
	 * beyond this is performed.  No default value.  Uses a standard prompt.
	 *
	 * @return a valid long
	 * @see #getLong(String, LongPredicate, Long)
	 * @see #getLong(String, LongPredicate)
	 * @see #getLong(String)
	 */
	public static long getLong() {
		return getLong(LONG_PROMPT, null, null);
	}
	
	/* Get double methods */

	/**
	 * Prompts for and gets a double from the keyboard.  Must be a valid double.  A validater can be
	 * entered for further validation.  Allows for a default value to be entered.
	 *
	 * @param prompt the prompt to print
	 * @param validater a double predicate to validate the input.  Can be null.
	 * @param defalt the default value if &lt;enter&gt; is pressed.  If there is no default, pass
	 *               <code>null</code>.  Any non-null value is considered valid, regardless of the
	 *               value of <b>validater</b>.
	 * @return the entered (and possibly validated) double
	 * @see #getDouble(String, DoublePredicate)
	 * @see #getDouble(String)
	 * @see #getDouble()
	 * @see #doubleRange(double, double)
	 */
	public static double getDouble(String prompt, DoublePredicate validater, Double defalt) {
		boolean inputInvalid = false;
		double result = 0.0;

		do {
			printPrompt(prompt, null);
			String input = KEYBOARD.nextLine();

			// User pressed <enter>
			if (input.isEmpty()) {

				// Take the default value
				if (defalt != null) {
					result = defalt;
					inputInvalid = false;
				} else {

					// Pressing <enter> is invalid if default value is null
					System.out.println(VALIDATER_FALSE_PROMPT);
					inputInvalid = true;
				}
			} else {
				try {
					result = Double.parseDouble(input);

					// Validate
					if (validater == null || validater.test(result)) {
						inputInvalid = false;
					} else {
						System.out.println(VALIDATER_FALSE_PROMPT);
						inputInvalid = true;
					}
				} catch (NumberFormatException nfe) {
					System.out.println(DOUBLE_ERROR_PROMPT);
					inputInvalid = true;
				}
			}

		} while (inputInvalid);

		return result;
	}

	/**
	 * Prompts for and gets a double from the keyboard.  Must be a valid double.  A validater can be
	 * entered for further validation.  No default value.
	 *
	 * @param prompt the prompt to print
	 * @param validater a double predicate to validate the input.  Can be null.
	 * @return the entered (and possibly validated) double
	 * @see #getDouble(String, DoublePredicate, Double)
	 * @see #getDouble(String)
	 * @see #getDouble()
	 */
	public static double getDouble(String prompt, DoublePredicate validater) {
		return getDouble(prompt, validater, null);
	}

	/**
	 * Prompts for and gets a double from the keyboard.  Must be a valid double.  No further validation
	 * is done.  No default value.
	 *
	 * @param prompt the prompt to print
	 * @return a valid double
	 * @see #getDouble(String, DoublePredicate, Double)
	 * @see #getDouble(String, DoublePredicate)
	 * @see #getDouble()
	 */
	public static double getDouble(String prompt) {
		return getDouble(prompt, null, null);
	}

	/**
	 * Prompts for and gets a double from the keyboard.  Must be a valid double.  No further validation
	 * is done.  No default value.  Uses a standard message for the prompt.
	 *
	 * @return a valid double
	 * @see #getDouble(String, DoublePredicate, Double)
	 * @see #getDouble(String, DoublePredicate)
	 * @see #getDouble(String)
	 */
	public static double getDouble() {
		return getDouble(DOUBLE_PROMPT, null, null);
	}

	/* Pause methods */

	/**
	 * Prompts the user, then waits for the &lt;enter&gt; key to be pressed.  Any input before the
	 * &lt;enter&gt; key is pressed is ignored.
	 *
	 * @param prompt the prompt to print
	 * @see #pause()
	 */
	public static void pause(String prompt) {
		printPrompt(prompt, null);
		KEYBOARD.nextLine();
	}

	/**
	 * Prompts the user, then waits for the &lt;enter&gt; key to be pressed.  Any input before the
	 * &lt;enter&gt; key is pressed is ignored.  A standard message is displayed.
	 *
	 * @see #pause(String)
	 */
	public static void pause() {
		pause(null);
	}

	/* Helper predicates for input verification */

	/**
	 * <p>Takes low and high integers and returns a predicate that tests if an integer
	 * is between the low and high, inclusive.  Used by the client program as an easy way to test for
	 * a range of integers.  For example:</p>
	 *
	 * <p>{@code int age = Inputer.getInt("Enter your age", Inputer.intRange(0, 130));}</p>
	 *
	 * @param low the lower range, inclusive
	 * @param high the higher range, inclusive
	 * @return a predicate that tests if an integer is within range
	 * @throws IllegalArgumentException if low is greater than high
	 */
	public static IntPredicate intRange(int low, int high) {
		if (low > high) {
			throw new IllegalArgumentException("Parameter low must be less than or equal to high");
		}

		return i -> i >= low && i <= high;
	}
	
	/**
	 * <p>Takes low and high longs and returns a predicate that tests if a long
	 * is between the low and high, inclusive.  Used by the client program as an easy way to test for
	 * a range of longs.  For example:</p>
	 *
	 * <p>{@code long num = Inputer.getLong("Enter number of stars", Inputer.longRange(0, 9999999999L));}</p>
	 *
	 * @param low the lower range, inclusive
	 * @param high the higher range, inclusive
	 * @return a predicate that tests if a long is within range
	 * @throws IllegalArgumentException if low is greater than high
	 */
	public static LongPredicate longRange(long low, long high) {
		if (low > high) {
			throw new IllegalArgumentException("Parameter low must be less than or equal to high");
		}

		return l -> l >= low && l <= high;
	}

	/**
	 * <p>Takes low and high doubles and returns a predicate that tests if a double
	 * is between low and high, inclusive.  Used by the client program as an easy way to test for
	 * a range of doubles.  For example:</p>
	 *
	 * <p>{@code double extra = Inputer.getDouble("Enter extra charge", Inputer.doubleRange(1.5, 9.5));}</p>
	 *
	 * @param low the lower range, inclusive
	 * @param high the higher range, inclusive
	 * @return a predicate that tests if a double is within range
	 * @throws IllegalArgumentException if low is greater than high
	 */
	public static DoublePredicate doubleRange(double low, double high) {
		if (low > high) {
			throw new IllegalArgumentException("Parameter low must be less than or equal to high");
		}

		return d -> d >= low && d <= high;
	}

	/**
	 * <p>Takes two or more strings and returns a predicate that test if a string
	 * matches any of the passed strings.  Used by the client program to easily test a string against
	 * a list of valid strings.  For instance:</p>
	 *
	 * <p>{@code String gender = Inputer.getString("Enter gender (m/f/t) ", Inputer.oneOfThese("m", "f", "t"));}</p>
	 *
	 * @param options one or more strings to test for validity against an entered string
	 * @return a predicate that tests if a string matches any of the options
	 * @throws IllegalArgumentException if less than two options are entered
	 */
	public static Predicate<String> oneOfThese(String... options) {
		if (options.length < 2) {
			throw new IllegalArgumentException("You must enter at least two options");
		}

		return input -> Arrays.stream(options).anyMatch(s -> s.equals(input));
	}

	/**
	 * <p>Returns a predicate that will test if a string starts with "Y", "N", "y", or "n".
	 * Used by the client program to test for a "y/n" response.  For example:</p>
	 *
	 * <p>{@code String agree = Inputer.getString("Do you agree? (y,n) ", Inputer.yesOrNo());}</p>
	 *
	 * @return the predicate
	 * @see #getYN(String)
	 */
	public static Predicate<String> yesOrNo() {
		return input -> "YNyn".chars().anyMatch(c -> (input + " ").charAt(0) == c);
	}

	/* Private methods */

	/**
	 * Prints {@code prompt}, checking whether it needs to add a colon to the end.
	 * Changes an empty or null prompt to a standard text.
	 *
	 * @param prompt the prompt to display
	 * @param defalt the default value if &lt;enter&gt; is pressed
	 */
	private static void printPrompt(String prompt, String defalt) {

		// Assume a "continue" prompt
		if (prompt == null || prompt.isEmpty()) {
			prompt = CONTINUE_PROMPT;
		} else {

			// Add "optional" or default value to the end of the prompt
			if (defalt != null && ! "null".equals(defalt)) {
				if (defalt.isEmpty()) {
					if (! REGEX_DONT_ADD_OPTIONAL.matcher(prompt).find() ) {
						prompt += " (optional)";
					}
				} else {
					prompt += " [" + defalt + "]";
				}
			}

			// Append a colon if it's not already there
			if (! REGEX_DONT_ADD_COLON.matcher(prompt).find() ) {
				prompt += ": ";
			}
		}

		System.out.print(prompt);
	}

}
