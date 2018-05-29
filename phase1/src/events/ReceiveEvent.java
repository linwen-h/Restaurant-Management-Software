package events;

import core.Order;
import employee.Cook;
import user.Restaurant;
import util.Log;

/**
 * A class representing a ReceiveEvent.
 */
public class ReceiveEvent extends Event {
	private Order order;
	private Cook cook; // The cook who receives the order

	/**
	 * Creates a ReceiveEvent.
	 *
	 * @param order      an Order to be received.
	 * @param cook       the Cook receiving an order.
	 * @param restaurant a Restaurant.
	 */
	private ReceiveEvent(Order order, Cook cook, Restaurant restaurant) {
		super(TYPE.RECEIVE, restaurant);
		this.order = order;
		this.cook = cook;
	}

	/**
	 * Returns the Cook for the ReceiveEvent.
	 *
	 * @return a Cook.
	 */
	public Cook getCook() {
		return cook;
	}

	/**
	 * Return this ReceiveEvent's order.
	 *
	 * @return a Order.
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * Executes a ReceiveEvent.
	 */
	@Override
	public void execute() {
		if (!order.isDispatched()) {
			System.err.println("Order needs to be dispatched before receiving");
			System.exit(1);
		}

		if (this.order.getProgress() == Order.ORDER_CANCELLED) {
			System.err.printf("Order %s was cancelled, cannot complete event\n", order);
			return;
		}

		if (this.cook.getId() != order.getCook().getId()) {
			System.err.printf("Cook %d could not receive order %d, Reason: order was dispatched to cook %d\n",
					this.cook.getId(), order.getOrderNumber(), order.getCook().getId());
			System.exit(1);
		}

		this.order.setProgress(Order.ORDER_RECEIVED);
		Log.logID(cook, cook.getId(), String.format("Order %s received and ready to cook", order));
	}

	/**
	 * Parses a String to create a ReceiveEvent.
	 *
	 * @param data A string to be parsed.
	 * @param res  A Restaurant.
	 * @return a ReceiveEvent, or exits if String malformed.
	 */
	public static ReceiveEvent parse(String data, Restaurant res) {
		String[] dataarr = data.split("/");
		if (dataarr.length != 2) {
			System.err.println("RECEIVE event must be in format:\n\tRECEIVE|<ORDER ID>/<COOK ID>");
			System.exit(1);
			return null;
		}

		Order order = res.getKitchen().getOrder(Integer.valueOf(dataarr[0]));
		Cook cook = res.getKitchen().getCook(Integer.valueOf(dataarr[1]));
		return new ReceiveEvent(order, cook, res);
	}
}
