package visual.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import core.Ingredient;
import core.Order;
import core.Restaurant;
import events.CookEvent;
import events.ReceiveEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import util.Wrapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A controller class for the cook GUI
 */
public class Cook extends Employee {
	// this cook's current currentOrder
	private Order currentOrder = null;
	private static ArrayList<Order> assigned = new ArrayList<>();

	@FXML
	JFXListView<Wrapper> orders;
	@FXML
	JFXListView<Wrapper> orderInfo;
	@FXML
	JFXButton takeButton;
	@FXML
	JFXButton cookedButton;


	/**
	 * Initializes a new cook
	 *
	 * @param user       the username of this cook
	 * @param restaurant a restaurant reference
	 */
	public Cook(String user, Restaurant restaurant) {
		super(user, "Cook", "cook.fxml", restaurant);
		this.isAvailable = true;
	}


	@Override
	public void initialize() {
		super.initialize();

		Callback<ListView<Wrapper>, ListCell<Wrapper>> customFactory = param -> new JFXListCell<Wrapper>() {
			@Override
			protected void updateItem(Wrapper item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					return;
				}

				if (item.getValue() instanceof String) {
					setStyle("-fx-text-fill: rgb(200, 140, 15); -fx-font-size: 1.2em;");
				}
				if (item.getData() == -1) {
					setStyle("-fx-text-fill: rgb(229, 162, 27); -fx-font-size: 1.3em;");
				}

				if ( item.getValue() instanceof Ingredient) {
					setText(item.toString() + " -- " + item.getData());


				}

				setDisable((item.getValue() instanceof String || item.getValue() instanceof Ingredient));



			}
		};

		cookedButton.setDisable(true);
		orderInfo.setCellFactory(customFactory);
		orders.setCellFactory(customFactory);

		orders.getSelectionModel().selectedItemProperty().addListener((a1, a2, a3) -> {
			if (a3 == null ) {
				return;
			}
			Order order = (Order) a3.getValue();
			viewOrder(order, false);
			takeButton.setDisable(false);


		});

		takeButton.setDisable(true);

		takeButton.setOnAction(a -> {
			Order order = (Order) orders.getSelectionModel().getSelectedItem().getValue();
			if(order != null){
				currentOrder = order;
				assigned.remove(order);
				order.setCook(this);
				viewOrder(order, true);
				orders.getItems().clear();
				orders.getItems().add(new Wrapper<>("Order Assigned", 0 , 0));
				ReceiveEvent receiveEvent = new ReceiveEvent(order,this,restaurant);
				receiveEvent.execute();
				takeButton.setDisable(true);
				cookedButton.setDisable(false);

			}
		});

		cookedButton.setOnAction(a -> {
			cookedButton.setDisable(true);
			CookEvent event = new CookEvent(currentOrder, this, restaurant);
			event.execute();
			orderInfo.getItems().clear();
		});
		refreshAssigned();



	}

	/**View order information
	 * @param order order to view
	 * @param selected order is taken by the cook
	 */
	private void viewOrder(Order order, boolean selected) {
		String text = "Selected: ";
		if(!selected)text="Viewing: ";

		orderInfo.getItems().clear();
		orderInfo.getItems().add(new Wrapper<>(text + order.getItem().toString(), -1, -1));

		HashMap<Ingredient, Integer> ingredients = order.getAllIngredients();
		for (Ingredient item : ingredients.keySet()) {
			orderInfo.getItems().add(new Wrapper<>(item, ingredients.get(item) , 0));
		}
	}

	/**
	 * Changes the cook's current availability status.
	 *
	 * @param bool the new availability status of the chef.
	 */
	public void setAvailable(boolean bool) {
		this.isAvailable = bool;
	}

	/**
	 * Checks if the cook is currently available to cook an currentOrder.
	 *
	 * @return true if the cook can currently cook an currentOrder.
	 */
	public boolean isAvailable() {
		return isAvailable;
	}

	/**
	 * Sets the Order of the Cook.
	 *
	 * @param assignedOrder an Order.
	 */
	public static void assignOrder(Order assignedOrder) {
		assigned.add(assignedOrder);


	}

	public void refreshAssigned(){
		if(!isAvailable)return;
		orders.getItems().clear();
		orders.getItems().add(new Wrapper<>("Available Orders", -1, -1));
		for (Order item : assigned) {
			orders.getItems().add(new Wrapper<>(item, assigned.indexOf(item), -2));
		}
	}

	/**
	 * Pops the currentOrder off the cook
	 *
	 * @return the Order
	 */
	public Order popOrder() {
		Order curr = this.currentOrder;
		this.currentOrder = null;
		return curr;
	}


	/**
	 * Gets whether the cook is available
	 *
	 * @return the availability of this cook
	 */
	public boolean available() {
		return this.currentOrder != null;
	}
}
