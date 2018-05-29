package events;

import util.Log;
import user.Restaurant;

/**
 * A class representing a DelayEvent.
 */

public class DelayEvent extends Event {
	private final int amount; // Amount of time to delay

	/**
	 * Creates a DelayedEvent.
	 *
	 * @param amount     The seconds to delay by.
	 * @param restaurant a Restaurant.
	 */
	private DelayEvent(int amount, Restaurant restaurant) {
		super(TYPE.DELAY, restaurant);
		this.amount = amount;
	}

	/**
	 * Executes a DelayEvent.
	 */
	@Override
	public void execute() {
		try {
			Log.log("Thread", String.format("Adding delay for %d seconds", amount));
			Thread.sleep(amount * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parses a String to create a DelayEvent.
	 *
	 * @param data a String to be parsed.
	 * @param res  a Restaurant.
	 * @return a S DelayEvent or exits if String is malformed.
	 */
	public static DelayEvent parse(String data, Restaurant res) {
		int amount;
		try {
			amount = Integer.valueOf(data.trim());
		} catch (Exception ex) {
			System.err.println("Each delay must be in the following format:");
			System.err.println("\tDELAY|<AMOUNT>");
			System.exit(1);
			return null;
		}

		return new DelayEvent(amount, res);
	}
}
