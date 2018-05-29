package events;

import core.Restaurant;
import visual.gui.Manager;

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
		String inv = this.restaurant.getInventory();
		manager.log(inv);
	}
}
