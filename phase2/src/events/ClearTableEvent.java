package events;

import core.Restaurant;
import core.Table;
import visual.gui.Server;

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
		this.table.clearPayment();
		//TODO: anything else to clear?
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
}
