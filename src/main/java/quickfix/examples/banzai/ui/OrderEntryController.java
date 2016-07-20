package quickfix.examples.banzai.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import quickfix.SessionID;
import quickfix.examples.banzai.*;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Component("orderEntryController")
public class OrderEntryController implements Initializable {

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

    @Autowired
    private Model model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.quantityTextField.textProperty().addListener(INTEGER_TEXT_FIELD_CHANGE_LISTENER);
        this.limitPriceTextField.textProperty().addListener(DOUBLE_TEXT_FIELD_CHANGE_LISTENER);
        this.stopPriceTextField.textProperty().addListener(DOUBLE_TEXT_FIELD_CHANGE_LISTENER);

        limitPriceTextField.disableProperty().bind(Bindings.createObjectBinding(() -> typeComboBox.getValue() != OrderType.LIMIT &&
                typeComboBox.getValue() != OrderType.STOP_LIMIT, typeComboBox.valueProperty()));

        stopPriceTextField.disableProperty().bind(Bindings.createObjectBinding(() -> typeComboBox.getValue() != OrderType.STOP &&
                typeComboBox.getValue() != OrderType.STOP_LIMIT, typeComboBox.valueProperty()));

        newButton.disableProperty().bind(Bindings.createBooleanBinding(() -> !isValidOrderEntry()
                , symbolTextField.textProperty(), quantityTextField.textProperty(), sideComboBox.valueProperty(),
                typeComboBox.valueProperty(), limitPriceTextField.textProperty(), stopPriceTextField.textProperty(), tifComboBox.valueProperty()));

        cancelButton.disableProperty().bind(Bindings.createBooleanBinding(() -> model.getSelectedOrder() == null, model.selectedOrderProperty()));

        replaceButton.disableProperty().bind(Bindings.createBooleanBinding(() -> model.getSelectedOrder() == null ||
                !isValidOrderEntry() || !isValidReplace(), quantityTextField.textProperty(), typeComboBox.valueProperty(), limitPriceTextField.textProperty(), stopPriceTextField.textProperty())
        );

        model.selectedOrderProperty().addListener((observable, oldOrder, newOrder) -> {
                    if (newOrder == null) {
                        this.symbolTextField.setText("");
                        this.quantityTextField.setText("");
                        this.sideComboBox.setValue(OrderSide.BUY);
                        this.typeComboBox.setValue(OrderType.MARKET);
                        this.limitPriceTextField.setText("");
                        this.stopPriceTextField.setText("");
                        this.tifComboBox.setValue(OrderTIF.DAY);

                    } else {
                        this.symbolTextField.setText(newOrder.getSymbol());
                        this.quantityTextField.setText(Integer.toString(newOrder.getQuantity()));
                        this.sideComboBox.setValue(newOrder.getSide());
                        this.typeComboBox.setValue(newOrder.getType());
                        this.limitPriceTextField.setText(newOrder.getLimit() != null ? Double.toString(newOrder.getLimit()) : "");
                        this.stopPriceTextField.setText(newOrder.getStop() != null ? Double.toString(newOrder.getStop()) : "");
                        this.tifComboBox.setValue(newOrder.getTIF());
                    }
                }
        );

        this.sideComboBox.setItems(model.getSideList());
        this.typeComboBox.setItems(model.getTypeList());
        this.tifComboBox.setItems(model.getTIFList());
    }

    public void onNewOrder(ActionEvent actionEvent) {
        Order order = orderEntry();
        model.addOrder(order);
        model.setSelectedOrder(null);
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

    public void onCancelOrder(ActionEvent actionEvent) {
    }

    public void onReplaceOrder(ActionEvent actionEvent) {
    }

    private boolean isValidOrderEntry() {
        return !isEmpty(symbolTextField.getText()) && !isEmpty(quantityTextField.getText()) && sideComboBox.getValue() != null &&
                typeComboBox.getValue() != null && tifComboBox.getValue() != null && isValidPrice();
    }

    private boolean isValidPrice() {
        switch (this.typeComboBox.getValue()) {
            case LIMIT:
                return !isEmpty(limitPriceTextField.getText());
            case STOP:
                return !isEmpty(stopPriceTextField.getText());
            case STOP_LIMIT:
                return !isEmpty(limitPriceTextField.getText()) && !isEmpty(stopPriceTextField.getText());
            case MARKET:
                return true;
            default:
                return false;
        }
    }

    private boolean isValidReplace() {
        Order origOrder = model.getSelectedOrder();
        return origOrder != null && symbolTextField.getText().equals(origOrder.getSymbol()) &&
                sideComboBox.getValue() == origOrder.getSide() && tifComboBox.getValue() == origOrder.getTIF() &&
                (Integer.parseInt(this.quantityTextField.getText()) != origOrder.getQuantity() ||
                        this.typeComboBox.getValue() != origOrder.getType() || isPriceDifferent());
    }

    private boolean isPriceDifferent() {
        Order origOrder = model.getSelectedOrder();
        if (this.typeComboBox.getValue() != origOrder.getType()) {
            return true;
        }

        switch (this.typeComboBox.getValue()) {
            case LIMIT:
                return !Objects.equals(this.limitPriceTextField.getText(), formatPrice(origOrder.getLimit()));
            case STOP:
                return !Objects.equals(this.stopPriceTextField.getText(), formatPrice(origOrder.getLimit()));
            case STOP_LIMIT:
                return !Objects.equals(this.limitPriceTextField.getText(), formatPrice(origOrder.getLimit())) || !Objects.equals(this.stopPriceTextField.getText(), formatPrice(origOrder.getLimit()));
            case MARKET:
                return true;
            default:
                return false;
        }
    }

    private String formatPrice(Double price) {
        return price != null ? Double.toString(price) : "";
    }
}
