package core;

import employee.Cook;
import employee.Server;
import manager.IngredientManager;
import user.Restaurant;
import util.InvalidIngredientException;
import util.Log;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Represents a kitchen in a restaurant.
 */
public class Kitchen {
	private final Restaurant restaurant;
	private final ArrayList<Cook> cooks;
	private final ArrayList<Server> servers;
	private final ArrayList<Order> allOrders; // All of the kitchen's orders
	private final Statistics statistics; // Tracks orders stats like number of times each item has been ordered

	/**
	 * Constructs a kitchen.
	 *
	 * @param restaurant The restaurant that the kitchen belongs to.
	 */
	public Kitchen(Restaurant restaurant) {
		this.restaurant = restaurant;
		this.cooks = new ArrayList<>();
		statistics = new Statistics(restaurant.getMenu(), restaurant.getIngredientManager().getIngredients());
		this.servers = new ArrayList<>();
		this.allOrders = new ArrayList<>();
	}

	/**
	 * Checks if there are enough ingredients to make an order.
	 *
	 * @param order the order to check.
	 * @return true if there are enough ingredients to make the order; false otherwise.
	 * @throws InvalidIngredientException an ingredient in the order is invalid.
	 */
	public boolean checkIngredients(Order order) throws InvalidIngredientException {
		for (Ingredient ingredient : order.getAllIngredients()) {
			Optional<Ingredient> ing = getInventoryList().stream().filter(a -> a.equals(ingredient)).findFirst();

			if (!ing.isPresent()) {
				throw new InvalidIngredientException(ingredient.getName() + " is not a valid ingredient.");
			} else {
				if (ingredient.amount < 1) return false;
			}
		}

		statistics.update(order);
		return true;
	}

	/**
	 * Updates the inventory of the kitchen.
	 *
	 * @param ingredient the ingredient to be added to the kitchen.
	 * @param amount     the amount of the specified ingredient to add.
	 */
	public void updateInventory(String ingredient, int amount) {
		Optional<Ingredient> ing = getInventoryList().stream().filter(a -> a.getName().equals(ingredient)).findFirst();
		if (!ing.isPresent()) {
			System.err.println(String.format("Wrong shipment received, ingredient %s does not exist", ingredient));
		} else {
			ing.get().addAmount(amount);
		}
	}

	/**
	 * Updates the ingredient inventory.
	 *
	 * @param order deduct based on this order.
	 */
	public void updateInventory(Order order) {
		order.getAllIngredients().forEach(a -> {
			// Update internal
			a.update(1);
		});
	}

	/**
	 * Forces the kitchen to check the thresholds.
	 */
	public void checkThresholds() {
		this.getInventoryList().forEach(a -> {
			// Update external file
			if (a.getAmount() < a.getThreshold()) {
				if (!restaurant.getIngredientManager().isInReorder(a))
					Log.log(restaurant.getKitchen(), String.format("Now requesting %s units of %s",
							IngredientManager.DEFAULT_ORDER_AMOUNT, a.getDisplayName()));
				restaurant.getIngredientManager().reorderIngredient(a);
			} else {
				restaurant.getIngredientManager().unReorderIngredient(a);
			}

			restaurant.getIngredientManager().updateRequestsFile();
		});
	}

	/**
	 * Returns an Order with the given order number.
	 *
	 * @param orderNumber an order number.
	 * @return a Order with a given order number, or null of the order does not exist.
	 */
	public Order getOrder(int orderNumber) {
		Optional<Order> order = this.allOrders.stream().filter(a -> a.getOrderNumber() == orderNumber).findFirst();
		return order.orElse(null);
	}

	/**
	 * Adds an order to the kitchen queue.
	 *
	 * @param order the order to be added.
	 */
	public void addOrderReference(Order order) {
		allOrders.add(order);
	}

	/**
	 * Adds Cooks to the restaurant.
	 *
	 * @param num the number of Cooks to add.
	 */
	public void addCooks(int num) {
		for (int i = 0; i < num; i++) {
			cooks.add(new Cook(restaurant));
		}
	}

	/**
	 * Adds Servers to the restaurant.
	 *
	 * @param num the number of Servers to add.
	 */
	public void addServer(int num) {
		for (int i = 0; i < num; i++) {
			servers.add(new Server(restaurant));
		}
	}

	/**
	 * Returns a cook with a given id.
	 *
	 * @param id the id of the Cook.
	 * @return a Cook with the given id, or null if the Cook does not exist.
	 */
	public Cook getCook(int id) {
		Optional<Cook> cook = cooks.stream().filter(a -> a.getId() == id).findFirst();
		return cook.orElse(null);
	}

	/**
	 * Returns a Server with a given id.
	 *
	 * @param id the id of the Server.
	 * @return a Server with the given id, or null if the Server does not exist.
	 */
	public Server getServer(int id) {
		Optional<Server> server = servers.stream().filter(a -> a.getId() == id).findFirst();
		return server.orElse(null);
	}

	/**
	 * Gets the Statistics of the Kitchen.
	 *
	 * @return the Statistics of the Kitchen.
	 */
	public Statistics getStatistics() {
		return this.statistics;
	}

	/**
	 * Gets the kitchen's associated restaurant.
	 *
	 * @return the restaurant that the kitchen exists in.
	 */
	public Restaurant getRestaurant() {
		return restaurant;
	}

	/**
	 * Gets a string representation of the kitchen's inventory.
	 *
	 * @return a string, representing the kitchen's inventory.
	 */
	public String getInventory() {
		StringBuilder sb = new StringBuilder("This kitchen's inventory:" + System.lineSeparator());
		for (Ingredient ingredient : getInventoryList()) {
			sb.append(String.format("\tIngredient: %s, Amount: %d", ingredient.getDisplayName(),
					ingredient.getAmount()));
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

	/**
	 * Gets a mapping of ingredients and their amounts for the kitchen's inventory.
	 *
	 * @return a map of the kitchen's inventory.
	 */
	public ArrayList<Ingredient> getInventoryList() {
		return restaurant.getIngredientManager().getIngredients();
	}

	/**
	 * Returns a String representation of the Kitchen.
	 *
	 * @return a String representation of the Kitchen containing which restaurant the kitchen belongs to.
	 */
	@Override
	public String toString() {
		return String.format("Kitchen (Restaurant %s)", restaurant.getName());
	}
}
