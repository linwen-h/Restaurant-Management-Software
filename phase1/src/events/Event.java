package events;

import user.Restaurant;

import java.util.Arrays;
import java.util.Optional;

/**
 * An event class.
 */
public abstract class Event {
	// All possible types of events
	public enum TYPE {
		ORDER("ORDER"),
		DISPATCH("DISPATCH"),
		RECEIVE("RECEIVE"),
		COOK("COOK"),
		DELIVER("DELIVER"),
		CANCEL("CANCEL"),
		RETURN("RETURN"),
		DELAY("DELAY"),
		SHIPMENT("SHIPMENT"),
		LOG_INVENTORY("LOGINV"),
		STATISTICS("STATISTICS"),
		GET_BILL("GETBILL"),
		CLEARTABLE("CLEARTABLE");

		String type;

		/**
		 * Constructs a TYPE.
		 *
		 * @param type a String representing a type of event.
		 */
		TYPE(String type) {
			this.type = type;
		}

		/**
		 * Returns the type of TYPE.
		 *
		 * @param type A String representing a type.
		 * @return a TYPE representing the String type.
		 */
		static TYPE getType(String type) {
			TYPE[] types = TYPE.class.getEnumConstants();
			Optional<TYPE> t = Arrays.stream(types).filter(a -> a.type.equals(type)).findFirst();
			if (t.isPresent()) {
				return t.get();
			} else {
				System.err.println("Invalid type of operation " + type + System.lineSeparator() + "Valid Types are:");
				Arrays.asList(types).forEach(a -> System.err.println("\t" + a.type));
				System.exit(1);
			}
			return null;
		}
	}

	private TYPE type;
	final Restaurant restaurant;

	/**
	 * The Constructor for an Event.
	 *
	 * @param type       the event TYPE.
	 * @param restaurant a Restaurant.
	 */
	Event(TYPE type, Restaurant restaurant) {
		super();
		this.type = type;
		this.restaurant = restaurant;
	}

	/**
	 * Returns the Event Type.
	 *
	 * @return the Event Type.
	 */
	public TYPE getType() {
		return type;
	}

	/**
	 * Takes a string and determines what type of event is occurring and parses based
	 * on that event type.
	 *
	 * @param e   A string to be parsed.
	 * @param res A restaurant.
	 * @return null if malformed, an Event otherwise.
	 */
	public static Event parse(String e, Restaurant res) {
		String[] str = e.split("\\|");
		if (str.length != 2) return null;
		String typeString = str[0];

		TYPE type = TYPE.getType(typeString);
		str[1] = str[1].trim();

		switch (type) {
			case ORDER:
				return OrderEvent.parse(str[1], res);
			case DISPATCH:
				return DispatchEvent.parse(str[1], res);
			case RECEIVE:
				return ReceiveEvent.parse(str[1], res);
			case COOK:
				return CookEvent.parse(str[1], res);
			case DELIVER:
				return DeliverEvent.parse(str[1], res);
			case DELAY:
				return DelayEvent.parse(str[1], res);
			case SHIPMENT:
				return ShipmentEvent.parse(str[1], res);
			case CANCEL:
				return CancelEvent.parse(str[1], res);
			case RETURN:
				return ReturnEvent.parse(str[1], res);
			case LOG_INVENTORY:
				return LogInventoryEvent.parse(str[1], res);
			case STATISTICS:
				return StatisticsEvent.parse(str[1], res);
			case GET_BILL:
				return GetBillEvent.parse(str[1], res);
			case CLEARTABLE:
				return ClearTableEvent.parse(str[1], res);
			default:
				return null;
		}
	}

	/**
	 * Executes this event.
	 */
	public abstract void execute();
}
