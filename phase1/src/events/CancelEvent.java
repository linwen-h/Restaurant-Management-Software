package events;

import core.Order;
import core.Table;
import employee.Cook;
import user.Restaurant;

/**
 * A class representing a CancelEvent.
 */
public class CancelEvent extends Event {
	private final Order order; // The order being cancelled
	private final REASON reason; // Reason for cancellation
	private final String description; // A more descriptive reason

	/**
	 * A String representing a REASON for a canceled Event.
	 */
	public enum REASON {
		OUT_OF_INGREDIENTS("Missing Ingredients"),
		CUSTOMER_CANCELLED("Customer cancelled the order"),
		CUSTOMER_RETURNED("Customer returned the order");

		final String reason;

		/**
		 * Creates a Reason.
		 *
		 * @param reason a String represents a Reason.
		 */
		REASON(String reason) {
			this.reason = reason;
		}

		/**
		 * Returns a String representation of a REASON.
		 *
		 * @return a String representing a REASON.
		 */
		@Override
		public String toString() {
			return reason;
		}
	}

	/**
	 * Creates a CancelEvent.
	 *
	 * @param restaurant  A Restaurant.
	 * @param order       A order being cancelled.
	 * @param reason      A REASON for cancellation.
	 * @param description A more description reason for cancellation.
	 */
	public CancelEvent(Restaurant restaurant, Order order, REASON reason, String description) {
		super(TYPE.CANCEL, restaurant);
		this.order = order;
		this.reason = reason;
		this.description = description;
	}

	/**
	 * Returns a Order for a CancelEvent.
	 *
	 * @return a Order.
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * Executes a CancelEvent.
	 */
	@Override
	public void execute() {
		order.setProgress(Order.ORDER_CANCELLED);
		Cook cook = order.getCook();
		cook.setAvailable(true);

		cook.log(String.format("Order %s cancelled, Reason: %s%s", order, getReason(), this.description != null ?
				", " + description : ""));
		order.setCook(null);

		Table table = order.getTable();
		if (table.hasOrder(order)) {
			table.removeOrder(order);
		}
	}

	/**
	 * Returns a REASON for this CancelEvent.
	 *
	 * @return a REASON.
	 */
	private REASON getReason() {
		return reason;
	}

	/**
	 * Parses a String and returns a CancelEvent.
	 *
	 * @param data       A String to be parsed.
	 * @param restaurant a Restaurant.
	 * @return a CancelEvent or null if String is malformed.
	 */
	public static CancelEvent parse(String data, Restaurant restaurant) {
		data = data.trim();
		if (data.isEmpty()) {
			System.err.println("CANCEL must be in format:\n\tCANCEL|<ORDER ID>");
			return null;
		}

		int orderNumber = Integer.valueOf(data);
		Order order = restaurant.getKitchen().getOrder(orderNumber);
		return new CancelEvent(restaurant, order, REASON.CUSTOMER_CANCELLED, null);
	}
}
