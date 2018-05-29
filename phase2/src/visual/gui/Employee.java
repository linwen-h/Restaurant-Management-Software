package visual.gui;

import com.jfoenix.controls.*;
import core.Ingredient;
import core.Restaurant;
import core.Table;
import events.ShipmentEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import util.Alert;
import util.Log;
import util.Tuple;
import util.Wrapper;
import visual.Login;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Observable;

/**
 * A Base Employee class and a valid GUI
 */
public class Employee implements Comparable { //TODO: Observer stuff if we plan on going that route
    protected String user, type; // the username and the type of the user
    protected Restaurant restaurant; // a restaurant reference
    Callback<ListView<Node>, ListCell<Node>> factory;
    protected Comparator<Node> comparator;
    protected boolean isAvailable;

    /*
    FXML variables
     */
    @FXML
    StackPane stackPane; // the base node of the FXML layout
    @FXML
    JFXTabPane tabs;

    Tab alertsTab;
    Tab shipmentsTab;

    JFXListView<Alert> alertsList; //GUI list of alerts for this employee
    JFXListView<Wrapper> ingredients; // GUI list of ingredients for shipment

    //various images for dialog boxes
    protected static Image noAlerts, alerts, pin, placeholderGraphic, error, success, favicon;

    private final int id; // 1-indexed IDs
    private static int idCounter = 0;

    /*
    Initializes the images
     */
    static {
        alerts = new Image("images/alerts.png");
        noAlerts = new Image("images/noAlerts.png");
        pin = new Image("images/pin.png");
        placeholderGraphic = new Image("images/phg.gif");
        error = new Image("images/error.png");
        success = new Image("images/success.png");
        favicon = new Image("images/favicon.png");
    }

    /**
     * Creates a new Employee GUI and reference
     *
     * @param user       the username of the employee
     * @param type       the type of the employee
     * @param fxml       the fxml file of the GUI for this employee
     * @param restaurant a restaurant reference
     */
    public Employee(String user, String type, String fxml, Restaurant restaurant) {
        this.user = user;
        this.type = type;
        this.restaurant = restaurant;
        id = idCounter++;
        this.isAvailable = true;

        factory = param -> new JFXListCell<Node>() {
            @Override
            protected void updateItem(Node item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    return;
                }
                if (item.getUserData() instanceof Wrapper) {
                    Wrapper wrapper = (Wrapper) item.getUserData();

                    boolean disabled = wrapper.getValue() instanceof String;

                    setMouseTransparent(disabled);
                    setFocusTraversable(disabled);

                }
            }

        };
        //Sorts nodes in the GUI, for example making headers appear above ingredient lists
        comparator = (a, b) -> {
            if (a.getUserData() == null || b.getUserData() == null) return 0;
            if (a.getUserData() instanceof Wrapper && b.getUserData() instanceof Wrapper) {
                Wrapper aWrapper = (Wrapper) a.getUserData();
                Wrapper bWrapper = (Wrapper) b.getUserData();

                int diff = aWrapper.getData() - bWrapper.getData();
                if (diff == 0) {
                    if (aWrapper.getValue() instanceof String) {
                        return -1;
                    } else if (bWrapper.getValue() instanceof String) {
                        return 1;
                    } else {
                        return aWrapper.compareTo(bWrapper);
                    }
                } else {
                    return diff;
                }
            } else {
                return 0;
            }
        };

        if (restaurant.isLoggedIn(this)) {
            return;
            // halt and do not start
            // TODO: make this more elegant
        }

        restaurant.login(this);

        try {
            start(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    GUI stuff
     */
    // Constants for the GUI
    private static final int WIDTH = 800, HEIGHT = 900;

    public void update(Observable observable, Object obj) {
    }

    /**
     * Launches the GUI from an FXML file.
     *
     * @param fxml the fxml location
     * @throws IOException if unable to read fxml file
     */
    void start(String fxml) throws IOException {
        Parent root;
        URL url = getClass().getResource("/fxml/" + fxml);
        FXMLLoader loader = new FXMLLoader(url);
        loader.setController(this);

        root = loader.load();

        initAlertsTab();
        initShipmentsTab();

        Stage stage = new Stage();
        stage.setResizable(true);
        stage.setScene(new Scene(root, WIDTH, HEIGHT));
        stage.setMinHeight(HEIGHT);
        stage.setMinWidth(WIDTH);

        JFXDecorator decorator = new JFXDecorator(stage, root);
        ImageView fav = new ImageView(favicon);
        fav.resize(20, 20);
        decorator.setGraphic(fav);
        decorator.setText(String.format("%s - %s", type, user));

        Scene sc = new Scene(decorator, WIDTH, HEIGHT);
        sc.getStylesheets().add(Login.getCSS()); //TODO: Fix this
        stage.setScene(sc);
        stage.setOnHiding(a -> restaurant.logout(this));

        stage.show();
    }

    /**
     * Logs to the log file and the console with reference to this employee.
     *
     * @param str the message
     */
    public void log(String str) {
        Log.log(this, str);
    }

    /**
     * Logs a formatted string to the log file and the console with reference to this employee.
     *
     * @param format the format
     * @param args   the arguments for the format
     */
    public void logf(String format, Object... args) {
        Log.log(this, String.format(format, args));
    }

    /**
     * Initializes the GUI, called by the fxml file
     */
    public void initialize() {
    }

	/*
    Dialog Stuff
	 */

    /**
     * Shows a popup dialog on the StackPane
     * for (int i = 0; i <= cols; i++) {
     * <p>
     * }
     *
     * @param message the message to show
     * @param graphic the graphic to show
     */
    protected void showDialog(String message, ImageView graphic) {
        Label contents = new Label(message, graphic);
        contents.getStyleClass().add("normal-label-white");
        contents.setContentDisplay(ContentDisplay.TOP);
        contents.setGraphicTextGap(30);

        showDialog(contents, "");
    }

    /**
     * Shows a popup dialog on the StackPage with a generic node
     *
     * @param node         the node to show
     * @param title        the title of this box
     * @param closeButtons the buttons which should trigger the dialog close function
     */
    protected void showDialog(Node node, String title, Button... closeButtons) {
        BorderPane dialogBox = new BorderPane();
        JFXButton exit = new JFXButton("Exit");
        BorderPane.setAlignment(exit, Pos.CENTER_RIGHT);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("subtitle-label");

        BorderPane.setAlignment(titleLabel, Pos.TOP_CENTER);
        BorderPane.setMargin(exit, new Insets(12, 12, 12, 12));
        BorderPane.setMargin(node, new Insets(12, 12, 12, 12));
        BorderPane.setMargin(titleLabel, new Insets(12, 12, 12, 12));

        dialogBox.setPrefSize(300, 200);
        dialogBox.getStyleClass().add("dialog-pane");
        dialogBox.setCenter(node);
        dialogBox.setTop(titleLabel);
        dialogBox.setBottom(exit);

        JFXDialog dia = new JFXDialog(stackPane, dialogBox, JFXDialog.DialogTransition.BOTTOM);
        exit.setOnAction(a -> dia.close());

        for (Button button : closeButtons) {
            button.addEventHandler(ActionEvent.ACTION, (e) -> dia.close());
        }

        dia.show();
    }

    public void getTableFromSelector(boolean allowAllTable, boolean selectOnlyOccupied, boolean seatSpecific, Callback<Tuple<Table, Integer>, Void> callback) {

        int width = 600, height = 600;

        VBox tableDialog = new VBox();
        tableDialog.setSpacing(10);

        Canvas tableSelector = new Canvas(width, height);

        drawTables(
                tableSelector,
                this.restaurant.getTables(),
                100000,
                100000);

        tableSelector.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            final Table table =
                    selectTable(tableSelector, this.restaurant.getTables(), event.getX(), event.getY());
            if (table == null || (selectOnlyOccupied && !table.isOccupied()))
                return;


            if (seatSpecific) {
                VBox seatDialog = new VBox(10);

                JFXComboBox<Wrapper> seatSelector = new JFXComboBox<>();
                if (allowAllTable)
                    seatSelector.getItems().add(new Wrapper<>("All Seats", 0, 0));
                for (int i = 0; i < table.getNumberOfSeats(); i++) {
                    seatSelector.getItems().add(new Wrapper<>(i + 1, i + 1, 0));
                }

                seatSelector.setPromptText("Seat Number");

                JFXButton select = new JFXButton("Select");

                select.setOnAction(action -> {
                    Wrapper wrap = seatSelector.getSelectionModel().getSelectedItem();
                    if (wrap == null) {
                        showDialog("Must select a seat", new ImageView(error));
                        return;

                    }

                    int seat = wrap.getData();

                    callback.call(new Tuple<>(table, seat));
                });

                seatDialog.getChildren().add(seatSelector);
                seatDialog.getChildren().add(select);
                seatDialog.setAlignment(Pos.CENTER);


                showDialog(seatDialog, "Seat", select);
            } else {
                callback.call(new Tuple<>(table, 0));
            }

        });

        tableSelector.addEventHandler(MouseEvent.MOUSE_MOVED, event ->
                drawTables(
                        tableSelector,
                        this.restaurant.getTables(),
                        event.getX(),
                        event.getY()));

        tableSelector.addEventHandler(MouseEvent.MOUSE_EXITED, event ->
                drawTables(
                        tableSelector,
                        this.restaurant.getTables(),
                        100000,
                        100000));

        tableDialog.getChildren().add(tableSelector);
        showDialog(tableDialog, "Table");
    }

    private Table selectTable(Canvas canvas, ArrayList<Table> tables, double mouseX, double mouseY) {

        int cols = (int) Math.ceil(Math.sqrt(tables.size()));

        double colWidth = canvas.getWidth() / cols;
        double colHeight = canvas.getWidth() / cols;

        int col = (int) (mouseX / colWidth);
        int row = (int) (mouseY / colHeight);

        int index = col + cols * row;

        if (index >= tables.size())
            return null;
        else
            return tables.get(index);

    }

    private void drawTables(Canvas canvas, ArrayList<Table> tables, double mouseX, double mouseY) {

        int cols = (int) Math.ceil(Math.sqrt(tables.size()));

        double colWidth = canvas.getWidth() / cols;
        double colHeight = canvas.getWidth() / cols;

        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setLineWidth(0);
        g.setTextAlign(TextAlignment.CENTER);

        for (int i = 0; i < tables.size(); i++) {
            Table table = tables.get(i);

            int x1 = i % cols;
            int y1 = i / cols;

            double xb = x1 * colWidth;
            double yb = y1 * colHeight;

            g.setFill(table.isOccupied() ? Color.hsb(200, 1, .4) : Color.hsb(0, 0, .4));
            g.fillRect(xb, yb, colWidth, colHeight);


            double x = (x1 + 0.5) * colWidth;
            double y = (y1 + 0.5) * colHeight;

            g.setFill(Color.rgb(200, 200, 200));
            g.fillText("Table: " + table.getTableNumber() + System.lineSeparator() + "Seats: " + table.getNumberOfSeats(), x, y);

        }

        int col = (int) (mouseX / colWidth);
        int row = (int) (mouseY / colHeight);

        if (0 <= col && col < cols && 0 <= row && row < cols) {
            g.setFill(Color.grayRgb(200, .2));
            g.fillRect(col * colWidth, row * colHeight, colWidth, colHeight);
        }


        g.setStroke(Color.gray(.2));
        g.setLineWidth(10);

        for (int i = 0; i <= cols; i++) {
            g.strokeLine(0, i * colHeight, canvas.getWidth(), i * colHeight);
            g.strokeLine(i * colWidth, 0, i * colWidth, canvas.getHeight());
        }

    }


    public void getDateFromSelector(Callback<LocalDate, Void> callback) {
        VBox vbox = new VBox(10);
        JFXDatePicker datePicker = new JFXDatePicker();
        datePicker.setDefaultColor(Color.rgb(229, 162, 27));
        datePicker.setBackground(new Background(new BackgroundFill(Color.gray(0.2), null, null)));
        JFXButton submit = new JFXButton("Submit");

//
//        final DatePicker datePicker = new DatePicker(LocalDate.now());
//        datePicker.setOnAction(event -> {
//            LocalDate date = datePicker.getValue();
//        });


        submit.setOnAction(a -> {
            LocalDate ldate = datePicker.getValue();
            callback.call(ldate);
        });


        vbox.getChildren().addAll(datePicker, submit);
        vbox.setAlignment(Pos.CENTER);
        showDialog(vbox, "Select Date", submit);

    }

    /*
    SHIPMENTS TAB
     */
    private void initShipmentsTab() {
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

                boolean disable = (item.getValue() instanceof String);
                setMouseTransparent(disable);
                setFocusTraversable(disable);

                setText(item.toString());
            }
        };


        this.shipmentsTab = new Tab("Receive Shipment");
        tabs.getTabs().add(shipmentsTab);

        HBox tabLayout = new HBox(10);
        shipmentsTab.setContent(tabLayout);

        JFXListView<Wrapper> shipmentIngredients = new JFXListView<>();
        shipmentIngredients.setCellFactory(customFactory);

        shipmentIngredients.getItems().add(new Wrapper<>("Ingredient", -1, 0));
        HBox.setMargin(shipmentIngredients, new Insets(25, 0, 25, 25));
        restaurant.getInventoryList().forEach(a -> shipmentIngredients.getItems().add(new Wrapper<>(a, 0, 0)));


        tabLayout.getChildren().add(shipmentIngredients);


        shipmentIngredients.getSelectionModel().selectedItemProperty().addListener((a1, a2, a3) -> {
            if (a3 == null || a3.getValue() instanceof String)
                return;

            Ingredient ing = (Ingredient) a3.getValue();

            VBox dialogBox = new VBox(10);
            dialogBox.setAlignment(Pos.CENTER);
            JFXTextField amount = new JFXTextField();
            amount.setPromptText("Amount");
            forceInt(amount);

            JFXButton submit = new JFXButton("Submit");
            submit.setOnAction(a -> {
                if (!amount.getText().trim().isEmpty()) {

                    int num = Integer.parseInt(amount.getText().trim());
                    HashMap<Ingredient, Integer> ings = new HashMap<>();
                    ings.put(ing, num);
                    new ShipmentEvent(ings, restaurant, this).execute();
                }
            });

            // clear selection
            Platform.runLater(shipmentIngredients.getSelectionModel()::clearSelection);

            dialogBox.getChildren().addAll(amount, submit);
            showDialog(dialogBox, "Ingredient amount", submit);
        });
    }

    public void forceInt(JFXTextField field) {
        field.textProperty().addListener((a1, a2, a3) -> {
            if (a3.isEmpty()) {
                return;
            }
            try {
                int integer = Integer.parseInt(a3);
                if (integer < 1)
                    throw new Exception();
            } catch (Exception e) {
                field.setText(a2);
            }
        });
    }


    /*
    ALERTS TAB
     */
    // An arraylist of the active alerts for this employee
    private ArrayList<Alert> activeAlerts;

    // The creation of the cells in the AlertsListView
    Callback<ListView<Alert>, ListCell<Alert>> alertCellFactory;

    /**
     * Initializes the Alerts tab, present on all employees
     */
    private void initAlertsTab() {
        this.alertCellFactory = param ->
                new JFXListCell<Alert>() {
                    @Override
                    protected void updateItem(Alert item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            return;
                        }
                        if (item.isPinned()) {
                            setGraphic(new ImageView(pin));
                        } else {
                            if (item.isRead()) {
                                setGraphic(new ImageView(noAlerts));
                            } else {
                                setGraphic(new ImageView(alerts));
                            }
                        }

                        setText(item.toString());
                    }
                };

        this.activeAlerts = new ArrayList<>();

        alertsTab = new Tab("Alerts");
        tabs.getTabs().add(alertsTab);
        alertsTab.setGraphic(new ImageView(noAlerts));

        VBox tabLayout = new VBox();
        tabLayout.setSpacing(10);

        alertsList = new JFXListView<>();
        alertsList.setCellFactory(this.alertCellFactory);

        VBox.setVgrow(alertsList, Priority.ALWAYS);
        tabLayout.setAlignment(Pos.CENTER_RIGHT);

        Label placeholder = new Label("You have no alerts");
        placeholder.setContentDisplay(ContentDisplay.TOP);
        placeholder.setGraphic(new ImageView(placeholderGraphic));
        alertsList.setPlaceholder(placeholder);
        tabLayout.getChildren().add(alertsList);

        HBox controlBox = new HBox();
        controlBox.setSpacing(10);

        VBox.setMargin(alertsList, new Insets(25, 25, 0, 25));
        VBox.setMargin(controlBox, new Insets(0, 25, 25, 25));

        JFXButton clearAlerts = new JFXButton("Clear Alerts");
        clearAlerts.setOnAction(a -> clearAlerts());

        JFXButton sendAlerts = new JFXButton("Send Alerts");
        sendAlerts.setOnAction(a -> {
            VBox contents = new VBox();
            contents.setAlignment(Pos.BOTTOM_CENTER);
            contents.setSpacing(10);

            JFXComboBox<Wrapper> employeePicker = new JFXComboBox<>();

            restaurant.getAllEmployees().forEach(employee -> {
                Wrapper<Employee> employeeWrapper = new Wrapper<>(employee, 0, -1);
                employeeWrapper.setToString(employee.toString() + (employee.equals(Employee.this) ? " - You" : ""));
                employeePicker.getItems().add(employeeWrapper);
            });

            restaurant.getEmployeeMap().forEach((k, v) -> {
                Wrapper<String> employeeWrapper = new Wrapper<>(k, 0, -1);
                employeeWrapper.setToString(k + ": All");
                employeePicker.getItems().add(employeeWrapper);
            });

            JFXTextField textField = new JFXTextField();

            BorderPane controls = new BorderPane();
            JFXCheckBox toPin = new JFXCheckBox("Send as pinned message");
            JFXButton send = new JFXButton("Send");
            controls.setLeft(toPin);
            controls.setRight(send);

            Label error = new Label();
            error.getStyleClass().add("normal-label-red");

            send.setOnAction(b -> {
                String text = textField.getText().trim();
                Wrapper recipient = employeePicker.getSelectionModel().getSelectedItem();

                if (text.isEmpty()) {
                    error.setText("Message cannot be empty");
                    return;
                }
                if (recipient == null) {
                    error.setText("Pick an employee to alert");
                    return;
                }
                if (recipient.getValue() instanceof String) {
                    restaurant.getEmployeeMap().get(recipient.getValue()).forEach(employee -> {
                        new Alert(text, Employee.this, employee, toPin.isSelected()).execute();
                    });
                } else if (recipient.getValue() instanceof Employee) {
                    new Alert(text, Employee.this, (Employee) recipient.getValue(), toPin.isSelected()).execute();
                }

            });

            contents.getChildren().addAll(employeePicker, textField, controls, error);
            showDialog(contents, "Alerts", send);
        });

        controlBox.setAlignment(Pos.CENTER);
        controlBox.getChildren().add(clearAlerts);
        controlBox.getChildren().add(sendAlerts);

        tabLayout.getChildren().add(controlBox);
        alertsTab.setContent(tabLayout);
    }

    /**
     * Alerts this employee with the alert
     *
     * @param alert the alert to alert this employee with
     */
    public void alert(Alert alert) {
        activeAlerts.add(alert);
        updateAlertsTab();
    }

    /**
     * Sets all alerts to read
     */
    public void readAlerts() {
        this.activeAlerts.forEach(Alert::read);
        updateAlertsTab();
    }

    /**
     * Clears all unpinned alerts
     */
    public void clearAlerts() {
        this.activeAlerts.removeIf(a -> !a.isPinned());
        updateAlertsTab();
    }

    /**
     * Updates the graphic in the alerts tab itself
     */
    public void updateAlertsTab() {
        boolean hasUnReads = activeAlerts.stream().anyMatch(a -> !a.isRead());
        alertsTab.setGraphic(hasUnReads ? new ImageView(alerts) : new ImageView(noAlerts));

        alertsList.getItems().setAll(activeAlerts);
        alertsList.getItems().sort(Comparator.comparing(Alert::getMessage));
    }

	/*
    Object properties
	 */

    /**
     * Get the username that is represented by this Employee
     *
     * @return Employee.user
     */
    public String getUser() {
        return user;
    }

    /**
     * Returns the ID of the employee.
     *
     * @return a int representing the ID of the Employee.
     */
    public int getId() {
        return id;
    }

    public boolean getIsAvailable() {
        return this.isAvailable;
    }

    public void setIsAvailable(boolean bool) {
        this.isAvailable = bool;
    }

    /**
     * Gets the type of this employee
     *
     * @return Employee.type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns whether this employee is the same as obj
     *
     * @param obj the object to compare with
     * @return true if usernames are the same, case insensitive
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Employee && ((Employee) obj).getUser().toLowerCase().equals(this.getUser().toLowerCase());
    }

    /**
     * Gets a string representation of this user
     *
     * @return type: user
     */
    @Override
    public String toString() {
        return this.type + ": " + this.user;
    }

    /**
     * Compares this employee with another employee, to determine the order for EmployeePicker in alerts
     *
     * @param o the object to compare to
     * @return compares employee type first, then sorts in alphabetical order
     */
    @Override
    public int compareTo(Object o) {
        if (o instanceof Employee) {
            Employee emp = (Employee) o;
            int typeCompare = this.getType().compareTo(emp.getType());
            if (typeCompare != 0)
                return typeCompare;
            else
                return this.getUser().compareTo(emp.getUser());
        } else
            return 0;
    }

    /*
    Static helper methods
     */
    public static String getSubtitleStyle() {
        return "-fx-text-fill: rgb(200, 140, 15); -fx-font-size: 1.2em;";
    }

    public static String getTitleStyle() {
        return "-fx-text-fill: rgb(229, 162, 27); -fx-font-size: 1.3em;";
    }

    public static String getNormalStyle() {
        return "-fx-text-fill: rgb(255, 255, 255); -fx-font-size: 1em;";
    }

    protected static Label createLabel(Object userData, String style) {
        Label label = new Label(userData.toString());
        label.setStyle(style);
        label.setUserData(userData);
        return label;
    }
}
