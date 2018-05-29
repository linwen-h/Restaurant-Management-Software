package events;

import employee.Manager;
import user.Restaurant;

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

	/**
	 * Parses a String into a Statistic Event.
	 *
	 * @param data A String representing data to be parsed.
	 * @param res  A Restaurant.
	 * @return a Statistics event built from the a String representation, or exit if malformed.
	 */
	public static StatisticsEvent parse(String data, Restaurant res) {
		int id = 0;

		try {
			id = Integer.parseInt(data);
		} catch (Exception e) {
			System.err.println("STATISTICS event must be in format:\n\tSTATISTICS|<MANAGER ID>");
			System.exit(1);
			return null;
		}

		Manager manager = res.getManager(id);
		return new StatisticsEvent(manager, res);
	}
}
