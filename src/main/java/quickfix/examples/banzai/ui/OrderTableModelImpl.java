package quickfix.examples.banzai.ui;

import static javafx.collections.FXCollections.observableArrayList;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import quickfix.examples.banzai.Order;

@Component("orderTableModel")
public class OrderTableModelImpl implements OrderTableModel {
  private final ObservableList<Order> orderList;
  private final Map<String, Order> idToOrder = new HashMap<>();

  public OrderTableModelImpl() {
    this.orderList = observableArrayList(
        o -> new Observable[] {o.executedProperty(), o.openProperty(), o.avgPxProperty(),
            o.messageProperty(), o.canceledProperty(), o.isNewProperty(), o.rejectedProperty()});
  }

  @Override
  public ObservableList<Order> getOrderList() {
    return this.orderList;
  }

  @Override
  public void addOrder(Order order) {
    idToOrder.put(order.getID(), order);
    orderList.add(order);
  }

  @Override
  public void addClOrdID(Order order, String id) {
    idToOrder.put(id, order);
  }

  @Override
  public Order getOrder(String ID) {
    return idToOrder.get(ID);
  }

  @Override
  public void updateOrder(Order order, String value) {
    throw new UnsupportedOperationException();
  }

}
