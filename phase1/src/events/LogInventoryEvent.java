package events;

import employee.Manager;
import user.Restaurant;

/**
 * A class representing a LogInventoryEvent.
 */
public class LogInventoryEvent extends Event {
	private final Manager manager;

	/**
	 * Creates a LogInventoryEvent.
	 *
	 * @param manager a Manager.
	 * @param res     a Restaurant.
	 */
	private LogInventoryEvent(Manager manager, Restaurant res) {
		super(TYPE.LOG_INVENTORY, res);
		this.manager = manager;
	}

	/**
	 * Executes a LogInventoryEvent.
	 */
	@Override
	public void execute() {
		String inv = this.restaurant.getKitchen().getInventory();
		manager.log(inv);
	}

	/**
	 * Parses a String to create a LogInventoryEvent.
	 *
	 * @param data A String to be parsed.
	 * @param res  A Restaurant.
	 * @return a LogInventoryEvent, or exits if String is malformed.
	 */
	public static LogInventoryEvent parse(String data, Restaurant res) {
		int id = 0;

		try {
			id = Integer.parseInt(data);
		} catch (Exception e) {
			System.err.println("LOGINV event must to be in format:\n\tLOGINV|<MANAGER ID>");
			System.exit(1);
			return null;
		}

		Manager manager = res.getManager(id);
		return new LogInventoryEvent(manager, res);
	}
}
