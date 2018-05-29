package events;

import core.Order;
import core.Restaurant;

/**
 * Represents a Return Event.
 */
public class ReturnEvent extends Event {
	private final Order order;// an order
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
}
