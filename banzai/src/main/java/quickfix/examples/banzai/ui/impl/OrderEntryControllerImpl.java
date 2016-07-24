package quickfix.examples.banzai.ui.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import quickfix.FixVersions;
import quickfix.SessionID;
import quickfix.examples.banzai.LogonEvent;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.OrderImpl;
import quickfix.examples.banzai.OrderSide;
import quickfix.examples.banzai.OrderTIF;
import quickfix.examples.banzai.OrderType;
import quickfix.examples.banzai.ui.OrderEntryController;
import quickfix.examples.banzai.ui.OrderEntryModel;
import quickfix.examples.banzai.ui.event.OrderEvent;
import quickfix.examples.banzai.ui.event.OrderEventType;
import quickfix.examples.banzai.ui.event.SimpleOrderEventSource;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static quickfix.examples.banzai.utils.FXUtils.doubleFieldChangeListener;
import static quickfix.examples.banzai.utils.FXUtils.integerFieldChangeListener;

@Component("orderEntryController")
@Lazy
public class OrderEntryControllerImpl extends SimpleOrderEventSource implements OrderEntryController, Initializable, Observer {

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
  private OrderEntryModel orderEntryModel;

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.quantityTextField.textProperty().addListener(integerFieldChangeListener());
    this.limitPriceTextField.textProperty().addListener(doubleFieldChangeListener());
    this.stopPriceTextField.textProperty().addListener(doubleFieldChangeListener());

    limitPriceIsOnlyValidForLimitOrStopLimitOrderType();

    stopPriceIsOnlyValidForStopOrStopLimitOrderType();

    enableNewOrderButtonForValidOrderEntry();

    enableCancelOrderButton();

    enableReplaceOrderButtonForValidUpdate();

    this.orderEntryModel.selectedOrderProperty().addListener((observable, oldOrder, newOrder) -> {
      if (newOrder == null) {
        reset();
      } else {
        setOrder(newOrder);
      }
    });

    this.sideComboBox.setItems(this.orderEntryModel.getSideList());
    this.typeComboBox.setItems(this.orderEntryModel.getTypeList());
    this.tifComboBox.setItems(this.orderEntryModel.getTIFList());
    this.sideComboBox.getSelectionModel().selectFirst();
    this.typeComboBox.getSelectionModel().selectFirst();
    this.tifComboBox.getSelectionModel().selectFirst();
    this.sessionComboBox.setItems(this.orderEntryModel.getSessionList());
    this.sessionComboBox.getSelectionModel().selectFirst();
  }

  private void enableNewOrderButtonForValidOrderEntry() {
    this.newButton.disableProperty()
            .bind(Bindings.createBooleanBinding(() -> !isValidOrderEntry(),
                    this.symbolTextField.textProperty(), this.quantityTextField.textProperty(),
                    this.sideComboBox.valueProperty(), this.typeComboBox.valueProperty(),
                    this.limitPriceTextField.textProperty(), this.stopPriceTextField.textProperty(),
                    this.tifComboBox.valueProperty(), this.sessionComboBox.valueProperty()));
  }

  private void enableReplaceOrderButtonForValidUpdate() {
    this.replaceButton.disableProperty()
            .bind(Bindings.createBooleanBinding(
                    () -> this.orderEntryModel.getSelectedOrder() == null || !isValidOrderEntry()
                            || !canReplace(),
                    this.quantityTextField.textProperty(), this.typeComboBox.valueProperty(),
                    this.limitPriceTextField.textProperty(), this.stopPriceTextField.textProperty()));
  }

  private void enableCancelOrderButton() {
    this.cancelButton.disableProperty()
            .bind(Bindings.createBooleanBinding(
                    () -> this.orderEntryModel.getSelectedOrder() == null || !canCancel(),
                    this.orderEntryModel.selectedOrderProperty()));
  }

  private void limitPriceIsOnlyValidForLimitOrStopLimitOrderType() {
    this.limitPriceTextField.disableProperty()
            .bind(
                    Bindings.createObjectBinding(
                            () -> this.typeComboBox.getValue() != OrderType.LIMIT
                                    && this.typeComboBox.getValue() != OrderType.STOP_LIMIT,
                            this.typeComboBox.valueProperty()));
  }

  private void stopPriceIsOnlyValidForStopOrStopLimitOrderType() {
    this.stopPriceTextField.disableProperty()
            .bind(
                    Bindings.createObjectBinding(
                            () -> this.typeComboBox.getValue() != OrderType.STOP
                                    && this.typeComboBox.getValue() != OrderType.STOP_LIMIT,
                            this.typeComboBox.valueProperty()));
  }

  @FXML
  public void onNewOrder(final ActionEvent actionEvent) {
    final Order order = orderEntry();
    notify(new OrderEvent(order, OrderEventType.New));
    this.orderEntryModel.setSelectedOrder(null);
  }

  @FXML
  public void onCancelOrder(final ActionEvent actionEvent) {
    final Order origOrder = this.orderEntryModel.getSelectedOrder();
    final Order newOrder = (Order) origOrder.clone();
    notify(new OrderEvent(newOrder, OrderEventType.Cancel, origOrder));
    this.orderEntryModel.setSelectedOrder(null);
  }

  @FXML
  public void onReplaceOrder(final ActionEvent actionEvent) {
    final Order origOrder = this.orderEntryModel.getSelectedOrder();
    final Order newOrder = (Order) origOrder.clone();
    newOrder.setQuantity(Integer.parseInt(this.quantityTextField.getText()));
    if (origOrder.getType() == OrderType.LIMIT || origOrder.getType() == OrderType.STOP_LIMIT) {
      newOrder.setLimit(Double.parseDouble(this.limitPriceTextField.getText()));
    }
    newOrder.setRejected(false);
    newOrder.setCanceled(false);

    notify(new OrderEvent(newOrder, OrderEventType.Replace, origOrder));
    this.orderEntryModel.setSelectedOrder(null);
  }

  @FXML
  public void onClear(final ActionEvent actionEvent) {
    reset();
    notify(new OrderEvent(null, OrderEventType.ClearAll));
  }

  public void setSelectedOrder(final Order order) {
    this.orderEntryModel.setSelectedOrder(order);
  }

  private void reset() {
    this.symbolTextField.setText("");
    this.quantityTextField.setText("");
    this.sideComboBox.getSelectionModel().selectFirst();
    this.typeComboBox.getSelectionModel().selectFirst();
    this.limitPriceTextField.setText("");
    this.stopPriceTextField.setText("");
    this.tifComboBox.getSelectionModel().selectFirst();
  }

  private void setOrder(final Order order) {
    this.symbolTextField.setText(order.getSymbol());
    this.quantityTextField.setText(Integer.toString(order.getQuantity()));
    this.sideComboBox.setValue(order.getSide());
    this.typeComboBox.setValue(order.getType());
    this.limitPriceTextField
            .setText(order.getLimit() != null ? Double.toString(order.getLimit()) : "");
    this.stopPriceTextField
            .setText(order.getStop() != null ? Double.toString(order.getStop()) : "");
    this.tifComboBox.setValue(order.getTIF());
    this.sessionComboBox.setValue(order.getSessionID());
  }

  private Order orderEntry() {
    final Order order = new OrderImpl();
    order.setSide(this.sideComboBox.getValue());
    order.setType(this.typeComboBox.getValue());
    order.setTIF(this.tifComboBox.getValue());

    order.setSymbol(this.symbolTextField.getText());
    order.setQuantity(Integer.parseInt(this.quantityTextField.getText()));
    order.setOpen(order.getQuantity());

    final OrderType type = order.getType();
    if (type == OrderType.LIMIT || type == OrderType.STOP_LIMIT)
      order.setLimit(this.limitPriceTextField.getText());
    if (type == OrderType.STOP || type == OrderType.STOP_LIMIT) {
      order.setStop(Double.parseDouble(this.stopPriceTextField.getText()));
    }
    order.setSessionID(this.sessionComboBox.getValue());
    return order;
  }

  private boolean isValidOrderEntry() {
    return !isEmpty(this.symbolTextField.getText()) && !isEmpty(this.quantityTextField.getText())
            && this.sideComboBox.getValue() != null && this.typeComboBox.getValue() != null
            && this.tifComboBox.getValue() != null && isValidPrice() && this.sessionComboBox.getValue() != null;
  }

  private boolean isValidPrice() {
    switch (this.typeComboBox.getValue()) {
      case LIMIT:
        return !isEmpty(this.limitPriceTextField.getText());
      case STOP:
        return !isEmpty(this.stopPriceTextField.getText());
      case STOP_LIMIT:
        return !isEmpty(this.limitPriceTextField.getText()) && !isEmpty(this.stopPriceTextField.getText());
      case MARKET:
        return true;
      default:
        return false;
    }
  }

  private boolean canCancel() {
    final Order origOrder = this.orderEntryModel.getSelectedOrder();
    return origOrder != null && origOrder.getOpen() > 0 && isSameSessionID(origOrder);
  }

  private boolean canReplace() {
    final Order origOrder = this.orderEntryModel.getSelectedOrder();
    return origOrder != null && origOrder.getOpen() > 0 && isSameSymbol(origOrder)
            && isSameSide(origOrder) && isSameTIF(origOrder) && isSameSessionID(origOrder)
            && (isDifferentQty(origOrder) || isDifferentOrderType(origOrder)
            || isDifferentLimitPrice(origOrder));
  }

  private boolean isSameSymbol(final Order origOrder) {
    return this.symbolTextField.getText().equals(origOrder.getSymbol());
  }

  private boolean isSameSide(final Order origOrder) {
    return this.sideComboBox.getValue() == origOrder.getSide();
  }

  private boolean isSameTIF(final Order origOrder) {
    return this.tifComboBox.getValue() == origOrder.getTIF();
  }

  private boolean isSameSessionID(final Order origOrder) {
    return this.sessionComboBox.getValue().equals(origOrder.getSessionID());
  }

  private boolean isDifferentQty(final Order origOrder) {
    return Integer.parseInt(this.quantityTextField.getText()) != origOrder.getQuantity();
  }

  private boolean isDifferentOrderType(final Order origOrder) {
    return this.typeComboBox.getValue() != origOrder.getType();
  }

  private boolean isDifferentLimitPrice(final Order origOrder) {
    return (this.typeComboBox.getValue() == OrderType.LIMIT
            || this.typeComboBox.getValue() == OrderType.STOP_LIMIT)
            && !Objects.equals(this.limitPriceTextField.getText(), formatPrice(origOrder.getLimit()));
  }

  private String formatPrice(final Double price) {
    return price != null ? Double.toString(price) : "";
  }

  @Override
  public void update(final Observable o, final Object arg) {
    final LogonEvent logonEvent = (LogonEvent) arg;
    if (logonEvent.isLoggedOn()) {
      logon(logonEvent.getSessionID());
    } else {
      logoff(logonEvent.getSessionID());
    }
  }

  private void logon(final SessionID sessionID) {
    final boolean wasEmpty = this.sessionComboBox.getItems().isEmpty();
    this.sessionComboBox.getItems().add(sessionID);
    if (wasEmpty || FixVersions.BEGINSTRING_FIX42.equals(sessionID.getBeginString())) {
      Platform.runLater(() -> {
        this.sessionComboBox.setValue(sessionID);
      });
    }
  }

  private void logoff(final SessionID sessionID) {
    this.sessionComboBox.getItems().remove(sessionID);
  }

}
