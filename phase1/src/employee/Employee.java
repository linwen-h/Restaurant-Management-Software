package employee;

import util.Log;
import user.Restaurant;

/**
 * Represents an employee of a restaurant.
 */
public abstract class Employee {
	final Restaurant restaurant;
	private final int id; // 1-indexed IDs

	/**
	 * Creates an Employee.
	 *
	 * @param id         the ID of the employee
	 * @param restaurant the Restaurant that the Employee works at.
	 */
	Employee(int id, Restaurant restaurant) {
		this.restaurant = restaurant;
		this.id = id;
	}

	/**
	 * Logs an Employee's id and a message.
	 *
	 * @param str The message to print out to console.
	 */
	public void log(String str) {
		Log.logID(this, id, str);
	}

	/**
	 * Returns the ID of the employee.
	 *
	 * @return a int representing the ID of the Employee.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Receives a shipment and updates the receiver's kitchen's inventory. The shipment string must be in the form
	 * "[ing, num]/[ing, num]/...".
	 * <p>
	 * For use in phase 2.
	 *
	 * @param shipment the shipment to be recorded, as a string.
	 */
	public void receiveShipment(String shipment) {
		String[] items = shipment.split("/");

		for (String item : items) {
			String revised = item.replaceAll("\\[|]", "");
			String[] itemInfo = revised.split(",");

			if (itemInfo.length != 2) {
				System.err.println("Each shipment intake must be in format: [Ingredient,Amount]");
			}

			restaurant.getKitchen().updateInventory(itemInfo[0], Integer.valueOf(itemInfo[1]));
		}
	}

	/**
	 * Returns a String representation of an Employee.
	 *
	 * @return a String representation of an Employee containing the Employee's id.
	 */
	@Override
	public String toString() {
		return String.format("Employee %d", id);
	}
}
