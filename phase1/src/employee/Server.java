package employee;

import user.Restaurant;

public class Server extends Employee {
	private static int counter = 0; // Ensures each server ID is unique

	/**
	 * Creates a Server working at a particular restaurant.
	 *
	 * @param restaurant the Restaurant that the Server works at.
	 */
	public Server(Restaurant restaurant) {
		super(++counter, restaurant);
	}

	/**
	 * Return a String representation of the Server.
	 *
	 * @return a String representation of the Server.
	 */
	@Override
	public String toString() {
		return "Server";
	}
}
