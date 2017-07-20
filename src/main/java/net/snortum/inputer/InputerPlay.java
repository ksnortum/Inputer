package net.snortum.inputer;

/**
 * Provides examples for how to use {@link Inputer}
 * 
 * @author Knute Snortum
 * @version 2017.07.18
 */
public class InputerPlay {

	public static void main(String[] args) {
		new InputerPlay().run();
	}
	
	private void run() {
		Inputer in = new Inputer();
		String name;
		int age;
		int number;
		String gender;
		double total;
		double extra;
		
		String agree = in.getString("Do you agree? (y,n) ", Inputer.yesOrNo());
		in.pause();
		
		do {
    		name = in.getString("Enter your name");
    		age = in.getInt("Enter your age", Inputer.intRange(0, 130));
    		number = in.getInt("Enter a positive integer", i -> i > 0);
    		gender = in.getString("Enter gender (m/f/t) ", Inputer.oneOfThese("m", "f", "t"));
    		total = in.getDouble("Enter a positive amount: ", d -> d > 0);
    		extra = in.getDouble("Enter extra charge", Inputer.doubleRange(1.5, 9.5));
		} while (in.getYN("Is this correct?") == 'n');
		
		System.out.printf("Name: %s, Age %d, Number: %d, Gender %s%n", name, age, number, gender);
		System.out.printf("Extra charge: %1.2f, Total: %,10.2f ", extra, total);
		System.out.println(agree.charAt(0) == 'y' ? "User agreed" : "User didn't agree");
	}

}
