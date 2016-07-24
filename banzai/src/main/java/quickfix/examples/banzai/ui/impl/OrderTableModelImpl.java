package quickfix.examples.banzai.ui.impl;

import org.springframework.stereotype.Component;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.OrderImpl;
import quickfix.examples.banzai.ui.OrderTableModel;

import static javafx.collections.FXCollections.observableArrayList;

@Component("orderTableModel")
public class OrderTableModelImpl implements OrderTableModel {
  private final ObservableList<Order> orderList;

  public OrderTableModelImpl() {
    this.orderList = observableArrayList(
            order -> {
              final OrderImpl o = (OrderImpl) order;
              return new Observable[]{o.executedProperty(), o.openProperty(), o.avgPxProperty(),
                      o.messageProperty(), o.canceledProperty(), o.isNewProperty(), o.rejectedProperty()};
            });
  }

  @Override
  public ObservableList<Order> getOrderList() {
    return this.orderList;
  }

  @Override
  public void addOrder(final Order order) {
    this.orderList.add(order);
  }

  @Override
  public void replaceOrder(final Order newOrder) {
    final int index = getIndex(newOrder.getOriginalID());
    if (index != -1) {
      this.orderList.set(index, newOrder);
    }
  }

  public int getIndex(final String ID) {
    for (int i = 0; i < this.orderList.size(); i++) {
      final Order item = this.orderList.get(i);
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
