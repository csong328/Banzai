package quickfix.examples.banzai.ui;

import static javafx.collections.FXCollections.observableArrayList;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import quickfix.SessionID;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.OrderSide;
import quickfix.examples.banzai.OrderTIF;
import quickfix.examples.banzai.OrderType;

@Component("orderEntryModel")
public class OrderEntryModelModelImpl implements OrderEntryModel {

  private ObjectProperty<Order> selectedOrder = new SimpleObjectProperty<>();

  private final ObservableList<OrderSide> sideList;
  private final ObservableList<OrderType> typeList;
  private final ObservableList<OrderTIF> tifList;
  private final ObservableList<SessionID> sessionList;

  public OrderEntryModelModelImpl() {
    this.sideList = observableArrayList();
    this.typeList = observableArrayList();
    this.tifList = observableArrayList();
    this.sessionList = observableArrayList();
  }

  @PostConstruct
  public void init() {
    this.sideList.addAll(OrderSide.values());
    this.typeList.addAll(OrderType.values());
    this.tifList.addAll(OrderTIF.values());
  }

  @Override
  public ObjectProperty<Order> selectedOrderProperty() {
    return this.selectedOrder;
  }

  @Override
  public Order getSelectedOrder() {
    return selectedOrderProperty().get();
  }

  @Override
  public void setSelectedOrder(Order order) {
    selectedOrderProperty().set(order);
  }

  @Override
  public ObservableList<OrderSide> getSideList() {
    return this.sideList;
  }

  @Override
  public ObservableList<OrderType> getTypeList() {
    return this.typeList;
  }

  @Override
  public ObservableList<OrderTIF> getTIFList() {
    return this.tifList;
  }

  @Override
  public ObservableList<SessionID> getSessionList() {
    return sessionList;
  }

  @Override
  public void logon(SessionID sessionID) {
    sessionList.add(sessionID);
  }

  @Override
  public void logoff(SessionID sessionID) {
    sessionList.remove(sessionID);
  }

}
