package core;

import java.util.ArrayList;

/**
 * Represents a table at a restaurant.
 */
public class Table {
	private final int tableNumber;
	// The first index refers to the seat number and the second for that seat's orders
	private final ArrayList<ArrayList<Order>> seatOrders;

	/**
	 * Creates a table with a particular table number and number of seats.
	 *
	 * @param tableNumber   the table's identifying number.
	 * @param numberOfSeats the number of seats at a Table.
	 */
	public Table(int tableNumber, int numberOfSeats) {
		this.tableNumber = tableNumber;

		// Create empty lists of orders for each seat at the table
		seatOrders = new ArrayList<>();
		for (int i = 0; i < numberOfSeats; i++) {
			seatOrders.add(new ArrayList<>());
		}
	}

	/**
	 * Gets this table's unique number.
	 *
	 * @return the tableNumber.
	 */
	public int getTableNumber() {
		return tableNumber;
	}

	/**
	 * Clears the orders of a Table.
	 */
	public void clearOrders() {
		seatOrders.forEach(ArrayList::clear);
	}

	/**
	 * Removes all orders from the table associated with a particular seat number.
	 *
	 * @param seat the seat number whose orders should be removed.
	 */
	public void clearOrders(int seat) {
		seatOrders.get(seat - 1).clear();
	}

	/**
	 * Updates the table's bill with a new order.
	 *
	 * @param order the order that is being added to the bill.
	 */
	public void updateBill(Order order) {
		seatOrders.get(order.getSeatNumber() - 1).add(order);
	}

	/**
	 * Removes an Order from a table's orders.
	 *
	 * @param order the order that is being removed from the bill.
	 */
	public void removeOrder(Order order) {
		ArrayList<Order> orders = seatOrders.get(order.getSeatNumber() - 1);
		if (orders.contains(order)) {
			orders.remove(order);
		}
	}

	/**
	 * Checks if a table has an order.
	 *
	 * @param order an order.
	 * @return true if a table has the order, false otherwise.
	 */
	public boolean hasOrder(Order order) {
		return seatOrders.get(order.getSeatNumber() - 1).contains(order);
	}

	/**
	 * Gets a list of all the orders at a table.
	 *
	 * @return a list containing all of the table's orders.
	 */
	public ArrayList<Order> getOrders() {
		ArrayList<Order> all = new ArrayList<>();
		this.seatOrders.forEach(all::addAll);
		return all;
	}

	/**
	 * Gets a string representation of this table, used for logging
	 *
	 * @return "Table (id)"
	 */
	@Override
	public String toString() {
		return "Table (" + this.getTableNumber() + ")";
	}
}
