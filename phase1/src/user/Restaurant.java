package user;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import core.Kitchen;
import core.Menu;
import core.Table;
import employee.Manager;
import events.Event;
import manager.IngredientManager;
import util.Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Represents a restaurant.
 * <p>
 * The main method can be executed to run the restaurant management tool.
 */
public class Restaurant {
	// Constants to store the file paths of required configuration files
	private static final String EVENTS_FP = "resources/events.txt";
	private static final String RESOURCES_FP = "resources/requests.txt";
	private static final String SETTINGS_FP = "resources/settings.txt";
	private static final String MENU_FP = "resources/menu.json";
	private static final String INGREDIENTS_FP = "resources/ingredients.json";

	/**
	 * Runs the restaurant management tool.
	 *
	 * @param args flags passed during execution (should be empty)
	 */
	public static void main(String[] args) {
		File eventsFile = new File(EVENTS_FP);
		File requestsFile = new File(RESOURCES_FP);
		File settingsFile = new File(SETTINGS_FP);
		File menuFile = new File(MENU_FP);
		File ingredientsFile = new File(INGREDIENTS_FP);

		try {
			eventsFile.createNewFile();
			requestsFile.createNewFile();
			settingsFile.createNewFile();
			menuFile.createNewFile();
			ingredientsFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		Restaurant res = new Restaurant(settingsFile, ingredientsFile, requestsFile, menuFile);
		res.parseQueue(eventsFile, res);
		res.executeQueue();
	}

	private Kitchen kitchen;
	private Menu menu;
	private String name;
	private IngredientManager ingredients;

	private ArrayList<Event> events;
	private ArrayList<Table> tables;
	private ArrayList<Manager> managers;

	/**
	 * Creates a restaurant. File paths are passed as arguments to help construct the restaurant.
	 *
	 * @param settings    the file path of the settings file.
	 * @param ingredients the file path of the ingredients file.
	 * @param menu        the file path of the menu file.
	 */
	private Restaurant(File settings, File ingredients, File requests, File menu) {
		Config config = ConfigFactory.parseFile(settings);
		String name = config.getString("restaurant.name");
		int numTables = config.getInt("restaurant.num_tables");
		int seatsPer = config.getInt("restaurant.seats_per_table");
		int numCooks = config.getInt("restaurant.num_cooks");
		int numServers = config.getInt("restaurant.num_servers");
		int numManagers = config.getInt("restaurant.num_managers");
		boolean fixConsole = config.getBoolean("restaurant.fix_console");

		if (fixConsole) {
			Tools.fixConsole();
		}

		this.ingredients = new IngredientManager(ingredients, requests);
		this.menu = new Menu(menu, this);
		this.kitchen = new Kitchen(this);
		this.events = new ArrayList<>();
		this.tables = new ArrayList<>();

		this.name = name;
		this.kitchen.addCooks(numCooks);
		this.kitchen.addServer(numServers);
		this.addManagers(numManagers);

		for (int i = 0; i < numTables; i++) {
			this.tables.add(new Table(i, seatsPer));
		}
	}

	/**
	 * Reads in commands from a file to be executed in order.
	 *
	 * @param events     the file path of the file containing the commands (events).
	 * @param restaurant the restaurant that the file is associated with.
	 */
	private void parseQueue(File events, Restaurant restaurant) {
		try (BufferedReader br = new BufferedReader(new FileReader(events))) {
			String currentLine;
			while ((currentLine = br.readLine()) != null) { // Read to the end of the file
				currentLine = currentLine.trim();           // Remove leading and trailing whitespace
				if (currentLine.startsWith("#")) {          // Ignore whitespace
					continue;
				}

				Event e = Event.parse(currentLine, restaurant); // Parse Event
				if (e != null) { // Don't add to queue if null, indicative of malformed events.txt
					this.events.add(e);
				}
			}
		} catch (IOException e) {
			e.printStackTrace(); // Couldn't read from file, exit.
			System.exit(1);
		}
	}

	/**
	 * Executes the commands waiting in the restaurant's queue (after parsing).
	 */
	private void executeQueue() {
		for (Event event : events) {
			event.execute();
		}
	}

	/**
	 * Determines if a table exists in the restaurant, and if so, returns it.
	 *
	 * @param tableNumber the table number of the table that is being checked.
	 * @return the table corresponding to the specified table number if it exists; null otherwise.
	 */
	public Table getTable(int tableNumber) {
		Optional<Table> table = tables.stream().filter(a -> a.getTableNumber() == tableNumber).findFirst();

		if (table.isPresent()) {
			return table.get();
		} else {
			System.err.println("Could not find table " + tableNumber);
			System.exit(1);
			return null;
		}
	}

	/**
	 * Adds a specified number of managers to the restaurant.
	 *
	 * @param numManagers the number of managers to add.
	 */
	private void addManagers(int numManagers) {
		this.managers = new ArrayList<>();
		for (int i = 0; i < numManagers; i++) {
			this.managers.add(new Manager(this, kitchen.getStatistics()));
		}
	}

	public Manager getManager(int id) {
		// One-liner to return the Manager with the given ID
		return managers.stream().filter(a -> a.getId() == id).findFirst().orElse(null);
	}

	public Menu getMenu() {
		return menu;
	}

	public Kitchen getKitchen() {
		return kitchen;
	}

	public IngredientManager getIngredientManager() {
		return ingredients;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
