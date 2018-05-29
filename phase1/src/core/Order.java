package core;

import employee.Cook;
import employee.Server;

import java.util.ArrayList;

/**
 * Represents an order, containing one item.
 */
public class Order {
	private MenuItem item; // The order's item
	private ArrayList<Ingredient> additions;
	private ArrayList<Ingredient> subtractions;

	private Table table; // Table that the order belongs to
	private int seatNumber; // Seat number at that table
	private Cook cook; // Assigned cook
	private final Server server; // Assigned server

	private int state; // See below for possible states
	private int orderNumber;


	/**
	 * Represents different states of an order (to avoid hardcoding).
	 */
	public static final int ORDER_ORDERED = 0;
	public static final int ORDER_DISPATCHED = 1;
	public static final int ORDER_RECEIVED = 2;
	public static final int ORDER_COOKED = 3;
	public static final int ORDER_DELIVERED = 4;
	public static final int ORDER_CANCELLED = 5;

	/**
	 * Creates an order with specific information.
	 *
	 * @param item         the menu item being ordered.
	 * @param additions    a list of additional ingredients to be added to the item.
	 * @param subtractions a list of ingredients that should be removed from the item.
	 * @param table        the table that the order corresponds to.
	 * @param seatNumber   the seat number a Order corresponds to.
	 * @param orderNumber  the Order number.
	 * @param server       a server taking the Order.
	 */
	public Order(MenuItem item,
				 ArrayList<Ingredient> additions,
				 ArrayList<Ingredient> subtractions,
				 Table table,
				 int seatNumber,
				 int orderNumber,
				 Server server) {
		this.item = item;
		this.additions = additions;
		this.subtractions = subtractions;
		this.table = table;
		this.seatNumber = seatNumber;
		this.orderNumber = orderNumber;
		this.server = server;
		state = ORDER_ORDERED;
	}

	/**
	 * Gets the price of the order.
	 *
	 * @return the price of the order.
	 */
	public double getPrice() {
		double price = item.getBasePrice();
		for (Ingredient addition : additions) {
			price += addition.getAdditionPrice();
		}
		return price;
	}

	/**
	 * Gets a list of all the ingredients used in the order.
	 *
	 * @return a list of all the ingredients used in the order.
	 */
	public ArrayList<Ingredient> getAllIngredients() {
		ArrayList<Ingredient> output = new ArrayList<>(item.getAllIngredients());
		output.addAll(additions);
		output.removeAll(subtractions);
		return output;
	}

	/**
	 * Gets the table that the order corresponds to.
	 *
	 * @return the table that requested the order.
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Gets the seat number this order is for.
	 *
	 * @return the seat number
	 */
	public int getSeatNumber() {
		return seatNumber;
	}

	/**
	 * Gets the cook that is responsible for the order.
	 *
	 * @return the cook responsible for the order.
	 */
	public Cook getCook() {
		return this.cook;
	}

	/**
	 * Sets the Cook for the Order.
	 *
	 * @param cook the cook for the order.
	 */

	public void setCook(Cook cook) {
		this.cook = cook;
	}

	/**
	 * Returns the item that this order corresponds to
	 *
	 * @return the order's item.
	 */
	public MenuItem getItem() {
		return item;
	}

	/**
	 * Returns the Order number.
	 *
	 * @return the order number.
	 */
	public int getOrderNumber() {
		return orderNumber;
	}

	/**
	 * Determines if the order has been received by a cook.
	 *
	 * @return true if the order has been received; false otherwise.
	 */
	public boolean isReceived() {
		return state >= ORDER_RECEIVED;
	}

	/**
	 * Determines if the order has been dispatched.
	 *
	 * @return true if the order has been dispatched; false otherwise.
	 */
	public boolean isDispatched() {
		return state >= ORDER_DISPATCHED;
	}

	/**
	 * Determines if the order has been delivered to its table.
	 *
	 * @return true if the order has been delivered; false otherwise.
	 */
	public boolean isDelivered() {
		return state >= ORDER_DELIVERED;
	}

	/**
	 * Determines if the order has been returned to the kitchen.
	 *
	 * @return true if the order has been returned; false otherwise.
	 */
	public boolean isCancelled() {
		return state >= ORDER_CANCELLED;
	}

	/**
	 * Determines if the order has been cooked.
	 *
	 * @return true is the order is cooked; false otherwise.
	 */
	public boolean isCooked() {
		return state >= ORDER_COOKED;
	}

	/**
	 * Sets the order's progress to a specified state.
	 *
	 * @param progress the new state of the order.
	 */
	public void setProgress(int progress) {
		if (progress >= ORDER_ORDERED && progress <= ORDER_CANCELLED) {
			state = progress;
		}
	}

	/**
	 * Returns the progress of the Order.
	 *
	 * @return a int representing the progress of the Order.
	 */
	public int getProgress() {
		return this.state;
	}

	/**
	 * Returns the Server of the Order.
	 *
	 * @return a Server.
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * Returns  String Representation of a Order.
	 *
	 * @return a String Representing the Order.
	 */
	@Override
	public String toString() {
		return String.format("(%s id:%d)", getItem().getName(), getOrderNumber());
	}
}
