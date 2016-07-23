package quickfix.examples.banzai.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import quickfix.examples.banzai.OrderSide;
import quickfix.examples.banzai.OrderTIF;
import quickfix.examples.banzai.OrderType;
import quickfix.examples.banzai.ui.event.OrderEvent;
import quickfix.examples.banzai.ui.event.OrderEventListener;
import quickfix.examples.banzai.ui.event.OrderEventType;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static quickfix.examples.banzai.utils.FXUtils.doubleFieldChangeListener;
import static quickfix.examples.banzai.utils.FXUtils.integerFieldChangeListener;

@Component("orderEntryController")
@Lazy
public class OrderEntryControllerImpl implements Initializable, Observer {

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

  private List<OrderEventListener> eventListenerList = new ArrayList<>();

  public void addOrderEventListener(OrderEventListener listener) {
    eventListenerList.add(listener);
  }

  private void notify(OrderEvent event) {
    eventListenerList.forEach(l -> l.handle(event));
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
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

    this.sideComboBox.setItems(orderEntryModel.getSideList());
    this.typeComboBox.setItems(orderEntryModel.getTypeList());
    this.tifComboBox.setItems(orderEntryModel.getTIFList());
    this.sideComboBox.getSelectionModel().selectFirst();
    this.typeComboBox.getSelectionModel().selectFirst();
    this.tifComboBox.getSelectionModel().selectFirst();
    this.sessionComboBox.setItems(orderEntryModel.getSessionList());
    this.sessionComboBox.getSelectionModel().selectFirst();
  }

  private void enableNewOrderButtonForValidOrderEntry() {
    newButton.disableProperty()
            .bind(Bindings.createBooleanBinding(() -> !isValidOrderEntry(),
                    symbolTextField.textProperty(), quantityTextField.textProperty(),
                    sideComboBox.valueProperty(), typeComboBox.valueProperty(),
                    limitPriceTextField.textProperty(), stopPriceTextField.textProperty(),
                    tifComboBox.valueProperty(), sessionComboBox.valueProperty()));
  }

  private void enableReplaceOrderButtonForValidUpdate() {
    replaceButton.disableProperty()
            .bind(Bindings.createBooleanBinding(
                    () -> orderEntryModel.getSelectedOrder() == null || !isValidOrderEntry()
                            || !canReplace(),
                    quantityTextField.textProperty(), typeComboBox.valueProperty(),
                    limitPriceTextField.textProperty(), stopPriceTextField.textProperty()));
  }

  private void enableCancelOrderButton() {
    cancelButton.disableProperty()
            .bind(Bindings.createBooleanBinding(
                    () -> orderEntryModel.getSelectedOrder() == null || !canCancel(),
                    orderEntryModel.selectedOrderProperty()));
  }

  private void limitPriceIsOnlyValidForLimitOrStopLimitOrderType() {
    limitPriceTextField.disableProperty()
            .bind(
                    Bindings.createObjectBinding(
                            () -> typeComboBox.getValue() != OrderType.LIMIT
                                    && typeComboBox.getValue() != OrderType.STOP_LIMIT,
                            typeComboBox.valueProperty()));
  }

  private void stopPriceIsOnlyValidForStopOrStopLimitOrderType() {
    stopPriceTextField.disableProperty()
            .bind(
                    Bindings.createObjectBinding(
                            () -> typeComboBox.getValue() != OrderType.STOP
                                    && typeComboBox.getValue() != OrderType.STOP_LIMIT,
                            typeComboBox.valueProperty()));
  }

  @FXML
  public void onNewOrder(ActionEvent actionEvent) {
    Order order = orderEntry();
    notify(new OrderEvent(order, OrderEventType.New));
    orderEntryModel.setSelectedOrder(null);
  }

  @FXML
  public void onCancelOrder(ActionEvent actionEvent) {
    Order origOrder = orderEntryModel.getSelectedOrder();
    Order newOrder = (Order) origOrder.clone();
    notify(new OrderEvent(newOrder, OrderEventType.Cancel, origOrder));
    orderEntryModel.setSelectedOrder(null);
  }

  @FXML
  public void onReplaceOrder(ActionEvent actionEvent) {
    Order origOrder = orderEntryModel.getSelectedOrder();
    Order newOrder = (Order) origOrder.clone();
    newOrder.setQuantity(Integer.parseInt(quantityTextField.getText()));
    if (origOrder.getType() == OrderType.LIMIT || origOrder.getType() == OrderType.STOP_LIMIT) {
      newOrder.setLimit(Double.parseDouble(limitPriceTextField.getText()));
    }
    newOrder.setRejected(false);
    newOrder.setCanceled(false);

    notify(new OrderEvent(newOrder, OrderEventType.Replace, origOrder));
    orderEntryModel.setSelectedOrder(null);
  }

  @FXML
  public void onClear(ActionEvent actionEvent) {
    reset();
    notify(new OrderEvent(null, OrderEventType.ClearAll));
  }

  public void setSelectedOrder(Order order) {
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

  private void setOrder(Order order) {
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
    Order order = new Order();
    order.setSide(sideComboBox.getValue());
    order.setType(typeComboBox.getValue());
    order.setTIF(tifComboBox.getValue());

    order.setSymbol(symbolTextField.getText());
    order.setQuantity(Integer.parseInt(quantityTextField.getText()));
    order.setOpen(order.getQuantity());

    OrderType type = order.getType();
    if (type == OrderType.LIMIT || type == OrderType.STOP_LIMIT)
      order.setLimit(limitPriceTextField.getText());
    if (type == OrderType.STOP || type == OrderType.STOP_LIMIT)
      order.setStop(stopPriceTextField.getText());
    order.setSessionID(sessionComboBox.getValue());
    return order;
  }

  private boolean isValidOrderEntry() {
    return !isEmpty(symbolTextField.getText()) && !isEmpty(quantityTextField.getText())
            && sideComboBox.getValue() != null && typeComboBox.getValue() != null
            && tifComboBox.getValue() != null && isValidPrice() && sessionComboBox.getValue() != null;
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

  private boolean canCancel() {
    Order origOrder = orderEntryModel.getSelectedOrder();
    return origOrder != null && origOrder.getOpen() > 0 && isSameSessionID(origOrder);
  }

  private boolean canReplace() {
    Order origOrder = orderEntryModel.getSelectedOrder();
    return origOrder != null && origOrder.getOpen() > 0 && isSameSymbol(origOrder)
            && isSameSide(origOrder) && isSameTIF(origOrder) && isSameSessionID(origOrder)
            && (isDifferentQty(origOrder) || isDifferentOrderType(origOrder)
            || isDifferentLimitPrice(origOrder));
  }

  private boolean isSameSymbol(Order origOrder) {
    return symbolTextField.getText().equals(origOrder.getSymbol());
  }

  private boolean isSameSide(Order origOrder) {
    return sideComboBox.getValue() == origOrder.getSide();
  }

  private boolean isSameTIF(Order origOrder) {
    return tifComboBox.getValue() == origOrder.getTIF();
  }

  private boolean isSameSessionID(Order origOrder) {
    return this.sessionComboBox.getValue().equals(origOrder.getSessionID());
  }

  private boolean isDifferentQty(Order origOrder) {
    return Integer.parseInt(this.quantityTextField.getText()) != origOrder.getQuantity();
  }

  private boolean isDifferentOrderType(Order origOrder) {
    return this.typeComboBox.getValue() != origOrder.getType();
  }

  private boolean isDifferentLimitPrice(Order origOrder) {
    return (this.typeComboBox.getValue() == OrderType.LIMIT
            || this.typeComboBox.getValue() == OrderType.STOP_LIMIT)
            && !Objects.equals(this.limitPriceTextField.getText(), formatPrice(origOrder.getLimit()));
  }

  private String formatPrice(Double price) {
    return price != null ? Double.toString(price) : "";
  }

  @Override
  public void update(Observable o, Object arg) {
    LogonEvent logonEvent = (LogonEvent) arg;
    if (logonEvent.isLoggedOn()) {
      logon(logonEvent.getSessionID());
    } else {
      logoff(logonEvent.getSessionID());
    }
  }

  private void logon(final SessionID sessionID) {
    boolean wasEmpty = this.sessionComboBox.getItems().isEmpty();
    this.sessionComboBox.getItems().add(sessionID);
    if (wasEmpty || FixVersions.BEGINSTRING_FIX42.equals(sessionID.getBeginString())) {
      Platform.runLater(() -> {
        this.sessionComboBox.setValue(sessionID);
      });
    }
  }

  private void logoff(SessionID sessionID) {
    this.sessionComboBox.getItems().remove(sessionID);
  }

}