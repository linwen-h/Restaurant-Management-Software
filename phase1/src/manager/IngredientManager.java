package manager;

import core.Ingredient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A manager for ingredients.
 */
public class IngredientManager {
	private final ArrayList<Ingredient> ingredients;
	public static final int DEFAULT_ORDER_AMOUNT = 20;
	private final Map<Ingredient, Integer> reorders; // Current re-orders; used to write to requests.txt
	private final File requests; // The requests file

	/**
	 * Constructs an IngredientManager.
	 *
	 * @param ingredientsFile a json file to read ingredients from.
	 * @param requests        a file to write re-order requests to.
	 */
	public IngredientManager(File ingredientsFile, File requests) {
		ingredients = new ArrayList<>();
		reorders = new HashMap<>();
		this.requests = requests;
		parseIngredients(ingredientsFile);
	}

	/**
	 * Gets the list of ingredients.
	 *
	 * @return a list of ingredients.
	 */
	public ArrayList<Ingredient> getIngredients() {
		return ingredients;
	}

	/**
	 * Parses a json file to create ingredients.
	 *
	 * @param file a json file of ingredients.
	 */
	private void parseIngredients(File file) {
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(file));
			JSONArray data = (JSONArray) obj;

			for (Object item : data) {
				JSONObject ingredientObject = (JSONObject) item;
				String name = (String) ingredientObject.get("name");

				String displayName = (String) ingredientObject.get("displayName");
				double additionPrice = (double) ingredientObject.get("additionPrice");
				int initialAmount = ((Long) ingredientObject.get("initialAmount")).intValue();
				int threshold = ((Long) ingredientObject.get("threshold")).intValue();
				boolean addable = (boolean) ingredientObject.get("addable");
				ingredients.add(new Ingredient(name, displayName, additionPrice, initialAmount, threshold, addable));
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets a particular ingredient from the list.
	 *
	 * @param ingredient the name of the ingredient to get.
	 * @return the specified ingredient if it exists; null otherwise.
	 */
	public Ingredient getIngredient(String ingredient) {
		Optional<Ingredient> t = ingredients.stream().filter(a -> a.getName().equals(ingredient)).findFirst();

		if (t.isPresent()) {
			return t.get();
		} else {
			System.err.println("Invalid type of ingredient " + ingredient + System.lineSeparator() +
					"Valid Types are:");
			ingredients.forEach(a -> System.err.println("\t" + a.getName()));
			new Exception().printStackTrace();
			System.exit(1);
		}

		return null;
	}

	/**
	 * @param ingredient the ingredient to check
	 * @return whether the ingredient is already due to get reordered.
	 */
	public boolean isInReorder(Ingredient ingredient) {
		return (this.reorders.containsKey(ingredient));
	}

	/**
	 * Orders an ingredient
	 *
	 * @param ingredient to order
	 */
	public void reorderIngredient(Ingredient ingredient) {
		reorders.put(ingredient, DEFAULT_ORDER_AMOUNT);
	}

	/**
	 * Removes an ingredient from the re-order map
	 *
	 * @param ingredient to un-reorder
	 */
	public void unReorderIngredient(Ingredient ingredient) {
		reorders.remove(ingredient);
	}

	/**
	 * Updates the amount of each ingredient that will be reordered.
	 *
	 * @param ingredient     the ingredient being reordered.
	 * @param newOrderAmount the new amount of a given ingredient to reorder.
	 */
	public void updateReorderIngredient(Ingredient ingredient, int newOrderAmount) {
		reorders.put(ingredient, newOrderAmount);
	}

	/**
	 * Updates the request.txt file to update the file with new shipment orders.
	 */
	public void updateRequestsFile() {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(requests, false)))) {
			for (Map.Entry<Ingredient, Integer> entry : reorders.entrySet()) {
				out.println(entry.getKey().getName() + "|" + entry.getValue());
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
