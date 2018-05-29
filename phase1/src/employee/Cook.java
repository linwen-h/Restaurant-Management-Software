package employee;

import core.Order;
import user.Restaurant;

/**
 * Represents a cook in a restaurant.
 */
public class Cook extends Employee {
	private boolean available; // If this cook is available to process an order
	private static int counter = 0; // Ensures cook IDs are unique
	private Order order; // The order assigned to the cook

	/**
	 * Creates a Cook.
	 *
	 * @param restaurant the Restaurant the Cook belongs to.
	 */
	public Cook(Restaurant restaurant) {
		super(++counter, restaurant);
		this.available = true;
		this.order = null;
	}

	/**
	 * Checks if the cook is currently available to cook an order.
	 *
	 * @return true if the cook can currently cook an order.
	 */
	public boolean isAvailable() {
		return available;
	}

	/**
	 * Changes the cook's current availability status.
	 *
	 * @param bool the new availability status of the chef.
	 */
	public void setAvailable(boolean bool) {
		this.available = bool;
	}

	/**
	 * Sets the Order of the Cook.
	 *
	 * @param order an Order.
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * Returns the current Order of the Cook.
	 *
	 * @return the current Order.
	 */
	public Order currentOrder() {
		return this.order;
	}

	/**
	 * Returns a String representation of the Cook.
	 *
	 * @return A String for the cook.
	 */
	@Override
	public String toString() {
		return "Cook";
	}
}
