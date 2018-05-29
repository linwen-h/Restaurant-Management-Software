package events;

import core.Restaurant;

import java.util.Arrays;
import java.util.Optional;

/**
 * An event class.
 */
public abstract class Event {
	// All possible types of events
	public enum TYPE { //TODO: do we still need this?
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

	/**
	 * The type of event.
	 */
	private TYPE type;
	/**
	 * A restaurant.
	 */
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
	 * Executes this event.
	 */
	public abstract void execute();
}
