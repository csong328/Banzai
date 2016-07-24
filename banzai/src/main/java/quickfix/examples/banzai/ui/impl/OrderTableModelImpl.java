package quickfix.examples.banzai.ui.impl;

import org.springframework.stereotype.Component;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import quickfix.examples.banzai.model.Order;
import quickfix.examples.banzai.model.OrderImpl;
import quickfix.examples.banzai.ui.OrderTableModel;

import static javafx.collections.FXCollections.observableArrayList;

@Component("orderTableModel")
public class OrderTableModelImpl implements OrderTableModel<OrderImpl> {
  private final ObservableList<OrderImpl> orderList;

  public OrderTableModelImpl() {
    this.orderList = observableArrayList(
            o -> new Observable[]{o.executedProperty(), o.openProperty(), o.avgPxProperty(),
                    o.messageProperty(), o.canceledProperty(), o.isNewProperty(), o.rejectedProperty()});
  }

  @Override
  public ObservableList<OrderImpl> getOrderList() {
    return this.orderList;
  }

  @Override
  public void addOrder(final OrderImpl order) {
    this.orderList.add(order);
  }

  @Override
  public void replaceOrder(final OrderImpl newOrder) {
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
