package events;

import core.Ingredient;
import util.Log;
import user.Restaurant;

import java.util.HashMap;

/**
 * Represents a Shipment Event.
 */
public class ShipmentEvent extends Event {
	private HashMap<Ingredient, Integer> ingredients;

	/**
	 * Creates a Shipment Event.
	 *
	 * @param ingredients the Ingredients in the shipment.
	 * @param res         A Restaurant.
	 */
	private ShipmentEvent(HashMap<Ingredient, Integer> ingredients, Restaurant res) {
		super(TYPE.SHIPMENT, res);
		this.ingredients = ingredients;
	}

	/**
	 * Parses a String to create and return a Shipment event.
	 *
	 * @param data a String representation of Shipment event.
	 * @param res  a Restaurant.
	 * @return a Shipment event, or exit if data malformed.
	 */
	public static ShipmentEvent parse(String data, Restaurant res) {
		try {
			String inter = data.replaceAll("\\{", "").replaceAll("}", "");
			String[] items = inter.split(",");

			HashMap<Ingredient, Integer> map = new HashMap<>();

			for (String item : items) {
				String[] tokens = item.split(":");
				Ingredient ing = res.getIngredientManager().getIngredient(tokens[0]);
				int amount = Integer.valueOf(tokens[1]);

				map.put(ing, amount);
			}

			return new ShipmentEvent(map, res);
		} catch (Exception e) {
			System.err.println("SHIPMENT event must be in format:");
			System.err.println("\tSHIPMENT|{item1:amount1,item2:amount2}");
			System.exit(1);
			return null;
		}
	}

	/**
	 * Executes a Shipment Event.
	 */
	@Override
	public void execute() {
		for (Ingredient ingredient : ingredients.keySet()) {
			ingredient.addAmount(ingredients.get(ingredient));
		}

		restaurant.getKitchen().checkThresholds();
		Log.log(restaurant.getKitchen(), "Shipment received, updated Inventory");
	}
}
