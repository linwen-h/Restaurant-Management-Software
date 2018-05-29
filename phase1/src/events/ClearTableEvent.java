package events;

import core.Table;
import employee.Server;
import user.Restaurant;

/**
 * A class that represents a ClearTableEvent.
 */
public class ClearTableEvent extends Event {
	private final Table table; // Table being cleared
	private final Server server; // Server clearing the table

	/**
	 * Creates a ClearTableEvent.
	 *
	 * @param table  The Table being cleared.
	 * @param server The Server Clearing the table.
	 * @param res    a Restaurant.
	 */
	private ClearTableEvent(Table table, Server server, Restaurant res) {
		super(TYPE.CLEARTABLE, res);
		this.server = server;
		this.table = table;
	}

	/**
	 * Executes a ClearTableEvent.
	 */
	@Override
	public void execute() {
		server.log(String.format("Cleared %s", table));
		this.table.clearOrders();
	}

	/**
	 * Returns this instance of ClearTableEvent's table.
	 *
	 * @return a Table.
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Returns this instance of ClearTableEvent's Server.
	 *
	 * @return a Server.
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * Parses a String to create a ClearTableEvent.
	 *
	 * @param data a String to be parsed.
	 * @param res  a restaurant.
	 * @return a ClearTableEvent or exits if String is malformed.
	 */
	public static ClearTableEvent parse(String data, Restaurant res) {
		String[] arr = data.trim().split("/");

		try {
			Table t = res.getTable(Integer.parseInt(arr[0].trim()));
			Server s = res.getKitchen().getServer(Integer.parseInt(arr[1].trim()));
			return new ClearTableEvent(t, s, res);
		} catch (Exception e) {
			System.err.println("CLEARTABLE needs to be in format:\n\tCLEARTABLE|<TABLE NUMBER>/<SERVER ID>");
			System.exit(1);
			return null;
		}
	}
}
