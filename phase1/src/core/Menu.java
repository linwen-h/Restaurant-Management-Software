package core;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import user.Restaurant;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A Menu of dishes. Can return a Menu with items of specific categories.
 *
 * Currently not used too extensively because of menu.json, but kept for possible expansion in phase 2.
 */
public class Menu extends ArrayList<MenuItem> {
	private Restaurant restaurant;

	/**
	 * Creates a Menu.
	 *
	 * @param items the items on a Menu.
	 * @param res   a Restaurant with this Menu.
	 */
	public Menu(ArrayList<MenuItem> items, Restaurant res) {
		super(items);
		this.restaurant = res;
	}

	/**
	 * Creates an empty Menu.
	 *
	 * @param res a Restaurant with this Menu.
	 */
	public Menu(Restaurant res) {
		super();
		this.restaurant = res;
	}

	/**
	 * Creates a menu from a file.
	 *
	 * @param file the Json the Menu is built from.
	 * @param res  a Restaurant with this Menu.
	 */
	public Menu(File file, Restaurant res) {
		super();
		this.restaurant = res;
		parseMenu(file);
	}

	/**
	 * Parses a menu from a json file to add ingredients to a the menu.
	 *
	 * @param file a json file containing menu items. Refer to resources/menu.json for format.
	 */
	private void parseMenu(File file) {
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(file));
			JSONArray data = (JSONArray) obj;

			for (Object item : data) {
				JSONObject dish = (JSONObject) item;
				String name = (String) dish.get("name");
				double price = (double) dish.get("price");

				Object[] tags = ((JSONArray) dish.get("tags")).toArray();
				ArrayList<String> tagsList = new ArrayList<>(Arrays.stream(tags).map(object ->
						Objects.toString(object, null)).collect(Collectors.toList()));

				Object[] ingredients = ((JSONArray) dish.get("ingredients")).toArray();

				ArrayList<Ingredient> ingredientsList = new ArrayList<>(Arrays.stream(ingredients)
						.map(ingredient -> restaurant.getIngredientManager().getIngredient(
								ingredient.toString())).collect(Collectors.toList()));

				add(new MenuItem(name, price, ingredientsList, tagsList));
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets an item from a menu.
	 *
	 * @param item the name of the menu item
	 * @return a menu item if it exists, or a string otherwise.
	 */
	public MenuItem get(String item) {
		Optional<MenuItem> menuItem = stream().filter(a -> a.getName().equals(item)).findFirst();
		if (!menuItem.isPresent()) {
			System.err.println("No such item " + item);
			System.err.println("Please select from the following items:");
			forEach(a -> System.err.println(a.getName()));
			return null;
		}
		return menuItem.get();
	}

	// Under construction; for future use
//    /**
//     * Returns a menu with specific tags.
//     *
//     * @param included the list of tags to be included in the menu
//     * @param excluded the list of tags to be excluded from the menu
//     * @return a menu with menu items with included tags and without items with excluded tags
//     */
//    public Menu getMenuWithTags(ArrayList<String> included, ArrayList<String> excluded) {
//        ArrayList<MenuItem> menu = new ArrayList<>();
//        for (MenuItem item : this) {
//            ArrayList<String> tags = item.getTags();
//            boolean skip = false;
//            for (String tag : tags) {
//                if (!included.contains(tag) || excluded.contains(tag)) {
//                    skip = true;
//                }
//            }
//            if (skip) continue;
//
//
//            menu.add(item);
//        }
//
//        return new Menu(menu);
//    }

	/**
	 * Sets the Restaurant of the menu.
	 *
	 * @param restaurant a Restaurant.
	 */
	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}
}