package quickfix.examples.banzai.ui.impl;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import quickfix.SessionID;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.OrderSide;
import quickfix.examples.banzai.OrderTIF;
import quickfix.examples.banzai.OrderType;
import quickfix.examples.banzai.ui.OrderEntryModel;

import static javafx.collections.FXCollections.observableArrayList;

@Component("orderEntryModel")
public class OrderEntryModelImpl implements OrderEntryModel<Order> {

  private final ObjectProperty<Order> selectedOrder = new SimpleObjectProperty<>();

  private final ObservableList<OrderSide> sideList;
  private final ObservableList<OrderType> typeList;
  private final ObservableList<OrderTIF> tifList;
  private final ObservableList<SessionID> sessionList;

  public OrderEntryModelImpl() {
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
  public void setSelectedOrder(final Order order) {
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
    return this.sessionList;
  }
}
