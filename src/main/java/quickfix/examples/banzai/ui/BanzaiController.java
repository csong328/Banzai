package quickfix.examples.banzai.ui;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import quickfix.SessionID;
import quickfix.examples.banzai.*;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class BanzaiController implements Initializable {
    public static final String INTEGER_PATTERN = "\\d*";
    public static final String DOUBLE_PATTERN = "\\d*(\\.\\d*)?";

    private static final ChangeListener<String> INTEGER_TEXT_FIELD_CHANGE_LISTENER = (observable, oldValue, newValue) -> {
        if (!newValue.matches(INTEGER_PATTERN))
            ((StringProperty) observable).set(oldValue);
    };
    private static final ChangeListener<String> DOUBLE_TEXT_FIELD_CHANGE_LISTENER = (observable, oldValue, newValue) -> {
        if (!newValue.matches(DOUBLE_PATTERN))
            ((StringProperty) observable).set(oldValue);
    };

    @FXML
    private TextField symbolTextField;
    @FXML
    private TextField quantityTextField;
    @FXML
    private ComboBox<OrderSide> sideComboBox;
    @FXML
    private ComboBox<OrderType> typeComboBox;
    @FXML
    private TextField limitPriceTextField;
    @FXML
    private TextField stopPriceTextField;
    @FXML
    private ComboBox<OrderTIF> tifComboBox;
    @FXML
    private ComboBox<SessionID> sessionComboBox;
    @FXML
    private Button newButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button replaceButton;
    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableView<Execution> executionTable;
    @FXML
    private TableView<TagValue> tagValueTable;

    private ChangeListener<OrderType> orderTypeChangeListener = (observable, oldValue, newValue) -> {
        switch (newValue) {
            case MARKET:
                enableLimitPrice(false);
                enableStopPrice(false);
                break;
            case LIMIT:
                enableLimitPrice(true);
                enableStopPrice(false);
                break;
            case STOP:
                enableLimitPrice(false);
                enableStopPrice(true);
                break;
            case STOP_LIMIT:
                enableLimitPrice(true);
                enableStopPrice(true);
                break;
        }
    };

    private ChangeListener<String> newOrderBtnTextFieldActivator = (observable, oldValue, newValue) -> {
        boolean valid = isValidOrderEntry();
        this.newButton.setDisable(!valid);
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.newButton.setDisable(true);
        this.replaceButton.setDisable(true);
        this.cancelButton.setDisable(true);
        this.limitPriceTextField.setDisable(true);
        this.stopPriceTextField.setDisable(true);

        this.quantityTextField.textProperty().addListener(INTEGER_TEXT_FIELD_CHANGE_LISTENER);
        this.quantityTextField.textProperty().addListener(newOrderBtnTextFieldActivator);

        this.limitPriceTextField.textProperty().addListener(DOUBLE_TEXT_FIELD_CHANGE_LISTENER);
        this.limitPriceTextField.textProperty().addListener(newOrderBtnTextFieldActivator);

        this.stopPriceTextField.textProperty().addListener(DOUBLE_TEXT_FIELD_CHANGE_LISTENER);
        this.stopPriceTextField.textProperty().addListener(newOrderBtnTextFieldActivator);

        this.sideComboBox.setItems(observableArrayList(OrderSide.values()));
        this.sideComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean valid = isValidOrderEntry();
            this.newButton.setDisable(!valid);
        });

        this.typeComboBox.setItems(observableArrayList(OrderType.values()));
        this.typeComboBox.valueProperty().addListener(orderTypeChangeListener);
        this.typeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean valid = isValidOrderEntry();
            this.newButton.setDisable(!valid);
        });

        this.tifComboBox.setItems(observableArrayList(OrderTIF.values()));
        this.tifComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean valid = isValidOrderEntry();
            this.newButton.setDisable(!valid);
        });

        this.orderTable.setItems(FXCollections.observableArrayList(sampleOrder()));
        this.executionTable.setItems(FXCollections.observableArrayList(sampleExecution()));
    }

    public void onNewOrder(ActionEvent actionEvent) {
        Order order = orderEntry();
        this.orderTable.getItems().add(order);
    }

    private Order orderEntry() {
        Order order = new Order();
        order.setSide(sideComboBox.getSelectionModel().getSelectedItem());
        order.setType(typeComboBox.getSelectionModel().getSelectedItem());
        order.setTIF(tifComboBox.getSelectionModel().getSelectedItem());

        order.setSymbol(symbolTextField.getText());
        if (!isEmpty(quantityTextField.getText())) {
            order.setQuantity(Integer.parseInt(quantityTextField.getText()));
            order.setOpen(order.getQuantity());
        }

        OrderType type = order.getType();
        if (type == OrderType.LIMIT || type == OrderType.STOP_LIMIT) {
            if (!isEmpty(limitPriceTextField.getText())) {
                order.setLimit(limitPriceTextField.getText());
            }
        }
        if (type == OrderType.STOP || type == OrderType.STOP_LIMIT) {
            if (!isEmpty(stopPriceTextField.getText())) {
                order.setStop(stopPriceTextField.getText());
            }
        }
        order.setSessionID(sessionComboBox.getSelectionModel().getSelectedItem());
        return order;
    }

    private void enableLimitPrice(boolean enabled) {
        this.limitPriceTextField.setDisable(!enabled);
    }

    private void enableStopPrice(boolean enabled) {
        this.stopPriceTextField.setDisable(!enabled);
    }

    public void onCancelOrder(ActionEvent actionEvent) {
    }

    public void onReplaceOrder(ActionEvent actionEvent) {
        this.replaceButton.setDisable(true);
    }

    private boolean isValidOrderEntry() {
        return isValid(orderEntry());
    }

    private boolean isValid(Order order) {
        return !isEmpty(order.getSymbol()) && order.getQuantity() > 0 && order.getSide() != null &&
                order.getType() != null && order.getTIF() != null && isValidPrice(order);
    }

    private boolean isValidPrice(Order order) {
        switch (order.getType()) {
            case LIMIT:
                return order.getLimit() != null;
            case STOP:
                return order.getStop() != null;
            case STOP_LIMIT:
                return order.getLimit() != null && order.getStop() != null;
            case MARKET:
                return order.getLimit() == null && order.getStop() == null;
            default:
                return false;
        }
    }

    private Order sampleOrder() {
        Order order = new Order();
        order.setSymbol("MSFT");
        order.setQuantity(100);
        order.setOpen(order.getQuantity());
        return order;
    }

    private Execution sampleExecution() {
        Execution execution = new Execution();
        execution.setSymbol("MSFT");
        execution.setQuantity(100);
        execution.setPrice(10.02);
        execution.setSide(OrderSide.BUY);
        return execution;
    }

    public void onOrderSelected(Event event) {
        Order order = this.orderTable.getSelectionModel().getSelectedItem();
        this.symbolTextField.setText(order.getSymbol());
        this.quantityTextField.setText(Integer.toString(order.getQuantity()));
        this.sideComboBox.getSelectionModel().select(order.getSide());
        this.typeComboBox.getSelectionModel().select(order.getType());

        if (order.getLimit() != null) {
            this.limitPriceTextField.setText(Double.toString(order.getLimit()));
        }
        if (order.getStop() != null) {
            this.stopPriceTextField.setText(Double.toString(order.getStop()));
        }

        this.tifComboBox.getSelectionModel().select(order.getTIF());
        this.replaceButton.setDisable(false);
        this.cancelButton.setDisable(false);

        this.tagValueTable.setItems(FXCollections.observableArrayList(order.tagValuePairs()));
    }

    public void onExecutionSelected(Event event) {
        Execution execution = this.executionTable.getSelectionModel().getSelectedItem();
        this.tagValueTable.setItems(FXCollections.observableArrayList(execution.tagValuePairs()));
    }
}
