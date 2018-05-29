package events;

import core.Order;
import user.Restaurant;

/**
 * Represents a Return Event.
 */
public class ReturnEvent extends Event {
	private final Order order;
	private final String message; // The reason for returning the order

	/**
	 * Creates a Return Event.
	 *
	 * @param restaurant A Restaurant.
	 * @param order      an Order to be returned.
	 * @param message    The reason for return.
	 */
	private ReturnEvent(Restaurant restaurant, Order order, String message) {
		super(TYPE.RETURN, restaurant);
		this.order = order;
		this.message = message;
		System.out.printf("Order returned: %s", message);
	}

	/**
	 * Returns an Order.
	 *
	 * @return an Order.
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * Executes a Return Event.
	 */
	@Override
	public void execute() {
		if (!order.isDelivered()) {
			System.err.println("Order needs to be delivered before returned");
			System.exit(1);
		}
		if (this.order.getProgress() == Order.ORDER_CANCELLED) {
			System.err.println("Order %s was cancelled, cannot complete event");
			System.err.flush();
			System.exit(1);
		}

		// First, cancel the order
		new CancelEvent(restaurant, order, CancelEvent.REASON.CUSTOMER_RETURNED, message).execute();
		// Now make a new order
		new OrderEvent(order, order.getServer(), restaurant).execute();
	}

	/**
	 * Parses a String to create a Return Event.
	 *
	 * @param data       A String to be parsed.
	 * @param restaurant A Restaurant.
	 * @return a ReturnEvent from a parse String, or exit program is String is malformed.
	 */
	public static ReturnEvent parse(String data, Restaurant restaurant) {
		data = data.trim();
		String[] splitData = data.split("/");

		if (splitData.length != 2) {
			System.err.println("RETURN event must be in format:");
			System.err.println("\tRETURN|<ORDER ID>/<RETURN REASON>");
			System.exit(1);
			return null;
		}

		int orderNumber = Integer.valueOf(splitData[0]);
		String returnReason = splitData[1];
		Order order = restaurant.getKitchen().getOrder(orderNumber);
		return new ReturnEvent(restaurant, order, returnReason);
	}
}
