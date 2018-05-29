package core;

import java.util.ArrayList;

/**
 * Represents a dish on a menu.
 */
public class MenuItem implements Comparable<MenuItem> {
	private String name; // Serves as both reference (system) and display name
	private ArrayList<Ingredient> baseIngredients;
	private ArrayList<String> tags; // For future usage
	private double basePrice;
	private int usage; // The number of times the item was ordered (for stats)

	/**
	 * Constructs a MenuItem without tags.
	 *
	 * @param name            name of the dish.
	 * @param basePrice       price of the dish without any additions.
	 * @param baseIngredients ingredients required to prepare the dish.
	 */
	public MenuItem(String name, double basePrice, ArrayList<Ingredient> baseIngredients) {
		this(name, basePrice, baseIngredients, new ArrayList<>());
	}

	/**
	 * Creates a MenuItem with tags.
	 *
	 * @param name            name of the dish.
	 * @param basePrice       price of the dish without any additions.
	 * @param baseIngredients ingredients required to prepare the dish.
	 * @param tags            tags describing the dish.
	 */
	MenuItem(String name, double basePrice, ArrayList<Ingredient> baseIngredients, ArrayList<String> tags) {
		this.basePrice = basePrice;
		this.name = name;
		this.baseIngredients = baseIngredients;
		this.tags = tags;
		this.usage = 0;
	}

	/**
	 * Returns the price of the item without any additions.
	 *
	 * @return base price of an item.
	 */
	public double getBasePrice() {
		return basePrice;
	}

	/**
	 * Returns the name of the dish.
	 *
	 * @return name of the dish.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the list of ingredients to prepare an item.
	 *
	 * @return list of Ingredients to prepare an Item.
	 */
	public ArrayList<Ingredient> getAllIngredients() {
		return baseIngredients;

	}

	/**
	 * Returns tags describing the dish. For future usage.
	 *
	 * @return tags describing the dish.
	 */
	public ArrayList<String> getTags() {
		return tags;
	}

	/**
	 * Increases the usage of the MenuItem by 1.
	 */
	public void use() {
		usage++;
	}

	/**
	 * Returns the usage of the MenuItem.
	 *
	 * @return a int representing the usage of the MenuItem.
	 */
	int getUsage() {
		return usage;
	}

	/**
	 * Compares the Usage of this instance of MenuItem with another MenuItem.
	 *
	 * @param o a MenuItem.
	 * @return greater than 0 if this instance's usage is greater than another MenuItem's usage.
	 */
	@Override
	public int compareTo(MenuItem o) {
		return o.usage - this.usage;
	}
}
