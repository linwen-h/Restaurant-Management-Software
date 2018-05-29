package events;

import core.Order;
import employee.Server;
import user.Restaurant;
import util.Log;

/**
 * A class representing a DeliverEvent.
 */
public class DeliverEvent extends Event {
	private final Order order;
	private final Server server; // Server assigned to deliver the order

	/**
	 * Creates a DeliverEvent.
	 *
	 * @param order      The Order being delivered.
	 * @param server     The Server delivering the order.
	 * @param restaurant a Restaurant.
	 */
	private DeliverEvent(Order order, Server server, Restaurant restaurant) {
		super(TYPE.COOK, restaurant);
		this.order = order;
		this.server = server;
	}

	/**
	 * Executes a DeliverEvent.
	 */
	@Override
	public void execute() {
		if (!order.isCooked()) {
			System.err.println("Order needs to be cooked before delivered");
			System.exit(1);
		}
		if (this.order.getProgress() == Order.ORDER_CANCELLED) {
			System.err.println(String.format("Order %s was cancelled, cannot complete event", order));
			return;
		}

		order.setProgress(Order.ORDER_DELIVERED);
		Log.logID(server, server.getId(), String.format("Order %s delivered to Table (%d)", order,
				order.getTable().getTableNumber()));
	}

	/**
	 * Parses a String into a DeliverEvent.
	 *
	 * @param data a String to be parsed.
	 * @param res  a Restaurant.
	 * @return a DeliverEvent, exits if String is malformed.
	 */
	public static DeliverEvent parse(String data, Restaurant res) {
		String[] dataarr = data.split("/");
		if (dataarr.length != 2) {
			System.err.println("DELIVER event must be in format:\n\tDELIVER|<ORDER ID>/<SERVER ID>");
			System.exit(1);
			return null;
		}

		Order order = res.getKitchen().getOrder(Integer.valueOf(dataarr[0]));
		Server server = res.getKitchen().getServer(Integer.valueOf(dataarr[1]));

		return new DeliverEvent(order, server, res);
	}
}
