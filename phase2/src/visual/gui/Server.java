package visual.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import core.*;
import core.MenuItem;
import events.CancelEvent;
import events.DeliverEvent;
import events.GetBillEvent;
import events.OrderEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import util.Wrapper;

import java.util.ArrayList;
import java.util.HashMap;

public class Server extends Employee {
    //IMPORTANT: DO NOT SET TO PRIVATE!!!!!
    @FXML
    JFXListView<Node> currentList;
    @FXML
    JFXListView<Node> toAdd;
    @FXML
    JFXListView<Node> toRemove;
    @FXML
    JFXListView<Node> menuList;
    @FXML
    JFXButton addAdditional;
    @FXML
    JFXButton removeAdditional;
    @FXML
    JFXButton addExisting;
    @FXML
    JFXButton removeExisting;
    @FXML
    JFXListView<Wrapper> orders;
    @FXML
    JFXListView<Wrapper> orderInfo;
    @FXML
    JFXListView<Wrapper> toDeliver;

    @FXML
    JFXButton submitButton;

    @FXML
    JFXButton deliver;
    @FXML
    JFXButton cancelButton;
    @FXML
    JFXButton returnButton;

    @FXML
    Tab orderTab; // Tab in the GUI window

    @FXML
    JFXButton getBill;
    @FXML
    JFXTextArea billTextBox;

    Order orderToBeDelivered;
    private Order viewing;


    public Server(String user, Restaurant restaurant) {
        super(user, "Server", "server.fxml", restaurant);
    }

    //UI STUFF
    @Override
    public void initialize() {
        super.initialize();
        clearIngredientLists();

        currentList.setCellFactory(factory);
        toAdd.setCellFactory(factory);
        toRemove.setCellFactory(factory);
        menuList.setCellFactory(factory);

        menuList.getItems().add(createLabel(new Wrapper<>("Menu Items", -1, -1), getTitleStyle()));

        for (MenuItem item : restaurant.getMenu()) {
            menuList.getItems().add(createLabel(new Wrapper<>(item, 0, -1), getNormalStyle()));
        }

        menuList.getItems().sort(comparator);
        menuList.getSelectionModel().selectedItemProperty().addListener((a1, a2, a3) -> {
            clearIngredientLists();
            if (a3 == null) {
                return;
            }
            if (a3.getUserData() instanceof Wrapper) {
                Wrapper wrapper = (Wrapper) a3.getUserData();
                if (wrapper.getValue() instanceof MenuItem) {
                    MenuItem item = (MenuItem) wrapper.getValue();
                    setIngredientLists(item);
                }
            }
            submitButton.setDisable(false);
        });

        addAdditional.setOnAction(a -> {
            //get from current list, put to additional list
            Node selected = currentList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }

            if (selected.getUserData() instanceof Wrapper) {
                Wrapper ingredient = (Wrapper) selected.getUserData();
                if (ingredient.getData() == 1 && ingredient.getValue() instanceof Ingredient) {
                    //checked
                    ingredient.addVariable(-1);
                    if (ingredient.getVariable() <= 0) {
                        currentList.getItems().remove(selected);
                        currentList.getSelectionModel().clearSelection();
                    }
                }
            }

            resortLists();
        });

        submitButton.setDisable(true);

        removeAdditional.setOnAction(a -> {
            Node selected = toAdd.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }

            if (selected.getUserData() instanceof Wrapper) {
                Wrapper ingredient = (Wrapper) selected.getUserData();
                if (ingredient.getValue() instanceof Ingredient) {
                    Ingredient ing = (Ingredient) ingredient.getValue();
                    if (currentList
                            .getItems().stream()
                            .filter(item -> ((Wrapper) item.getUserData()).getValue().equals(ing))
                            .anyMatch(item -> ((Wrapper) item.getUserData()).getData() == 1)) {

                        currentList.getItems().forEach(item -> {
                            Wrapper wrap = ((Wrapper) item.getUserData());
                            if (wrap.getValue().equals(ing) && wrap.getData() == 1) {
                                wrap.addVariable(1);
                            }
                        });
                    } else {
                        Wrapper<Ingredient> wrapper = new Wrapper<>(ing, 1, 1);
                        Label label = createLabel(wrapper.getValue(), getNormalStyle());
                        Label amount = createLabel(wrapper.getVariable(), getNormalStyle());

                        wrapper.addObserver((o, arg) -> amount.setText(String.valueOf(wrapper.getVariable())));

                        BorderPane borderPane = new BorderPane();
                        borderPane.setUserData(wrapper);
                        borderPane.setLeft(label);
                        borderPane.setRight(amount);

                        currentList.getItems().add(borderPane);
                    }
                }
            }

            resortLists();
        });

        addExisting.setOnAction(a -> handleMovingFromExisting(currentList, toRemove));


        removeExisting.setOnAction(a -> handleMovingFromExisting(toRemove, currentList));

        this.tabs.getSelectionModel().selectedItemProperty().addListener((a1, a2, a3) -> {
            if (a3.equals(this.alertsTab))
                readAlerts();
        });

        submitButton.setOnAction((a) -> {

            getTableFromSelector(false, false, true, tup -> {
                Order order = getOrderFromFields(tup.x, tup.y);
                if (!this.getIsAvailable()) {
                    showDialog(orderToBeDelivered + " for table " +
                                    orderToBeDelivered.getTable().getTableNumber() + "needs to delivered first",
                            new ImageView(error)); //TODO: change image/check this
                } else if (order == null) {
                    showDialog("Could not place order: out of ingredients", new ImageView(error));
                } else {
                    OrderEvent createOrder = new OrderEvent(order, this, this.restaurant);
                    createOrder.execute();
                    showDialog("Order sent", new ImageView(success));

                }

                return null;
            });
        });

        deliver.setOnAction(a -> {
            Order order = (Order) toDeliver.getSelectionModel().getSelectedItem().getValue();
            DeliverEvent event = new DeliverEvent(order, this, restaurant);
            event.execute();
            refreshOrders();
            refreshView();
            deliver.setDisable(true);
        });

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

                setText(item.toString());
            }
        };

        orderInfo.setCellFactory(customFactory);
        orders.setCellFactory(customFactory);
        toDeliver.setCellFactory(customFactory);
        refreshOrders();
        deliver.setDisable(true);


        orders.getSelectionModel().selectedItemProperty().addListener((a1, a2, a3) -> {
            if (a3 == null) return;
            Order order = (Order) a3.getValue();
            viewOrder(order);
        });

        toDeliver.getSelectionModel().selectedItemProperty().addListener((a1, a2, a3) -> {
            if (a3 == null) return;
            deliver.setDisable(false);
        });

        orderInfo.getItems().add(new Wrapper<>("Order Information", -1, -1));

        getBill.setOnAction(a -> {
            getTableFromSelector(true, true, true, (tuple) -> {
                Table table = tuple.x;
                int seat = tuple.y;

                if (!table.isOccupied())
                    return null;

                new GetBillEvent(restaurant, table, seat, billTextBox).execute();

                return null;
            });
        });

        cancelButton.setOnAction(a -> {
            getTableFromSelector(true, true, false, (tuple) -> {
                Table table = tuple.x;

                ArrayList<Order> orders = table.getOrders();

                VBox dialog = new VBox(10);

                ComboBox<Order> orderComboBox = new ComboBox<>();
                orders.forEach(orderComboBox.getItems()::add);

                Label error = new Label();
                error.setStyle("normal-label-red");

                JFXButton confirm = new JFXButton("Confirm");
                confirm.setOnAction(a1 -> {
                    Order order = orderComboBox.getSelectionModel().getSelectedItem();
                    if(order == null){
                        error.setText("Must select an order");
                        return;
                    }

                    new CancelEvent(restaurant, order, CancelEvent.REASON.CUSTOMER_CANCELLED, null).execute();
                });

                dialog.getChildren().addAll(orderComboBox, confirm, error);
                showDialog(dialog, "Select Order", confirm);

                return null;
            });
        });
    }

    private void handleMovingFromExisting(JFXListView<Node> removeList, JFXListView<Node> addList) {
        //get from current list, put to additional list
        Node selected = removeList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        if (selected.getUserData() instanceof Wrapper) {
            Wrapper ingredient = (Wrapper) selected.getUserData();
            if (ingredient.getData() == 0 && ingredient.getValue() instanceof Ingredient) {
                //checked
                ingredient.addVariable(-1);
                if (ingredient.getVariable() <= 0) {
                    removeList.getItems().remove(selected);
                    removeList.getSelectionModel().clearSelection();
                }

                if (addList
                        .getItems().stream()
                        .filter(item -> ((Wrapper) item.getUserData()).getValue().equals(ingredient.getValue()))
                        .anyMatch(item -> ((Wrapper) item.getUserData()).getData() == 0)) {
                    addList.getItems().forEach(node -> {
                        if (node.getUserData() instanceof Wrapper) {
                            Wrapper wrapper = (Wrapper) node.getUserData();
                            if (wrapper.getValue() instanceof Ingredient) {
                                Ingredient ingredient1 = (Ingredient) wrapper.getValue();
                                if (ingredient1.equals(ingredient.getValue())) {
                                    wrapper.addVariable(1);
                                }
                            }
                        }
                    });
                } else {
                    Wrapper<Ingredient> wrapper = new Wrapper<>((Ingredient) ingredient.getValue(), 0, 1);
                    Label label = createLabel(wrapper.getValue(), getNormalStyle());
                    Label amount = createLabel(wrapper.getVariable(), getNormalStyle());

                    wrapper.addObserver((o, arg) -> amount.setText(String.valueOf(wrapper.getVariable())));

                    BorderPane borderPane = new BorderPane();
                    borderPane.setUserData(wrapper);
                    borderPane.setLeft(label);
                    borderPane.setRight(amount);

                    addList.getItems().add(borderPane);
                }
            }
        }

        resortLists();
    }

    private Order getOrderFromFields(Table table, int seat) {
        if (menuList.getSelectionModel().getSelectedItem().getUserData() instanceof Wrapper) {
            Wrapper wrapper = (Wrapper) menuList.getSelectionModel().getSelectedItem().getUserData();
            if (wrapper.getValue() instanceof MenuItem) {
                MenuItem item = (MenuItem) wrapper.getValue();
                HashMap<Ingredient, Integer>
                        additional = new HashMap<>(),
                        subtractions = new HashMap<>();

                currentList.getItems().forEach(a -> populate(a, 1, additional));
                toRemove.getItems().forEach(a -> populate(a, 0, subtractions));

                Order order = new Order(item, additional, subtractions, table, seat, this);
                //TODO: increment table seats LIMWEN
                if (restaurant.checkIngredients(order)) {
                    return order;
                }
            }
        }

        return null;
    }

    private void populate(Node a, int flag, HashMap<Ingredient, Integer> toPopulate) {
        if (a.getUserData() instanceof Wrapper) {
            Wrapper wrapper = (Wrapper) a.getUserData();
            if (wrapper.getValue() instanceof Ingredient && wrapper.getData() == flag) {
                Ingredient ingredient = (Ingredient) wrapper.getValue();
                int amount = wrapper.getVariable();
                toPopulate.put(ingredient, amount);
            }
        }
    }

    private void clearIngredientLists() {
        currentList.getItems().clear();
        toAdd.getItems().clear();
        toRemove.getItems().clear();

        Label title = createLabel(new Wrapper<>("Ingredients", -1, -1), getTitleStyle());
        Label def = createLabel(new Wrapper<>("Default Ingredients", 0, -1), getSubtitleStyle());
        Label add = createLabel(new Wrapper<>("Additional Ingredients", 1, -1), getSubtitleStyle());

        this.currentList.getItems().addAll(title, def, add);

        Label toAddLabel = createLabel(new Wrapper<>("Additional Ingredients", -1, -1),
                getTitleStyle());
        toAdd.getItems().add(toAddLabel);

        Label toRemoveLabel = createLabel(new Wrapper<>("Removals", -1, -1), getTitleStyle());
        toRemove.getItems().add(toRemoveLabel);
    }

    private void setIngredientLists(MenuItem menuItem) {
        clearIngredientLists();

        HashMap<Ingredient, Integer> amounts = menuItem.getAllIngredients();
        amounts.forEach((k, v) -> {
            Wrapper<Ingredient> wrapper = new Wrapper<>(k, 0, v);
            Label label = createLabel(wrapper.getValue(), getNormalStyle());
            Label amount = createLabel(wrapper.getVariable(), getNormalStyle());

            wrapper.addObserver((o, arg) -> {
                System.out.println("Changed");
                amount.setText(String.valueOf(wrapper.getVariable()));
            });

            BorderPane borderPane = new BorderPane();
            borderPane.setUserData(wrapper);
            borderPane.setLeft(label);
            borderPane.setRight(amount);

            this.currentList.getItems().add(borderPane);
        });

        ArrayList<Ingredient> ingredientArrayList = restaurant.getIngredientManager().getIngredients();
        ingredientArrayList.forEach(a -> {
            Wrapper<Ingredient> ingredientWrapper = new Wrapper<>(a, 0, -1);
            Label ingredientLabel = createLabel(ingredientWrapper, getNormalStyle());
            toAdd.getItems().add(ingredientLabel);
        });

        resortLists();
    }

    private void resortLists() {
        currentList.getItems().sort(comparator);
        toAdd.getItems().sort(comparator);
        toRemove.getItems().sort(comparator);
    }

    public void setOrder(Order order) {
        orderToBeDelivered = order;
    }


    /**
     * Refresh active orders
     */
    public void refreshOrders() {
        if (!isAvailable) return;
        orders.getItems().clear();
        orders.getItems().add(new Wrapper<>("Active Orders", -1, -1));
        toDeliver.getItems().clear();
        toDeliver.getItems().add(new Wrapper<>("Ready to Deliver", -1, -1));
        for (Order item : restaurant.getAllOrders()) {
            if (item.isCooked() && !item.isDelivered()) {
                toDeliver.getItems().add(new Wrapper<>(item, item.getOrderNumber(), -2));
            }
            if (item.isDelivered() || item.isCancelled() || item.getServer() != this) continue;
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
        orderInfo.getItems().add(new Wrapper<>("Order Number: " + order.getOrderNumber(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>("Table: " + order.getTable(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>("Seat: " + order.getSeatNumber(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>("Received: " + order.isReceived(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>("Cooked: " + order.isCooked(), 0, 42));
    }
}
