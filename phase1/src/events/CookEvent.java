package events;

import core.Order;
import employee.Cook;
import user.Restaurant;
import util.InvalidIngredientException;

/**
 * A class representing a CookEvent.
 */
public class CookEvent extends Event {
	private final Order order;
	private final Cook cook; // Cook assigned to the order

	/**
	 * Creates a Cook event.
	 *
	 * @param order      the order to be cooked.
	 * @param cook       the Cook that cooks the Order.
	 * @param restaurant A restaurant.
	 */
	private CookEvent(Order order, Cook cook, Restaurant restaurant) {
		super(TYPE.COOK, restaurant);
		this.order = order;
		this.cook = cook;
	}

	/**
	 * Returns this instance of CookEvent's Cook.
	 *
	 * @return a Cook.
	 */
	private Cook getCook() {
		return cook;
	}

	/**
	 * Returns this instance of CookEvent's Order.
	 *
	 * @return a Order.
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * Executes a CookEvent.
	 */
	@Override
	public void execute() {
		if (!order.isReceived()) {
			System.err.println("Order needs to be received before cooking");
			System.exit(1);
		}

		if (this.order.getProgress() == Order.ORDER_CANCELLED) {
			System.err.println(String.format("Order %s was cancelled, cannot complete event", order));
			this.restaurant.getKitchen().checkThresholds();
			return;
		}

		if (this.order.getCook().getId() != this.getCook().getId()) {
			System.err.printf("Cook %d could not cook order %d, Reason: order was received by cook %d\n",
					this.cook.getId(),
					this.order.getOrderNumber(),
					this.order.getCook().getId());
			System.exit(1);
		}

		try {
			boolean valid = this.restaurant.getKitchen().checkIngredients(order);
			if (!valid) {
				System.err.println("Not enough ingredients to cook order " + order.toString());
				new CancelEvent(restaurant, order, CancelEvent.REASON.OUT_OF_INGREDIENTS, null).execute();
			}
		} catch (InvalidIngredientException e) {
			e.printStackTrace();
		}

		this.order.setProgress(Order.ORDER_COOKED);
		this.restaurant.getKitchen().updateInventory(order);
		this.restaurant.getKitchen().checkThresholds();

		this.cook.setAvailable(true);
		cook.log(String.format("Order %s cooked and ready to bus", order));
		order.getServer().log(String.format("Notified to deliver order %s", order));
	}

	/**
	 * Parses a String to create a CookEvent.
	 *
	 * @param data a String to be parsed.
	 * @param res  a Restaurant.
	 * @return a CookEvent or null if String is malformed.
	 */
	public static CookEvent parse(String data, Restaurant res) {
		String[] dataarr = data.split("/");
		if (dataarr.length != 2) {
			System.err.println("COOK event must be in format:\n\tCOOK|<ORDER ID>/<COOK ID>");
			return null;
		}
		Order order = res.getKitchen().getOrder(Integer.valueOf(dataarr[0].trim()));
		Cook cook = res.getKitchen().getCook(Integer.valueOf(dataarr[1].trim()));

		return new CookEvent(order, cook, res);
	}
}
