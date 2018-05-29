package employee;

import core.Ingredient;
import core.Statistics;
import user.Restaurant;

import java.util.ArrayList;

/**
 * Represents a manager of a restaurant.
 */
public class Manager extends Employee {
	private final Statistics statistics; // The manager's restaurant statistics
	private static int counter = 0; // Ensures each manager has a unique ID

	/**
	 * Creates a Manager, working at a particular restaurant.
	 *
	 * @param restaurant the Restaurant that the Manager works at.
	 * @param statistics the Statistics of a Restaurant.
	 */
	public Manager(Restaurant restaurant, Statistics statistics) {
		super(++counter, restaurant);
		this.statistics = statistics;
	}

	/**
	 * Print the Ingredients in the Restaurant and their current usages.
	 */
	public void printIngredients() {
		statistics.printIngredients();
	}

	/**
	 * Print the MenuItems in the Restaurant and their current usages.
	 */
	public void printMenuItems() {
		statistics.printMenuItems();
	}

	/**
	 * Prints an inventory of all the ingredients in the restaurant to the console window.
	 * <p>
	 * Replaced by the LOGINV event; kept for possible usage in phase 2.
	 */
	@Deprecated
	public void printInventory() {
		System.out.println(restaurant.getKitchen().getInventory());
	}

	/**
	 * Updates the amount ordered for each Ingredient in requests.txt.
	 *
	 * @param ingredient  the ingredient to reorder.
	 * @param orderAmount the new amount of the Ingredient to reorder.
	 */
	public void updateReorderEmail(Ingredient ingredient, int orderAmount) {
		restaurant.getIngredientManager().updateReorderIngredient(ingredient, orderAmount);
	}

	/**
	 * Returns a list of Ingredients and their amounts for the restaurant.
	 * <p>
	 * Replaced by the LOGINV event; kept for possible usage in phase 2.
	 *
	 * @return a list of the Restaurant's Ingredients and amounts.
	 */
	@Deprecated
	public ArrayList<Ingredient> checkInventory() {
		return restaurant.getKitchen().getInventoryList();
	}

	/**
	 * Returns a String representation of a Manager.
	 *
	 * @return a String Representation of a Manager.
	 */
	@Override
	public String toString() {
		return "Manager";
	}
}
