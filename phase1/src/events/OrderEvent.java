package events;

import core.Ingredient;
import core.MenuItem;
import core.Order;
import core.Table;
import employee.Server;
import user.Restaurant;
import util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A class representing a OrderEvent.
 */
public class OrderEvent extends Event {
	private final Order order;
	private final Server server; // The server who took the order

	/**
	 * Creates an OrderEvent
	 *
	 * @param order      the order this event represents
	 * @param server     the server who took the order
	 * @param restaurant the restaurant
	 */
	OrderEvent(Order order, Server server, Restaurant restaurant) {
		super(TYPE.ORDER, restaurant);
		this.server = server;
		this.order = order;
	}

	/**
	 * Returns this instance of OrderEvent's Order.
	 *
	 * @return a Order
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * Parses and returns an order from a formatted string.
	 *
	 * @param order      a formatted string containing order information.
	 * @param restaurant the restaurant that the order belongs to.
	 * @return a new OrderEvent, exits if order malformed, or null if item does not exist on menu.
	 */
	//TODO: Cleanup
	public static OrderEvent parse(String order, Restaurant restaurant) {
		String[] data = order.split("/");
		if (data.length != 6) {
			System.err.println("ORDER event must be in the format:");
			System.err.println("\t\nORDER|<ORDER ID>/<SERVER ID>/<TABLE-SEAT>/<ITEM>/[ADDITIONS]/[SUBTRACTIONS]");
			System.exit(1);
		}

		try {
			int orderNumber = Integer.valueOf(data[0].trim());
			Server server = restaurant.getKitchen().getServer(Integer.valueOf(data[1].trim()));
			String td = data[2].trim();

			String[] dat = td.split("-");
			Table t = restaurant.getTable(Integer.valueOf(dat[0].trim()));
			int seatNum = Integer.valueOf(dat[1].trim());

			MenuItem item = restaurant.getMenu().get(data[3].trim());
			if (item == null)
				return null;

			String[] addstr = data[4].replaceAll("\\[|]", "").split(",");
			ArrayList<Ingredient> additions = new ArrayList<>(Arrays.stream(addstr).filter(a -> !a.trim().isEmpty())
					.map(str -> restaurant.getIngredientManager().getIngredient(str.trim()))
					.collect(Collectors.toList()));

			String[] substr = data[5].replaceAll("\\[|]", "").split(",");
			ArrayList<Ingredient> subtractions = new ArrayList<>(Arrays.stream(substr).filter(a -> !a.trim().isEmpty())
					.map(str -> restaurant.getIngredientManager().getIngredient(str.trim()))
					.collect(Collectors.toList()));

			Order orderNew = new Order(item, additions, subtractions, t, seatNum, orderNumber, server);
			restaurant.getKitchen().addOrderReference(orderNew);

			return new OrderEvent(orderNew, server, restaurant);
		} catch (Exception e) {
			System.err.println("Each order must be in the following format:");
			System.err.println("\tORDER|<ORDER ID>/<SERVER ID>/<Table Number-Seat ID>/<ITEM>/[ADDITION1,ADDITION2]" +
					"/[SUBTRACTION1,SUBTRACTION2]");
			System.exit(1);
			return null;
		}
	}

	/**
	 * Executes an OrderEvent.
	 */
	@Override
	public void execute() {
		order.setProgress(Order.ORDER_ORDERED);
		order.getTable().updateBill(order);
		Log.logID(server, server.getId(), String.format("Order %s ordered for Table %d", order,
				order.getTable().getTableNumber()));
	}
}
