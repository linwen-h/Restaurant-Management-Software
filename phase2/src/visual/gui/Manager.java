package visual.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import core.*;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.Wrapper;

import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.parser.ParseException;


/**
 * A controller class for this Manager
 */
public class Manager extends Employee {
    @FXML
    JFXListView<Wrapper> dishStat;
    @FXML
    JFXListView<Wrapper> ingredientStat;
    @FXML
    JFXListView<Wrapper> inventory;
    @FXML
    JFXListView<Wrapper> arrivals;
    @FXML
    JFXListView<Wrapper> orders;
    @FXML
    JFXListView<Wrapper> orderInfo;

    @FXML
    JFXTextArea requestsText;
    @FXML
    JFXButton copy;
    @FXML
    JFXButton getRequests;
    @FXML
    JFXButton getPayments;



    Statistics statistics;

    private Order viewing;

    /**
     * Initializes a new Manager
     *
     * @param user       the Manager's username
     * @param restaurant a restaurant reference
     */
    public Manager(String user, Restaurant restaurant) {
        super(user, "Manager", "manager.fxml", restaurant);
    }

    /**
     * Print the Ingredients in the Restaurant and their current usages.
     */
    public void printIngredients() {
        statistics.getIngredients();
    }

    /**
     * Print the MenuItems in the Restaurant and their current usages.
     */
    public void printMenuItems() {
        statistics.getMenuItems();
    }

    @Override
    public void initialize() {
        super.initialize();
        statistics = restaurant.getStatistics();

        Callback<ListView<Wrapper>, ListCell<Wrapper>> customFactory = param -> new JFXListCell<Wrapper>() {
            @Override
            protected void updateItem(Wrapper item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    return;
                }

                if (item.getValue() instanceof String && item.getVariable() != 42) {
                    setStyle("-fx-text-fill: rgb(200, 140, 15); -fx-font-size: 1.2em;");
                }
                if (item.getData() == -1) {
                    setStyle("-fx-text-fill: rgb(229, 162, 27); -fx-font-size: 1.3em;");
                }

                setDisable((item.getValue() instanceof String) || item.getVariable() == 42);

                if (item.getValue() instanceof MenuItem || item.getValue() instanceof Ingredient) {
                    setText(item.toString() + " -- " + item.getData());
                } else {
                    setText(item.toString());
                }
            }
        };

        dishStat.setCellFactory(customFactory);
        ingredientStat.setCellFactory(customFactory);
        inventory.setCellFactory(customFactory);
        arrivals.setCellFactory(customFactory);
        orderInfo.setCellFactory(customFactory);
        orders.setCellFactory(customFactory);

        arrivals.getItems().add(new Wrapper<>("Arrivals", -1, -1));


        getRequests.setOnAction(a -> {
            this.requestsText.clear();
            this.requestsText.setText(this.restaurant.getIngredientManager().getReorderString());
        });

        //TODO:
        getPayments.setOnAction(a -> {
            this.requestsText.clear();

            getDateFromSelector(date -> {parsePayments(date);
                return null;
            });

        });

        orders.getSelectionModel().selectedItemProperty().addListener((a1, a2, a3) -> {
            if (a3 == null) {
                return;
            }
            Order order = (Order) a3.getValue();
            viewOrder(order);

        });


        updateMenuStat();
        updateIngredientStat();
        updateInventory();
        refreshOrders();
        orderInfo.getItems().add(new Wrapper<>("Order Information", -1, -1));


    }

    //TODO: check if this works once get bill is done

    private String parsePayments( LocalDate ldate) {
        StringBuilder payments = new StringBuilder();
        payments.append(String.format("Payments for %S", ldate.toString()));
        double dailyEarnings = 0;
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader("resources/data/payments.json"));
            JSONArray data = (JSONArray) obj;

            for (Object item : data) {
                JSONObject paymentObject = (JSONObject) item;
                Date date = (Date) paymentObject.get("date");

                if (date.equals(ldate)){
                    String server = (String) paymentObject.get("server");
                    Double payment = (double) paymentObject.get("payment");
                    int tableNum = ((Long) paymentObject.get("tableNumber")).intValue();
                    payments.append(String.format("%s Table Number: %s, Server: %s, Total Payment: %d", System.lineSeparator(), tableNum, server,payment ));
                    dailyEarnings += payment;

                }
            }
            payments.append(String.format("%s Total payments for the day: %s", System.lineSeparator(), payments));

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        return payments.toString(); //TODO: when no orders for the day
    }


    /**
     * Record the latest arrival
     *
     * @param ingredient ingredient arrived
     * @param amount     amount arrived
     */
    public void addArrival(Ingredient ingredient, int amount) {
        arrivals.getItems().add(new Wrapper<>(ingredient, amount, -3));
    }

    /**
     * Update Inventory display
     */
    public void updateInventory() {
        inventory.getItems().clear();
        inventory.getItems().add(new Wrapper<>("Inventory", -1, -1));
        for (Ingredient item : restaurant.getInventoryList()) {
            inventory.getItems().add(new Wrapper<>(item, item.getAmount(), -2));
        }
    }

    /**
     * Update MenuItem statistics display
     */
    public void updateMenuStat() {
        dishStat.getItems().clear();
        dishStat.getItems().add(new Wrapper<>("Menu Items", -1, -1));
        ArrayList<MenuItem> items = statistics.getMenuItems();
        for (MenuItem item : items) {
            dishStat.getItems().add(new Wrapper<>(item, item.getUsage(), -1));
        }
    }

    /**
     * Update Ingredient statistics display
     */
    public void updateIngredientStat() {
        ingredientStat.getItems().clear();
        ingredientStat.getItems().add(new Wrapper<>("Ingredients", -1, -1));
        ArrayList<Ingredient> items = statistics.getIngredients();
        for (Ingredient item : items) {
            ingredientStat.getItems().add(new Wrapper<>(item, item.getUsage(), -1));
        }
    }


    /**
     * Refresh active orders
     */
    public void refreshOrders() {
        if (!isAvailable) return;
        orders.getItems().clear();
        orders.getItems().add(new Wrapper<>("Active Orders", -1, -1));
        for (Order item : restaurant.getAllOrders()) {
            if (item.isDelivered() || item.isCancelled()) continue;
            orders.getItems().add(new Wrapper<>(item, item.getOrderNumber(), -2));
        }
    }

    /**
     * Refresh order information of the order currently viewing
     */
    public void refreshView() {
        if (viewing == null) return;
        viewOrder(viewing);
    }

    /**
     * View order information
     *
     * @param order order to view
     */
    private void viewOrder(Order order) {
        viewing = order;
        orderInfo.getItems().clear();
        orderInfo.getItems().add(new Wrapper<>("Order Information", -1, -1));
        orderInfo.getItems().add(new Wrapper<>("Menu Item: " + order.getItem(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>("Cook: " + order.getCook(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>("Server: " + order.getServer(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>("Order Number: " + order.getOrderNumber(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>("Table: " + order.getTable(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>("Seat: " + order.getSeatNumber(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>("Received: " + order.isReceived(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>("Cooked: " + order.isCooked(), 0, 42));


    }
}
