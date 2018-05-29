package events;

import core.Order;
import core.Table;
import user.Restaurant;

/**
 * Represents an event where a table requests its bill.
 */
public class GetBillEvent extends Event {
	private Table table; // The bill for the table
	private int seat; // The bill for the seat (optional)

	/**
	 * Returns a GetBillEvent.
	 *
	 * @param restaurant A Restaurant that this GetBill Event corresponds to.
	 * @param table      A Table that this GetBill Event corresponds to.
	 */
	private GetBillEvent(Restaurant restaurant, Table table) {
		super(TYPE.GET_BILL, restaurant);
		this.table = table;
	}

	/**
	 * Returns a GetBillEvent.
	 *
	 * @param restaurant A Restaurant that this GetBill Event corresponds to.
	 * @param table      a Table that this GetBill Event corresponds to.
	 * @param seat       a seat that this GetBill Event corresponds to.
	 */
	private GetBillEvent(Restaurant restaurant, Table table, int seat) {
		this(restaurant, table);
		this.seat = seat;
	}

	/**
	 * Executes a GetBillEvent.
	 */
	@Override
	public void execute() {
		double finalPrice = 0d;

		StringBuilder sb = new StringBuilder(System.lineSeparator() + "Table Number: " + table.getTableNumber());
		sb.append(System.lineSeparator());

		if (seat == 0) { // Gets the whole table's bill
			for (Order order : table.getOrders()) {
				sb.append(formatBill(order));
				finalPrice += order.getPrice();
			}

		} else if (seat >= 1) { // Gets an individual seat's bill
			sb.append("Seat Number: ").append(seat);
			sb.append(System.lineSeparator());

			for (Order order : table.getOrders()) {
				if (order.getSeatNumber() == seat) {
					sb.append(formatBill(order));
					finalPrice += order.getPrice();
				}
			}

		}

		sb.append(System.lineSeparator());
		sb.append("Total Price: ").append(String.format("$%.2f", finalPrice)).append(System.lineSeparator());
		System.out.println(sb.toString());
	}

	/**
	 * Formats a bill for a specific order (used in both table and seat bills).
	 *
	 * @param order the order to format a bill for.
	 * @return a string, representing a portion of a bill for the specific order.
	 */
	private String formatBill(Order order) {
		if (order == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder(System.lineSeparator());
		sb.append("Order ===========");
		sb.append(System.lineSeparator());
		sb.append("\t");
		sb.append(order);
		sb.append(System.lineSeparator());
		sb.append("\t\tPrice: ");
		sb.append(String.format("$%.2f", order.getPrice()));

		return sb.toString();
	}

	/**
	 * Parses a String into a GetBillEvent.
	 *
	 * @param data       A String to be parsed.
	 * @param restaurant A Restaurant.
	 * @return a GetBillEvent or null if no information in String.
	 */
	public static GetBillEvent parse(String data, Restaurant restaurant) {
		data = data.trim();
		if (data.length() == 0) {
			System.err.println("GETBILL event must be in format:\n\tGETBILL|<TABLE> or GETBILL|<TABLE-SEAT>");
			System.exit(1);
			return null;
		}

		boolean getSeatBill = data.contains("-");
		int tableNumber;

		if (!getSeatBill) {
			tableNumber = Integer.valueOf(data);
			return new GetBillEvent(restaurant, restaurant.getTable(tableNumber));
		} else {
			String[] splitData = data.split("-");
			tableNumber = Integer.valueOf(splitData[0]);
			int seatNumber = Integer.valueOf(splitData[1]);

			return new GetBillEvent(restaurant, restaurant.getTable(tableNumber), seatNumber);
		}
	}
}
