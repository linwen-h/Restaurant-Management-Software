package events;

import core.Order;
import employee.Cook;
import user.Restaurant;
import util.Log;

/**
 * Represents a DispatchEvent, i.e. the Kitchen sending the order to a cook.
 */
public class DispatchEvent extends Event {
	private final Order order;
	private final Cook cook; // Cook assigned to the order

	/**
	 * Creates a DispatchEvent.
	 *
	 * @param order      the order being dispatched.
	 * @param cook       the Cook the order is dispatched to.
	 * @param restaurant a Restaurant.
	 */
	private DispatchEvent(Order order, Cook cook, Restaurant restaurant) {
		super(TYPE.DISPATCH, restaurant);
		this.order = order;
		this.cook = cook;
	}

	/**
	 * Returns this DispatchEvent's Cook.
	 *
	 * @return a Cook.
	 */
	public Cook getCook() {
		return cook;
	}

	/**
	 * Returns this DispatchEvent's Order.
	 *
	 * @return a Order.
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * Executes a DispatchEvent.
	 */
	@Override
	public void execute() {
		if (this.order.getProgress() == Order.ORDER_CANCELLED) {
			System.err.println(String.format("Order %s was cancelled, cannot complete event", order));
			System.err.flush();
			return;
		}

		this.order.setProgress(Order.ORDER_DISPATCHED);
		this.order.setCook(cook);

		if (!cook.isAvailable()) {
			System.err.printf("Error dispatching order %d to cook %d. Reason: cook currently cooking order %d\n",
					order.getOrderNumber(), cook.getId(), cook.currentOrder().getOrderNumber());
			System.exit(1);
		}

		cook.setAvailable(false);
		cook.setOrder(order);
		Log.log(restaurant.getKitchen(), String.format("Dispatching order %s to Cook #%d", order, cook.getId()));
	}

	/**
	 * Parses a String to a DispatchEvent.
	 *
	 * @param data a String to be parsed.
	 * @param res  a Restaurant.
	 * @return a DispatchEvent or null if String malformed.
	 */
	public static DispatchEvent parse(String data, Restaurant res) {
		String[] dataarr = data.split("/");
		if (dataarr.length != 2) {
			System.err.println("DISPATCH event must be in format:\n\tDISPATCH|<ORDER ID>/<COOK ID>");
			System.exit(1);
			return null;
		}

		Order order = res.getKitchen().getOrder(Integer.valueOf(dataarr[0].trim()));
		Cook cook = res.getKitchen().getCook(Integer.valueOf(dataarr[1].trim()));

		return new DispatchEvent(order, cook, res);
	}
}
