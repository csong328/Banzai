package quickfix.examples.banzai.ui;

import org.springframework.stereotype.Component;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import quickfix.examples.banzai.Order;

import static javafx.collections.FXCollections.observableArrayList;

@Component("orderTableModel")
public class OrderTableModelImpl implements OrderTableModel {
  private final ObservableList<Order> orderList;

  public OrderTableModelImpl() {
    this.orderList = observableArrayList(
            o -> new Observable[]{o.executedProperty(), o.openProperty(), o.avgPxProperty(),
                    o.messageProperty(), o.canceledProperty(), o.isNewProperty(), o.rejectedProperty()});
  }

  @Override
  public ObservableList<Order> getOrderList() {
    return this.orderList;
  }

  @Override
  public void addOrder(Order order) {
    orderList.add(order);
  }

  @Override
  public void replaceOrder(Order newOrder) {
    int index = getIndex(newOrder.getOriginalID());
    if (index != -1) {
      orderList.set(index, newOrder);
    }
  }

  public int getIndex(String ID) {
    for (int i = 0; i < orderList.size(); i++) {
      Order item = orderList.get(i);
      if (item.getID().equals(ID)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public void clear() {
    this.orderList.clear();
  }
}
