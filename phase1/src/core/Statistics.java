package core;

import java.util.ArrayList;

/**
 * Keeps track of most ordered dishes and most used ingredients in a Restaurant.
 */
public class Statistics {
	private ArrayList<Ingredient> ingredients;
	private ArrayList<MenuItem> menuItems;

	/**
	 * Creates a Statistics object
	 *
	 * @param menu        a Menu of all the MenuItems
	 * @param ingredients a list of all the Ingredients
	 */
	public Statistics(Menu menu, ArrayList<Ingredient> ingredients) {
		this.menuItems = menu;
		this.ingredients = ingredients;
	}

	/**
	 * Prints each ingredient and the current usage of each ingredient.
	 */
	public void printIngredients() {
		ingredients.sort(Ingredient::compareTo);
		for (Ingredient ingredient : ingredients) {
			System.out.printf("\t%s %d%n", ingredient.getDisplayName(), ingredient.getUsage());
		}
	}

	/**
	 * Prints each MenuItem and the current usage of each MenuItem.
	 */
	public void printMenuItems() {
		menuItems.sort(MenuItem::compareTo);
		for (MenuItem menuItem : menuItems) {
			System.out.printf("\t%s %d%n", menuItem.getName(), menuItem.getUsage());
		}
	}

	/**
	 * Updates the statistics
	 *
	 * @param order order to account for in the statistics
	 */
	public void update(Order order) {
		order.getItem().use();

		for (Ingredient ingredient : order.getAllIngredients()) {
			ingredient.use(1);
		}
	}
}
