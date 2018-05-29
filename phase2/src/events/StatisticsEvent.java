package events;

import core.Restaurant;
import visual.gui.Manager;

/**
 * Represents a Statistics Event.
 */
public class StatisticsEvent extends Event {
	private final Manager manager;

	/**
	 * The constructor for an Statistic Event.
	 *
	 * @param manager A Manager.
	 * @param res     A Restaurant.
	 */
	private StatisticsEvent(Manager manager, Restaurant res) {
		super(TYPE.STATISTICS, res);
		this.manager = manager;
	}

	/**
	 * Executes a Statistics Event.
	 */
	@Override
	public void execute() {
		System.out.println("==STATISTICS FOR THE RESTAURANT==");
		System.out.println("Most used Ingredients:");
		manager.printIngredients();
		System.out.println("Most ordered items:");
		manager.printMenuItems();
		System.out.println("==END STATISTICS==");
	}
}
